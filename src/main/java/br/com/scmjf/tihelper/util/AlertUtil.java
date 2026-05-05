package br.com.scmjf.tihelper.util;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class AlertUtil {

    private static final String STYLESHEET = "/br/com/scmjf/tihelper/styles/app.css";

    private AlertUtil() {
    }

    public static void info(String title, String message) {
        show(MessageType.INFO, title, message);
    }

    public static void warning(String title, String message) {
        show(MessageType.WARNING, title, message);
    }

    public static void error(String title, String message) {
        show(MessageType.ERROR, title, message);
    }

    public static Optional<String> askReason(String title, String header) {
        Stage dialog = createDialog(title);
        AtomicReference<Optional<String>> result = new AtomicReference<>(Optional.empty());

        Label icon = createIcon(MessageType.INFO);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");

        Label messageLabel = new Label(header);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Descreva o motivo");
        reasonArea.setPrefRowCount(4);
        reasonArea.setWrapText(true);
        reasonArea.setMaxWidth(Double.MAX_VALUE);

        Label validationLabel = new Label("Informe um motivo para continuar.");
        validationLabel.getStyleClass().add("dialog-validation");
        validationLabel.setVisible(false);
        validationLabel.setManaged(false);

        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("dialog-secondary-button");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> dialog.close());

        Button confirmButton = new Button("Confirmar");
        confirmButton.getStyleClass().add("dialog-primary-button");
        confirmButton.setDefaultButton(true);
        confirmButton.setOnAction(event -> {
            String reason = reasonArea.getText().trim();
            if (reason.isBlank()) {
                validationLabel.setVisible(true);
                validationLabel.setManaged(true);
                reasonArea.requestFocus();
                return;
            }

            result.set(Optional.of(reason));
            dialog.close();
        });

        HBox actions = createActions(cancelButton, confirmButton);
        VBox textContent = new VBox(10, titleLabel, messageLabel, reasonArea, validationLabel, actions);
        textContent.setAlignment(Pos.CENTER_LEFT);
        textContent.setMaxWidth(Double.MAX_VALUE);
        HBox content = new HBox(16, icon, textContent);
        content.setAlignment(Pos.TOP_LEFT);

        showDialog(dialog, content);
        return result.get();
    }

    private static void show(MessageType type, String title, String message) {
        Stage dialog = createDialog(title);

        Label icon = createIcon(type);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("dialog-primary-button");
        okButton.setDefaultButton(true);
        okButton.setCancelButton(true);
        okButton.setOnAction(event -> dialog.close());

        HBox actions = createActions(okButton);
        VBox textContent = new VBox(10, titleLabel, messageLabel, actions);
        textContent.setAlignment(Pos.CENTER_LEFT);
        textContent.setMaxWidth(Double.MAX_VALUE);

        HBox content = new HBox(16, icon, textContent);
        content.setAlignment(Pos.TOP_LEFT);
        showDialog(dialog, content);
    }

    private static Stage createDialog(String title) {
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.setTitle(title);

        findOwner().ifPresent(owner -> {
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
        });
        if (dialog.getModality() == Modality.NONE) {
            dialog.initModality(Modality.APPLICATION_MODAL);
        }

        dialog.setResizable(false);
        return dialog;
    }

    private static void showDialog(Stage dialog, HBox content) {
        VBox card = new VBox(content);
        card.getStyleClass().add("dialog-card");
        card.setPadding(new Insets(22));
        card.setMaxWidth(520);

        Scene scene = new Scene(card);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(AlertUtil.class.getResource(STYLESHEET).toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                dialog.close();
            }
        });

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static HBox createActions(Button... buttons) {
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        actions.getChildren().add(spacer);
        actions.getChildren().addAll(buttons);
        return actions;
    }

    private static Label createIcon(MessageType type) {
        Label icon = new Label(type.iconText);
        icon.getStyleClass().addAll("dialog-icon", type.styleClass);
        icon.setMinSize(42, 42);
        icon.setPrefSize(42, 42);
        icon.setMaxSize(42, 42);
        return icon;
    }

    private static Optional<Window> findOwner() {
        return Window.getWindows().stream()
                .filter(Window::isShowing)
                .filter(window -> window instanceof Stage)
                .findFirst();
    }

    private enum MessageType {
        INFO("i", "dialog-icon-info"),
        WARNING("!", "dialog-icon-warning"),
        ERROR("x", "dialog-icon-error");

        private final String iconText;
        private final String styleClass;

        MessageType(String iconText, String styleClass) {
            this.iconText = iconText;
            this.styleClass = styleClass;
        }
    }
}
