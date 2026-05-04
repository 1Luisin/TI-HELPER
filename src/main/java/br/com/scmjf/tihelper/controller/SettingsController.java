package br.com.scmjf.tihelper.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class SettingsController {

    private static final String APP_VERSION = "0.1.0-SNAPSHOT";

    @FXML
    private Label loggedUserLabel;

    @FXML
    private Label profileLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label environmentLabel;

    @FXML
    private VBox adminPanel;

    @FXML
    private Label adminAccessLabel;

    @FXML
    private TextField serverNameField;

    @FXML
    private TextField serverHostField;

    @FXML
    private TextField serverOsField;

    @FXML
    private TextField serverEnvironmentField;

    @FXML
    private TextField serverStatusField;

    @FXML
    private TableView<ServerInfo> serversTable;

    @FXML
    private TableColumn<ServerInfo, String> serverNameColumn;

    @FXML
    private TableColumn<ServerInfo, String> serverHostColumn;

    @FXML
    private TableColumn<ServerInfo, String> serverOsColumn;

    @FXML
    private TableColumn<ServerInfo, String> serverEnvironmentColumn;

    @FXML
    private TableColumn<ServerInfo, String> serverStatusColumn;

    @FXML
    private TextField serviceServerField;

    @FXML
    private TextField serviceNameField;

    @FXML
    private TextArea serviceDescriptionArea;

    @FXML
    private TextField serviceStatusField;

    @FXML
    private TableView<ServiceInfo> servicesTable;

    @FXML
    private TableColumn<ServiceInfo, String> serviceServerColumn;

    @FXML
    private TableColumn<ServiceInfo, String> serviceNameColumn;

    @FXML
    private TableColumn<ServiceInfo, String> serviceDescriptionColumn;

    @FXML
    private TableColumn<ServiceInfo, String> serviceStatusColumn;

    @FXML
    private TextField queryNameField;

    @FXML
    private TextField queryParametersField;

    @FXML
    private TableView<QueryDefinition> queriesTable;

    @FXML
    private TableColumn<QueryDefinition, String> queryNameColumn;

    @FXML
    private TableColumn<QueryDefinition, String> queryParametersColumn;

    @FXML
    private TextField scriptNameField;

    @FXML
    private TableView<String> scriptsTable;

    @FXML
    private TableColumn<String, String> scriptNameColumn;

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        loggedUserLabel.setText(user == null ? "-" : user.getUsername());
        profileLabel.setText(user == null ? "-" : user.getProfileName());
        versionLabel.setText(APP_VERSION);
        environmentLabel.setText("Protótipo");

        configureTables();
        configureAdminAccess(user);
        refreshAdministrativeTables();
    }

    @FXML
    private void addServer() {
        if (hasBlank(serverNameField, serverHostField, serverOsField, serverEnvironmentField, serverStatusField)) {
            AlertUtil.warning("Cadastrar servidor", "Preencha todos os campos do servidor.");
            return;
        }

        AppContext.mockDataService().addServer(new ServerInfo(
                serverNameField.getText().trim(),
                serverHostField.getText().trim(),
                serverOsField.getText().trim(),
                serverEnvironmentField.getText().trim(),
                serverStatusField.getText().trim()));
        clear(serverNameField, serverHostField, serverOsField, serverEnvironmentField, serverStatusField);
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar servidor", "Servidor registrado no mock em memória.");
    }

    @FXML
    private void addService() {
        if (hasBlank(serviceServerField, serviceNameField, serviceStatusField)
                || serviceDescriptionArea.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar serviço", "Preencha todos os campos do serviço.");
            return;
        }

        AppContext.mockDataService().addService(new ServiceInfo(
                serviceServerField.getText().trim(),
                serviceNameField.getText().trim(),
                serviceDescriptionArea.getText().trim(),
                serviceStatusField.getText().trim(),
                LocalDateTime.now()));
        clear(serviceServerField, serviceNameField, serviceStatusField);
        serviceDescriptionArea.clear();
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar serviço", "Serviço registrado no mock em memória.");
    }

    @FXML
    private void addQuery() {
        if (queryNameField.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar query", "Informe o nome da query.");
            return;
        }

        List<String> parameters = Arrays.stream(queryParametersField.getText().split(","))
                .map(String::trim)
                .filter(parameter -> !parameter.isBlank())
                .toList();

        AppContext.mockDataService().addQuery(new QueryDefinition(queryNameField.getText().trim(), parameters));
        clear(queryNameField, queryParametersField);
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar query", "Query registrada no mock em memória.");
    }

    @FXML
    private void addScript() {
        if (scriptNameField.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar script", "Informe o nome do script aprovado.");
            return;
        }

        AppContext.mockDataService().addScript(scriptNameField.getText().trim());
        clear(scriptNameField);
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar script", "Script registrado no mock em memória.");
    }

    private void configureTables() {
        serverNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serverHostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        serverOsColumn.setCellValueFactory(new PropertyValueFactory<>("operatingSystem"));
        serverEnvironmentColumn.setCellValueFactory(new PropertyValueFactory<>("environment"));
        serverStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(serversTable, new double[]{1.1, 1.4, 1.8, 1.2, 1},
                serverNameColumn, serverHostColumn, serverOsColumn, serverEnvironmentColumn, serverStatusColumn);

        serviceServerColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        serviceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(servicesTable, new double[]{1.1, 1.4, 2.4, 1},
                serviceServerColumn, serviceNameColumn, serviceDescriptionColumn, serviceStatusColumn);

        queryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        queryParametersColumn.setCellValueFactory(new PropertyValueFactory<>("parameterSummary"));
        TableUtil.bindColumnWidths(queriesTable, new double[]{1.5, 2.5}, queryNameColumn, queryParametersColumn);

        scriptNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        TableUtil.bindColumnWidths(scriptsTable, new double[]{1}, scriptNameColumn);
    }

    private void configureAdminAccess(User user) {
        boolean admin = user != null && user.getProfile() == UserProfile.ADMIN;
        adminPanel.setVisible(admin);
        adminPanel.setManaged(admin);
        adminAccessLabel.setVisible(!admin);
        adminAccessLabel.setManaged(!admin);
    }

    private void refreshAdministrativeTables() {
        serversTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getServers()));
        servicesTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getServices()));
        queriesTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getQueryDefinitions()));
        scriptsTable.setItems(FXCollections.observableArrayList(AppContext.mockDataService().getScripts()));
    }

    private boolean hasBlank(TextField... fields) {
        return Arrays.stream(fields).anyMatch(field -> field.getText().trim().isBlank());
    }

    private void clear(TextField... fields) {
        Arrays.stream(fields).forEach(TextField::clear);
    }
}
