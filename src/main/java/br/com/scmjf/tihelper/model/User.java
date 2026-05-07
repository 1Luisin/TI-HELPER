package br.com.scmjf.tihelper.model;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class User {

    private final String username;
    private final UserProfile profile;
    private final String fullName;
    private final String email;
    private final String sector;
    private String profilePhotoUri;
    private final Set<UserModule> modules;

    public User(String username, UserProfile profile) {
        this(username, profile, null);
    }

    public User(String username, UserProfile profile, String profilePhotoUri) {
        this(username, profile, "", "", "", profilePhotoUri, UserModule.defaultsFor(profile));
    }

    public User(
            String username,
            UserProfile profile,
            String fullName,
            String email,
            String sector,
            String profilePhotoUri,
            Set<UserModule> modules) {
        this.username = username;
        this.profile = profile;
        this.fullName = fullName == null ? "" : fullName;
        this.email = email == null ? "" : email;
        this.sector = sector == null ? "" : sector;
        this.profilePhotoUri = profilePhotoUri;
        this.modules = modules == null || modules.isEmpty()
                ? EnumSet.noneOf(UserModule.class)
                : EnumSet.copyOf(modules);
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

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getSector() {
        return sector;
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

    public boolean hasModule(UserModule module) {
        return modules.contains(module);
    }

    public String getModulesDisplay() {
        return modules.stream()
                .map(UserModule::getDisplayName)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
