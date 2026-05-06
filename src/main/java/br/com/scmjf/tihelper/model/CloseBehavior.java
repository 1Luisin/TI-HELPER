package br.com.scmjf.tihelper.model;

public enum CloseBehavior {
    ASK("Perguntar ao fechar"),
    MINIMIZE_TO_TRAY("Ficar em segundo plano"),
    EXIT_APPLICATION("Fechar totalmente");

    private final String displayName;

    CloseBehavior(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
