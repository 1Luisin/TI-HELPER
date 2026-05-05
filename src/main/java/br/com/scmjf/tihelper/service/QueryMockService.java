package br.com.scmjf.tihelper.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.QueryExecutionResult;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class QueryMockService implements QueryService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public QueryMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public List<String> listarNomes() {
        return store.queries().stream()
                .map(QueryDefinition::getName)
                .toList();
    }

    @Override
    public List<QueryDefinition> listarDefinicoes() {
        return List.copyOf(store.queries());
    }

    @Override
    public List<String> listarParametros(String queryName) {
        return store.queries().stream()
                .filter(query -> query.getName().equals(queryName))
                .findFirst()
                .map(QueryDefinition::getParameters)
                .orElse(List.of());
    }

    @Override
    public ActionResult cadastrar(QueryDefinition query, User user) {
        if (query == null || ValidationUtil.isBlank(query.getName())) {
            return new ActionResult(false, "Informe o nome da query.");
        }
        if (ValidationUtil.isBlank(query.getQueryText())) {
            return new ActionResult(false, "Informe o SQL da query.");
        }

        store.queries().add(query);
        historicoService.registrar(user, TipoAcao.CADASTRAR_QUERY, "-", query.getName(),
                StatusExecucao.SUCESSO, "-", "Query registrada no mock em memória.");
        return new ActionResult(true, "Query registrada no mock em memória.");
    }

    @Override
    public QueryExecutionResult executar(String queryName, Map<String, String> parameters, User user) {
        QueryExecutionResult result = switch (queryName) {
            case "Buscar paciente por CPF" -> patientByCpf(parameters);
            case "Buscar atendimento por número" -> appointmentByNumber(parameters);
            case "Consultar respostas de pesquisa" -> surveyAnswers(parameters);
            case "Verificar integração pendente" -> pendingIntegration(parameters);
            default -> genericRegisteredQuery(queryName, parameters);
        };

        historicoService.registrar(user, TipoAcao.EXECUTAR_QUERY, "-", queryName, StatusExecucao.SIMULADO, "-",
                "Consulta simulada executada com " + result.rows().size() + " registro(s).");
        return result;
    }

    private QueryExecutionResult patientByCpf(Map<String, String> parameters) {
        String cpf = parameters.getOrDefault("CPF", "000.000.000-00");
        return new QueryExecutionResult(
                List.of("CPF", "Paciente", "Status", "Último atendimento"),
                List.of(row(
                        "CPF", cpf.isBlank() ? "123.456.789-00" : cpf,
                        "Paciente", "Maria de Lourdes Silva",
                        "Status", "Ativo",
                        "Último atendimento", "02/05/2026")));
    }

    private QueryExecutionResult appointmentByNumber(Map<String, String> parameters) {
        String number = parameters.getOrDefault("Número", "A-2026-0001");
        return new QueryExecutionResult(
                List.of("Atendimento", "Paciente", "Unidade", "Situação"),
                List.of(row(
                        "Atendimento", number.isBlank() ? "A-2026-0001" : number,
                        "Paciente", "João Carlos Pereira",
                        "Unidade", "Central SCMJF",
                        "Situação", "Em análise")));
    }

    private QueryExecutionResult surveyAnswers(Map<String, String> parameters) {
        String initialDate = parameters.getOrDefault("Data inicial", "01/05/2026");
        String finalDate = parameters.getOrDefault("Data final", "04/05/2026");
        return new QueryExecutionResult(
                List.of("Período", "Pesquisa", "Respostas", "Satisfação"),
                List.of(
                        row("Período", initialDate + " a " + finalDate, "Pesquisa", "Atendimento presencial", "Respostas", "42", "Satisfação", "92%"),
                        row("Período", initialDate + " a " + finalDate, "Pesquisa", "Totem tablet", "Respostas", "18", "Satisfação", "88%")));
    }

    private QueryExecutionResult pendingIntegration(Map<String, String> parameters) {
        String integration = parameters.getOrDefault("Integração", "Pesquisa Tablet");
        return new QueryExecutionResult(
                List.of("Integração", "Fila", "Pendências", "Última tentativa"),
                List.of(
                        row("Integração", integration.isBlank() ? "Pesquisa Tablet" : integration, "Fila", "respostas_pesquisa", "Pendências", "3", "Última tentativa", "04/05/2026 13:55"),
                        row("Integração", "SCMJF Core", "Fila", "atendimentos", "Pendências", "0", "Última tentativa", "04/05/2026 14:02")));
    }

    private QueryExecutionResult genericRegisteredQuery(String queryName, Map<String, String> parameters) {
        String parameterSummary = parameters.isEmpty()
                ? "Sem parâmetros"
                : parameters.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + (entry.getValue().isBlank() ? "(vazio)" : entry.getValue()))
                        .reduce((left, right) -> left + "; " + right)
                        .orElse("Sem parâmetros");

        return new QueryExecutionResult(
                List.of("Query", "Parâmetros", "Resultado", "Status"),
                List.of(row(
                        "Query", queryName,
                        "Parâmetros", parameterSummary,
                        "Resultado", "Execução mockada",
                        "Status", "SIMULADO")));
    }

    private Map<String, String> row(String key, String value) {
        return Map.of(key, value);
    }

    private Map<String, String> row(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put(key1, value1);
        row.put(key2, value2);
        row.put(key3, value3);
        row.put(key4, value4);
        return row;
    }
}
