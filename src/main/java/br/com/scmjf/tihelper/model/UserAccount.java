package br.com.scmjf.tihelper.model;

import java.util.EnumSet;
import java.util.Set;

public class UserAccount {

    private final String username;
    private String password;
    private UserProfile profile;
    private String fullName;
    private String email;
    private String sector;
    private String profilePhotoUri;
    private Set<UserModule> modules;

    public UserAccount(String username, String password, UserProfile profile, String profilePhotoUri) {
        this(username, password, profile, "", "", "", profilePhotoUri, UserModule.defaultsFor(profile));
    }

    public UserAccount(
            String username,
            String password,
            UserProfile profile,
            String fullName,
            String email,
            String sector,
            String profilePhotoUri,
            Set<UserModule> modules) {
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.fullName = fullName == null ? "" : fullName;
        this.email = email == null ? "" : email;
        this.sector = sector == null ? "" : sector;
        this.profilePhotoUri = profilePhotoUri;
        setModules(modules == null ? UserModule.defaultsFor(profile) : modules);
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? "" : fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector == null ? "" : sector;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public Set<UserModule> getModules() {
        return Set.copyOf(modules);
    }

    public void setModules(Set<UserModule> modules) {
        this.modules = modules == null || modules.isEmpty()
                ? EnumSet.noneOf(UserModule.class)
                : EnumSet.copyOf(modules);
    }

    public User toUser() {
        return new User(username, profile, fullName, email, sector, profilePhotoUri, modules);
    }
}
