package br.com.scmjf.tihelper.util;

import java.util.Arrays;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class TableUtil {

    private static final double TABLE_PADDING = 20;

    private TableUtil() {
    }

    public static void bindColumnWidths(TableView<?> table, double[] weights, TableColumn<?, ?>... columns) {
        if (weights.length != columns.length) {
            throw new IllegalArgumentException("A quantidade de pesos deve acompanhar a quantidade de colunas.");
        }

        double totalWeight = Arrays.stream(weights).sum();
        for (int index = 0; index < columns.length; index++) {
            TableColumn<?, ?> column = columns[index];
            column.prefWidthProperty().bind(table.widthProperty()
                    .subtract(TABLE_PADDING)
                    .multiply(weights[index] / totalWeight));
        }
    }
}
