package pl.milewczyk.karol;

import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import lombok.AllArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

@AllArgsConstructor
public class NewUserController {
    @NotNull
    private GUI gui;
    @NotNull
    private RSAKeysModel RSAKeysModel;

    public void generateKeyPair(MouseEvent mouseEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        gui.goToPrvPane();
    }
}
