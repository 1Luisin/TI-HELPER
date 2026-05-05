package br.com.scmjf.tihelper.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExecutionHistory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final long id;
    private final LocalDateTime dateTime;
    private final String username;
    private final UserProfile profile;
    private final TipoAcao tipoAcao;
    private final String server;
    private final String target;
    private final StatusExecucao status;
    private final String reason;
    private final String message;

    public ExecutionHistory(
            long id,
            LocalDateTime dateTime,
            String username,
            UserProfile profile,
            TipoAcao tipoAcao,
            String server,
            String target,
            StatusExecucao status,
            String reason,
            String message) {
        this.id = id;
        this.dateTime = dateTime;
        this.username = normalize(username, "sistema");
        this.profile = profile;
        this.tipoAcao = tipoAcao;
        this.server = normalize(server, "-");
        this.target = normalize(target, "-");
        this.status = status;
        this.reason = normalize(reason, "-");
        this.message = normalize(message, "-");
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getDataHora() {
        return dateTime;
    }

    public String getFormattedDateTime() {
        return dateTime.format(FORMATTER);
    }

    public String getUsername() {
        return username;
    }

    public String getPerfil() {
        return profile == null ? "-" : profile.getDisplayName();
    }

    public UserProfile getProfile() {
        return profile;
    }

    public TipoAcao getTipoAcao() {
        return tipoAcao;
    }

    public String getActionType() {
        return tipoAcao == null ? "-" : tipoAcao.getDisplayName();
    }

    public String getServer() {
        return server;
    }

    public String getTarget() {
        return target;
    }

    public StatusExecucao getStatusExecucao() {
        return status;
    }

    public String getStatus() {
        return status == null ? "-" : status.getDisplayName();
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
