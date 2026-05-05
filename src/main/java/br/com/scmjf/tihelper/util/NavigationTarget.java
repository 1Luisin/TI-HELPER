package br.com.scmjf.tihelper.util;

public enum NavigationTarget {
    DASHBOARD("Dashboard", "dashboard.fxml"),
    SERVERS("Servidores", "servers.fxml"),
    PANELS("Painéis", "panels.fxml"),
    SERVICES("Serviços", "services.fxml"),
    QUERIES("Queries", "queries.fxml"),
    SCRIPTS("Scripts", "scripts.fxml"),
    HISTORY("Histórico", "history.fxml"),
    SETTINGS("Configurações", "settings.fxml");

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
