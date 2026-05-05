package br.com.scmjf.tihelper.controller;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.PermissionUtil;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class ScriptsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> scriptCombo;

    @FXML
    private TextField serverTargetField;

    @FXML
    private TextArea reasonArea;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button executeButton;

    private List<String> allScripts;

    @FXML
    private void initialize() {
        allScripts = AppContext.scriptService().listarNomes();
        scriptCombo.setItems(FXCollections.observableArrayList(allScripts));
        scriptCombo.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((observable, oldValue, value) -> filterScripts(value));
        executeButton.setDisable(!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_SCRIPT));
    }

    @FXML
    private void executeScript() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_SCRIPT)) {
            AppContext.denyAction(TipoAcao.EXECUTAR_SCRIPT, "Scripts", "Seu perfil não pode executar scripts.");
            return;
        }

        String script = scriptCombo.getSelectionModel().getSelectedItem();
        String server = serverTargetField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (script == null) {
            AlertUtil.warning("Executar script", "Selecione um script aprovado.");
            return;
        }
        if (server.isBlank()) {
            AlertUtil.warning("Executar script", "Informe o servidor destino.");
            return;
        }
        if (reason.isBlank()) {
            AlertUtil.warning("Executar script", "Informe o motivo da execução.");
            return;
        }

        feedbackLabel.setText("Executando script de forma simulada...");
        executeButton.setDisable(true);
        PauseTransition delay = new PauseTransition(Duration.millis(1000));
        delay.setOnFinished(event -> {
            ActionResult result = AppContext.scriptService()
                    .executar(script, server, reason, AppContext.getCurrentUser());
            feedbackLabel.setText("Simulado com sucesso.");
            executeButton.setDisable(false);
            reasonArea.clear();
            AlertUtil.info("Executar script", result.message());
        });
        delay.play();
    }

    private void filterScripts(String text) {
        String filter = text == null ? "" : text.trim().toLowerCase();
        List<String> filtered = allScripts.stream()
                .filter(script -> filter.isBlank() || script.toLowerCase().contains(filter))
                .toList();
        scriptCombo.setItems(FXCollections.observableArrayList(filtered));
        scriptCombo.getSelectionModel().selectFirst();
    }
}
