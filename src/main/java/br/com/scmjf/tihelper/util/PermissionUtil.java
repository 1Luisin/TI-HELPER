package br.com.scmjf.tihelper.util;

import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserModule;
import br.com.scmjf.tihelper.model.UserProfile;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean canAccess(User user, NavigationTarget target) {
        UserProfile profile = user == null ? null : user.getProfile();
        UserModule module = moduleFor(target);
        if (profile == null || module == null || !user.hasModule(module)) {
            return false;
        }

        if (target == NavigationTarget.HISTORY) {
            return profile == UserProfile.ADMIN;
        }
        return isAllowedByProfile(profile, target);
    }

    public static boolean canRunAction(User user, TipoAcao action) {
        UserProfile profile = user == null ? null : user.getProfile();
        if (profile == UserProfile.ADMIN) {
            return hasActionModule(user, action);
        }
        if (profile == UserProfile.OPERADOR_TI) {
            return hasActionModule(user, action)
                    && (action == TipoAcao.TESTAR_CONEXAO
                    || action == TipoAcao.REINICIAR_SERVICO
                    || action == TipoAcao.EXECUTAR_QUERY
                    || action == TipoAcao.EXECUTAR_SCRIPT);
        }
        return false;
    }

    public static boolean canAdmin(User user) {
        return user != null && user.getProfile() == UserProfile.ADMIN;
    }

    public static UserModule moduleFor(NavigationTarget target) {
        return switch (target) {
            case DASHBOARD -> UserModule.DASHBOARD;
            case SERVERS -> UserModule.SERVERS;
            case PANELS -> UserModule.PANELS;
            case SERVICES -> UserModule.SERVICES;
            case QUERIES -> UserModule.QUERIES;
            case SCRIPTS -> UserModule.SCRIPTS;
            case HISTORY -> UserModule.HISTORY;
            case SETTINGS -> UserModule.SETTINGS;
            case DIAGNOSTICS -> UserModule.ABOUT;
        };
    }

    private static boolean isAllowedByProfile(UserProfile profile, NavigationTarget target) {
        if (profile == UserProfile.ADMIN) {
            return true;
        }
        if (profile == UserProfile.OPERADOR_TI) {
            return target != NavigationTarget.SETTINGS
                    && target != NavigationTarget.HISTORY;
        }
        if (profile == UserProfile.CONSULTA) {
            return target == NavigationTarget.DASHBOARD
                    || target == NavigationTarget.SERVERS
                    || target == NavigationTarget.PANELS
                    || target == NavigationTarget.DIAGNOSTICS;
        }
        return false;
    }

    private static boolean hasActionModule(User user, TipoAcao action) {
        if (user == null) {
            return false;
        }
        return switch (action) {
            case TESTAR_CONEXAO -> user.hasModule(UserModule.SERVERS);
            case REINICIAR_SERVICO -> user.hasModule(UserModule.SERVICES);
            case EXECUTAR_QUERY -> user.hasModule(UserModule.QUERIES);
            case EXECUTAR_SCRIPT -> user.hasModule(UserModule.SCRIPTS);
            default -> true;
        };
    }
}
