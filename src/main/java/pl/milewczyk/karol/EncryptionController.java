package pl.milewczyk.karol;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class EncryptionController {
    @NonNull
    private GUI gui;
    @NonNull
    private RSAKeysModel keysModel;

    @FXML
    public ListView addresseeListView;

    public void addAddressee(ActionEvent actionEvent) throws IOException {
        AddAddresseeDialog dialog = new AddAddresseeDialog(keysModel);
        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(users -> Arrays.stream(users)
                .forEach(user -> {
                    if (!addresseeListView.getItems().contains(user))
                        addresseeListView.getItems().add(user);
                }));
    }

    public void removeAddressee(ActionEvent actionEvent) {
        addresseeListView.getSelectionModel().getSelectedIndices().stream()
                .forEach((i -> addresseeListView.getItems().remove(((Integer)i).intValue())));
    }

    public void encrypt(ActionEvent actionEvent) {
    }

    public void chooseInputFile(ActionEvent actionEvent) {
    }

    public void chooseOutputFile(ActionEvent actionEvent) {
    }
}
