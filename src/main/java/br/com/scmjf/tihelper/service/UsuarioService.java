package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public interface UsuarioService {

    boolean registerUser(String username, String password, UserProfile profile);

    boolean updateUser(String originalUsername, String username, String password, UserProfile profile);

    boolean deleteUser(String username);

    List<User> getUsers();

    void updateProfilePhoto(String username, String profilePhotoUri);
}
