package br.com.scmjf.tihelper.service;

import java.util.Map;
import java.util.Optional;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public class AuthService {

    private final Map<String, UserCredential> users = Map.of(
            "admin", new UserCredential("admin", UserProfile.ADMIN),
            "ti", new UserCredential("ti", UserProfile.OPERADOR_TI),
            "consulta", new UserCredential("consulta", UserProfile.CONSULTA));

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        UserCredential credential = users.get(username.trim().toLowerCase());
        if (credential == null || !credential.password().equals(password)) {
            return Optional.empty();
        }

        return Optional.of(new User(username.trim().toLowerCase(), credential.profile()));
    }

    private record UserCredential(String password, UserProfile profile) {
    }
}
