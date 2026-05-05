package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.model.UserProfile;

public interface HistoricoService {

    void registrar(User user, TipoAcao tipoAcao, String server, String target, StatusExecucao status, String reason, String message);

    void registrar(String username, UserProfile profile, TipoAcao tipoAcao, String server, String target, StatusExecucao status, String reason, String message);

    List<ExecutionHistory> listarTodos();

    List<ExecutionHistory> listarUltimos(int limit);

    long contarHoje();

    long contarFalhasHoje();
}
