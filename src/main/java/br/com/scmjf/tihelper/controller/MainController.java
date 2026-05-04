package br.com.scmjf.tihelper.controller;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Label screenTitleLabel;

    @FXML
    private Label loggedUserLabel;

    @FXML
    private Label profileLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button serversButton;

    @FXML
    private Button servicesButton;

    @FXML
    private Button queriesButton;

    @FXML
    private Button scriptsButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button settingsButton;

    @FXML
    private StackPane contentArea;

    private final Map<NavigationTarget, Button> navigationButtons = new EnumMap<>(NavigationTarget.class);

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        if (user != null) {
            loggedUserLabel.setText(user.getUsername());
            profileLabel.setText(user.getProfileName());
        }

        navigationButtons.put(NavigationTarget.DASHBOARD, dashboardButton);
        navigationButtons.put(NavigationTarget.SERVERS, serversButton);
        navigationButtons.put(NavigationTarget.SERVICES, servicesButton);
        navigationButtons.put(NavigationTarget.QUERIES, queriesButton);
        navigationButtons.put(NavigationTarget.SCRIPTS, scriptsButton);
        navigationButtons.put(NavigationTarget.HISTORY, historyButton);
        navigationButtons.put(NavigationTarget.SETTINGS, settingsButton);

        AppContext.setNavigationHandler(this::showScreen);
        showScreen(NavigationTarget.DASHBOARD);
    }

    @FXML
    private void showDashboard() {
        showScreen(NavigationTarget.DASHBOARD);
    }

    @FXML
    private void showServers() {
        showScreen(NavigationTarget.SERVERS);
    }

    @FXML
    private void showServices() {
        showScreen(NavigationTarget.SERVICES);
    }

    @FXML
    private void showQueries() {
        showScreen(NavigationTarget.QUERIES);
    }

    @FXML
    private void showScripts() {
        showScreen(NavigationTarget.SCRIPTS);
    }

    @FXML
    private void showHistory() {
        showScreen(NavigationTarget.HISTORY);
    }

    @FXML
    private void showSettings() {
        showScreen(NavigationTarget.SETTINGS);
    }

    @FXML
    private void logout() throws IOException {
        AppContext.clearSession();

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/br/com/scmjf/tihelper/view/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1080, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/br/com/scmjf/tihelper/styles/app.css")).toExternalForm());

        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void showScreen(NavigationTarget target) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/br/com/scmjf/tihelper/view/" + target.getFxml()));
            Parent content = loader.load();
            contentArea.getChildren().setAll(content);
            screenTitleLabel.setText(target.getTitle());
            markActive(target);
        } catch (IOException exception) {
            AlertUtil.error("Erro de navegação", "Não foi possível carregar a tela " + target.getTitle() + ".");
        }
    }

    private void markActive(NavigationTarget target) {
        navigationButtons.values().forEach(button -> button.getStyleClass().remove("active"));
        Button activeButton = navigationButtons.get(target);
        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
