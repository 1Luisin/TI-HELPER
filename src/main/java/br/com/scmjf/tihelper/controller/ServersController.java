package br.com.scmjf.tihelper.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServersController {

    private static final URI PRODUCTION_BUSY_URI = URI.create("http://172.18.2.6");

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
        TableUtil.bindColumnWidths(serversTable, new double[]{1.2, 1.6, 1.9, 1.2, 1}, nameColumn, hostColumn, osColumn, environmentColumn, statusColumn);
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

    @FXML
    private void openProductionBusy() {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            AlertUtil.warning("Busy produção", "Este ambiente não permite abrir links automaticamente.");
            return;
        }

        try {
            Desktop.getDesktop().browse(PRODUCTION_BUSY_URI);
        } catch (IOException | SecurityException exception) {
            AlertUtil.error("Busy produção", "Não foi possível abrir http://172.18.2.6.");
        }
    }
}
