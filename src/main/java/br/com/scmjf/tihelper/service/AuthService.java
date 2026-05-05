package br.com.scmjf.tihelper.service;

import java.util.Optional;

import br.com.scmjf.tihelper.model.User;

public interface AuthService {

    Optional<User> login(String username, String password);
}
