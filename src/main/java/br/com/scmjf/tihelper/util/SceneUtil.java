package br.com.scmjf.tihelper.util;

import java.util.Objects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class SceneUtil {

    private static final String STYLESHEET = "/br/com/scmjf/tihelper/styles/app.css";
    private static final String APP_ICON = "/br/com/scmjf/tihelper/assets/TIHELPER.png";

    private SceneUtil() {
    }

    public static Scene createScene(Stage stage, Parent content, double width, double height) {
        applyApplicationIcon(stage);
        Scene scene = new Scene(WindowChrome.wrap(stage, content), width, height);
        scene.getStylesheets().add(Objects.requireNonNull(SceneUtil.class.getResource(STYLESHEET)).toExternalForm());
        return scene;
    }

    private static void applyApplicationIcon(Stage stage) {
        if (!stage.getIcons().isEmpty()) {
            return;
        }

        stage.getIcons().add(new Image(Objects.requireNonNull(SceneUtil.class.getResourceAsStream(APP_ICON))));
    }
}
