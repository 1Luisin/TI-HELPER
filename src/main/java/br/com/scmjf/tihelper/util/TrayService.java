package br.com.scmjf.tihelper.util;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.stage.Stage;

public final class TrayService {

    private static final String APP_TITLE = "TI Helper - SCMJF";
    private static final String TRAY_ICON = "/br/com/scmjf/tihelper/assets/TIHELPER.png";

    private static TrayIcon trayIcon;
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
            trayIcon = null;
            return false;
        }
    }

    public static void hideToTray(Stage stage) {
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
        if (trayIcon == null || !SystemTray.isSupported()) {
            return;
        }

        SystemTray.getSystemTray().remove(trayIcon);
        trayIcon = null;
    }

    private static TrayIcon createTrayIcon(Stage stage) throws IOException {
        PopupMenu menu = new PopupMenu();

        MenuItem openItem = new MenuItem("Abrir");
        openItem.addActionListener(event -> Platform.runLater(() -> showStage(stage)));

        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(event -> Platform.runLater(() -> {
            shutdown();
            Platform.setImplicitExit(true);
            Platform.exit();
        }));

        menu.add(openItem);
        menu.addSeparator();
        menu.add(exitItem);

        TrayIcon icon = new TrayIcon(loadTrayImage(), APP_TITLE, menu);
        icon.setImageAutoSize(true);
        icon.addActionListener(event -> Platform.runLater(() -> showStage(stage)));
        return icon;
    }

    private static java.awt.Image loadTrayImage() throws IOException {
        BufferedImage image = ImageIO.read(Objects.requireNonNull(TrayService.class.getResourceAsStream(TRAY_ICON)));
        return image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
    }

    private static void showStage(Stage stage) {
        if (stage.isIconified()) {
            stage.setIconified(false);
        }
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }
}
