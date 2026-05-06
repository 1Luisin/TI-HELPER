package br.com.scmjf.tihelper.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.SqlSyntaxHighlighter;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class SettingsController {

    private static final String APP_VERSION = "0.1.1-SNAPSHOT";

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
    private TextField panelNameField;

    @FXML
    private TextField panelIpField;

    @FXML
    private TextField panelLocationField;

    @FXML
    private ComboBox<String> panelStatusCombo;

    @FXML
    private TableView<PanelInfo> panelsTable;

    @FXML
    private TableColumn<PanelInfo, String> panelNameColumn;

    @FXML
    private TableColumn<PanelInfo, String> panelIpColumn;

    @FXML
    private TableColumn<PanelInfo, String> panelLocationColumn;

    @FXML
    private TableColumn<PanelInfo, String> panelStatusColumn;

    @FXML
    private TextField queryNameField;

    @FXML
    private VBox queryEditorHost;

    @FXML
    private CheckBox queryHasParametersCheckBox;

    @FXML
    private VBox queryParametersBox;

    @FXML
    private TextField queryParametersField;

    private CodeArea queryCodeArea;

    @FXML
    private TableView<QueryDefinition> queriesTable;

    @FXML
    private TableColumn<QueryDefinition, String> queryNameColumn;

    @FXML
    private TableColumn<QueryDefinition, String> queryTextColumn;

    @FXML
    private TextField scriptNameField;

    @FXML
    private TabPane scriptSourceTabs;

    @FXML
    private Tab scriptBodyTab;

    @FXML
    private TextArea scriptBodyArea;

    @FXML
    private Label scriptFileLabel;

    @FXML
    private TableView<ScriptDefinition> scriptsTable;

    @FXML
    private TableColumn<ScriptDefinition, String> scriptNameColumn;

    @FXML
    private TableColumn<ScriptDefinition, String> scriptSourceColumn;

    @FXML
    private TableColumn<ScriptDefinition, String> scriptSummaryColumn;

    private File selectedScriptFile;

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        loggedUserLabel.setText(user == null ? "-" : user.getUsername());
        profileLabel.setText(user == null ? "-" : user.getProfileName());
        versionLabel.setText(APP_VERSION);
        environmentLabel.setText("Protótipo");

        configurePanelStatusOptions();
        configureSqlEditor();
        configureQueryParameterToggle();
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

        ActionResult result = AppContext.servidorService().cadastrar(new ServerInfo(
                serverNameField.getText().trim(),
                serverHostField.getText().trim(),
                serverOsField.getText().trim(),
                serverEnvironmentField.getText().trim(),
                serverStatusField.getText().trim()), AppContext.getCurrentUser());
        if (!result.success()) {
            AlertUtil.warning("Cadastrar servidor", result.message());
            return;
        }
        clear(serverNameField, serverHostField, serverOsField, serverEnvironmentField, serverStatusField);
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar servidor", result.message());
    }

    @FXML
    private void addService() {
        if (hasBlank(serviceServerField, serviceNameField, serviceStatusField)
                || serviceDescriptionArea.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar serviço", "Preencha todos os campos do serviço.");
            return;
        }

        ActionResult result = AppContext.servicoServidorService().cadastrar(new ServiceInfo(
                serviceServerField.getText().trim(),
                serviceNameField.getText().trim(),
                serviceDescriptionArea.getText().trim(),
                serviceStatusField.getText().trim(),
                LocalDateTime.now()), AppContext.getCurrentUser());
        if (!result.success()) {
            AlertUtil.warning("Cadastrar serviço", result.message());
            return;
        }
        clear(serviceServerField, serviceNameField, serviceStatusField);
        serviceDescriptionArea.clear();
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar serviço", result.message());
    }

    @FXML
    private void addPanel() {
        if (hasBlank(panelNameField, panelIpField, panelLocationField)
                || panelStatusCombo.getSelectionModel().getSelectedItem() == null) {
            AlertUtil.warning("Cadastrar painel", "Preencha todos os campos do painel.");
            return;
        }

        ActionResult result = AppContext.painelService().cadastrar(new PanelInfo(
                panelNameField.getText().trim(),
                panelIpField.getText().trim(),
                panelLocationField.getText().trim(),
                panelStatusCombo.getSelectionModel().getSelectedItem()), AppContext.getCurrentUser());
        if (!result.success()) {
            AlertUtil.warning("Cadastrar painel", result.message());
            return;
        }
        clear(panelNameField, panelIpField, panelLocationField);
        panelStatusCombo.getSelectionModel().select("Ligado");
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar painel", result.message());
    }

    @FXML
    private void addQuery() {
        if (queryNameField.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar query", "Informe o nome da query.");
            return;
        }
        if (queryCodeArea.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar query", "Informe o texto da query.");
            return;
        }

        List<String> parameters = queryHasParametersCheckBox.isSelected()
                ? parseCommaSeparatedParameters(queryParametersField.getText())
                : List.of();

        ActionResult result = AppContext.queryService().cadastrar(new QueryDefinition(
                queryNameField.getText().trim(),
                queryCodeArea.getText().trim(),
                parameters), AppContext.getCurrentUser());
        if (!result.success()) {
            AlertUtil.warning("Cadastrar query", result.message());
            return;
        }
        clear(queryNameField, queryParametersField);
        queryCodeArea.clear();
        queryHasParametersCheckBox.setSelected(false);
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar query", result.message());
    }

    @FXML
    private void addScript() {
        if (scriptNameField.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar script", "Informe o nome do script aprovado.");
            return;
        }

        boolean useBody = scriptSourceTabs.getSelectionModel().getSelectedItem() == scriptBodyTab;
        if (useBody && scriptBodyArea.getText().trim().isBlank()) {
            AlertUtil.warning("Cadastrar script", "Informe o corpo do script.");
            return;
        }
        if (!useBody && selectedScriptFile == null) {
            AlertUtil.warning("Cadastrar script", "Selecione um arquivo de script.");
            return;
        }

        String scriptBody = useBody ? scriptBodyArea.getText().trim() : readSelectedScriptFile();
        if (scriptBody == null) {
            return;
        }
        if (scriptBody.isBlank()) {
            AlertUtil.warning("Cadastrar script", "O script informado esta vazio.");
            return;
        }

        ActionResult result = AppContext.scriptService().cadastrar(new ScriptDefinition(
                scriptNameField.getText().trim(),
                useBody ? "Corpo" : "Arquivo",
                scriptBody,
                useBody ? "" : selectedScriptFile.getName()), AppContext.getCurrentUser());
        if (!result.success()) {
            AlertUtil.warning("Cadastrar script", result.message());
            return;
        }
        clear(scriptNameField);
        scriptBodyArea.clear();
        clearSelectedScriptFile();
        refreshAdministrativeTables();
        AlertUtil.info("Cadastrar script", result.message());
    }

    @FXML
    private void chooseScriptFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar arquivo de script");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Scripts", "*.bat", "*.cmd", "*.ps1", "*.txt"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*"));

        File file = chooser.showOpenDialog(scriptNameField.getScene().getWindow());
        if (file == null) {
            return;
        }

        selectedScriptFile = file;
        scriptFileLabel.setText(file.getName());
    }

    @FXML
    private void clearSelectedScriptFile() {
        selectedScriptFile = null;
        scriptFileLabel.setText("Nenhum arquivo selecionado");
    }

    private void configureTables() {
        serverNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serverHostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        serverOsColumn.setCellValueFactory(new PropertyValueFactory<>("operatingSystem"));
        serverEnvironmentColumn.setCellValueFactory(new PropertyValueFactory<>("environment"));
        serverStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(serversTable, new double[]{1.1, 1.4, 1.8, 1.2, 1},
                serverNameColumn, serverHostColumn, serverOsColumn, serverEnvironmentColumn, serverStatusColumn);
        serversTable.setPlaceholder(new Label("Nenhum servidor cadastrado."));

        serviceServerColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        serviceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(servicesTable, new double[]{1.1, 1.4, 2.4, 1},
                serviceServerColumn, serviceNameColumn, serviceDescriptionColumn, serviceStatusColumn);
        servicesTable.setPlaceholder(new Label("Nenhum serviço cadastrado."));

        panelNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        panelIpColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        panelLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        panelStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableUtil.bindColumnWidths(panelsTable, new double[]{1.4, 1.2, 1.4, 1},
                panelNameColumn, panelIpColumn, panelLocationColumn, panelStatusColumn);
        panelsTable.setPlaceholder(new Label("Nenhum painel cadastrado."));

        queryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        queryTextColumn.setCellValueFactory(new PropertyValueFactory<>("queryPreview"));
        TableUtil.bindColumnWidths(queriesTable, new double[]{1.4, 3}, queryNameColumn, queryTextColumn);
        queriesTable.setPlaceholder(new Label("Nenhuma query cadastrada."));

        scriptNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scriptSourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceType"));
        scriptSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("sourceSummary"));
        TableUtil.bindColumnWidths(scriptsTable, new double[]{1.4, 0.8, 2.6},
                scriptNameColumn, scriptSourceColumn, scriptSummaryColumn);
        scriptsTable.setPlaceholder(new Label("Nenhum script cadastrado."));
    }

    private void configurePanelStatusOptions() {
        panelStatusCombo.setItems(FXCollections.observableArrayList("Ligado", "Desligado"));
        panelStatusCombo.getSelectionModel().select("Ligado");
    }

    private void configureSqlEditor() {
        queryCodeArea = new CodeArea();
        queryCodeArea.getStyleClass().add("sql-code-area");
        queryCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(queryCodeArea));
        queryCodeArea.setWrapText(false);
        queryCodeArea.setStyleSpans(0, SqlSyntaxHighlighter.computeHighlighting(queryCodeArea.getText()));
        queryCodeArea.textProperty().addListener((observable, oldText, newText) ->
                queryCodeArea.setStyleSpans(0, SqlSyntaxHighlighter.computeHighlighting(newText)));
        queryCodeArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                queryCodeArea.insertText(queryCodeArea.getCaretPosition(), "    ");
                event.consume();
            }
        });
        queryCodeArea.setPrefHeight(180);
        queryCodeArea.setMinHeight(150);
        queryCodeArea.setMaxWidth(Double.MAX_VALUE);

        queryEditorHost.getChildren().setAll(queryCodeArea);
    }

    private void configureQueryParameterToggle() {
        queryParametersBox.visibleProperty().bind(queryHasParametersCheckBox.selectedProperty());
        queryParametersBox.managedProperty().bind(queryHasParametersCheckBox.selectedProperty());
    }

    private void configureAdminAccess(User user) {
        boolean admin = PermissionUtil.canAdmin(user);
        adminPanel.setVisible(admin);
        adminPanel.setManaged(admin);
        adminAccessLabel.setVisible(!admin);
        adminAccessLabel.setManaged(!admin);
    }

    private void refreshAdministrativeTables() {
        serversTable.setItems(FXCollections.observableArrayList(AppContext.servidorService().listar()));
        servicesTable.setItems(FXCollections.observableArrayList(AppContext.servicoServidorService().listar()));
        panelsTable.setItems(FXCollections.observableArrayList(AppContext.painelService().listar()));
        queriesTable.setItems(FXCollections.observableArrayList(AppContext.queryService().listarDefinicoes()));
        scriptsTable.setItems(FXCollections.observableArrayList(AppContext.scriptService().listarDefinicoes()));
    }

    private boolean hasBlank(TextField... fields) {
        return Arrays.stream(fields).anyMatch(field -> field.getText().trim().isBlank());
    }

    private void clear(TextField... fields) {
        Arrays.stream(fields).forEach(TextField::clear);
    }

    private List<String> parseCommaSeparatedParameters(String parametersText) {
        return Arrays.stream(parametersText.split(","))
                .map(String::trim)
                .filter(parameter -> !parameter.isBlank())
                .distinct()
                .toList();
    }

    private String readSelectedScriptFile() {
        try {
            return Files.readString(selectedScriptFile.toPath(), Charset.defaultCharset()).trim();
        } catch (IOException exception) {
            AlertUtil.error("Cadastrar script", "Nao foi possivel ler o arquivo selecionado.");
            return null;
        }
    }
}
