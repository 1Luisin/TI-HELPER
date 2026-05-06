package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserAccount;
import br.com.scmjf.tihelper.model.UserProfile;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class UsuarioMockService implements UsuarioService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public UsuarioMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public boolean registerUser(String username, String password, UserProfile profile) {
        if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password) || profile == null) {
            return false;
        }

        String normalizedUsername = username.trim().toLowerCase();
        if (store.users().containsKey(normalizedUsername)) {
            return false;
        }

        store.users().put(normalizedUsername, new UserAccount(normalizedUsername, password, profile, null));
        store.persistUsers();
        historicoService.registrar(normalizedUsername, profile, TipoAcao.CADASTRAR_USUARIO, "-", normalizedUsername,
                StatusExecucao.SUCESSO, "-", "Usuario cadastrado no mock local.");
        return true;
    }

    @Override
    public boolean updateUser(String originalUsername, String username, String password, UserProfile profile) {
        if (ValidationUtil.isBlank(originalUsername) || ValidationUtil.isBlank(username) || profile == null) {
            return false;
        }

        String originalKey = originalUsername.trim().toLowerCase();
        String newKey = username.trim().toLowerCase();
        UserAccount existing = store.users().get(originalKey);
        if (existing == null || (!originalKey.equals(newKey) && store.users().containsKey(newKey))) {
            return false;
        }

        String finalPassword = ValidationUtil.isBlank(password) ? existing.getPassword() : password;
        UserAccount updated = new UserAccount(newKey, finalPassword, profile, existing.getProfilePhotoUri());
        if (!originalKey.equals(newKey)) {
            store.users().remove(originalKey);
        }
        store.users().put(newKey, updated);
        store.persistUsers();
        historicoService.registrar(newKey, profile, TipoAcao.ALTERAR_USUARIO, "-", newKey,
                StatusExecucao.SUCESSO, "-", "Usuario alterado no mock local.");
        return true;
    }

    @Override
    public boolean deleteUser(String username) {
        if (ValidationUtil.isBlank(username)) {
            return false;
        }

        String key = username.trim().toLowerCase();
        UserAccount removed = store.users().remove(key);
        if (removed == null) {
            return false;
        }

        store.persistUsers();
        historicoService.registrar(key, removed.getProfile(), TipoAcao.EXCLUIR_USUARIO, "-", key,
                StatusExecucao.SUCESSO, "-", "Usuario excluido do mock local.");
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
            store.persistUsers();
            historicoService.registrar(account.toUser(), TipoAcao.ALTERAR_USUARIO, "-", account.getUsername(),
                    StatusExecucao.SUCESSO, "-", "Foto de perfil alterada.");
        }
    }
}
