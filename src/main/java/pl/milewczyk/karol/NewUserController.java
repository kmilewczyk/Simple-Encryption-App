package pl.milewczyk.karol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class NewUserController {
    @NonNull
    private GUI gui;
    @NonNull
    private RSAKeysModel RSAKeysModel;

    @FXML
    public TextField userEmailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatedPasswordField;
    @FXML
    public Label errorContentLabel;

    private final static String CORRECT_USER_NAME_REGEX = "[\\S&&[^\\,]]*";
    private final static String CORRECT_PASSWORD_REGEX = ".*";

    public void generateKeyPair(MouseEvent mouseEvent) {
        if (!passwordField.getText().equals(repeatedPasswordField.getText())){
            gui.showErrorDialog("Hasła nie są identyczne");
            return;
        } else if (passwordField.getText().equals("")){
            gui.showErrorDialog("Pole hasła jest puste");
            return;
        } else if (!Pattern.matches(CORRECT_PASSWORD_REGEX, passwordField.getText())){
            gui.showErrorDialog("Hasło zawiera zabronione znaki");
            return;
        } else if (userEmailField.getText().equals("")){
            gui.showErrorDialog("Pole nazwy użytkownika jest puste");
            return;
        }
        else if (!Pattern.matches(CORRECT_USER_NAME_REGEX, userEmailField.getCharacters())){
            gui.showErrorDialog("Pole nazwy użytkownika nie może zawierać białych znaków i przecinka");
            return;
        }

        try {
            RSAKeysModel.generateNewSecuredKeyPair(passwordField.getText(), userEmailField.getText());
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
            throw new RuntimeException("RSA key generation failed", e);
        }
    }

    public void goBack(ActionEvent actionEvent) {
        gui.goToPrvPane();
    }
}
