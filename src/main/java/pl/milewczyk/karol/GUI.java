package pl.milewczyk.karol;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import pl.milewczyk.karol.crypto.RSAKeysModel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@NoArgsConstructor
public class GUI {
    private Pane baseApp;
    private Pane loginPane;
    private Pane newUserPane;
    private Pane encryptionPane;
    private Stage primaryStage;
    private AppPanes prvPane = AppPanes.LOGIN;
    private AppPanes currentPane = AppPanes.LOGIN;

    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 500;

    enum AppPanes {LOGIN, NEW_USER, ENCRYPTION};



    public void initializeWindow(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Aplikacja szyfrująca");
        primaryStage.setScene(new Scene(baseApp, WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    public void show(){
        primaryStage.show();
    }

    public void changePane(AppPanes pane){
        if (baseApp.getChildren().size() > 1){
            prvPane = currentPane;
            baseApp.getChildren().remove(1);
        }

        switch (pane){
            case LOGIN:
                baseApp.getChildren().add(loginPane);
                break;
            case NEW_USER:
                baseApp.getChildren().add(newUserPane);
                break;
            case ENCRYPTION:
                baseApp.getChildren().add(encryptionPane);
                break;
        }

        currentPane = pane;
    }

    public void goToPrvPane () {
        changePane(prvPane);
    }


    public void loadLoginPane() throws IOException {
        URL url = new File("src/main/java/pl/milewczyk/karol/login_pane.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(c -> new LoginController(this));
        loginPane = loader.load();
    }


    public void loadBaseApp() throws IOException {
        URL url = new File("src/main/java/pl/milewczyk/karol/base_app.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(c -> new BaseAppController(this));
        baseApp = loader.load();
    }


    public void loadNewUserPane(RSAKeysModel model) throws IOException {
        URL url = new File("src/main/java/pl/milewczyk/karol/new_user_pane.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(c -> new NewUserController(this, model));
        newUserPane = loader.load();
    }


    public void loadEncryptionPane() throws IOException {
        URL url = new File("src/main/java/pl/milewczyk/karol/encryption_pane.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        encryptionPane = loader.load();
    }
}
