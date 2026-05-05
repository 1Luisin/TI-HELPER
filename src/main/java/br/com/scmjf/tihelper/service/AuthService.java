package br.com.scmjf.tihelper.service;

import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public class AuthService {

    private final Map<String, UserCredential> users = new LinkedHashMap<>();

    public AuthService() {
        registerUser("admin", "admin", UserProfile.ADMIN);
        registerUser("ti", "ti", UserProfile.OPERADOR_TI);
        registerUser("consulta", "consulta", UserProfile.CONSULTA);
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        UserCredential credential = users.get(username.trim().toLowerCase());
        if (credential == null || !credential.password().equals(password)) {
            return Optional.empty();
        }

        return Optional.of(new User(username.trim().toLowerCase(), credential.profile(), credential.profilePhotoUri()));
    }

    public boolean registerUser(String username, String password, UserProfile profile) {
        if (username == null || password == null || profile == null) {
            return false;
        }

        String normalizedUsername = username.trim().toLowerCase();
        if (normalizedUsername.isBlank() || password.isBlank() || users.containsKey(normalizedUsername)) {
            return false;
        }

        users.put(normalizedUsername, new UserCredential(password, profile, null));
        return true;
    }

    public List<User> getUsers() {
        return users.entrySet().stream()
                .map(entry -> new User(entry.getKey(), entry.getValue().profile(), entry.getValue().profilePhotoUri()))
                .toList();
    }

    public void updateProfilePhoto(String username, String profilePhotoUri) {
        if (username == null) {
            return;
        }

        String normalizedUsername = username.trim().toLowerCase();
        UserCredential credential = users.get(normalizedUsername);
        if (credential != null) {
            users.put(normalizedUsername, new UserCredential(credential.password(), credential.profile(), profilePhotoUri));
        }
    }

    private record UserCredential(String password, UserProfile profile, String profilePhotoUri) {
    }
}
