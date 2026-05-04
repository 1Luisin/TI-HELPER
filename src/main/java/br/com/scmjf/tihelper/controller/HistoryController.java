package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.util.AppContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    @FXML
    private TableView<ExecutionHistory> historyTable;

    @FXML
    private TableColumn<ExecutionHistory, String> dateColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> userColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> typeColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> serverColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> targetColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> statusColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> messageColumn;

    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        historyTable.setItems(FXCollections.observableArrayList(AppContext.historyService().getAll()));
    }
}
