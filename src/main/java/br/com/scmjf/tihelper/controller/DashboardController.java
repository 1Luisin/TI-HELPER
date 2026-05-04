package br.com.scmjf.tihelper.controller;

import java.util.List;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private GridPane metricsGrid;

    @FXML
    private VBox serversMetricCard;

    @FXML
    private VBox servicesMetricCard;

    @FXML
    private VBox queriesMetricCard;

    @FXML
    private VBox scriptsMetricCard;

    @FXML
    private VBox executionsMetricCard;

    @FXML
    private VBox failuresMetricCard;

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
        TableUtil.bindColumnWidths(latestTable, new double[]{1.5, 1, 1.4, 2, 1}, dateColumn, userColumn, typeColumn, targetColumn, statusColumn);
        latestTable.setItems(FXCollections.observableArrayList(AppContext.historyService().getLatest(8)));
        configureResponsiveMetrics();
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

    private void configureResponsiveMetrics() {
        List<VBox> cards = List.of(
                serversMetricCard,
                servicesMetricCard,
                queriesMetricCard,
                scriptsMetricCard,
                executionsMetricCard,
                failuresMetricCard);

        cards.forEach(card -> {
            card.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(card, Priority.ALWAYS);
        });

        metricsGrid.widthProperty().addListener((observable, oldWidth, newWidth) ->
                layoutMetrics(cards, newWidth.doubleValue()));
        layoutMetrics(cards, 900);
    }

    private void layoutMetrics(List<VBox> cards, double width) {
        int columns = width >= 860 ? 3 : width >= 560 ? 2 : 1;

        metricsGrid.getChildren().clear();
        metricsGrid.getColumnConstraints().clear();
        for (int index = 0; index < columns; index++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100.0 / columns);
            constraints.setHgrow(Priority.ALWAYS);
            metricsGrid.getColumnConstraints().add(constraints);
        }

        for (int index = 0; index < cards.size(); index++) {
            metricsGrid.add(cards.get(index), index % columns, index / columns);
        }
    }
}
