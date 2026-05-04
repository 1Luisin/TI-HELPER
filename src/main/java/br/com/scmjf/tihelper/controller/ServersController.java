package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServersController {

    @FXML
    private TableView<ServerInfo> serversTable;

    @FXML
    private TableColumn<ServerInfo, String> nameColumn;

    @FXML
    private TableColumn<ServerInfo, String> hostColumn;

    @FXML
    private TableColumn<ServerInfo, String> osColumn;

    @FXML
    private TableColumn<ServerInfo, String> environmentColumn;

    @FXML
    private TableColumn<ServerInfo, String> statusColumn;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        osColumn.setCellValueFactory(new PropertyValueFactory<>("operatingSystem"));
        environmentColumn.setCellValueFactory(new PropertyValueFactory<>("environment"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        serversTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getServers()));
    }

    @FXML
    private void testConnection() {
        ServerInfo selected = serversTable.getSelectionModel().getSelectedItem();
        ActionResult result = AppContext.actionSimulationService().testConnection(selected);

        if (result.success()) {
            AlertUtil.info("Teste de conexão", result.message());
        } else {
            AlertUtil.warning("Teste de conexão", result.message());
        }
    }

    @FXML
    private void viewServices() {
        AppContext.navigateTo(NavigationTarget.SERVICES);
    }
}
