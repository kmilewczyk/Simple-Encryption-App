package pl.milewczyk.karol;

import javafx.event.ActionEvent;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class BaseAppController {
    @NonNull
    private GUI gui;

    public void goToNewUserPane(ActionEvent actionEvent) {
        gui.changePane(GUI.AppPanes.NEW_USER);
    }

    public void wyloguj(ActionEvent actionEvent) {
        // TODO wylogowywanie
        gui.changePane(GUI.AppPanes.LOGIN);
    }

    public void goToNewPublicKeyPane(ActionEvent actionEvent) {
        gui.changePane(GUI.AppPanes.NEW_PUBLIC_KEY);
    }
}
