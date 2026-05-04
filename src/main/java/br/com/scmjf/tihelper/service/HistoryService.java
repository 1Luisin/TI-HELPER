package br.com.scmjf.tihelper.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.User;

public class HistoryService {

    private final List<ExecutionHistory> records = new ArrayList<>();

    public HistoryService() {
        seedInitialRecords();
    }

    public void addRecord(User user, String actionType, String server, String target, String status, String message) {
        String username = user == null ? "sistema" : user.getUsername();
        records.add(0, new ExecutionHistory(LocalDateTime.now(), username, actionType, server, target, status, message));
    }

    public List<ExecutionHistory> getAll() {
        return List.copyOf(records);
    }

    public List<ExecutionHistory> getLatest(int limit) {
        return records.stream()
                .limit(limit)
                .toList();
    }

    public long countToday() {
        LocalDate today = LocalDate.now();
        return records.stream()
                .filter(record -> record.getDateTime().toLocalDate().equals(today))
                .count();
    }

    public long countFailuresToday() {
        LocalDate today = LocalDate.now();
        return records.stream()
                .filter(record -> record.getDateTime().toLocalDate().equals(today))
                .filter(record -> !"SUCESSO".equalsIgnoreCase(record.getStatus()))
                .count();
    }

    private void seedInitialRecords() {
        records.add(new ExecutionHistory(
                LocalDateTime.now().minusMinutes(18),
                "ti",
                "QUERY",
                "-",
                "Buscar paciente por CPF",
                "SUCESSO",
                "Consulta simulada retornou 1 registro."));
        records.add(new ExecutionHistory(
                LocalDateTime.now().minusHours(1),
                "admin",
                "SCRIPT",
                "SRV-APP-01",
                "Limpar pasta temporária",
                "SUCESSO",
                "Execução mockada concluída."));
        records.add(new ExecutionHistory(
                LocalDateTime.now().minusHours(2),
                "ti",
                "REINICIO_SERVICO",
                "SRV-INT-01",
                "IntegradorSCMJF",
                "FALHA",
                "Serviço indisponível na simulação."));
        records.add(new ExecutionHistory(
                LocalDateTime.now().minusDays(1),
                "consulta",
                "QUERY",
                "-",
                "Consultar respostas de pesquisa",
                "SUCESSO",
                "Consulta simulada retornou 12 registros."));
    }
}
