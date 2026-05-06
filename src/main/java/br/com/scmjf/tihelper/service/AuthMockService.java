package br.com.scmjf.tihelper.service;

import java.util.Optional;

import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserAccount;
import br.com.scmjf.tihelper.util.AppLogger;

public class AuthMockService implements AuthService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public AuthMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            historicoService.registrar("desconhecido", null, TipoAcao.LOGIN, "-", "Login", StatusExecucao.ERRO, "-", "Credenciais nao informadas.");
            AppLogger.warning("Tentativa de login sem credenciais.");
            return Optional.empty();
        }

        String normalizedUsername = username.trim().toLowerCase();
        UserAccount account = store.users().get(normalizedUsername);
        if (account == null || !account.getPassword().equals(password)) {
            String loggedUsername = normalizedUsername.isBlank() ? "desconhecido" : normalizedUsername;
            historicoService.registrar(loggedUsername, null, TipoAcao.LOGIN, "-", "Login", StatusExecucao.ERRO, "-", "Usuario ou senha invalidos.");
            AppLogger.warning("Tentativa de login invalida para usuario: " + loggedUsername);
            return Optional.empty();
        }

        User user = account.toUser();
        historicoService.registrar(user, TipoAcao.LOGIN, "-", "Login", StatusExecucao.SUCESSO, "-", "Login mockado efetuado.");
        AppLogger.info("Login efetuado: " + user.getUsername());
        return Optional.of(user);
    }
}
