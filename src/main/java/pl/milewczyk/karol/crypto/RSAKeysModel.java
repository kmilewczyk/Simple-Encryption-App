package pl.milewczyk.karol.crypto;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

    @Getter
    @Setter
    private String currentUserPassword;

    @Setter
    @Getter
    private String currentUsername;

    private final static String KEY_FOLDER_NAME = ".rsa";
    private final static Path PRIVATE_KEY_DIR_PATH = Paths.get(KEY_FOLDER_NAME, "pv");
    private final static Path PUBLIC_KEY_DIR_PATH = Paths.get(KEY_FOLDER_NAME, "pu");
    private final static Path KEY_LINK_FILE_PATH = Paths.get(KEY_FOLDER_NAME, "lookup");

    public static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    public static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    public static final String BEGIN_ENCRYPTED_PRIVATE_KEY = "-----BEGIN ENCRYPTED PRIVATE KEY-----";
    public static final String END_ENCRYPTED_PRIVATE_KEY = "-----END ENCRYPTED PRIVATE KEY-----";

    public enum StoredKeyType {
        PUBLIC("public"),
        PAIR("pair");
        private final String val;
        private static final Map<String, StoredKeyType> lookup = new HashMap<>();
        static {
            for (StoredKeyType attr : StoredKeyType.values()){
                lookup.put(attr.get(), attr);
            }
        }
        StoredKeyType(final String val) {
            this.val = val;
        }
        public final String get(){
            return val;
        }
        public static StoredKeyType get(String key){
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
        CSVReader csvreader = new CSVReader(new FileReader(KEY_LINK_FILE_PATH.toString()));

       for (Iterator<String[]> it = csvreader.iterator(); it.hasNext(); ) {
           String[] line = it.next();
           switch (StoredKeyType.get(line[2])){
               case PUBLIC:
                   break;
               case PAIR:
                   byte[] publicKey = readKeyFile(new File(new File(PUBLIC_KEY_DIR_PATH.toString()), line[0] + ".pub"),
                           BEGIN_PUBLIC_KEY, END_PUBLIC_KEY);

                   byte[] privateKey = readKeyFile(new File(new File(PRIVATE_KEY_DIR_PATH.toString()), line[0]),
                           BEGIN_ENCRYPTED_PRIVATE_KEY, END_ENCRYPTED_PRIVATE_KEY);


                   keyring.put(line[1],
                           new SecuredKeyPair(getPublicKey(publicKey), privateKey));
                   break;
           }
       }
   }

   private PublicKey getPublicKey(byte[] publicKey) throws InvalidKeySpecException {
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
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

   public StoredKeyType getKeyType(String username){
        if (!this.userExists(username))
            throw new IllegalArgumentException("User is not in dictionary!");

        if (keyring.get(username).securedPrivateKey == null)
            return StoredKeyType.PUBLIC;
        else return StoredKeyType.PAIR;
   }


   public boolean userExists(String username){
        return keyring.get(username) != null;
   }


   public void addNewPublicKey(String userName, File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
       if (this.userExists(userName)) {
           throw new IllegalArgumentException("User is already registered");
       }

       byte[] publicKey = readKeyFile(file, BEGIN_PUBLIC_KEY, END_PUBLIC_KEY);
       String filename = getFilenameFromUsername(userName);
       writePublicKeyFile(filename, publicKey);
       bindNewPublicKey(getPublicKey(publicKey), filename, userName);
   }


   private void bindNewKeyPair(String keysFileName, String userName, SecuredKeyPair keypair) throws IOException {
       CSVWriter csvwriter = new CSVWriter(new FileWriter(KEY_LINK_FILE_PATH.toString(), true));
       csvwriter.writeNext(new String[]{keysFileName, userName, StoredKeyType.PAIR.get()});
       csvwriter.close();

       keyring.put(userName, keypair);
   }


   private void bindNewPublicKey(PublicKey publicKey, String keyFileName, String userName) throws IOException {
       CSVWriter csvwriter = new CSVWriter(new FileWriter(KEY_LINK_FILE_PATH.toString(), true));
       csvwriter.writeNext(new String[]{keyFileName, userName, StoredKeyType.PAIR.get()});
       csvwriter.close();

       keyring.put(userName, new SecuredKeyPair(publicKey, null));
   }

   private void writePrivateKeyFile(String filename, byte[] securedPrivateKey) throws IOException {
        FileWriter writer = new FileWriter(new File(new File(PRIVATE_KEY_DIR_PATH.toString()), filename));
        writer.write(BEGIN_ENCRYPTED_PRIVATE_KEY + System.lineSeparator());
        String encodedKey = Base64.getEncoder().encodeToString(securedPrivateKey);
        int i;
        for (i = 0; i+64 < encodedKey.length(); i += 64) {
            writer.write(encodedKey, i, 64);
            writer.write(System.lineSeparator());
        }
        writer.write(encodedKey, i, encodedKey.length()-i);
        writer.write(System.lineSeparator());
        writer.write(END_ENCRYPTED_PRIVATE_KEY + System.lineSeparator());
        writer.close();
   }

   private void writePublicKeyFile(String filename, byte[] publicKey) throws IOException {
        FileWriter writer = new FileWriter(new File(new File(PUBLIC_KEY_DIR_PATH.toString()), filename + ".pub"));
       writer.write(BEGIN_PUBLIC_KEY + System.lineSeparator());
        String encodedKey = Base64.getEncoder().encodeToString(publicKey);
        int i;
        for (i = 0; i+64 < encodedKey.length(); i += 64) {
            writer.write(encodedKey, i, 64);
            writer.write(System.lineSeparator());
        }
        writer.write(encodedKey, i, encodedKey.length()-i);
        writer.write(System.lineSeparator());
        writer.write(END_PUBLIC_KEY + System.lineSeparator());
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
       initCipherWithPassword(Cipher.ENCRYPT_MODE, userPassword);

       byte[] secureSecret = passwordCipher.doFinal(keyPair.getPrivate().getEncoded());

       String filename = getFilenameFromUsername(userName);

       writePrivateKeyFile(filename, secureSecret);
       writePublicKeyFile(filename, keyPair.getPublic().getEncoded());
       bindNewKeyPair(filename, userName, new SecuredKeyPair(keyPair.getPublic(), secureSecret));
   }

    @NotNull
    private String getFilenameFromUsername(String userName) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(md.digest(userName.getBytes())).replace('/', '-');
    }

    public byte[] encrypt(String userName, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (!this.userExists(userName)){
            throw new IllegalArgumentException("User is not registered");
        }

       rsaCipher.init(Cipher.ENCRYPT_MODE, keyring.get(userName).publicKey);
        return rsaCipher.doFinal(data);
   }


   public byte[] decrypt(String userName, byte[] data) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException {
       if (currentUserPassword == null)
            throw new IllegalStateException("No currentUserPassword provided");
       if (!this.userExists(userName))
           throw new IllegalArgumentException("User is not registered");

       SecuredKeyPair pair = keyring.get(userName);
       if (pair.securedPrivateKey == null){
           throw new IllegalArgumentException("There is not private key bound to user");
       }

       initCipherWithPassword(Cipher.DECRYPT_MODE, currentUserPassword);
       byte[] plainSecretKey = passwordCipher.doFinal(pair.securedPrivateKey);

       rsaCipher.init(Cipher.DECRYPT_MODE,
               keyFactory.generatePrivate(new PKCS8EncodedKeySpec(plainSecretKey)));

       return rsaCipher.doFinal(data);
   }

   public String[] getUserNames(){
        return keyring.keySet().toArray(new String[keyring.size()]);
   }
}
