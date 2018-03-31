package pl.milewczyk.karol;

import javafx.scene.input.MouseEvent;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class LoginController {
    @NonNull
    public GUI gui;

    public void loginEvent(MouseEvent mouseEvent) {
        gui.changePane(GUI.AppPanes.ENCRYPTION);
    }


}
