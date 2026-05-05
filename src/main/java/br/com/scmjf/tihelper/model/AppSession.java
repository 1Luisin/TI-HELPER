package br.com.scmjf.tihelper.model;

import java.time.LocalDateTime;

public class AppSession {

    private final User user;
    private final LocalDateTime loginDateTime;

    public AppSession(User user, LocalDateTime loginDateTime) {
        this.user = user;
        this.loginDateTime = loginDateTime;
    }

    public User getUser() {
        return user;
    }

    public UserProfile getProfile() {
        return user == null ? null : user.getProfile();
    }

    public String getProfilePhotoUri() {
        return user == null ? null : user.getProfilePhotoUri();
    }

    public LocalDateTime getLoginDateTime() {
        return loginDateTime;
    }
}
