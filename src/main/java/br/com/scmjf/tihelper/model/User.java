package br.com.scmjf.tihelper.model;

public class User {

    private final String username;
    private final UserProfile profile;
    private String profilePhotoUri;

    public User(String username, UserProfile profile) {
        this(username, profile, null);
    }

    public User(String username, UserProfile profile, String profilePhotoUri) {
        this.username = username;
        this.profile = profile;
        this.profilePhotoUri = profilePhotoUri;
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

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }
}
