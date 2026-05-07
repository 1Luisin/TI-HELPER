package br.com.scmjf.tihelper.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserModule;
import br.com.scmjf.tihelper.model.UserProfile;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.SqlSyntaxHighlighter;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SettingsController {

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
    private CheckBox serviceRestartAllowedCheckBox;
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
    private TableColumn<ServiceInfo, String> serviceRestartAllowedColumn;

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
    @FXML
    private TableView<QueryDefinition> queriesTable;
    @FXML
    private TableColumn<QueryDefinition, String> queryNameColumn;
    @FXML
    private TableColumn<QueryDefinition, String> queryTextColumn;
    private CodeArea queryCodeArea;

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

    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField userPasswordField;
    @FXML
    private TextField userFullNameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userSectorField;
    @FXML
    private ComboBox<UserProfile> userProfileCombo;
    @FXML
    private FlowPane userModulesPane;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userDisplayNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userSectorColumn;
    @FXML
    private TableColumn<User, String> userProfileColumn;
    @FXML
    private TableColumn<User, String> userModulesColumn;

    private File selectedScriptFile;
    private String selectedServerName;
    private ServiceInfo selectedService;
    private String selectedPanelName;
    private String selectedQueryName;
    private String selectedScriptName;
    private String selectedUsername;
    private final Map<UserModule, CheckBox> userModuleChecks = new EnumMap<>(UserModule.class);

    @FXML
    private void initialize() {
        User user = AppContext.getCurrentUser();
        configurePanelStatusOptions();
        configureUserProfileOptions();
        configureUserModuleOptions();
        configureSqlEditor();
        configureQueryParameterToggle();
        configureTables();
        configureSelections();
        configureAdminAccess(user);
        refreshAdministrativeTables();
    }

    @FXML
    private void addServer() {
        ActionResult result = AppContext.servidorService().cadastrar(buildServerFromFields(), AppContext.getCurrentUser());
        finishAction("Cadastrar servidor", result, this::clearServerFields);
    }

    @FXML
    private void editServer() {
        if (selectedServerName == null) {
            AlertUtil.warning("Editar servidor", "Selecione um servidor.");
            return;
        }
        ActionResult result = AppContext.servidorService().alterar(selectedServerName, buildServerFromFields(), AppContext.getCurrentUser());
        finishAction("Editar servidor", result, this::clearServerFields);
    }

    @FXML
    private void deleteServer() {
        ServerInfo selected = serversTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir servidor", "Deseja excluir o servidor selecionado?")) {
            return;
        }
        ActionResult result = AppContext.servidorService().excluir(selected, AppContext.getCurrentUser());
        finishAction("Excluir servidor", result, this::clearServerFields);
    }

    @FXML
    private void clearServerFields() {
        selectedServerName = null;
        serversTable.getSelectionModel().clearSelection();
        clear(serverNameField, serverHostField, serverOsField, serverEnvironmentField, serverStatusField);
    }

    @FXML
    private void addService() {
        ActionResult result = AppContext.servicoServidorService().cadastrar(buildServiceFromFields(), AppContext.getCurrentUser());
        finishAction("Cadastrar servico", result, this::clearServiceFields);
    }

    @FXML
    private void editService() {
        if (selectedService == null) {
            AlertUtil.warning("Editar servico", "Selecione um servico.");
            return;
        }
        ActionResult result = AppContext.servicoServidorService().alterar(selectedService, buildServiceFromFields(), AppContext.getCurrentUser());
        finishAction("Editar servico", result, this::clearServiceFields);
    }

    @FXML
    private void deleteService() {
        ServiceInfo selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir servico", "Deseja excluir o servico selecionado?")) {
            return;
        }
        ActionResult result = AppContext.servicoServidorService().excluir(selected, AppContext.getCurrentUser());
        finishAction("Excluir servico", result, this::clearServiceFields);
    }

    @FXML
    private void clearServiceFields() {
        selectedService = null;
        servicesTable.getSelectionModel().clearSelection();
        clear(serviceServerField, serviceNameField, serviceStatusField);
        serviceDescriptionArea.clear();
        serviceRestartAllowedCheckBox.setSelected(true);
    }

    @FXML
    private void addPanel() {
        ActionResult result = AppContext.painelService().cadastrar(buildPanelFromFields(), AppContext.getCurrentUser());
        finishAction("Cadastrar painel", result, this::clearPanelFields);
    }

    @FXML
    private void editPanel() {
        if (selectedPanelName == null) {
            AlertUtil.warning("Editar painel", "Selecione um painel.");
            return;
        }
        ActionResult result = AppContext.painelService().alterar(selectedPanelName, buildPanelFromFields(), AppContext.getCurrentUser());
        finishAction("Editar painel", result, this::clearPanelFields);
    }

    @FXML
    private void deletePanel() {
        PanelInfo selected = panelsTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir painel", "Deseja excluir o painel selecionado?")) {
            return;
        }
        ActionResult result = AppContext.painelService().excluir(selected, AppContext.getCurrentUser());
        finishAction("Excluir painel", result, this::clearPanelFields);
    }

    @FXML
    private void clearPanelFields() {
        selectedPanelName = null;
        panelsTable.getSelectionModel().clearSelection();
        clear(panelNameField, panelIpField, panelLocationField);
        panelStatusCombo.getSelectionModel().select("Ligado");
    }

    @FXML
    private void addQuery() {
        ActionResult result = AppContext.queryService().cadastrar(buildQueryFromFields(), AppContext.getCurrentUser());
        finishAction("Cadastrar query", result, this::clearQueryFields);
    }

    @FXML
    private void editQuery() {
        if (selectedQueryName == null) {
            AlertUtil.warning("Editar query", "Selecione uma query.");
            return;
        }
        ActionResult result = AppContext.queryService().alterar(selectedQueryName, buildQueryFromFields(), AppContext.getCurrentUser());
        finishAction("Editar query", result, this::clearQueryFields);
    }

    @FXML
    private void deleteQuery() {
        QueryDefinition selected = queriesTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir query", "Deseja excluir a query selecionada?")) {
            return;
        }
        ActionResult result = AppContext.queryService().excluir(selected, AppContext.getCurrentUser());
        finishAction("Excluir query", result, this::clearQueryFields);
    }

    @FXML
    private void clearQueryFields() {
        selectedQueryName = null;
        queriesTable.getSelectionModel().clearSelection();
        clear(queryNameField, queryParametersField);
        queryCodeArea.clear();
        queryHasParametersCheckBox.setSelected(false);
    }

    @FXML
    private void addScript() {
        ScriptDefinition script = buildScriptFromFields();
        if (script == null) {
            return;
        }
        ActionResult result = AppContext.scriptService().cadastrar(script, AppContext.getCurrentUser());
        finishAction("Cadastrar script", result, this::clearScriptFields);
    }

    @FXML
    private void editScript() {
        if (selectedScriptName == null) {
            AlertUtil.warning("Editar script", "Selecione um script.");
            return;
        }
        ScriptDefinition script = buildScriptFromFields();
        if (script == null) {
            return;
        }
        ActionResult result = AppContext.scriptService().alterar(selectedScriptName, script, AppContext.getCurrentUser());
        finishAction("Editar script", result, this::clearScriptFields);
    }

    @FXML
    private void deleteScript() {
        ScriptDefinition selected = scriptsTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir script", "Deseja excluir o script selecionado?")) {
            return;
        }
        ActionResult result = AppContext.scriptService().excluir(selected, AppContext.getCurrentUser());
        finishAction("Excluir script", result, this::clearScriptFields);
    }

    @FXML
    private void clearScriptFields() {
        selectedScriptName = null;
        scriptsTable.getSelectionModel().clearSelection();
        clear(scriptNameField);
        scriptBodyArea.clear();
        scriptSourceTabs.getSelectionModel().select(scriptBodyTab);
        clearSelectedScriptFile();
    }

    @FXML
    private void addUser() {
        boolean result = AppContext.usuarioService().registerUser(
                userNameField.getText(),
                userPasswordField.getText(),
                userProfileCombo.getSelectionModel().getSelectedItem(),
                userFullNameField.getText(),
                userEmailField.getText(),
                userSectorField.getText(),
                selectedUserModules());
        finishUserAction("Cadastrar usuario", result, "Usuario cadastrado no mock local.");
    }

    @FXML
    private void editUser() {
        if (selectedUsername == null) {
            AlertUtil.warning("Editar usuario", "Selecione um usuario.");
            return;
        }
        boolean result = AppContext.usuarioService().updateUser(
                selectedUsername,
                userNameField.getText(),
                userPasswordField.getText(),
                userProfileCombo.getSelectionModel().getSelectedItem(),
                userFullNameField.getText(),
                userEmailField.getText(),
                userSectorField.getText(),
                selectedUserModules());
        finishUserAction("Editar usuario", result, "Usuario alterado no mock local.");
    }

    @FXML
    private void deleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null || !AlertUtil.confirm("Excluir usuario", "Deseja excluir o usuario selecionado?")) {
            return;
        }
        boolean result = AppContext.usuarioService().deleteUser(selected.getUsername());
        finishUserAction("Excluir usuario", result, "Usuario excluido do mock local.");
    }

    @FXML
    private void clearUserFields() {
        selectedUsername = null;
        usersTable.getSelectionModel().clearSelection();
        clear(userNameField, userFullNameField, userEmailField, userSectorField);
        userPasswordField.clear();
        userProfileCombo.getSelectionModel().select(UserProfile.OPERADOR_TI);
        selectUserModules(UserModule.defaultsFor(UserProfile.OPERADOR_TI));
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

    @FXML
    private void restoreDefaultData() {
        if (!AlertUtil.confirm("Restaurar dados", "Deseja restaurar os dados mockados padrao?")) {
            return;
        }
        AppContext.restoreDefaultMockData();
        refreshAdministrativeTables();
        clearAllForms();
        AlertUtil.info("Restaurar dados", "Dados mockados padrao restaurados.");
    }

    @FXML
    private void exportBackup() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar backup JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        chooser.setInitialFileName("ti-helper-backup.json");
        File file = chooser.showSaveDialog(adminPanel.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            AppContext.exportBackup(file.toPath());
            AlertUtil.info("Exportar backup", "Backup JSON exportado.");
        } catch (IOException exception) {
            AppLogger.error("Erro ao exportar backup JSON.", exception);
            AlertUtil.error("Exportar backup", "Nao foi possivel exportar o backup.");
        }
    }

    @FXML
    private void importBackup() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar backup JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = chooser.showOpenDialog(adminPanel.getScene().getWindow());
        if (file == null || !AlertUtil.confirm("Importar backup", "A importacao substituirá os dados locais. Continuar?")) {
            return;
        }

        try {
            AppContext.importBackup(file.toPath());
            refreshAdministrativeTables();
            clearAllForms();
            AlertUtil.info("Importar backup", "Backup JSON importado.");
        } catch (IOException exception) {
            AppLogger.error("Erro ao importar backup JSON.", exception);
            AlertUtil.error("Importar backup", "Nao foi possivel importar o backup.");
        }
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
        serviceRestartAllowedColumn.setCellValueFactory(new PropertyValueFactory<>("restartPermissionLabel"));
        TableUtil.bindColumnWidths(servicesTable, new double[]{1.1, 1.4, 2.2, 1, 1},
                serviceServerColumn, serviceNameColumn, serviceDescriptionColumn, serviceStatusColumn, serviceRestartAllowedColumn);
        servicesTable.setPlaceholder(new Label("Nenhum servico cadastrado."));

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

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userDisplayNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userSectorColumn.setCellValueFactory(new PropertyValueFactory<>("sector"));
        userProfileColumn.setCellValueFactory(new PropertyValueFactory<>("profileName"));
        userModulesColumn.setCellValueFactory(new PropertyValueFactory<>("modulesDisplay"));
        TableUtil.bindColumnWidths(usersTable, new double[]{1.1, 1.5, 1.7, 1.1, 1, 2.4},
                userNameColumn, userDisplayNameColumn, userEmailColumn, userSectorColumn, userProfileColumn, userModulesColumn);
        usersTable.setPlaceholder(new Label("Nenhum usuario cadastrado."));
    }

    private void configureSelections() {
        serversTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedServerName = selected.getName();
            serverNameField.setText(selected.getName());
            serverHostField.setText(selected.getHost());
            serverOsField.setText(selected.getOperatingSystem());
            serverEnvironmentField.setText(selected.getEnvironment());
            serverStatusField.setText(selected.getStatus());
        });

        servicesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedService = selected;
            serviceServerField.setText(selected.getServer());
            serviceNameField.setText(selected.getName());
            serviceDescriptionArea.setText(selected.getDescription());
            serviceStatusField.setText(selected.getStatus());
            serviceRestartAllowedCheckBox.setSelected(selected.isRestartAllowed());
        });

        panelsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedPanelName = selected.getName();
            panelNameField.setText(selected.getName());
            panelIpField.setText(selected.getIpAddress());
            panelLocationField.setText(selected.getLocation());
            panelStatusCombo.getSelectionModel().select(selected.getStatus());
        });

        queriesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedQueryName = selected.getName();
            queryNameField.setText(selected.getName());
            queryCodeArea.replaceText(selected.getQueryText());
            queryHasParametersCheckBox.setSelected(!selected.getParameters().isEmpty());
            queryParametersField.setText(String.join(", ", selected.getParameters()));
        });

        scriptsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedScriptName = selected.getName();
            scriptNameField.setText(selected.getName());
            scriptBodyArea.setText(selected.getBody());
            scriptSourceTabs.getSelectionModel().select(scriptBodyTab);
            scriptFileLabel.setText(selected.getFileName().isBlank() ? "Nenhum arquivo selecionado" : selected.getFileName());
            selectedScriptFile = null;
        });

        usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                return;
            }
            selectedUsername = selected.getUsername();
            userNameField.setText(selected.getUsername());
            userPasswordField.clear();
            userFullNameField.setText(selected.getFullName());
            userEmailField.setText(selected.getEmail());
            userSectorField.setText(selected.getSector());
            userProfileCombo.getSelectionModel().select(selected.getProfile());
            selectUserModules(selected.getModules());
        });
    }

    private void configurePanelStatusOptions() {
        panelStatusCombo.setItems(FXCollections.observableArrayList("Ligado", "Desligado"));
        panelStatusCombo.getSelectionModel().select("Ligado");
    }

    private void configureUserProfileOptions() {
        userProfileCombo.setItems(FXCollections.observableArrayList(UserProfile.values()));
        userProfileCombo.getSelectionModel().select(UserProfile.OPERADOR_TI);
        userProfileCombo.valueProperty().addListener((observable, oldValue, profile) -> {
            updateModuleAvailability(profile);
            if (selectedUsername == null) {
                selectUserModules(UserModule.defaultsFor(profile));
            }
        });
    }

    private void configureUserModuleOptions() {
        userModulesPane.getChildren().clear();
        userModuleChecks.clear();
        Arrays.stream(UserModule.values()).forEach(module -> {
            CheckBox checkBox = new CheckBox(module.getDisplayName());
            checkBox.getStyleClass().add("module-check");
            userModuleChecks.put(module, checkBox);
            userModulesPane.getChildren().add(checkBox);
        });
        selectUserModules(UserModule.defaultsFor(userProfileCombo.getSelectionModel().getSelectedItem()));
        updateModuleAvailability(userProfileCombo.getSelectionModel().getSelectedItem());
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
        usersTable.setItems(FXCollections.observableArrayList(AppContext.usuarioService().getUsers()));
    }

    private void finishAction(String title, ActionResult result, Runnable clearAction) {
        if (!result.success()) {
            AlertUtil.warning(title, result.message());
            return;
        }
        clearAction.run();
        refreshAdministrativeTables();
        AlertUtil.info(title, result.message());
    }

    private void finishUserAction(String title, boolean success, String successMessage) {
        if (!success) {
            AlertUtil.warning(title, "Informe dados validos. O login deve ser unico e o perfil obrigatorio.");
            return;
        }
        clearUserFields();
        refreshAdministrativeTables();
        AlertUtil.info(title, successMessage);
    }

    private ServerInfo buildServerFromFields() {
        return new ServerInfo(
                serverNameField.getText().trim(),
                serverHostField.getText().trim(),
                serverOsField.getText().trim(),
                serverEnvironmentField.getText().trim(),
                serverStatusField.getText().trim());
    }

    private ServiceInfo buildServiceFromFields() {
        return new ServiceInfo(
                serviceServerField.getText().trim(),
                serviceNameField.getText().trim(),
                serviceDescriptionArea.getText().trim(),
                serviceStatusField.getText().trim(),
                LocalDateTime.now(),
                serviceRestartAllowedCheckBox.isSelected());
    }

    private PanelInfo buildPanelFromFields() {
        return new PanelInfo(
                panelNameField.getText().trim(),
                panelIpField.getText().trim(),
                panelLocationField.getText().trim(),
                panelStatusCombo.getSelectionModel().getSelectedItem());
    }

    private QueryDefinition buildQueryFromFields() {
        List<String> parameters = queryHasParametersCheckBox.isSelected()
                ? parseCommaSeparatedParameters(queryParametersField.getText())
                : List.of();
        return new QueryDefinition(queryNameField.getText().trim(), queryCodeArea.getText().trim(), parameters);
    }

    private ScriptDefinition buildScriptFromFields() {
        if (scriptNameField.getText().trim().isBlank()) {
            AlertUtil.warning("Script", "Informe o nome do script aprovado.");
            return null;
        }

        boolean useBody = scriptSourceTabs.getSelectionModel().getSelectedItem() == scriptBodyTab;
        String scriptBody = useBody ? scriptBodyArea.getText().trim() : readSelectedScriptFile();
        if (scriptBody == null) {
            return null;
        }
        return new ScriptDefinition(
                scriptNameField.getText().trim(),
                useBody ? "Corpo" : "Arquivo",
                scriptBody,
                useBody ? "" : selectedScriptFile.getName());
    }

    private List<String> parseCommaSeparatedParameters(String parametersText) {
        return Arrays.stream(parametersText.split(","))
                .map(String::trim)
                .filter(parameter -> !parameter.isBlank())
                .distinct()
                .toList();
    }

    private Set<UserModule> selectedUserModules() {
        return userModuleChecks.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    private void selectUserModules(Set<UserModule> modules) {
        Set<UserModule> selectedModules = modules == null ? Set.of() : modules;
        userModuleChecks.forEach((module, checkBox) -> checkBox.setSelected(selectedModules.contains(module)));
        updateModuleAvailability(userProfileCombo.getSelectionModel().getSelectedItem());
    }

    private void updateModuleAvailability(UserProfile profile) {
        Set<UserModule> allowedModules = UserModule.defaultsFor(profile);
        userModuleChecks.forEach((module, checkBox) -> {
            boolean allowed = allowedModules.contains(module);
            checkBox.setDisable(!allowed);
            if (!allowed) {
                checkBox.setSelected(false);
            }
        });
    }

    private String readSelectedScriptFile() {
        if (selectedScriptFile == null) {
            AlertUtil.warning("Script", "Selecione um arquivo de script.");
            return null;
        }
        try {
            return Files.readString(selectedScriptFile.toPath(), Charset.defaultCharset()).trim();
        } catch (IOException exception) {
            AppLogger.error("Erro ao ler arquivo de script selecionado.", exception);
            AlertUtil.error("Script", "Nao foi possivel ler o arquivo selecionado.");
            return null;
        }
    }

    private void clearAllForms() {
        clearServerFields();
        clearServiceFields();
        clearPanelFields();
        clearQueryFields();
        clearScriptFields();
        clearUserFields();
    }

    private void clear(TextField... fields) {
        Arrays.stream(fields).forEach(TextField::clear);
    }
}
