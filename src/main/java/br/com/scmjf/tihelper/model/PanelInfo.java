package br.com.scmjf.tihelper.model;

public class PanelInfo {

    private final String name;
    private final String ipAddress;
    private final String location;
    private final String status;

    public PanelInfo(String name, String ipAddress, String location, String status) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.location = location;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOnline() {
        return "Ligado".equalsIgnoreCase(status);
    }
}
