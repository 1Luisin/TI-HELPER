package br.com.scmjf.tihelper.controller;

import java.io.IOException;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.SceneUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        AppContext.authService().login(username, password)
                .ifPresentOrElse(this::openMainWindow, () -> errorLabel.setText("Usuário ou senha inválidos."));
    }

    private void openMainWindow(User user) {
        try {
            AppContext.setCurrentUser(user);

            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/br/com/scmjf/tihelper/view/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(SceneUtil.createScene(stage, root, 1180, 760));
            stage.centerOnScreen();
        } catch (IOException exception) {
            errorLabel.setText("Não foi possível abrir a tela principal.");
        }
    }
}
