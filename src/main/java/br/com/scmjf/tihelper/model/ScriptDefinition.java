package br.com.scmjf.tihelper.model;

public class ScriptDefinition {

    private final String name;
    private final String sourceType;
    private final String body;
    private final String fileName;

    public ScriptDefinition(String name, String sourceType, String body, String fileName) {
        this.name = name == null ? "" : name.trim();
        this.sourceType = sourceType == null ? "" : sourceType.trim();
        this.body = body == null ? "" : body.trim();
        this.fileName = fileName == null ? "" : fileName.trim();
    }

    public String getName() {
        return name;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getBody() {
        return body;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSourceSummary() {
        if (!fileName.isBlank()) {
            return fileName;
        }
        if (body.isBlank()) {
            return "Mock inicial";
        }

        String compact = body.replaceAll("\\s+", " ").trim();
        if (compact.length() <= 90) {
            return compact;
        }
        return compact.substring(0, 87) + "...";
    }
}
