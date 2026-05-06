package br.com.scmjf.tihelper.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public class HistoricoMockService implements HistoricoService {

    private final MockDataStore store;

    public HistoricoMockService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public void registrar(User user, TipoAcao tipoAcao, String server, String target, StatusExecucao status, String reason, String message) {
        registrar(
                user == null ? "sistema" : user.getUsername(),
                user == null ? null : user.getProfile(),
                tipoAcao,
                server,
                target,
                status,
                reason,
                message);
    }

    @Override
    public void registrar(String username, UserProfile profile, TipoAcao tipoAcao, String server, String target, StatusExecucao status, String reason, String message) {
        store.history().add(0, new ExecutionHistory(
                store.nextHistoryId(),
                LocalDateTime.now(),
                username,
                profile,
                tipoAcao,
                server,
                target,
                status,
                reason,
                message));
        store.persistHistory();
    }

    @Override
    public List<ExecutionHistory> listarTodos() {
        return List.copyOf(store.history());
    }

    @Override
    public List<ExecutionHistory> listarUltimos(int limit) {
        return store.history().stream()
                .limit(limit)
                .toList();
    }

    @Override
    public long contarHoje() {
        LocalDate today = LocalDate.now();
        return store.history().stream()
                .filter(record -> record.getDateTime().toLocalDate().equals(today))
                .count();
    }

    @Override
    public long contarFalhasHoje() {
        LocalDate today = LocalDate.now();
        return store.history().stream()
                .filter(record -> record.getDateTime().toLocalDate().equals(today))
                .filter(record -> record.getStatusExecucao() == StatusExecucao.ERRO
                        || record.getStatusExecucao() == StatusExecucao.NEGADO)
                .count();
    }
}
