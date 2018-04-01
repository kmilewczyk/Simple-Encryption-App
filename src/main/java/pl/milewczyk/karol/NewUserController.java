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



    public void generateKeyPair(MouseEvent mouseEvent) {
        ValidityAnswer answer;
        answer = ValidityChecker.validUsername(userEmailField.getText());
        if (!answer.isValid){
            gui.showErrorDialog(answer.reason);
            return;
        }
        answer = ValidityChecker.validPassword(passwordField.getText(), repeatedPasswordField.getText());
        if (!answer.isValid){
            gui.showErrorDialog(answer.reason);
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
