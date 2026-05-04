package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML
    private Label serversCountLabel;

    @FXML
    private Label servicesCountLabel;

    @FXML
    private Label queriesCountLabel;

    @FXML
    private Label scriptsCountLabel;

    @FXML
    private Label executionsTodayLabel;

    @FXML
    private Label failuresTodayLabel;

    @FXML
    private TableView<ExecutionHistory> latestTable;

    @FXML
    private TableColumn<ExecutionHistory, String> dateColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> userColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> typeColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> targetColumn;

    @FXML
    private TableColumn<ExecutionHistory, String> statusColumn;

    @FXML
    private void initialize() {
        serversCountLabel.setText(String.valueOf(AppContext.mockDataService().getServers().size()));
        servicesCountLabel.setText(String.valueOf(AppContext.mockDataService().getServices().size()));
        queriesCountLabel.setText(String.valueOf(AppContext.mockDataService().getQueries().size()));
        scriptsCountLabel.setText(String.valueOf(AppContext.mockDataService().getScripts().size()));
        executionsTodayLabel.setText(String.valueOf(AppContext.historyService().countToday()));
        failuresTodayLabel.setText(String.valueOf(AppContext.historyService().countFailuresToday()));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        latestTable.setItems(FXCollections.observableArrayList(AppContext.historyService().getLatest(8)));
    }

    @FXML
    private void quickRestartService() {
        AppContext.navigateTo(NavigationTarget.SERVICES);
    }

    @FXML
    private void quickExecuteQuery() {
        AppContext.navigateTo(NavigationTarget.QUERIES);
    }

    @FXML
    private void quickInstallVnc() {
        AppContext.navigateTo(NavigationTarget.SCRIPTS);
    }

    @FXML
    private void quickViewHistory() {
        AppContext.navigateTo(NavigationTarget.HISTORY);
    }
}
