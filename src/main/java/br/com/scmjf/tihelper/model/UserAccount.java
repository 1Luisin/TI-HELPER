package br.com.scmjf.tihelper.model;

public class UserAccount {

    private final String username;
    private String password;
    private UserProfile profile;
    private String profilePhotoUri;

    public UserAccount(String username, String password, UserProfile profile, String profilePhotoUri) {
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.profilePhotoUri = profilePhotoUri;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public User toUser() {
        return new User(username, profile, profilePhotoUri);
    }
}
