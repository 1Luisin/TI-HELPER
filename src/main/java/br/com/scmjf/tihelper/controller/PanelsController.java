package br.com.scmjf.tihelper.controller;

import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.util.AlertUtil;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PanelsController {

    @FXML
    private Label totalPanelsLabel;

    @FXML
    private Label onlinePanelsLabel;

    @FXML
    private Label offlinePanelsLabel;

    @FXML
    private TextField searchField;

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

    private FilteredList<PanelInfo> filteredPanels;

    @FXML
    private void initialize() {
        panelNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        panelIpColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        panelLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        panelStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        panelStatusColumn.setCellFactory(column -> new StatusCell());
        TableUtil.bindColumnWidths(panelsTable, new double[]{1.6, 1.2, 1.6, 1},
                panelNameColumn, panelIpColumn, panelLocationColumn, panelStatusColumn);
        panelsTable.setPlaceholder(new Label("Nenhum painel cadastrado."));
        searchField.textProperty().addListener((observable, oldValue, value) -> applyFilter(value));

        refreshPanels();
    }

    @FXML
    private void refreshPanels() {
        var panels = AppContext.painelService().listar();
        filteredPanels = new FilteredList<>(FXCollections.observableArrayList(panels), panel -> true);
        panelsTable.setItems(filteredPanels);
        applyFilter(searchField.getText());

        long online = panels.stream().filter(PanelInfo::isOnline).count();
        totalPanelsLabel.setText(String.valueOf(panels.size()));
        onlinePanelsLabel.setText(String.valueOf(online));
        offlinePanelsLabel.setText(String.valueOf(panels.size() - online));
    }

    @FXML
    private void showMockNotice() {
        AlertUtil.info("Painéis", "Status exibido a partir dos cadastros mockados em memória.");
    }

    private void applyFilter(String text) {
        if (filteredPanels == null) {
            return;
        }
        String filter = text == null ? "" : text.trim().toLowerCase();
        filteredPanels.setPredicate(panel -> filter.isBlank()
                || contains(panel.getName(), filter)
                || contains(panel.getIpAddress(), filter)
                || contains(panel.getLocation(), filter)
                || contains(panel.getStatus(), filter));
    }

    private boolean contains(String value, String filter) {
        return value != null && value.toLowerCase().contains(filter);
    }

    private static class StatusCell extends TableCell<PanelInfo, String> {

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            Label label = new Label(status);
            label.getStyleClass().addAll("status-pill",
                    "Ligado".equalsIgnoreCase(status) ? "status-on" : "status-off");
            setGraphic(label);
            setText(null);
        }
    }
}
