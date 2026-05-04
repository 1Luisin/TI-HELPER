package br.com.scmjf.tihelper.util;

import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        attachSnapPopup(stage, windowState, maximizeButton);

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

    private static void attachSnapPopup(Stage stage, WindowState windowState, Button maximizeButton) {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        VBox panel = new VBox(8);
        panel.getStyleClass().add("snap-popup");

        Label title = new Label("Ajustar janela");
        title.getStyleClass().add("snap-title");

        GridPane options = new GridPane();
        options.getStyleClass().add("snap-options");
        options.setHgap(8);
        options.setVgap(8);
        options.add(snapButton("Tela cheia", SnapLayout.FULL, stage, windowState, maximizeButton, popup), 0, 0);
        options.add(snapButton("Esquerda", SnapLayout.LEFT, stage, windowState, maximizeButton, popup), 1, 0);
        options.add(snapButton("Direita", SnapLayout.RIGHT, stage, windowState, maximizeButton, popup), 2, 0);
        options.add(snapButton("Sup. esq.", SnapLayout.TOP_LEFT, stage, windowState, maximizeButton, popup), 0, 1);
        options.add(snapButton("Sup. dir.", SnapLayout.TOP_RIGHT, stage, windowState, maximizeButton, popup), 1, 1);
        options.add(snapButton("Inf. esq.", SnapLayout.BOTTOM_LEFT, stage, windowState, maximizeButton, popup), 2, 1);
        options.add(snapButton("Inf. dir.", SnapLayout.BOTTOM_RIGHT, stage, windowState, maximizeButton, popup), 0, 2);

        panel.getChildren().addAll(title, options);
        popup.getContent().add(panel);

        PauseTransition hideDelay = new PauseTransition(Duration.millis(180));
        hideDelay.setOnFinished(event -> hideSnapPopupIfNeeded(popup, panel, maximizeButton));

        maximizeButton.setOnMouseEntered(event -> {
            hideDelay.stop();
            showSnapPopup(popup, maximizeButton);
        });
        maximizeButton.setOnMouseExited(event -> hideDelay.playFromStart());
        panel.setOnMouseEntered(event -> hideDelay.stop());
        panel.setOnMouseExited(event -> hideDelay.playFromStart());
    }

    private static Button snapButton(
            String text,
            SnapLayout layout,
            Stage stage,
            WindowState windowState,
            Button maximizeButton,
            Popup popup) {
        Button button = new Button(text);
        button.getStyleClass().add("snap-option");
        button.setContentDisplay(ContentDisplay.TOP);
        button.setGraphic(snapGraphic(layout));
        button.setOnAction(event -> {
            applySnap(stage, windowState, maximizeButton, layout);
            popup.hide();
            event.consume();
        });
        return button;
    }

    private static GridPane snapGraphic(SnapLayout layout) {
        GridPane graphic = new GridPane();
        graphic.getStyleClass().add("snap-graphic");
        graphic.setHgap(3);
        graphic.setVgap(3);
        graphic.getColumnConstraints().addAll(cellColumn(), cellColumn());
        graphic.getRowConstraints().addAll(cellRow(), cellRow());

        switch (layout) {
            case FULL -> {
                Region cell = snapCell(true, 55, 35);
                GridPane.setColumnSpan(cell, 2);
                GridPane.setRowSpan(cell, 2);
                graphic.add(cell, 0, 0);
            }
            case LEFT -> {
                Region left = snapCell(true, 26, 35);
                Region right = snapCell(false, 26, 35);
                GridPane.setRowSpan(left, 2);
                GridPane.setRowSpan(right, 2);
                graphic.add(left, 0, 0);
                graphic.add(right, 1, 0);
            }
            case RIGHT -> {
                Region left = snapCell(false, 26, 35);
                Region right = snapCell(true, 26, 35);
                GridPane.setRowSpan(left, 2);
                GridPane.setRowSpan(right, 2);
                graphic.add(left, 0, 0);
                graphic.add(right, 1, 0);
            }
            case TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT -> {
                graphic.add(snapCell(layout == SnapLayout.TOP_LEFT, 26, 16), 0, 0);
                graphic.add(snapCell(layout == SnapLayout.TOP_RIGHT, 26, 16), 1, 0);
                graphic.add(snapCell(layout == SnapLayout.BOTTOM_LEFT, 26, 16), 0, 1);
                graphic.add(snapCell(layout == SnapLayout.BOTTOM_RIGHT, 26, 16), 1, 1);
            }
        }

        return graphic;
    }

    private static ColumnConstraints cellColumn() {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setMinWidth(26);
        constraints.setPrefWidth(26);
        return constraints;
    }

    private static RowConstraints cellRow() {
        RowConstraints constraints = new RowConstraints();
        constraints.setMinHeight(16);
        constraints.setPrefHeight(16);
        return constraints;
    }

    private static Region snapCell(boolean active, double width, double height) {
        Region cell = new Region();
        cell.getStyleClass().add("snap-cell");
        if (active) {
            cell.getStyleClass().add("snap-cell-active");
        }
        cell.setMinSize(width, height);
        cell.setPrefSize(width, height);
        return cell;
    }

    private static void showSnapPopup(Popup popup, Button maximizeButton) {
        if (popup.isShowing()) {
            return;
        }

        Bounds buttonBounds = maximizeButton.localToScreen(maximizeButton.getBoundsInLocal());
        popup.show(maximizeButton, buttonBounds.getMaxX() - 284, buttonBounds.getMaxY() + 6);
    }

    private static void hideSnapPopupIfNeeded(Popup popup, VBox panel, Button maximizeButton) {
        if (!panel.isHover() && !maximizeButton.isHover()) {
            popup.hide();
        }
    }

    private static void applySnap(Stage stage, WindowState windowState, Button maximizeButton, SnapLayout layout) {
        if (layout == SnapLayout.FULL) {
            maximize(stage, windowState, maximizeButton);
            return;
        }

        Rectangle2D bounds = currentScreen(stage).getVisualBounds();
        double halfWidth = bounds.getWidth() / 2;
        double halfHeight = bounds.getHeight() / 2;

        double x = switch (layout) {
            case RIGHT, TOP_RIGHT, BOTTOM_RIGHT -> bounds.getMinX() + halfWidth;
            default -> bounds.getMinX();
        };
        double y = switch (layout) {
            case BOTTOM_LEFT, BOTTOM_RIGHT -> bounds.getMinY() + halfHeight;
            default -> bounds.getMinY();
        };
        double width = switch (layout) {
            case LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT -> halfWidth;
            default -> bounds.getWidth();
        };
        double height = switch (layout) {
            case TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT -> halfHeight;
            default -> bounds.getHeight();
        };

        stage.setX(x);
        stage.setY(y);
        stage.setWidth(Math.max(stage.getMinWidth(), width));
        stage.setHeight(Math.max(stage.getMinHeight(), height));
        windowState.maximized = false;
        maximizeButton.setText(MAXIMIZE_TEXT);
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

    private enum SnapLayout {
        FULL,
        LEFT,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
