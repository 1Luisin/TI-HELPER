package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ScriptsController {

    @FXML
    private ComboBox<String> scriptCombo;

    @FXML
    private TextField serverTargetField;

    @FXML
    private TextArea reasonArea;

    @FXML
    private void initialize() {
        scriptCombo.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getScripts()));
        scriptCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void executeScript() {
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

        ActionResult result = AppContext.actionSimulationService()
                .executeScript(script, server, reason, AppContext.getCurrentUser());
        AlertUtil.info("Executar script", result.message());
        reasonArea.clear();
    }
}
