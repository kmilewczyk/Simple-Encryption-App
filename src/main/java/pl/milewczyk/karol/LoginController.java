package pl.milewczyk.karol;

import com.sun.istack.internal.NotNull;
import javafx.scene.input.MouseEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginController {
    @NotNull
    public GUI gui;

    public void loginEvent(MouseEvent mouseEvent) {
        gui.changePane(GUI.AppPanes.ENCRYPTION);
    }


}
