package br.com.scmjf.tihelper.util;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import br.com.scmjf.tihelper.model.AppSession;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.service.AuthMockService;
import br.com.scmjf.tihelper.service.AuthService;
import br.com.scmjf.tihelper.service.HistoricoMockService;
import br.com.scmjf.tihelper.service.HistoricoService;
import br.com.scmjf.tihelper.service.MockDataStore;
import br.com.scmjf.tihelper.service.PainelMockService;
import br.com.scmjf.tihelper.service.PainelService;
import br.com.scmjf.tihelper.service.QueryMockService;
import br.com.scmjf.tihelper.service.QueryService;
import br.com.scmjf.tihelper.service.ScriptMockService;
import br.com.scmjf.tihelper.service.ScriptService;
import br.com.scmjf.tihelper.service.ServicoServidorMockService;
import br.com.scmjf.tihelper.service.ServicoServidorService;
import br.com.scmjf.tihelper.service.ServidorMockService;
import br.com.scmjf.tihelper.service.ServidorService;
import br.com.scmjf.tihelper.service.UsuarioMockService;
import br.com.scmjf.tihelper.service.UsuarioService;

public final class AppContext {

    private static final MockDataStore STORE = new MockDataStore();
    private static final HistoricoService HISTORICO_SERVICE = new HistoricoMockService(STORE);
    private static final AuthService AUTH_SERVICE = new AuthMockService(STORE, HISTORICO_SERVICE);
    private static final UsuarioService USUARIO_SERVICE = new UsuarioMockService(STORE, HISTORICO_SERVICE);
    private static final ServidorService SERVIDOR_SERVICE = new ServidorMockService(STORE, HISTORICO_SERVICE);
    private static final ServicoServidorService SERVICO_SERVIDOR_SERVICE = new ServicoServidorMockService(STORE, HISTORICO_SERVICE);
    private static final PainelService PAINEL_SERVICE = new PainelMockService(STORE, HISTORICO_SERVICE);
    private static final QueryService QUERY_SERVICE = new QueryMockService(STORE, HISTORICO_SERVICE);
    private static final ScriptService SCRIPT_SERVICE = new ScriptMockService(STORE, HISTORICO_SERVICE);

    private static AppSession session;
    private static Consumer<NavigationTarget> navigationHandler;

    private AppContext() {
    }

    public static AuthService authService() {
        return AUTH_SERVICE;
    }

    public static UsuarioService usuarioService() {
        return USUARIO_SERVICE;
    }

    public static ServidorService servidorService() {
        return SERVIDOR_SERVICE;
    }

    public static ServicoServidorService servicoServidorService() {
        return SERVICO_SERVIDOR_SERVICE;
    }

    public static PainelService painelService() {
        return PAINEL_SERVICE;
    }

    public static QueryService queryService() {
        return QUERY_SERVICE;
    }

    public static ScriptService scriptService() {
        return SCRIPT_SERVICE;
    }

    public static HistoricoService historicoService() {
        return HISTORICO_SERVICE;
    }

    public static Path dataDirectory() {
        return STORE.getDataDirectory();
    }

    public static void restoreDefaultMockData() {
        STORE.restoreDefaults();
        HISTORICO_SERVICE.registrar(getCurrentUser(), TipoAcao.RESTAURAR_DADOS, "-", "Dados mockados",
                StatusExecucao.SUCESSO, "-", "Dados mockados padrao restaurados.");
    }

    public static void exportBackup(Path destination) throws java.io.IOException {
        STORE.exportBackup(destination);
        HISTORICO_SERVICE.registrar(getCurrentUser(), TipoAcao.EXPORTAR_BACKUP, "-", destination.toString(),
                StatusExecucao.SUCESSO, "-", "Backup JSON exportado.");
    }

    public static void importBackup(Path source) throws java.io.IOException {
        STORE.importBackup(source);
        HISTORICO_SERVICE.registrar(getCurrentUser(), TipoAcao.IMPORTAR_BACKUP, "-", source.toString(),
                StatusExecucao.SUCESSO, "-", "Backup JSON importado.");
    }

    public static AppSession getSession() {
        return session;
    }

    public static User getCurrentUser() {
        return session == null ? null : session.getUser();
    }

    public static void startSession(User user) {
        session = new AppSession(user, LocalDateTime.now());
    }

    public static void setNavigationHandler(Consumer<NavigationTarget> navigationHandler) {
        AppContext.navigationHandler = navigationHandler;
    }

    public static void navigateTo(NavigationTarget target) {
        if (navigationHandler != null) {
            navigationHandler.accept(target);
        }
    }

    public static boolean denyAction(TipoAcao tipoAcao, String target, String message) {
        HISTORICO_SERVICE.registrar(getCurrentUser(), tipoAcao, "-", target, StatusExecucao.NEGADO, "-", message);
        AlertUtil.warning("Permissao negada", message);
        return false;
    }

    public static void clearSession() {
        if (session != null) {
            HISTORICO_SERVICE.registrar(session.getUser(), TipoAcao.LOGOUT, "-", "Sessao", StatusExecucao.SUCESSO, "-", "Logout efetuado.");
            AppLogger.info("Logout efetuado: " + session.getUser().getUsername());
        }
        session = null;
        navigationHandler = null;
    }
}
