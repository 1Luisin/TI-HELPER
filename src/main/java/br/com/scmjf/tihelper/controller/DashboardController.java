package br.com.scmjf.tihelper.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.TableUtil;
import br.com.scmjf.tihelper.util.UiUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button quickRestartButton;

    @FXML
    private Button quickQueryButton;

    @FXML
    private Button quickScriptButton;

    @FXML
    private Button quickHistoryButton;

    @FXML
    private Label latestHistoryTitle;

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
        boolean canViewHistory = PermissionUtil.canAccess(AppContext.getCurrentUser(), NavigationTarget.HISTORY);
        serversCountLabel.setText(String.valueOf(AppContext.servidorService().listar().size()));
        servicesCountLabel.setText(String.valueOf(AppContext.servicoServidorService().listar().size()));
        queriesCountLabel.setText(String.valueOf(AppContext.queryService().listarNomes().size()));
        scriptsCountLabel.setText(String.valueOf(AppContext.scriptService().listarNomes().size()));
        executionsTodayLabel.setText(canViewHistory ? String.valueOf(AppContext.historicoService().contarHoje()) : "0");
        failuresTodayLabel.setText(canViewHistory ? String.valueOf(AppContext.historicoService().contarFalhasHoje()) : "0");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(latestTable, new double[]{1.5, 1, 1.4, 2, 1}, dateColumn, userColumn, typeColumn, targetColumn, statusColumn);
        latestTable.setPlaceholder(new Label("Nenhuma execução registrada."));
        latestTable.setItems(FXCollections.observableArrayList(canViewHistory ? AppContext.historicoService().listarUltimos(8) : List.of()));
        configureActionPermissions();
        configureHistoryVisibility(canViewHistory);
        configureResponsiveMetrics();
    }

    @FXML
    private void quickRestartService() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO)) {
            AppContext.denyAction(TipoAcao.REINICIAR_SERVICO, "Dashboard", "Seu perfil não pode reiniciar serviços.");
            return;
        }
        AppContext.navigateTo(NavigationTarget.SERVICES);
    }

    @FXML
    private void quickExecuteQuery() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_QUERY)) {
            AppContext.denyAction(TipoAcao.EXECUTAR_QUERY, "Dashboard", "Seu perfil não pode executar queries.");
            return;
        }
        AppContext.navigateTo(NavigationTarget.QUERIES);
    }

    @FXML
    private void quickInstallVnc() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_SCRIPT)) {
            AppContext.denyAction(TipoAcao.EXECUTAR_SCRIPT, "Dashboard", "Seu perfil não pode executar scripts.");
            return;
        }
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

    private void configureActionPermissions() {
        UiUtil.setVisibleManaged(quickRestartButton,
                PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO));
        UiUtil.setVisibleManaged(quickQueryButton,
                PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_QUERY));
        UiUtil.setVisibleManaged(quickScriptButton,
                PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.EXECUTAR_SCRIPT));
    }

    private void configureHistoryVisibility(boolean canViewHistory) {
        UiUtil.setVisibleManaged(executionsMetricCard, canViewHistory);
        UiUtil.setVisibleManaged(failuresMetricCard, canViewHistory);
        UiUtil.setVisibleManaged(quickHistoryButton, canViewHistory);
        UiUtil.setVisibleManaged(latestHistoryTitle, canViewHistory);
        UiUtil.setVisibleManaged(latestTable, canViewHistory);
    }

    private void layoutMetrics(List<VBox> cards, double width) {
        List<VBox> visibleCards = new ArrayList<>(cards.stream()
                .filter(VBox::isManaged)
                .toList());
        int columns = width >= 860 ? 3 : width >= 560 ? 2 : 1;

        metricsGrid.getChildren().clear();
        metricsGrid.getColumnConstraints().clear();
        for (int index = 0; index < columns; index++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100.0 / columns);
            constraints.setHgrow(Priority.ALWAYS);
            metricsGrid.getColumnConstraints().add(constraints);
        }

        for (int index = 0; index < visibleCards.size(); index++) {
            metricsGrid.add(visibleCards.get(index), index % columns, index / columns);
        }
    }
}
