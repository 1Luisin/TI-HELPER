package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServicesController {

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
    private TableColumn<ServiceInfo, String> lastCheckColumn;

    @FXML
    private void initialize() {
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lastCheckColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLastVerification"));
        TableUtil.bindColumnWidths(servicesTable, new double[]{1.1, 1.5, 2.5, 1, 1.4}, serverColumn, nameColumn, descriptionColumn, statusColumn, lastCheckColumn);
        servicesTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getServices()));
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
                        + "\nÚltima verificação: " + selected.getFormattedLastVerification());
    }

    @FXML
    private void restartService() {
        ServiceInfo selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Reiniciar serviço", "Selecione um serviço para reiniciar.");
            return;
        }

        AlertUtil.askReason("Reiniciar serviço", "Confirme o reinício de " + selected.getName())
                .ifPresentOrElse(reason -> {
                    ActionResult result = AppContext.actionSimulationService()
                            .restartService(selected, reason, AppContext.getCurrentUser());
                    servicesTable.refresh();
                    AlertUtil.info("Reiniciar serviço", result.message());
                }, () -> AlertUtil.warning("Reiniciar serviço", "Informe um motivo para registrar a ação."));
    }
}
