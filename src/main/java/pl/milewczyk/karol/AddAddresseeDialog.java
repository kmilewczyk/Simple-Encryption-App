package pl.milewczyk.karol;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import pl.milewczyk.karol.crypto.RSAKeysModel;

import java.io.IOException;
import java.util.Optional;

public class AddAddresseeDialog extends Dialog<String[]> {
    public AddAddresseeDialog(RSAKeysModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(Bsk1App.class.getResource("/add_addressee_dialog.fxml"));
        loader.setControllerFactory(c -> new AddAddresseeDialogController(model));
        Parent root = loader.load();

        getDialogPane().setContent(root);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        AddAddresseeDialogController controller = loader.getController();

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.CLOSE)
                return null;
            else
                return controller.getSelectedUsers();
        });

    }
}
