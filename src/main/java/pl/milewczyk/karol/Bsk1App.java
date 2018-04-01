package pl.milewczyk.karol;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.milewczyk.karol.crypto.RSAKeysModel;

public class Bsk1App extends Application {
    private GUI gui;
    private RSAKeysModel rsaKeyModel;


    @Override
    public void start(Stage primaryStage) throws Exception{
        gui = new GUI();
        rsaKeyModel = new RSAKeysModel();

        loadPanes();
        gui.initializeWindow(primaryStage);
        gui.changePane(GUI.AppPanes.LOGIN);
        gui.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void loadPanes() throws Exception{
        gui.loadBaseApp();
        gui.loadLoginPane();
        gui.loadEncryptionPane(rsaKeyModel);
        gui.loadNewPublicKeyPane(rsaKeyModel);
        gui.loadNewUserPane(rsaKeyModel);

    }
}
