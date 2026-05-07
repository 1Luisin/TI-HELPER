package br.com.scmjf.tihelper.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppInfo;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.AppPaths;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DiagnosticsController {

    @FXML
    private Label appNameLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label environmentLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label profileLabel;
    @FXML
    private Label dataPathLabel;
    @FXML
    private Label logPathLabel;
    @FXML
    private Label osLabel;
    @FXML
    private Label javaLabel;
    @FXML
    private Label copyrightLabel;

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        appNameLabel.setText(AppInfo.NAME);
        versionLabel.setText(AppInfo.VERSION);
        environmentLabel.setText(AppInfo.ENVIRONMENT);
        userLabel.setText(user == null ? "-" : user.getUsername());
        profileLabel.setText(user == null ? "-" : user.getProfileName());
        dataPathLabel.setText(AppPaths.dataDirectory().toString());
        logPathLabel.setText(AppPaths.logsDirectory().toString());
        osLabel.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
        javaLabel.setText(System.getProperty("java.version"));
        copyrightLabel.setText("Desenvolvido pela equipe de TI Santa Casa. Todos os direitos reservados - "
                + Year.now().getValue() + ".");
    }

    @FXML
    private void openDataFolder() {
        openFolder(AppPaths.dataDirectory(), "Abrir pasta de dados");
    }

    @FXML
    private void openLogsFolder() {
        openFolder(AppPaths.logsDirectory(), "Abrir pasta de logs");
    }

    private void openFolder(Path folder, String title) {
        try {
            Files.createDirectories(folder);
            Desktop.getDesktop().open(folder.toFile());
        } catch (IOException | UnsupportedOperationException exception) {
            AppLogger.error("Erro ao abrir pasta: " + folder, exception);
            AlertUtil.error(title, "Nao foi possivel abrir a pasta.");
        }
    }
}
