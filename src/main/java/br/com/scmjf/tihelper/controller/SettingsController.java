package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SettingsController {

    private static final String APP_VERSION = "0.1.0-SNAPSHOT";

    @FXML
    private Label loggedUserLabel;

    @FXML
    private Label profileLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label environmentLabel;

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        loggedUserLabel.setText(user == null ? "-" : user.getUsername());
        profileLabel.setText(user == null ? "-" : user.getProfileName());
        versionLabel.setText(APP_VERSION);
        environmentLabel.setText("Protótipo");
    }
}
