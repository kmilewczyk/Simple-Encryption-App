package pl.milewczyk.karol;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

@RequiredArgsConstructor
public class AddAddresseeDialogController {
    @NonNull
    RSAKeysModel keysModel;

    @FXML
    public ListView usersListView;
    @FXML
    public DialogPane basePane;

    @FXML
    public void initialize(){
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        usersListView.setItems(FXCollections.observableArrayList(keysModel.getUserNames()));
    }

    public String[] getSelectedUsers(){
        ObservableList selectedItems = usersListView.getSelectionModel().getSelectedItems();
        return (String[]) selectedItems
                .toArray(new String[selectedItems.size()]);
    }

}
