package br.com.scmjf.tihelper.model;

import java.util.EnumSet;
import java.util.Set;

public enum UserModule {
    DASHBOARD("Dashboard"),
    SERVERS("Servidores"),
    PANELS("Painéis"),
    SERVICES("Serviços"),
    QUERIES("Queries"),
    SCRIPTS("Scripts"),
    HISTORY("Histórico"),
    SETTINGS("Configurações"),
    ABOUT("Sobre");

    private final String displayName;

    UserModule(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Set<UserModule> defaultsFor(UserProfile profile) {
        if (profile == UserProfile.ADMIN) {
            return EnumSet.allOf(UserModule.class);
        }
        if (profile == UserProfile.OPERADOR_TI) {
            return EnumSet.of(DASHBOARD, SERVERS, PANELS, SERVICES, QUERIES, SCRIPTS, ABOUT);
        }
        if (profile == UserProfile.CONSULTA) {
            return EnumSet.of(DASHBOARD, SERVERS, PANELS, ABOUT);
        }
        return EnumSet.noneOf(UserModule.class);
    }
}
