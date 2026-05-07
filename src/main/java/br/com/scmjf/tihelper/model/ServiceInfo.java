package br.com.scmjf.tihelper.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceInfo {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String server;
    private final String name;
    private final String description;
    private String status;
    private LocalDateTime lastVerification;
    private final boolean restartAllowed;

    public ServiceInfo(String server, String name, String description, String status, LocalDateTime lastVerification) {
        this(server, name, description, status, lastVerification, true);
    }

    public ServiceInfo(String server, String name, String description, String status, LocalDateTime lastVerification, boolean restartAllowed) {
        this.server = server;
        this.name = name;
        this.description = description;
        this.status = status;
        this.lastVerification = lastVerification;
        this.restartAllowed = restartAllowed;
    }

    public String getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastVerification() {
        return lastVerification;
    }

    public boolean isRestartAllowed() {
        return restartAllowed;
    }

    public String getRestartPermissionLabel() {
        return restartAllowed ? "Permitido" : "Bloqueado";
    }

    public void setLastVerification(LocalDateTime lastVerification) {
        this.lastVerification = lastVerification;
    }

    public String getFormattedLastVerification() {
        return lastVerification.format(FORMATTER);
    }
}
