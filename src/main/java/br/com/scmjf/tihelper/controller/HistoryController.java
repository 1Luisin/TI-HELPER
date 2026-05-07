package br.com.scmjf.tihelper.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.NavigationTarget;
import br.com.scmjf.tihelper.util.PermissionUtil;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class HistoryController {

    private static final String ALL = "Todos";

    @FXML
    private ComboBox<String> typeFilterCombo;
    @FXML
    private ComboBox<String> statusFilterCombo;
    @FXML
    private ComboBox<String> userFilterCombo;
    @FXML
    private DatePicker initialDatePicker;
    @FXML
    private DatePicker finalDatePicker;

    @FXML
    private TableView<ExecutionHistory> historyTable;
    @FXML
    private TableColumn<ExecutionHistory, String> idColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> dateColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> userColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> profileColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> typeColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> serverColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> targetColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> statusColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> reasonColumn;
    @FXML
    private TableColumn<ExecutionHistory, String> messageColumn;
    @FXML
    private TextArea detailsArea;

    private FilteredList<ExecutionHistory> filteredHistory;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        profileColumn.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableUtil.bindColumnWidths(historyTable, new double[]{0.6, 1.4, 0.9, 1, 1.25, 1.1, 1.6, 0.9, 1.5, 2.4},
                idColumn, dateColumn, userColumn, profileColumn, typeColumn, serverColumn, targetColumn, statusColumn, reasonColumn, messageColumn);
        historyTable.setPlaceholder(new Label("Nenhum registro de historico."));

        boolean canViewHistory = PermissionUtil.canAccess(AppContext.getCurrentUser(), NavigationTarget.HISTORY);
        List<ExecutionHistory> records = canViewHistory ? AppContext.historicoService().listarTodos() : List.of();
        typeFilterCombo.setItems(FXCollections.observableArrayList(buildTypeOptions()));
        statusFilterCombo.setItems(FXCollections.observableArrayList(buildStatusOptions()));
        userFilterCombo.setItems(FXCollections.observableArrayList(buildUserOptions(records)));
        typeFilterCombo.getSelectionModel().select(ALL);
        statusFilterCombo.getSelectionModel().select(ALL);
        userFilterCombo.getSelectionModel().select(ALL);

        typeFilterCombo.setOnAction(event -> applyFilters());
        statusFilterCombo.setOnAction(event -> applyFilters());
        userFilterCombo.setOnAction(event -> applyFilters());
        initialDatePicker.setOnAction(event -> applyFilters());
        finalDatePicker.setOnAction(event -> applyFilters());

        filteredHistory = new FilteredList<>(FXCollections.observableArrayList(records), record -> true);
        historyTable.setItems(filteredHistory);
        historyTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> showDetails(selected));
        if (canViewHistory) {
            showDetails(null);
        } else {
            detailsArea.setText("Historico restrito a usuarios ADMIN.");
        }
    }

    @FXML
    private void clearFilters() {
        typeFilterCombo.getSelectionModel().select(ALL);
        statusFilterCombo.getSelectionModel().select(ALL);
        userFilterCombo.getSelectionModel().select(ALL);
        initialDatePicker.setValue(null);
        finalDatePicker.setValue(null);
        applyFilters();
    }

    @FXML
    private void exportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar historico CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        chooser.setInitialFileName("historico-ti-helper.csv");
        File file = chooser.showSaveDialog(historyTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            java.nio.file.Files.writeString(file.toPath(), buildCsv(), StandardCharsets.UTF_8);
            AlertUtil.info("Exportar CSV", "Historico exportado em CSV.");
        } catch (IOException exception) {
            AppLogger.error("Erro ao exportar historico CSV.", exception);
            AlertUtil.error("Exportar CSV", "Nao foi possivel exportar o historico.");
        }
    }

    private void applyFilters() {
        String type = typeFilterCombo.getSelectionModel().getSelectedItem();
        String status = statusFilterCombo.getSelectionModel().getSelectedItem();
        String username = userFilterCombo.getSelectionModel().getSelectedItem();
        LocalDate initialDate = initialDatePicker.getValue();
        LocalDate finalDate = finalDatePicker.getValue();

        filteredHistory.setPredicate(record -> matchesTextFilter(type, record.getActionType())
                && matchesTextFilter(status, record.getStatus())
                && matchesTextFilter(username, record.getUsername())
                && matchesInitialDate(record, initialDate)
                && matchesFinalDate(record, finalDate));
    }

    private boolean matchesTextFilter(String filter, String value) {
        return filter == null || ALL.equals(filter) || filter.equals(value);
    }

    private boolean matchesInitialDate(ExecutionHistory record, LocalDate initialDate) {
        return initialDate == null || !record.getDateTime().toLocalDate().isBefore(initialDate);
    }

    private boolean matchesFinalDate(ExecutionHistory record, LocalDate finalDate) {
        return finalDate == null || !record.getDateTime().toLocalDate().isAfter(finalDate);
    }

    private void showDetails(ExecutionHistory record) {
        if (record == null) {
            detailsArea.setText("Selecione um registro para ver todos os detalhes.");
            return;
        }

        detailsArea.setText("""
                ID: %s
                Data/Hora: %s
                Usuario: %s
                Perfil: %s
                Tipo da acao: %s
                Servidor: %s
                Alvo: %s
                Status: %s
                Motivo: %s
                Mensagem: %s
                """.formatted(
                record.getId(),
                record.getFormattedDateTime(),
                record.getUsername(),
                record.getPerfil(),
                record.getActionType(),
                record.getServer(),
                record.getTarget(),
                record.getStatus(),
                record.getReason(),
                record.getMessage()));
    }

    private String buildCsv() {
        List<String> lines = new ArrayList<>();
        lines.add("id,dataHora,usuario,perfil,tipoAcao,servidor,alvo,status,motivo,mensagem");
        lines.addAll(filteredHistory.stream()
                .map(record -> List.of(
                        String.valueOf(record.getId()),
                        record.getFormattedDateTime(),
                        record.getUsername(),
                        record.getPerfil(),
                        record.getActionType(),
                        record.getServer(),
                        record.getTarget(),
                        record.getStatus(),
                        record.getReason(),
                        record.getMessage()).stream()
                        .map(this::escapeCsv)
                        .collect(Collectors.joining(",")))
                .toList());
        return String.join(System.lineSeparator(), lines);
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    private List<String> buildTypeOptions() {
        List<String> values = new ArrayList<>();
        values.add(ALL);
        for (TipoAcao tipo : TipoAcao.values()) {
            values.add(tipo.getDisplayName());
        }
        return values;
    }

    private List<String> buildStatusOptions() {
        List<String> values = new ArrayList<>();
        values.add(ALL);
        for (StatusExecucao status : StatusExecucao.values()) {
            values.add(status.getDisplayName());
        }
        return values;
    }

    private List<String> buildUserOptions(List<ExecutionHistory> records) {
        List<String> values = new ArrayList<>();
        values.add(ALL);
        values.addAll(records.stream()
                .map(ExecutionHistory::getUsername)
                .distinct()
                .sorted()
                .toList());
        return values;
    }
}
