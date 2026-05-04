package br.com.scmjf.tihelper.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.QueryExecutionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.User;

public class ActionSimulationService {

    private final HistoryService historyService;
    private final Random random = new Random();

    public ActionSimulationService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public ActionResult testConnection(ServerInfo server) {
        if (server == null) {
            return new ActionResult(false, "Selecione um servidor para testar a conexão.");
        }

        boolean success = "Online".equalsIgnoreCase(server.getStatus())
                || ("Instável".equalsIgnoreCase(server.getStatus()) && random.nextBoolean());

        if (success) {
            return new ActionResult(true, "Conexão simulada com " + server.getName() + " concluída com sucesso.");
        }

        return new ActionResult(false, "Falha simulada ao conectar em " + server.getName() + ".");
    }

    public ActionResult restartService(ServiceInfo service, String reason, User user) {
        if (service == null) {
            return new ActionResult(false, "Selecione um serviço para reiniciar.");
        }

        service.setStatus("Em execução");
        service.setLastVerification(LocalDateTime.now());
        historyService.addRecord(
                user,
                "REINICIO_SERVICO",
                service.getServer(),
                service.getName(),
                "SUCESSO",
                "Reinício simulado. Motivo: " + reason);

        return new ActionResult(true, "Serviço " + service.getName() + " reiniciado com sucesso na simulação.");
    }

    public QueryExecutionResult executeQuery(String queryName, Map<String, String> parameters, User user) {
        QueryExecutionResult result = switch (queryName) {
            case "Buscar paciente por CPF" -> patientByCpf(parameters);
            case "Buscar atendimento por número" -> appointmentByNumber(parameters);
            case "Consultar respostas de pesquisa" -> surveyAnswers(parameters);
            case "Verificar integração pendente" -> pendingIntegration(parameters);
            default -> new QueryExecutionResult(List.of("Mensagem"), List.of(row("Mensagem", "Consulta não reconhecida.")));
        };

        historyService.addRecord(
                user,
                "QUERY",
                "-",
                queryName,
                "SUCESSO",
                "Consulta simulada executada com " + result.rows().size() + " registro(s).");

        return result;
    }

    public ActionResult executeScript(String scriptName, String server, String reason, User user) {
        historyService.addRecord(
                user,
                "SCRIPT",
                server,
                scriptName,
                "SUCESSO",
                "Script simulado executado. Motivo: " + reason);

        return new ActionResult(true, "Script " + scriptName + " executado com sucesso na simulação.");
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
