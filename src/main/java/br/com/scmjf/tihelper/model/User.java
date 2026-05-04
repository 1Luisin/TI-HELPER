package br.com.scmjf.tihelper.model;

public class User {

    private final String username;
    private final UserProfile profile;

    public User(String username, UserProfile profile) {
        this.username = username;
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public String getProfileName() {
        return profile.getDisplayName();
    }
}
