package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.TableUtil;
import br.com.scmjf.tihelper.util.UiUtil;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class ServicesController {

    @FXML
    private TextField searchField;

    @FXML
    private Button restartButton;

    @FXML
    private Label feedbackLabel;

    @FXML
    private TableView<ServiceInfo> servicesTable;

    @FXML
    private TableColumn<ServiceInfo, String> serverColumn;

    @FXML
    private TableColumn<ServiceInfo, String> nameColumn;

    @FXML
    private TableColumn<ServiceInfo, String> descriptionColumn;

    @FXML
    private TableColumn<ServiceInfo, String> statusColumn;

    @FXML
    private TableColumn<ServiceInfo, String> restartAllowedColumn;

    @FXML
    private TableColumn<ServiceInfo, String> lastCheckColumn;

    private FilteredList<ServiceInfo> filteredServices;

    @FXML
    private void initialize() {
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        restartAllowedColumn.setCellValueFactory(new PropertyValueFactory<>("restartPermissionLabel"));
        lastCheckColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLastVerification"));
        TableUtil.bindColumnWidths(servicesTable, new double[]{1.1, 1.5, 2.3, 1, 1, 1.4},
                serverColumn, nameColumn, descriptionColumn, statusColumn, restartAllowedColumn, lastCheckColumn);
        servicesTable.setPlaceholder(new Label("Nenhum serviço cadastrado."));

        filteredServices = new FilteredList<>(FXCollections.observableArrayList(AppContext.servicoServidorService().listar()), service -> true);
        servicesTable.setItems(filteredServices);
        searchField.textProperty().addListener((observable, oldValue, value) -> applyFilter(value));

        UiUtil.setVisibleManaged(restartButton,
                PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO));
        servicesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> updateRestartButtonState(selected));
        updateRestartButtonState(null);
    }

    @FXML
    private void viewStatus() {
        ServiceInfo selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Status do serviço", "Selecione um serviço para consultar.");
            return;
        }

        AlertUtil.info(
                "Status do serviço",
                selected.getName() + " em " + selected.getServer() + ": " + selected.getStatus()
                        + "\nÚltima verificação: " + selected.getFormattedLastVerification()
                        + "\nReinício: " + selected.getRestartPermissionLabel());
    }

    @FXML
    private void restartService() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO)) {
            AppContext.denyAction(TipoAcao.REINICIAR_SERVICO, "Serviços", "Seu perfil não pode reiniciar serviços.");
            return;
        }

        ServiceInfo selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Reiniciar serviço", "Selecione um serviço para reiniciar.");
            return;
        }
        if (!selected.isRestartAllowed()) {
            AppContext.historicoService().registrar(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO,
                    selected.getServer(), selected.getName(), StatusExecucao.NEGADO, "-",
                    "Serviço configurado para não permitir reinício.");
            AlertUtil.warning("Reiniciar serviço", "Este serviço está configurado para não permitir reinício.");
            return;
        }

        AlertUtil.askReason("Reiniciar serviço", "Confirme o reinício de " + selected.getName())
                .ifPresentOrElse(reason -> startRestartSimulation(selected, reason),
                        () -> AppContext.historicoService().registrar(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO,
                                selected.getServer(), selected.getName(), StatusExecucao.CANCELADO, "-",
                                "Reinício cancelado pelo usuário."));
    }

    private void startRestartSimulation(ServiceInfo selected, String reason) {
        selected.setStatus("Reiniciando");
        servicesTable.refresh();
        feedbackLabel.setText("Reiniciando serviço de forma simulada...");
        restartButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.millis(1200));
        delay.setOnFinished(event -> {
            ActionResult result = AppContext.servicoServidorService()
                    .finalizarReinicio(selected, reason, AppContext.getCurrentUser());
            servicesTable.refresh();
            feedbackLabel.setText("Serviço simulado em execução.");
            updateRestartButtonState(servicesTable.getSelectionModel().getSelectedItem());
            AlertUtil.info("Reiniciar serviço", result.message());
        });
        delay.play();
    }

    private void applyFilter(String text) {
        String filter = text == null ? "" : text.trim().toLowerCase();
        filteredServices.setPredicate(service -> filter.isBlank()
                || contains(service.getServer(), filter)
                || contains(service.getName(), filter)
                || contains(service.getDescription(), filter)
                || contains(service.getStatus(), filter)
                || contains(service.getRestartPermissionLabel(), filter)
                || contains(service.getFormattedLastVerification(), filter));
    }

    private void updateRestartButtonState(ServiceInfo selected) {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.REINICIAR_SERVICO)) {
            restartButton.setDisable(true);
            return;
        }
        restartButton.setDisable(selected == null || !selected.isRestartAllowed());
    }

    private boolean contains(String value, String filter) {
        return value != null && value.toLowerCase().contains(filter);
    }
}
