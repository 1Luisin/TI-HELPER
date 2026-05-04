package br.com.scmjf.tihelper.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class WindowChrome {

    private static final String APP_TITLE = "TI Helper - SCMJF";
    private static final double RESIZE_MARGIN = 7;

    private WindowChrome() {
    }

    public static Parent wrap(Stage stage, Parent content) {
        BorderPane frame = new BorderPane();
        frame.getStyleClass().add("window-frame");
        frame.setTop(createTitleBar(stage));
        frame.setCenter(content);
        addResizeSupport(stage, frame);
        return frame;
    }

    private static HBox createTitleBar(Stage stage) {
        Label logo = new Label("TI");
        logo.getStyleClass().add("window-logo");
        logo.setMinSize(30, 30);
        logo.setPrefSize(30, 30);
        logo.setMaxSize(30, 30);

        Label title = new Label(APP_TITLE);
        title.getStyleClass().add("window-title");

        Label subtitle = new Label("Protótipo desktop");
        subtitle.getStyleClass().add("window-subtitle");

        VBox titleGroup = new VBox(title, subtitle);
        titleGroup.setSpacing(1);
        titleGroup.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeButton = windowButton("−");
        minimizeButton.setOnAction(event -> stage.setIconified(true));

        Button maximizeButton = windowButton("□");
        maximizeButton.setOnAction(event -> stage.setMaximized(!stage.isMaximized()));
        stage.maximizedProperty().addListener((observable, oldValue, maximized) ->
                maximizeButton.setText(maximized ? "❐" : "□"));

        Button closeButton = windowButton("×");
        closeButton.getStyleClass().add("window-close-button");
        closeButton.setOnAction(event -> stage.close());

        HBox titleBar = new HBox(10, logo, titleGroup, spacer, minimizeButton, maximizeButton, closeButton);
        titleBar.getStyleClass().add("window-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(8, 10, 8, 12));
        addDragSupport(stage, titleBar);
        return titleBar;
    }

    private static Button windowButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().addAll("window-control-button");
        button.setMinSize(36, 28);
        button.setPrefSize(36, 28);
        button.setMaxSize(36, 28);
        return button;
    }

    private static void addDragSupport(Stage stage, HBox titleBar) {
        DragState state = new DragState();

        titleBar.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || isWindowButtonTarget(event.getTarget())) {
                return;
            }

            state.sceneX = event.getSceneX();
            state.sceneY = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown() || isWindowButtonTarget(event.getTarget())) {
                return;
            }

            if (stage.isMaximized()) {
                stage.setMaximized(false);
                state.sceneX = Math.min(stage.getWidth() / 2, state.sceneX);
            }

            stage.setX(event.getScreenX() - state.sceneX);
            stage.setY(event.getScreenY() - state.sceneY);
        });

        titleBar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2
                    && !isWindowButtonTarget(event.getTarget())) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
    }

    private static boolean isWindowButtonTarget(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }

        Node current = node;
        while (current != null) {
            if (current.getStyleClass().contains("window-control-button")) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static void addResizeSupport(Stage stage, BorderPane frame) {
        ResizeState state = new ResizeState();

        frame.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!stage.isMaximized()) {
                frame.setCursor(resolveCursor(frame, event));
            }
        });

        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Cursor cursor = resolveCursor(frame, event);
            if (cursor == Cursor.DEFAULT || stage.isMaximized()) {
                return;
            }

            state.cursor = cursor;
            state.screenX = event.getScreenX();
            state.screenY = event.getScreenY();
            state.stageX = stage.getX();
            state.stageY = stage.getY();
            state.width = stage.getWidth();
            state.height = stage.getHeight();
            event.consume();
        });

        frame.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (state.cursor == Cursor.DEFAULT || stage.isMaximized()) {
                return;
            }

            resize(stage, state, event);
            event.consume();
        });

        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            state.cursor = Cursor.DEFAULT;
            frame.setCursor(Cursor.DEFAULT);
        });
    }

    private static Cursor resolveCursor(Region frame, MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        double width = frame.getWidth();
        double height = frame.getHeight();

        boolean left = x <= RESIZE_MARGIN;
        boolean right = x >= width - RESIZE_MARGIN;
        boolean top = y <= RESIZE_MARGIN;
        boolean bottom = y >= height - RESIZE_MARGIN;

        if (top && left) {
            return Cursor.NW_RESIZE;
        }
        if (top && right) {
            return Cursor.NE_RESIZE;
        }
        if (bottom && left) {
            return Cursor.SW_RESIZE;
        }
        if (bottom && right) {
            return Cursor.SE_RESIZE;
        }
        if (left) {
            return Cursor.W_RESIZE;
        }
        if (right) {
            return Cursor.E_RESIZE;
        }
        if (top) {
            return Cursor.N_RESIZE;
        }
        if (bottom) {
            return Cursor.S_RESIZE;
        }
        return Cursor.DEFAULT;
    }

    private static void resize(Stage stage, ResizeState state, MouseEvent event) {
        double deltaX = event.getScreenX() - state.screenX;
        double deltaY = event.getScreenY() - state.screenY;
        double minWidth = Math.max(stage.getMinWidth(), 820);
        double minHeight = Math.max(stage.getMinHeight(), 560);

        if (resizesRight(state.cursor)) {
            stage.setWidth(Math.max(minWidth, state.width + deltaX));
        }
        if (resizesBottom(state.cursor)) {
            stage.setHeight(Math.max(minHeight, state.height + deltaY));
        }
        if (resizesLeft(state.cursor)) {
            double targetWidth = Math.max(minWidth, state.width - deltaX);
            stage.setWidth(targetWidth);
            stage.setX(state.stageX + state.width - targetWidth);
        }
        if (resizesTop(state.cursor)) {
            double targetHeight = Math.max(minHeight, state.height - deltaY);
            stage.setHeight(targetHeight);
            stage.setY(state.stageY + state.height - targetHeight);
        }
    }

    private static boolean resizesLeft(Cursor cursor) {
        return cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE;
    }

    private static boolean resizesRight(Cursor cursor) {
        return cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE;
    }

    private static boolean resizesTop(Cursor cursor) {
        return cursor == Cursor.N_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.NE_RESIZE;
    }

    private static boolean resizesBottom(Cursor cursor) {
        return cursor == Cursor.S_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.SE_RESIZE;
    }

    private static final class DragState {
        private double sceneX;
        private double sceneY;
    }

    private static final class ResizeState {
        private Cursor cursor = Cursor.DEFAULT;
        private double screenX;
        private double screenY;
        private double stageX;
        private double stageY;
        private double width;
        private double height;
    }
}
