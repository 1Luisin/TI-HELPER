package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    private static final String ALL = "Todos";

    @FXML
    private ComboBox<String> typeFilterCombo;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private TableView<ExecutionHistory> historyTable;

    @FXML
    private TableColumn<ExecutionHistory, String> idColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> dateColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> userColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> profileColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> typeColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> serverColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> targetColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> statusColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> reasonColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> messageColumn;

    private FilteredList<ExecutionHistory> filteredHistory;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        profileColumn.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableUtil.bindColumnWidths(historyTable, new double[]{0.6, 1.4, 0.9, 1, 1.25, 1.1, 1.6, 0.9, 1.5, 2.4},
                idColumn, dateColumn, userColumn, profileColumn, typeColumn, serverColumn, targetColumn, statusColumn, reasonColumn, messageColumn);
        historyTable.setPlaceholder(new Label("Nenhum registro de histórico."));

        typeFilterCombo.setItems(FXCollections.observableArrayList(buildTypeOptions()));
        statusFilterCombo.setItems(FXCollections.observableArrayList(buildStatusOptions()));
        typeFilterCombo.getSelectionModel().select(ALL);
        statusFilterCombo.getSelectionModel().select(ALL);
        typeFilterCombo.setOnAction(event -> applyFilters());
        statusFilterCombo.setOnAction(event -> applyFilters());

        filteredHistory = new FilteredList<>(FXCollections.observableArrayList(AppContext.historicoService().listarTodos()), record -> true);
        historyTable.setItems(filteredHistory);
    }

    private void applyFilters() {
        String type = typeFilterCombo.getSelectionModel().getSelectedItem();
        String status = statusFilterCombo.getSelectionModel().getSelectedItem();
        filteredHistory.setPredicate(record -> (ALL.equals(type) || record.getActionType().equals(type))
                && (ALL.equals(status) || record.getStatus().equals(status)));
    }

    private java.util.List<String> buildTypeOptions() {
        java.util.List<String> values = new java.util.ArrayList<>();
        values.add(ALL);
        for (TipoAcao tipo : TipoAcao.values()) {
            values.add(tipo.getDisplayName());
        }
        return values;
    }

    private java.util.List<String> buildStatusOptions() {
        java.util.List<String> values = new java.util.ArrayList<>();
        values.add(ALL);
        for (StatusExecucao status : StatusExecucao.values()) {
            values.add(status.getDisplayName());
        }
        return values;
    }
}
