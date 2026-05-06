package br.com.scmjf.tihelper.util;

public enum NavigationTarget {
    DASHBOARD("Dashboard", "dashboard.fxml"),
    SERVERS("Servidores", "servers.fxml"),
    PANELS("Paineis", "panels.fxml"),
    SERVICES("Servicos", "services.fxml"),
    QUERIES("Queries", "queries.fxml"),
    SCRIPTS("Scripts", "scripts.fxml"),
    HISTORY("Historico", "history.fxml"),
    SETTINGS("Configuracoes", "settings.fxml"),
    DIAGNOSTICS("Sobre", "diagnostics.fxml");

    private final String title;
    private final String fxml;

    NavigationTarget(String title, String fxml) {
        this.title = title;
        this.fxml = fxml;
    }

    public String getTitle() {
        return title;
    }

    public String getFxml() {
        return fxml;
    }
}
