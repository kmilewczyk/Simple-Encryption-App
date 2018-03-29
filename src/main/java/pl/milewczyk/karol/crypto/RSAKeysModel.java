package pl.milewczyk.karol.crypto;


import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Observable;

/**
 * Keeps RSA key pairs connections and wraps key generation and management
 */
public class RSAKeysModel extends Observable {

    private KeyPairGenerator generator;
    private HashMap<String, SecuredKeyPair> keyring;
    public final int RSA_KEY_SIZE = 1024;

    private final String PRIVATE_KEY_DIRECTORY = "/.rsa/pv";
    private final String PUBLIC_KEY_DIRECTORY = "/.rsa/pu";

   public RSAKeysModel() throws NoSuchAlgorithmException {
       try {
           generator = KeyPairGenerator.getInstance("RSA");
           generator.initialize(RSA_KEY_SIZE);
       } catch (NoSuchAlgorithmException e) {
           throw new NoSuchAlgorithmException("RSA wasn't provided to environment", e);
       }
   }


   public void generateNewSecuredKeyPair(Cipher cipher, String passwordHash, String email){
       KeyPair keyPair = generator.generateKeyPair();
   }
}
