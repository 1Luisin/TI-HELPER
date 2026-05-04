package br.com.scmjf.tihelper.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExecutionHistory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime dateTime;
    private final String username;
    private final String actionType;
    private final String server;
    private final String target;
    private final String status;
    private final String message;

    public ExecutionHistory(
            LocalDateTime dateTime,
            String username,
            String actionType,
            String server,
            String target,
            String status,
            String message) {
        this.dateTime = dateTime;
        this.username = username;
        this.actionType = actionType;
        this.server = server;
        this.target = target;
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFormattedDateTime() {
        return dateTime.format(FORMATTER);
    }

    public String getUsername() {
        return username;
    }

    public String getActionType() {
        return actionType;
    }

    public String getServer() {
        return server;
    }

    public String getTarget() {
        return target;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
