package pl.milewczyk.karol.crypto;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.Setter;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * Keeps RSA key pairs connections and wraps key generation and management
 */
public class RSAKeysModel extends Observable {
    // TODO change to Beans
    public final int RSA_KEY_SIZE = 1024;

    private KeyPairGenerator generator;
    private KeyFactory keyFactory;
    private Cipher rsaCipher;
    private Cipher passwordCipher;

    /**
     * userName -> keypair
     */
    private HashMap<String, SecuredKeyPair> keyring = new HashMap<>();
    @Setter
    private String userPassword;

    private final static String KEY_FOLDER_NAME = ".rsa";
    private final static Path PRIVATE_KEY_DIR_PATH = Paths.get(KEY_FOLDER_NAME, "pv");
    private final static Path PUBLIC_KEY_DIR_PATH = Paths.get(KEY_FOLDER_NAME, "pu");
    private final static Path KEY_LINK_FILE_PATH = Paths.get(KEY_FOLDER_NAME, "lookup");

    private enum LookupAttribute{
        PUBLIC("public"),
        PAIR("pair");
        private final String val;
        private static final Map<String, LookupAttribute> lookup = new HashMap<>();
        static {
            for (LookupAttribute attr : LookupAttribute.values()){
                lookup.put(attr.get(), attr);
            }
        }
        LookupAttribute(final String val) {
            this.val = val;
        }
        public final String get(){
            return val;
        }
        public static LookupAttribute get(String key){
            return lookup.get(key);
        }
    }

   public RSAKeysModel() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchPaddingException {
       try {
           generator = KeyPairGenerator.getInstance("RSA");
           keyFactory = KeyFactory.getInstance("RSA");
           generator.initialize(RSA_KEY_SIZE);
           rsaCipher = Cipher.getInstance("RSA");
           passwordCipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
       } catch (NoSuchAlgorithmException e) {
           throw new NoSuchAlgorithmException("RSA wasn't provided to environment", e);
       }

       createDirectories();

       readKeyringData();
   }


   private void createDirectories() throws IOException {
       try {
           Files.createDirectory(Paths.get(KEY_FOLDER_NAME));
           Files.setAttribute(Paths.get(KEY_FOLDER_NAME), "dos:hidden", true);
       } catch(FileAlreadyExistsException e){}

       try {
           Files.createDirectory(PRIVATE_KEY_DIR_PATH);
       } catch(FileAlreadyExistsException e){}

       try {
           Files.createDirectory(PUBLIC_KEY_DIR_PATH);
       } catch(FileAlreadyExistsException e){}

       try {
           Files.createFile(KEY_LINK_FILE_PATH);
       } catch(FileAlreadyExistsException e){}
   }
   private void readKeyringData() throws IOException, InvalidKeySpecException {
        Base64.Decoder decoder = Base64.getDecoder();
        CSVReader csvreader = new CSVReader(new FileReader(KEY_LINK_FILE_PATH.toString()));

       for (Iterator<String[]> it = csvreader.iterator(); it.hasNext(); ) {
           String[] line = it.next();
           switch (LookupAttribute.get(line[2])){
               case PUBLIC:
                   break;
               case PAIR:
                   byte[] publicKey = readKeyFile(new File(new File(PUBLIC_KEY_DIR_PATH.toString()), line[0] + ".pub"),
                          "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");

                   byte[] privateKey = readKeyFile(new File(new File(PRIVATE_KEY_DIR_PATH.toString()), line[0]),
                           "-----BEGIN ENCRYPTED PRIVATE KEY-----", "-----END ENCRYPTED PRIVATE KEY-----");

                   keyring.put(line[1], new SecuredKeyPair(
                           keyFactory.generatePublic(new X509EncodedKeySpec(publicKey)),
                           privateKey));
                           break;
           }
       }
   }


   private byte[] readKeyFile(File file, String header, String footer) throws IOException {
       StringBuffer keyBuffer = new StringBuffer();
       BufferedReader reader = new BufferedReader(new FileReader(file));
       if (!reader.readLine().equals(header))
           throw new FileFormatException("Key header invalid");

       String line;
       while (!footer.equals(line = reader.readLine())){
           if (line == null)
               throw new FileFormatException("Sudden end of file");

           keyBuffer.append(line);
       }

       reader.close();
       return Base64.getDecoder().decode(keyBuffer.toString());
   }


   public boolean userExists(String username){
        return keyring.get(username) != null;
   }


   public void addNewPublicKey(String userName, Path keyFilePath){
       // check validity
       // check if one doesn't already exists
       // write to file
       // bind to lookup
   }


