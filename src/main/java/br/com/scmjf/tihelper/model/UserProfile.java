package br.com.scmjf.tihelper.model;

public enum UserProfile {
    ADMIN("ADMIN"),
    OPERADOR_TI("OPERADOR TI"),
    CONSULTA("CONSULTA");

    private final String displayName;

    UserProfile(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
