module br.com.scmjf.tihelper {
    requires java.desktop;
    requires java.logging;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;

    opens br.com.scmjf.tihelper.controller to javafx.fxml;
    opens br.com.scmjf.tihelper.model to javafx.base;
    opens br.com.scmjf.tihelper.persistence to com.fasterxml.jackson.databind;

    exports br.com.scmjf.tihelper;
}
