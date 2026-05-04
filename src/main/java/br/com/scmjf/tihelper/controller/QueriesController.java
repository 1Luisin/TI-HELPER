package br.com.scmjf.tihelper.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.scmjf.tihelper.model.QueryExecutionResult;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class QueriesController {

    @FXML
    private ComboBox<String> queryCombo;

    @FXML
    private GridPane parametersPane;

    @FXML
    private TableView<Map<String, String>> resultTable;

    private final Map<String, TextField> parameterFields = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        queryCombo.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getQueries()));
        queryCombo.getSelectionModel().selectFirst();
        renderParameters();
    }

    @FXML
    private void renderParameters() {
        parametersPane.getChildren().clear();
        parameterFields.clear();

        String selectedQuery = queryCombo.getSelectionModel().getSelectedItem();
        List<String> fields = fieldsFor(selectedQuery);
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
        String selectedQuery = queryCombo.getSelectionModel().getSelectedItem();
        if (selectedQuery == null) {
            AlertUtil.warning("Executar query", "Selecione uma consulta.");
            return;
        }

        Map<String, String> parameters = new LinkedHashMap<>();
        parameterFields.forEach((name, field) -> parameters.put(name, field.getText().trim()));

        QueryExecutionResult result = AppContext.actionSimulationService()
                .executeQuery(selectedQuery, parameters, AppContext.getCurrentUser());
        renderResultTable(result);
        AlertUtil.info("Executar query", "Consulta simulada executada e registrada no histórico.");
    }

    private void renderResultTable(QueryExecutionResult result) {
        resultTable.getColumns().clear();
        for (String columnName : result.columns()) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getOrDefault(columnName, "")));
            column.setPrefWidth(180);
            resultTable.getColumns().add(column);
        }
        resultTable.setItems(FXCollections.observableArrayList(result.rows()));
    }

    private List<String> fieldsFor(String selectedQuery) {
        return switch (selectedQuery) {
            case "Buscar paciente por CPF" -> List.of("CPF");
            case "Buscar atendimento por número" -> List.of("Número");
            case "Consultar respostas de pesquisa" -> List.of("Data inicial", "Data final");
            case "Verificar integração pendente" -> List.of("Integração");
            default -> List.of();
        };
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
