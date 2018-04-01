package pl.milewczyk.karol;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RequiredArgsConstructor
public class NewPublicKeyController {
    @NonNull
    private GUI gui;
    @NonNull
    private RSAKeysModel RSAKeysModel;

    @FXML
    public AnchorPane basePane;
    @FXML
    public Label filePath;
    @FXML
    public TextField usernameField;

    private File chosenFile;

    public void goBack(ActionEvent actionEvent) {
        gui.goToPrvPane();
    }

    public void addKey(ActionEvent actionEvent) throws IOException {
        ValidityAnswer ans = ValidityChecker.validUsername(usernameField.getText());
        if (!ans.isValid){
            gui.showErrorDialog(ans.reason);
            return;
        } else if (chosenFile == null){
            gui.showErrorDialog("Nie wybrano żadnego pliku");
            return;
        }

        try {
            RSAKeysModel.addNewPublicKey(usernameField.getText(), chosenFile);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("RSA environment was not suppoted");
        }

        chosenFile = null;
        filePath.setText("");
    }

    public void chooseKey(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz klucz publiczny");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Klucze publiczne", ".pub"));
        chosenFile = fileChooser.showOpenDialog(basePane.getScene().getWindow());
        if (chosenFile != null)
            filePath.setText(chosenFile.getAbsolutePath());
    }
}
