package br.com.scmjf.tihelper.util;

import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean canAccess(User user, NavigationTarget target) {
        UserProfile profile = user == null ? null : user.getProfile();
        if (profile == UserProfile.ADMIN) {
            return true;
        }
        if (profile == UserProfile.OPERADOR_TI) {
            return target != NavigationTarget.SETTINGS;
        }
        if (profile == UserProfile.CONSULTA) {
            return target == NavigationTarget.DASHBOARD
                    || target == NavigationTarget.SERVERS
                    || target == NavigationTarget.PANELS
                    || target == NavigationTarget.HISTORY;
        }
        return false;
    }

    public static boolean canRunAction(User user, TipoAcao action) {
        UserProfile profile = user == null ? null : user.getProfile();
        if (profile == UserProfile.ADMIN) {
            return true;
        }
        if (profile == UserProfile.OPERADOR_TI) {
            return action == TipoAcao.TESTAR_CONEXAO
                    || action == TipoAcao.REINICIAR_SERVICO
                    || action == TipoAcao.EXECUTAR_QUERY
                    || action == TipoAcao.EXECUTAR_SCRIPT;
        }
        return false;
    }

    public static boolean canAdmin(User user) {
        return user != null && user.getProfile() == UserProfile.ADMIN;
    }
}
