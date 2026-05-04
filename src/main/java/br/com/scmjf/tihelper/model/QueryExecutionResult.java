package br.com.scmjf.tihelper.model;

import java.util.List;
import java.util.Map;

public record QueryExecutionResult(List<String> columns, List<Map<String, String>> rows) {
}
