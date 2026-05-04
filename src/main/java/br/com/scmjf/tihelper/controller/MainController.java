package br.com.scmjf.tihelper.controller;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.SceneUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    private static final double COMPACT_BREAKPOINT = 1040;
    private static final double SIDEBAR_WIDTH = 238;
    private static final double COMPACT_SIDEBAR_WIDTH = 94;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label screenTitleLabel;

    @FXML
    private Label screenSubtitleLabel;

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
    private Button logoutButton;

    @FXML
    private Label sidebarCaption;

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea;

    private final Map<NavigationTarget, Button> navigationButtons = new EnumMap<>(NavigationTarget.class);
    private final Map<NavigationTarget, String> compactLabels = new EnumMap<>(NavigationTarget.class);

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

        compactLabels.put(NavigationTarget.DASHBOARD, "Dash");
        compactLabels.put(NavigationTarget.SERVERS, "Serv.");
        compactLabels.put(NavigationTarget.SERVICES, "Svc.");
        compactLabels.put(NavigationTarget.QUERIES, "SQL");
        compactLabels.put(NavigationTarget.SCRIPTS, "Run");
        compactLabels.put(NavigationTarget.HISTORY, "Hist.");
        compactLabels.put(NavigationTarget.SETTINGS, "Cfg.");

        navigationButtons.forEach((target, button) -> button.setTooltip(new Tooltip(target.getTitle())));
        logoutButton.setTooltip(new Tooltip("Sair"));
        configureResponsiveShell();

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

        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setScene(SceneUtil.createScene(stage, root, 1080, 720));
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

    private void configureResponsiveShell() {
        contentArea.sceneProperty().addListener((observable, oldScene, scene) -> {
            if (scene == null) {
                return;
            }

            updateResponsiveShell(scene.getWidth());
            scene.widthProperty().addListener((widthObservable, oldWidth, newWidth) ->
                    updateResponsiveShell(newWidth.doubleValue()));
        });
    }

    private void updateResponsiveShell(double width) {
        boolean compact = width < COMPACT_BREAKPOINT;
        toggleStyleClass(rootPane, "compact-shell", compact);

        double targetWidth = compact ? COMPACT_SIDEBAR_WIDTH : SIDEBAR_WIDTH;
        sidebar.setPrefWidth(targetWidth);
        sidebar.setMinWidth(targetWidth);
        sidebar.setMaxWidth(targetWidth);

        sidebarCaption.setVisible(!compact);
        sidebarCaption.setManaged(!compact);
        screenSubtitleLabel.setVisible(!compact);
        screenSubtitleLabel.setManaged(!compact);

        navigationButtons.forEach((target, button) ->
                button.setText(compact ? compactLabels.get(target) : target.getTitle()));
    }

    private void toggleStyleClass(Node node, String styleClass, boolean enabled) {
        if (enabled && !node.getStyleClass().contains(styleClass)) {
            node.getStyleClass().add(styleClass);
        } else if (!enabled) {
            node.getStyleClass().remove(styleClass);
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
