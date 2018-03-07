package pl.milewczyk.karol.crypto;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;

import java.security.PublicKey;

@AllArgsConstructor
public class SecuredKeyPair {
    @NotNull
    public PublicKey publicKey;
    @NotNull
    public String securedPrivateKey;
}
