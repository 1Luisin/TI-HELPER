package br.com.scmjf.tihelper.controller;

import java.io.IOException;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.SceneUtil;
import br.com.scmjf.tihelper.util.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
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
    private HBox profileAnchor;

    @FXML
    private StackPane profileAvatarPane;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label profileInitialsLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button serversButton;

    @FXML
    private Button panelsButton;

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
    private Button diagnosticsButton;

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
    private Popup profilePopup;

    @FXML
    private void initialize() {
        refreshProfileHeader();

        navigationButtons.put(NavigationTarget.DASHBOARD, dashboardButton);
        navigationButtons.put(NavigationTarget.SERVERS, serversButton);
        navigationButtons.put(NavigationTarget.PANELS, panelsButton);
        navigationButtons.put(NavigationTarget.SERVICES, servicesButton);
        navigationButtons.put(NavigationTarget.QUERIES, queriesButton);
        navigationButtons.put(NavigationTarget.SCRIPTS, scriptsButton);
        navigationButtons.put(NavigationTarget.HISTORY, historyButton);
        navigationButtons.put(NavigationTarget.SETTINGS, settingsButton);
        navigationButtons.put(NavigationTarget.DIAGNOSTICS, diagnosticsButton);

        compactLabels.put(NavigationTarget.DASHBOARD, "Dash");
        compactLabels.put(NavigationTarget.SERVERS, "Serv.");
        compactLabels.put(NavigationTarget.PANELS, "Pain.");
        compactLabels.put(NavigationTarget.SERVICES, "Svc.");
        compactLabels.put(NavigationTarget.QUERIES, "SQL");
        compactLabels.put(NavigationTarget.SCRIPTS, "Run");
        compactLabels.put(NavigationTarget.HISTORY, "Hist.");
        compactLabels.put(NavigationTarget.SETTINGS, "Cfg.");
        compactLabels.put(NavigationTarget.DIAGNOSTICS, "Sobre");

        navigationButtons.forEach((target, button) -> button.setTooltip(new Tooltip(target.getTitle())));
        logoutButton.setTooltip(new Tooltip("Sair"));
        configureMenuPermissions();
        configureResponsiveShell();

        AppContext.setNavigationHandler(this::showScreen);
        showScreen(NavigationTarget.DASHBOARD);
    }

    @FXML
    private void toggleProfileMenu() {
        if (profilePopup != null && profilePopup.isShowing()) {
            profilePopup.hide();
            return;
        }

        profilePopup = createProfilePopup();
        Bounds bounds = profileAnchor.localToScreen(profileAnchor.getBoundsInLocal());
        profilePopup.show(profileAnchor, bounds.getMaxX() - 390, bounds.getMaxY() + 8);
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
    private void showPanels() {
        showScreen(NavigationTarget.PANELS);
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
    private void showDiagnostics() {
        showScreen(NavigationTarget.DIAGNOSTICS);
    }

    @FXML
    private void logout() throws IOException {
        if (profilePopup != null) {
            profilePopup.hide();
        }
        AppContext.clearSession();

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/br/com/scmjf/tihelper/view/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setScene(SceneUtil.createScene(stage, root, 1080, 720));
        stage.centerOnScreen();
    }

    private void showScreen(NavigationTarget target) {
        if (!PermissionUtil.canAccess(AppContext.getCurrentUser(), target)) {
            AppContext.denyAction(deniedActionFor(target), target.getTitle(), "Seu perfil não pode acessar " + target.getTitle() + ".");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/br/com/scmjf/tihelper/view/" + target.getFxml()));
            Parent content = loader.load();
            contentArea.getChildren().setAll(content);
            screenTitleLabel.setText(target.getTitle());
            markActive(target);
        } catch (IOException exception) {
            AppLogger.error("Erro ao carregar FXML da tela: " + target.getFxml(), exception);
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

    private Popup createProfilePopup() {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        VBox root = new VBox(12);
        root.getStyleClass().add("profile-popup");
        root.setPadding(new Insets(14));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("profile-tabs");
        tabs.getTabs().add(createProfileTab());

        User user = AppContext.getCurrentUser();
        if (PermissionUtil.canAdmin(user)) {
            tabs.getTabs().add(createUsersTab());
        }

        root.getChildren().setAll(tabs);
        popup.getContent().add(root);
        return popup;
    }

    private Tab createProfileTab() {
        User user = AppContext.getCurrentUser();
        Label avatar = new Label(user == null ? "-" : initialsFor(user.getUsername()));
        avatar.getStyleClass().add("profile-popup-avatar");

        ImageView avatarImage = new ImageView();
        avatarImage.setFitWidth(72);
        avatarImage.setFitHeight(72);
        avatarImage.setPreserveRatio(false);
        avatarImage.setClip(new Circle(36, 36, 36));
        if (user != null && user.getProfilePhotoUri() != null && !user.getProfilePhotoUri().isBlank()) {
            avatarImage.setImage(new Image(user.getProfilePhotoUri(), true));
            avatar.setGraphic(avatarImage);
            avatar.setText("");
        }

        Label username = new Label(user == null ? "-" : user.getUsername());
        username.getStyleClass().add("profile-popup-name");

        Label profile = new Label(user == null ? "-" : user.getProfileName());
        profile.getStyleClass().add("profile-popup-role");

        Button photoButton = new Button("Trocar foto");
        photoButton.getStyleClass().add("secondary-button");
        photoButton.setMaxWidth(Double.MAX_VALUE);
        photoButton.setOnAction(event -> chooseProfilePhoto());

        Button settingsButton = new Button("Abrir configurações");
        settingsButton.getStyleClass().add("secondary-button");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        UiUtil.setVisibleManaged(settingsButton, PermissionUtil.canAccess(user, NavigationTarget.SETTINGS));
        settingsButton.setOnAction(event -> {
            if (profilePopup != null) {
                profilePopup.hide();
            }
            showSettings();
        });

        Button logoutProfileButton = new Button("Sair");
        logoutProfileButton.getStyleClass().add("logout-profile-button");
        logoutProfileButton.setMaxWidth(Double.MAX_VALUE);
        logoutProfileButton.setOnAction(event -> {
            try {
                logout();
            } catch (IOException exception) {
                AlertUtil.error("Sair", "Não foi possível voltar para a tela de login.");
            }
        });

        VBox content = new VBox(10, avatar, username, profile, photoButton, settingsButton, logoutProfileButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(12));

        Tab tab = new Tab("Perfil");
        tab.setContent(content);
        return tab;
    }

    private Tab createUsersTab() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        ComboBox<UserProfile> profileCombo = new ComboBox<>(FXCollections.observableArrayList(UserProfile.values()));
        profileCombo.getSelectionModel().select(UserProfile.OPERADOR_TI);
        profileCombo.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Cadastrar usuário");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        ListView<String> usersList = new ListView<>();
        usersList.getStyleClass().add("profile-users-list");
        usersList.setPrefHeight(132);
        refreshUsersList(usersList);

        registerButton.setOnAction(event -> {
            boolean registered = AppContext.usuarioService().registerUser(
                    usernameField.getText(),
                    passwordField.getText(),
                    profileCombo.getSelectionModel().getSelectedItem());
            if (!registered) {
                AlertUtil.warning("Cadastrar usuário", "Informe usuário, senha e perfil válidos. O login também deve ser único.");
                return;
            }

            usernameField.clear();
            passwordField.clear();
            profileCombo.getSelectionModel().select(UserProfile.OPERADOR_TI);
            refreshUsersList(usersList);
            AlertUtil.info("Cadastrar usuário", "Usuário cadastrado no mock em memória.");
        });

        VBox content = new VBox(10,
                new Label("Novo usuário"),
                usernameField,
                passwordField,
                profileCombo,
                registerButton,
                new Label("Usuários cadastrados"),
                usersList);
        content.setPadding(new Insets(12));

        Tab tab = new Tab("Usuários");
        tab.setContent(content);
        return tab;
    }

    private void chooseProfilePhoto() {
        User user = AppContext.getCurrentUser();
        if (user == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar foto de perfil");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selected = chooser.showOpenDialog(profileAnchor.getScene().getWindow());
        if (selected == null) {
            return;
        }

        String photoUri = selected.toURI().toString();
        user.setProfilePhotoUri(photoUri);
        AppContext.usuarioService().updateProfilePhoto(user.getUsername(), photoUri);
        refreshProfileHeader();
        if (profilePopup != null) {
            profilePopup.hide();
        }
    }

    private void refreshProfileHeader() {
        User user = AppContext.getCurrentUser();
        if (user == null) {
            loggedUserLabel.setText("-");
            profileLabel.setText("-");
            return;
        }

        loggedUserLabel.setText(user.getUsername());
        profileLabel.setText(user.getProfileName());
        profileInitialsLabel.setText(initialsFor(user.getUsername()));

        profileImageView.setClip(new Circle(17, 17, 17));
        if (user.getProfilePhotoUri() == null || user.getProfilePhotoUri().isBlank()) {
            profileImageView.setVisible(false);
            profileImageView.setManaged(false);
            profileInitialsLabel.setVisible(true);
            profileInitialsLabel.setManaged(true);
            return;
        }

        profileImageView.setImage(new Image(user.getProfilePhotoUri(), true));
        profileImageView.setVisible(true);
        profileImageView.setManaged(true);
        profileInitialsLabel.setVisible(false);
        profileInitialsLabel.setManaged(false);
    }

    private void refreshUsersList(ListView<String> usersList) {
        usersList.setItems(FXCollections.observableArrayList(AppContext.usuarioService().getUsers().stream()
                .map(user -> user.getUsername() + "  |  " + user.getProfileName())
                .toList()));
    }

    private String initialsFor(String username) {
        if (username == null || username.isBlank()) {
            return "-";
        }

        String normalized = username.trim().toUpperCase();
        return normalized.substring(0, Math.min(2, normalized.length()));
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

    private void configureMenuPermissions() {
        User user = AppContext.getCurrentUser();
        navigationButtons.forEach((target, button) -> UiUtil.setVisibleManaged(button, PermissionUtil.canAccess(user, target)));
    }

    private TipoAcao deniedActionFor(NavigationTarget target) {
        return switch (target) {
            case SERVICES -> TipoAcao.REINICIAR_SERVICO;
            case QUERIES -> TipoAcao.EXECUTAR_QUERY;
            case SCRIPTS -> TipoAcao.EXECUTAR_SCRIPT;
            case SETTINGS -> TipoAcao.ALTERAR_USUARIO;
            case DIAGNOSTICS -> TipoAcao.LOGIN;
            default -> TipoAcao.LOGIN;
        };
    }
}
