package pl.milewczyk.karol;

import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseAppController {
    @NotNull
    private GUI gui;

    public void goToNewUserPane(ActionEvent actionEvent) {
        gui.changePane(GUI.AppPanes.NEW_USER);
    }

    public void wyloguj(ActionEvent actionEvent) {
        // TODO wylogowywanie
        gui.changePane(GUI.AppPanes.LOGIN);
    }
}
