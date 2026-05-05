package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserAccount;
import br.com.scmjf.tihelper.model.UserProfile;

public class UsuarioMockService implements UsuarioService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public UsuarioMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public boolean registerUser(String username, String password, UserProfile profile) {
        if (username == null || password == null || profile == null) {
            return false;
        }

        String normalizedUsername = username.trim().toLowerCase();
        if (normalizedUsername.isBlank() || password.isBlank() || store.users().containsKey(normalizedUsername)) {
            return false;
        }

        store.users().put(normalizedUsername, new UserAccount(normalizedUsername, password, profile, null));
        historicoService.registrar(normalizedUsername, profile, TipoAcao.ALTERAR_USUARIO, "-", normalizedUsername,
                StatusExecucao.SUCESSO, "-", "Usuário cadastrado no mock em memória.");
        return true;
    }

    @Override
    public List<User> getUsers() {
        return store.users().values().stream()
                .map(UserAccount::toUser)
                .toList();
    }

    @Override
    public void updateProfilePhoto(String username, String profilePhotoUri) {
        if (username == null) {
            return;
        }

        UserAccount account = store.users().get(username.trim().toLowerCase());
        if (account != null) {
            account.setProfilePhotoUri(profilePhotoUri);
            historicoService.registrar(account.toUser(), TipoAcao.ALTERAR_USUARIO, "-", account.getUsername(),
                    StatusExecucao.SUCESSO, "-", "Foto de perfil alterada.");
        }
    }
}
