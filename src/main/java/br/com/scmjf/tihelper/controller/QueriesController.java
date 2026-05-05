package br.com.scmjf.tihelper.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.scmjf.tihelper.model.QueryExecutionResult;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.PermissionUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class QueriesController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> queryCombo;

    @FXML
    private Button executeButton;

    @FXML
    private Label feedbackLabel;

    @FXML
    private GridPane parametersPane;

    @FXML
    private TableView<Map<String, String>> resultTable;

    private final Map<String, TextField> parameterFields = new LinkedHashMap<>();
    private List<String> allQueries;

    @FXML
    private void initialize() {
        allQueries = AppContext.queryService().listarNomes();
        queryCombo.setItems(FXCollections.observableArrayList(allQueries));
        queryCombo.getSelectionModel().selectFirst();
        resultTable.setPlaceholder(new Label("Execute uma query para visualizar o resultado simulado."));
        searchField.textProperty().addListener((observable, oldValue, value) -> filterQueries(value));
        executeButton.setDisable(!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_QUERY));
        renderParameters();
    }

    @FXML
    private void renderParameters() {
        parametersPane.getChildren().clear();
        parameterFields.clear();

        String selectedQuery = queryCombo.getSelectionModel().getSelectedItem();
        List<String> fields = AppContext.queryService().listarParametros(selectedQuery);
        for (int index = 0; index < fields.size(); index++) {
            String fieldName = fields.get(index);
            Label label = new Label(fieldName);
            label.getStyleClass().add("field-label");

            TextField field = new TextField();
            field.setPromptText(promptFor(fieldName));
            field.setMaxWidth(Double.MAX_VALUE);

            parameterFields.put(fieldName, field);
            parametersPane.add(label, 0, index);
            parametersPane.add(field, 1, index);
        }
    }

    @FXML
    private void executeQuery() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_QUERY)) {
            AppContext.denyAction(TipoAcao.EXECUTAR_QUERY, "Queries", "Seu perfil não pode executar queries.");
            return;
        }

        String selectedQuery = queryCombo.getSelectionModel().getSelectedItem();
        if (selectedQuery == null) {
            AlertUtil.warning("Executar query", "Selecione uma consulta.");
            return;
        }

        Map<String, String> parameters = new LinkedHashMap<>();
        parameterFields.forEach((name, field) -> parameters.put(name, field.getText().trim()));

        QueryExecutionResult result = AppContext.queryService()
                .executar(selectedQuery, parameters, AppContext.getCurrentUser());
        renderResultTable(result);
        feedbackLabel.setText("Consulta simulada executada e registrada.");
        AlertUtil.info("Executar query", "Consulta simulada executada e registrada no histórico.");
    }

    private void filterQueries(String text) {
        String filter = text == null ? "" : text.trim().toLowerCase();
        List<String> filtered = allQueries.stream()
                .filter(query -> filter.isBlank() || query.toLowerCase().contains(filter))
                .toList();
        queryCombo.setItems(FXCollections.observableArrayList(filtered));
        queryCombo.getSelectionModel().selectFirst();
        renderParameters();
    }

    private void renderResultTable(QueryExecutionResult result) {
        resultTable.getColumns().clear();
        for (String columnName : result.columns()) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getOrDefault(columnName, "")));
            column.setMinWidth(120);
            column.prefWidthProperty().bind(resultTable.widthProperty()
                    .subtract(20)
                    .divide(Math.max(1, result.columns().size())));
            resultTable.getColumns().add(column);
        }
        resultTable.setItems(FXCollections.observableArrayList(result.rows()));
    }

    private String promptFor(String fieldName) {
        return switch (fieldName) {
            case "CPF" -> "000.000.000-00";
            case "Número" -> "A-2026-0001";
            case "Data inicial" -> "01/05/2026";
            case "Data final" -> "04/05/2026";
            case "Integração" -> "Pesquisa Tablet";
            default -> "Valor";
        };
    }
}
