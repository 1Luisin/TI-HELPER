package br.com.scmjf.tihelper.util;

import javafx.scene.Node;

public final class UiUtil {

    private UiUtil() {
    }

    public static void setVisibleManaged(Node node, boolean visible) {
        if (node == null) {
            return;
        }

        node.setVisible(visible);
        node.setManaged(visible);
    }
}
