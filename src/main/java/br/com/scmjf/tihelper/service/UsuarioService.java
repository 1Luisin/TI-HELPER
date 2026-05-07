package br.com.scmjf.tihelper.service;

import java.util.List;
import java.util.Set;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserModule;
import br.com.scmjf.tihelper.model.UserProfile;

public interface UsuarioService {

    default boolean registerUser(String username, String password, UserProfile profile) {
        return registerUser(username, password, profile, "", "", "", UserModule.defaultsFor(profile));
    }

    boolean registerUser(
            String username,
            String password,
            UserProfile profile,
            String fullName,
            String email,
            String sector,
            Set<UserModule> modules);

    default boolean updateUser(String originalUsername, String username, String password, UserProfile profile) {
        return updateUser(originalUsername, username, password, profile, "", "", "", UserModule.defaultsFor(profile));
    }

    boolean updateUser(
            String originalUsername,
            String username,
            String password,
            UserProfile profile,
            String fullName,
            String email,
            String sector,
            Set<UserModule> modules);

    boolean deleteUser(String username);

    List<User> getUsers();

    void updateProfilePhoto(String username, String profilePhotoUri);
}
