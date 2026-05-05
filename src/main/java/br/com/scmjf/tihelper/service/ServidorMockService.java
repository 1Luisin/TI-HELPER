package br.com.scmjf.tihelper.service;

import java.util.List;
import java.util.Random;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class ServidorMockService implements ServidorService {

    private final MockDataStore store;
    private final HistoricoService historicoService;
    private final Random random = new Random();

    public ServidorMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public List<ServerInfo> listar() {
        return List.copyOf(store.servers());
    }

    @Override
    public ActionResult cadastrar(ServerInfo server, User user) {
        if (server == null || ValidationUtil.isBlank(server.getName())) {
            return new ActionResult(false, "Informe o nome do servidor.");
        }
        if (ValidationUtil.isBlank(server.getHost())) {
            return new ActionResult(false, "Informe o IP do servidor.");
        }
        if (!ValidationUtil.isValidIpv4(server.getHost())) {
            return new ActionResult(false, "Informe um IPv4 válido para o servidor.");
        }

        store.servers().add(server);
        historicoService.registrar(user, TipoAcao.CADASTRAR_SERVIDOR, server.getName(), server.getName(),
                StatusExecucao.SUCESSO, "-", "Servidor registrado no mock em memória.");
        return new ActionResult(true, "Servidor registrado no mock em memória.");
    }

    @Override
    public ActionResult testarConexao(ServerInfo server, User user) {
        if (server == null) {
            return new ActionResult(false, "Selecione um servidor para testar a conexão.");
        }

        boolean success = "Online".equalsIgnoreCase(server.getStatus())
                || ("Instável".equalsIgnoreCase(server.getStatus()) && random.nextBoolean());
        StatusExecucao status = success ? StatusExecucao.SIMULADO : StatusExecucao.ERRO;
        String message = success
                ? "Conexão simulada com " + server.getName() + " concluída com sucesso."
                : "Falha simulada ao conectar em " + server.getName() + ".";
        historicoService.registrar(user, TipoAcao.TESTAR_CONEXAO, server.getName(), server.getHost(), status, "-", message);
        return new ActionResult(success, message);
    }
}
