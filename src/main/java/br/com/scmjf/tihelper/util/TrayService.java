package br.com.scmjf.tihelper.util;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.imageio.ImageIO;

import br.com.scmjf.tihelper.model.CloseBehavior;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class TrayService {

    private static final String APP_TITLE = "TI Helper - SCMJF";
    private static final String TRAY_ICON = "/br/com/scmjf/tihelper/assets/TIHELPER.png";
    private static final String STYLESHEET = "/br/com/scmjf/tihelper/styles/app.css";

    private static TrayIcon trayIcon;
    private static Stage trayMenuStage;
    private static boolean notificationShown;

    private TrayService() {
    }

    public static boolean install(Stage stage) {
        if (!SystemTray.isSupported()) {
            return false;
        }

        if (trayIcon != null) {
            return true;
        }

        try {
            trayIcon = createTrayIcon(stage);
            SystemTray.getSystemTray().add(trayIcon);
            return true;
        } catch (AWTException | IOException exception) {
            AppLogger.error("Nao foi possivel instalar icone na bandeja.", exception);
            trayIcon = null;
            return false;
        }
    }

    public static void requestClose(Stage stage) {
        CloseBehavior behavior = AppContext.getCloseBehavior();
        if (behavior == CloseBehavior.ASK) {
            AlertUtil.askCloseBehavior().ifPresent(choice -> {
                AppContext.saveCloseBehavior(choice);
                applyCloseBehavior(stage, choice);
            });
            return;
        }

        applyCloseBehavior(stage, behavior);
    }

    public static void hideToTray(Stage stage) {
        hideTrayMenu();
        stage.hide();
        if (!notificationShown && trayIcon != null) {
            trayIcon.displayMessage(
                    APP_TITLE,
                    "O aplicativo continua rodando em segundo plano.",
                    TrayIcon.MessageType.INFO);
            notificationShown = true;
        }
    }

    public static void shutdown() {
        hideTrayMenu();
        if (trayIcon == null || !SystemTray.isSupported()) {
            return;
        }

        SystemTray.getSystemTray().remove(trayIcon);
        trayIcon = null;
    }

    private static TrayIcon createTrayIcon(Stage stage) throws IOException {
        TrayIcon icon = new TrayIcon(loadTrayImage(), APP_TITLE);
        icon.setImageAutoSize(true);
        icon.addActionListener(event -> Platform.runLater(() -> showStage(stage)));
        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                if (event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1) {
                    Platform.runLater(() -> showStage(stage));
                    return;
                }

                if (event.isPopupTrigger() || event.getButton() == MouseEvent.BUTTON1 || event.getButton() == MouseEvent.BUTTON3) {
                    Platform.runLater(() -> showTrayMenu(stage, event.getXOnScreen(), event.getYOnScreen()));
                }
            }
        });
        return icon;
    }

    private static java.awt.Image loadTrayImage() throws IOException {
        BufferedImage image = ImageIO.read(Objects.requireNonNull(TrayService.class.getResourceAsStream(TRAY_ICON)));
        return image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
    }

    private static void showTrayMenu(Stage owner, double screenX, double screenY) {
        hideTrayMenu();

        trayMenuStage = new Stage(StageStyle.TRANSPARENT);
        trayMenuStage.setAlwaysOnTop(true);
        trayMenuStage.setResizable(false);
        trayMenuStage.setTitle(APP_TITLE);

        VBox root = createTrayMenuContent(owner);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(TrayService.class.getResource(STYLESHEET)).toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                hideTrayMenu();
            }
        });

        trayMenuStage.setScene(scene);
        trayMenuStage.focusedProperty().addListener((observable, oldValue, focused) -> {
            if (!focused) {
                hideTrayMenu();
            }
        });
        trayMenuStage.setOnShown(event -> positionTrayMenu(screenX, screenY));
        trayMenuStage.show();
        trayMenuStage.requestFocus();
    }

    private static VBox createTrayMenuContent(Stage owner) {
        CloseBehavior behavior = AppContext.getCloseBehavior();

        Label logo = new Label("TI");
        logo.getStyleClass().add("tray-popup-logo");

        Label title = new Label(APP_TITLE);
        title.getStyleClass().add("tray-popup-title");

        Label status = new Label(owner.isShowing() ? "Janela aberta" : "Rodando em segundo plano");
        status.getStyleClass().add("tray-popup-status");

        VBox titleBox = new VBox(2, title, status);
        HBox header = new HBox(10, logo, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);

        Button openButton = trayButton("Abrir janela", () -> {
            hideTrayMenu();
            showStage(owner);
        });
        Button hideButton = trayButton("Ocultar em segundo plano", () -> hideToTray(owner));
        Button closeButton = trayButton("Fechar / sair", () -> requestClose(owner));

        Label preferenceLabel = new Label("Comportamento ao fechar/sair");
        preferenceLabel.getStyleClass().add("tray-popup-section");

        Button askButton = preferenceButton(CloseBehavior.ASK, behavior);
        Button minimizePreferenceButton = preferenceButton(CloseBehavior.MINIMIZE_TO_TRAY, behavior);
        Button exitPreferenceButton = preferenceButton(CloseBehavior.EXIT_APPLICATION, behavior);

        Button dataButton = trayButton("Abrir pasta de dados", () -> openFolder(AppPaths.dataDirectory()));
        Button logsButton = trayButton("Abrir pasta de logs", () -> openFolder(AppPaths.logsDirectory()));
        Button aboutButton = trayButton("Sobre", TrayService::showAbout);
        Button exitButton = trayDangerButton("Sair completamente", TrayService::exitApplication);

        VBox root = new VBox(10,
                header,
                separator(),
                openButton,
                hideButton,
                closeButton,
                separator(),
                preferenceLabel,
                askButton,
                minimizePreferenceButton,
                exitPreferenceButton,
                separator(),
                dataButton,
                logsButton,
                aboutButton,
                exitButton);
        root.getStyleClass().add("tray-popup");
        root.setPadding(new Insets(14));
        return root;
    }

    private static Button preferenceButton(CloseBehavior behavior, CloseBehavior selected) {
        Button button = trayButton(behavior.getDisplayName(), () -> {
            AppContext.saveCloseBehavior(behavior);
            hideTrayMenu();
            AlertUtil.info("Preferencia salva", "Ao fechar/sair: " + behavior.getDisplayName() + ".");
        });
        if (behavior == selected) {
            button.getStyleClass().add("tray-popup-option-selected");
        }
        return button;
    }

    private static Button trayButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("tray-popup-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private static Button trayDangerButton(String text, Runnable action) {
        Button button = trayButton(text, action);
        button.getStyleClass().add("tray-popup-danger-button");
        return button;
    }

    private static Region separator() {
        Region separator = new Region();
        separator.getStyleClass().add("tray-popup-separator");
        separator.setMinHeight(1);
        separator.setPrefHeight(1);
        separator.setMaxHeight(1);
        return separator;
    }

    private static void applyCloseBehavior(Stage stage, CloseBehavior behavior) {
        if (behavior == CloseBehavior.MINIMIZE_TO_TRAY) {
            hideToTray(stage);
            return;
        }

        if (behavior == CloseBehavior.EXIT_APPLICATION) {
            exitApplication();
        }
    }

    private static void exitApplication() {
        hideTrayMenu();
        shutdown();
        Platform.setImplicitExit(true);
        Platform.exit();
    }

    private static void showStage(Stage stage) {
        hideTrayMenu();
        if (stage.isIconified()) {
            stage.setIconified(false);
        }
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }

    private static void hideTrayMenu() {
        if (trayMenuStage != null) {
            trayMenuStage.hide();
            trayMenuStage = null;
        }
    }

    private static void positionTrayMenu(double screenX, double screenY) {
        if (trayMenuStage == null) {
            return;
        }

        Rectangle2D bounds = Screen.getScreensForRectangle(screenX, screenY, 1, 1).stream()
                .findFirst()
                .orElse(Screen.getPrimary())
                .getVisualBounds();
        double width = trayMenuStage.getWidth();
        double height = trayMenuStage.getHeight();
        double x = Math.min(Math.max(bounds.getMinX() + 8, screenX - width + 12), bounds.getMaxX() - width - 8);
        double y = Math.min(Math.max(bounds.getMinY() + 8, screenY - height - 12), bounds.getMaxY() - height - 8);
        trayMenuStage.setX(x);
        trayMenuStage.setY(y);
    }

    private static void openFolder(Path folder) {
        hideTrayMenu();
        try {
            Files.createDirectories(folder);
            Desktop.getDesktop().open(folder.toFile());
        } catch (IOException | RuntimeException exception) {
            AppLogger.error("Nao foi possivel abrir pasta: " + folder, exception);
            AlertUtil.warning("Abrir pasta", "Nao foi possivel abrir a pasta local.");
        }
    }

    private static void showAbout() {
        hideTrayMenu();
        AlertUtil.info("Sobre",
                AppInfo.NAME + "\n"
                        + "Versao: " + AppInfo.VERSION + "\n"
                        + "Ambiente: " + AppInfo.ENVIRONMENT + "\n"
                        + "Dados: " + AppPaths.dataDirectory() + "\n"
                        + "Logs: " + AppPaths.logFile());
    }
}
