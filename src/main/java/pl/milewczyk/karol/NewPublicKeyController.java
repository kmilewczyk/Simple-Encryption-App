package pl.milewczyk.karol;


import javafx.event.ActionEvent;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.milewczyk.karol.crypto.RSAKeysModel;

@AllArgsConstructor
public class NewPublicKeyController {
    @NonNull
    private GUI gui;
    @NonNull
    private RSAKeysModel RSAKeysModel;

    public void goBack(ActionEvent actionEvent) {
        gui.goToPrvPane();
    }

    public void addKey(ActionEvent actionEvent) {
    }
}
