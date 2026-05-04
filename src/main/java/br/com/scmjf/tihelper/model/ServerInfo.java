package br.com.scmjf.tihelper.model;

public class ServerInfo {

    private final String name;
    private final String host;
    private final String operatingSystem;
    private final String environment;
    private final String status;

    public ServerInfo(String name, String host, String operatingSystem, String environment, String status) {
        this.name = name;
        this.host = host;
        this.operatingSystem = operatingSystem;
        this.environment = environment;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getStatus() {
        return status;
    }
}
