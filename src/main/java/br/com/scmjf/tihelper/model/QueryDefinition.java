package br.com.scmjf.tihelper.model;

import java.util.List;

public class QueryDefinition {

    private final String name;
    private final List<String> parameters;

    public QueryDefinition(String name, List<String> parameters) {
        this.name = name;
        this.parameters = parameters.stream()
                .map(String::trim)
                .filter(parameter -> !parameter.isBlank())
                .toList();
    }

    public String getName() {
        return name;
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
