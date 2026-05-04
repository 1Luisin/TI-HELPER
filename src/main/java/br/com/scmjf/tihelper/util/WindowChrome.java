package br.com.scmjf.tihelper.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class WindowChrome {

    private static final String APP_TITLE = "TI Helper - SCMJF";
    private static final double RESIZE_MARGIN = 7;
    private static final String MINIMIZE_TEXT = "\ue921";
    private static final String MAXIMIZE_TEXT = "\ue922";
    private static final String RESTORE_TEXT = "\ue923";
    private static final String CLOSE_TEXT = "\ue8bb";

    private WindowChrome() {
    }

    public static Parent wrap(Stage stage, Parent content) {
        WindowState windowState = new WindowState();
        BorderPane frame = new BorderPane();
        frame.getStyleClass().add("window-frame");
        frame.setTop(createTitleBar(stage, windowState));
        frame.setCenter(content);
        addResizeSupport(stage, frame, windowState);
        return frame;
    }

    private static HBox createTitleBar(Stage stage, WindowState windowState) {
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

        Button minimizeButton = windowButton(MINIMIZE_TEXT);
        minimizeButton.setOnAction(event -> {
            stage.setIconified(true);
            event.consume();
        });

        Button maximizeButton = windowButton(MAXIMIZE_TEXT);
        maximizeButton.setOnAction(event -> {
            toggleMaximize(stage, windowState, maximizeButton);
            event.consume();
        });

        Button closeButton = windowButton(CLOSE_TEXT);
        closeButton.getStyleClass().add("window-close-button");
        closeButton.setOnAction(event -> {
            stage.close();
            event.consume();
        });

        HBox controls = new HBox(minimizeButton, maximizeButton, closeButton);
        controls.getStyleClass().add("window-controls");

        HBox titleBar = new HBox(10, logo, titleGroup, spacer, controls);
        titleBar.getStyleClass().add("window-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setMinHeight(50);
        titleBar.setPrefHeight(50);
        titleBar.setPadding(new Insets(0, 0, 0, 12));
        addDragSupport(stage, titleBar, windowState, maximizeButton);
        return titleBar;
    }

    private static Button windowButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().addAll("window-control-button");
        button.setFocusTraversable(false);
        button.setMinSize(46, 50);
        button.setPrefSize(46, 50);
        button.setMaxSize(46, 50);
        return button;
    }

    private static void addDragSupport(Stage stage, HBox titleBar, WindowState windowState, Button maximizeButton) {
        DragState state = new DragState();

        titleBar.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || isWindowButtonTarget(event.getTarget())) {
                return;
            }

            state.sceneX = event.getSceneX();
            state.sceneY = event.getSceneY();
            event.consume();
        });

        titleBar.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown() || isWindowButtonTarget(event.getTarget())) {
                return;
            }

            if (windowState.maximized) {
                double mouseRatio = event.getSceneX() / Math.max(stage.getWidth(), 1);
                restore(stage, windowState, maximizeButton);
                state.sceneX = Math.max(30, Math.min(stage.getWidth() - 30, stage.getWidth() * mouseRatio));
                state.sceneY = Math.min(state.sceneY, 26);
            }

            stage.setX(event.getScreenX() - state.sceneX);
            stage.setY(event.getScreenY() - state.sceneY);
            event.consume();
        });

        titleBar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2
                    && !isWindowButtonTarget(event.getTarget())) {
                toggleMaximize(stage, windowState, maximizeButton);
                event.consume();
            }
        });
    }

    private static void toggleMaximize(Stage stage, WindowState windowState, Button maximizeButton) {
        if (windowState.maximized) {
            restore(stage, windowState, maximizeButton);
        } else {
            maximize(stage, windowState, maximizeButton);
        }
    }

    private static void maximize(Stage stage, WindowState windowState, Button maximizeButton) {
        if (!windowState.maximized) {
            windowState.restoreX = stage.getX();
            windowState.restoreY = stage.getY();
            windowState.restoreWidth = stage.getWidth();
            windowState.restoreHeight = stage.getHeight();
        }

        Rectangle2D bounds = currentScreen(stage).getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        windowState.maximized = true;
        maximizeButton.setText(RESTORE_TEXT);
    }

    private static void restore(Stage stage, WindowState windowState, Button maximizeButton) {
        stage.setX(windowState.restoreX);
        stage.setY(windowState.restoreY);
        stage.setWidth(Math.max(stage.getMinWidth(), windowState.restoreWidth));
        stage.setHeight(Math.max(stage.getMinHeight(), windowState.restoreHeight));

        windowState.maximized = false;
        maximizeButton.setText(MAXIMIZE_TEXT);
    }

    private static Screen currentScreen(Stage stage) {
        return Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary());
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

    private static void addResizeSupport(Stage stage, BorderPane frame, WindowState windowState) {
        ResizeState state = new ResizeState();

        frame.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (windowState.maximized || isWindowButtonTarget(event.getTarget())) {
                frame.setCursor(Cursor.DEFAULT);
                return;
            }
            frame.setCursor(resolveCursor(frame, event));
        });

        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY || windowState.maximized || isWindowButtonTarget(event.getTarget())) {
                return;
            }

            Cursor cursor = resolveCursor(frame, event);
            if (cursor == Cursor.DEFAULT) {
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
            if (state.cursor == Cursor.DEFAULT || windowState.maximized || isWindowButtonTarget(event.getTarget())) {
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

    private static final class WindowState {
        private boolean maximized;
        private double restoreX;
        private double restoreY;
        private double restoreWidth;
        private double restoreHeight;
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