   private void bindNewKeyPair(String keysFileName, String userName, SecuredKeyPair keypair) throws IOException {
       CSVWriter csvwriter = new CSVWriter(new FileWriter(KEY_LINK_FILE_PATH.toString(), true));
       csvwriter.writeNext(new String[]{keysFileName, userName, LookupAttribute.PAIR.get()});
       csvwriter.close();

       keyring.put(userName, keypair);
   }


   private void bindNewPublicKey(String keyFileName, String userName){
   }

   private void writePrivateKeyFile(String filename, byte[] securedPrivateKey) throws IOException {
        FileWriter writer = new FileWriter(new File(new File(PRIVATE_KEY_DIR_PATH.toString()), filename));
        writer.write("-----BEGIN ENCRYPTED PRIVATE KEY-----" + System.lineSeparator());
        String encodedKey = Base64.getEncoder().encodeToString(securedPrivateKey);
        int i;
        for (i = 0; i+64 < encodedKey.length(); i += 64) {
            writer.write(encodedKey, i, 64);
            writer.write(System.lineSeparator());
        }
        writer.write(encodedKey, i, encodedKey.length()-i);
        writer.write(System.lineSeparator());
        writer.write("-----END ENCRYPTED PRIVATE KEY-----" + System.lineSeparator());
        writer.close();
   }

   private void writePublicKeyFile(String filename, byte[] publicKey) throws IOException {
        FileWriter writer = new FileWriter(new File(new File(PUBLIC_KEY_DIR_PATH.toString()), filename + ".pub"));
        writer.write("-----BEGIN PUBLIC KEY-----" + System.lineSeparator());
        String encodedKey = Base64.getEncoder().encodeToString(publicKey);
        int i;
        for (i = 0; i+64 < encodedKey.length(); i += 64) {
            writer.write(encodedKey, i, 64);
            writer.write(System.lineSeparator());
        }
        writer.write(encodedKey, i, encodedKey.length()-i);
        writer.write(System.lineSeparator());
        writer.write("-----END PUBLIC KEY-----" + System.lineSeparator());
        writer.close();
   }

   private void initCipherWithPassword(int mode, String userPassword) throws NoSuchAlgorithmException, InvalidKeyException {
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       passwordCipher.init(mode,
               new SecretKeySpec(md.digest(userPassword.getBytes()), 0, 64 / 8, "Blowfish"));
   }


   public void generateNewSecuredKeyPair(String userPassword, String userName) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
       KeyPair keyPair = generator.generateKeyPair();
       Base64.Encoder encoder = Base64.getEncoder();
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       initCipherWithPassword(Cipher.ENCRYPT_MODE, userPassword);

       byte[] secureSecret = passwordCipher.doFinal(keyPair.getPrivate().getEncoded());
       String filename = encoder.encodeToString(md.digest(userName.getBytes())).replace('/', '-');

       writePrivateKeyFile(filename, secureSecret);
       writePublicKeyFile(filename, keyPair.getPublic().getEncoded());
       bindNewKeyPair(filename, userName, new SecuredKeyPair(keyPair.getPublic(), secureSecret));
   }

   public byte[] encrypt(String userName, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (!this.userExists(userName)){
            throw new IllegalArgumentException("User is not registered");
        }

       rsaCipher.init(Cipher.ENCRYPT_MODE, keyring.get(userName).publicKey);
        return rsaCipher.doFinal(data);
   }


   public byte[] decrypt(String userName, byte[] data) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException {
       if (userPassword == null)
            throw new IllegalStateException("No userPassword provided");
       if (!this.userExists(userName))
           throw new IllegalArgumentException("User is not registered");

       SecuredKeyPair pair = keyring.get(userName);
       initCipherWithPassword(Cipher.DECRYPT_MODE, userPassword);
       byte[] plainSecretKey = passwordCipher.doFinal(pair.securedPrivateKey);

       rsaCipher.init(Cipher.DECRYPT_MODE,
               keyFactory.generatePrivate(new PKCS8EncodedKeySpec(plainSecretKey)));

       return rsaCipher.doFinal(data);
   }
}

//
//    KeyPair keyPair = generator.generateKeyPair();
//    Base64.Encoder encoder = Base64.getEncoder();
//    Base64.Decoder decoder = Base64.getDecoder();
//    byte[] secret = keyPair.getPrivate().getEncoded();
//
//      try {
//              System.out.println(encoder.encodeToString(secret));
//              byte[] encoded = cipher.doFinal(secret);
//              String encryptedString = encoder.encodeToString(encoded);
//              cipher.init(Cipher.DECRYPT_MODE, key);
//              encoded = decoder.decode(encryptedString);
//              System.out.println(encoder.encodeToString(cipher.doFinal(encoded)));
//              } catch (IllegalBlockSizeException e) {
//              e.printStackTrace();
//              } catch (BadPaddingException e) {
//              e.printStackTrace();
//              } catch (InvalidKeyException e) {
//              e.printStackTrace();
//              }