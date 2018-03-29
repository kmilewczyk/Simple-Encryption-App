package pl.milewczyk.karol;


import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import lombok.AllArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

@AllArgsConstructor
public class NewPublicKeyController {
    @NotNull
    private GUI gui;
    @NotNull
    private RSAKeysModel RSAKeysModel;

    public void goBack(ActionEvent actionEvent) {
        gui.goToPrvPane();
    }

}
