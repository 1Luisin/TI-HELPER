package br.com.scmjf.tihelper.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.NavigationTarget;
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

public class ServersController {

    private static final String PRODUCTION_BUSY_URL = "http://172.18.3.109:4005";

    @FXML
    private TextField searchField;

    @FXML
    private Button testConnectionButton;

    @FXML
    private Button viewServicesButton;

    @FXML
    private Label feedbackLabel;

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

    private FilteredList<ServerInfo> filteredServers;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        osColumn.setCellValueFactory(new PropertyValueFactory<>("operatingSystem"));
        environmentColumn.setCellValueFactory(new PropertyValueFactory<>("environment"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(serversTable, new double[]{1.2, 1.6, 1.9, 1.2, 1}, nameColumn, hostColumn, osColumn, environmentColumn, statusColumn);
        serversTable.setPlaceholder(new Label("Nenhum servidor cadastrado."));

        filteredServers = new FilteredList<>(FXCollections.observableArrayList(AppContext.servidorService().listar()), server -> true);
        serversTable.setItems(filteredServers);
        searchField.textProperty().addListener((observable, oldValue, value) -> applyFilter(value));

        UiUtil.setVisibleManaged(testConnectionButton,
                PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.TESTAR_CONEXAO));
        UiUtil.setVisibleManaged(viewServicesButton,
                PermissionUtil.canAccess(AppContext.getCurrentUser(), NavigationTarget.SERVICES));
    }

    @FXML
    private void testConnection() {
        if (!PermissionUtil.canRunAction(AppContext.getCurrentUser(), TipoAcao.TESTAR_CONEXAO)) {
            AppContext.denyAction(TipoAcao.TESTAR_CONEXAO, "Servidores", "Seu perfil não pode testar conexão.");
            return;
        }

        ServerInfo selected = serversTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Teste de conexão", "Selecione um servidor para testar a conexão.");
            return;
        }

        feedbackLabel.setText("Testando conexão simulada...");
        testConnectionButton.setDisable(true);
        PauseTransition delay = new PauseTransition(Duration.millis(850));
        delay.setOnFinished(event -> {
            ActionResult result = AppContext.servidorService().testarConexao(selected, AppContext.getCurrentUser());
            feedbackLabel.setText(result.success() ? "Conexão simulada concluída." : "Falha simulada na conexão.");
            testConnectionButton.setDisable(false);
            if (result.success()) {
                AlertUtil.info("Teste de conexão", result.message());
            } else {
                AlertUtil.warning("Teste de conexão", result.message());
            }
        });
        delay.play();
    }

    @FXML
    private void viewServices() {
        if (!PermissionUtil.canAccess(AppContext.getCurrentUser(), NavigationTarget.SERVICES)) {
            AppContext.denyAction(TipoAcao.REINICIAR_SERVICO, "Servidores", "Seu perfil não pode acessar Serviços.");
            return;
        }
        AppContext.navigateTo(NavigationTarget.SERVICES);
    }

    @FXML
    private void openProductionBusy() {
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                throw new UnsupportedOperationException("Abertura de links não suportada pelo sistema.");
            }
            Desktop.getDesktop().browse(URI.create(PRODUCTION_BUSY_URL));
        } catch (IOException | UnsupportedOperationException | SecurityException exception) {
            AppLogger.error("Erro ao abrir o busy de produção: " + PRODUCTION_BUSY_URL, exception);
            AlertUtil.error("Busy de produção", "Não foi possível abrir o endereço no navegador padrão.");
        }
    }

    private void applyFilter(String text) {
        String filter = text == null ? "" : text.trim().toLowerCase();
        filteredServers.setPredicate(server -> filter.isBlank()
                || contains(server.getName(), filter)
                || contains(server.getHost(), filter)
                || contains(server.getOperatingSystem(), filter)
                || contains(server.getEnvironment(), filter)
                || contains(server.getStatus(), filter));
    }

    private boolean contains(String value, String filter) {
        return value != null && value.toLowerCase().contains(filter);
    }
}
