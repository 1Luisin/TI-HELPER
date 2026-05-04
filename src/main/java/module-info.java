module br.com.scmjf.tihelper {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;

    opens br.com.scmjf.tihelper.controller to javafx.fxml;
    opens br.com.scmjf.tihelper.model to javafx.base;

    exports br.com.scmjf.tihelper;
}
