package br.com.scmjf.tihelper.model;

import java.util.List;

public class QueryDefinition {

    private final String name;
    private final String queryText;
    private final List<String> parameters;

    public QueryDefinition(String name, List<String> parameters) {
        this(name, "", parameters);
    }

    public QueryDefinition(String name, String queryText, List<String> parameters) {
        this.name = name;
        this.queryText = queryText == null ? "" : queryText.trim();
        this.parameters = parameters.stream()
                .map(String::trim)
                .filter(parameter -> !parameter.isBlank())
                .toList();
    }

    public String getName() {
        return name;
    }

    public String getQueryText() {
        return queryText;
    }

    public String getQueryPreview() {
        if (queryText.isBlank()) {
            return "Query mockada";
        }

        String compact = queryText.replaceAll("\\s+", " ").trim();
        if (compact.length() <= 90) {
            return compact;
        }
        return compact.substring(0, 87) + "...";
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getParameterSummary() {
        if (parameters.isEmpty()) {
            return "Sem parâmetros";
        }
        return String.join(", ", parameters);
    }
}
