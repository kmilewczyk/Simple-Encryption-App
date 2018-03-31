package pl.milewczyk.karol.crypto;

import lombok.NonNull;
import lombok.AllArgsConstructor;

import java.security.Key;
import java.security.PublicKey;

@AllArgsConstructor
public class SecuredKeyPair {
    @NonNull
    public PublicKey publicKey;
    @NonNull
    public byte[] securedPrivateKey;
}
