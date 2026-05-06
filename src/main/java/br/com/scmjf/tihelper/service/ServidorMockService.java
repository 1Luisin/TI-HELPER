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
        ActionResult validation = validate(server);
        if (!validation.success()) {
            return validation;
        }

        store.servers().add(server);
        store.persistServers();
        historicoService.registrar(user, TipoAcao.CADASTRAR_SERVIDOR, server.getName(), server.getName(),
                StatusExecucao.SUCESSO, "-", "Servidor registrado no mock local.");
        return new ActionResult(true, "Servidor registrado no mock local.");
    }

    @Override
    public ActionResult alterar(String originalName, ServerInfo server, User user) {
        if (ValidationUtil.isBlank(originalName)) {
            return new ActionResult(false, "Selecione um servidor para editar.");
        }
        ActionResult validation = validate(server);
        if (!validation.success()) {
            return validation;
        }

        for (int index = 0; index < store.servers().size(); index++) {
            if (store.servers().get(index).getName().equals(originalName)) {
                store.servers().set(index, server);
                store.persistServers();
                historicoService.registrar(user, TipoAcao.ALTERAR_SERVIDOR, server.getName(), server.getName(),
                        StatusExecucao.SUCESSO, "-", "Servidor alterado no mock local.");
                return new ActionResult(true, "Servidor alterado no mock local.");
            }
        }
        return new ActionResult(false, "Servidor selecionado nao foi encontrado.");
    }

    @Override
    public ActionResult excluir(ServerInfo server, User user) {
        if (server == null) {
            return new ActionResult(false, "Selecione um servidor para excluir.");
        }

        boolean removed = store.servers().removeIf(item -> item.getName().equals(server.getName()));
        if (!removed) {
            return new ActionResult(false, "Servidor selecionado nao foi encontrado.");
        }

        store.persistServers();
        historicoService.registrar(user, TipoAcao.EXCLUIR_SERVIDOR, server.getName(), server.getName(),
                StatusExecucao.SUCESSO, "-", "Servidor excluido do mock local.");
        return new ActionResult(true, "Servidor excluido do mock local.");
    }

    @Override
    public ActionResult testarConexao(ServerInfo server, User user) {
        if (server == null) {
            return new ActionResult(false, "Selecione um servidor para testar a conexao.");
        }

        boolean success = "Online".equalsIgnoreCase(server.getStatus())
                || ("Instavel".equalsIgnoreCase(server.getStatus()) && random.nextBoolean());
        StatusExecucao status = success ? StatusExecucao.SIMULADO : StatusExecucao.ERRO;
        String message = success
                ? "Conexao simulada com " + server.getName() + " concluida com sucesso."
                : "Falha simulada ao conectar em " + server.getName() + ".";
        historicoService.registrar(user, TipoAcao.TESTAR_CONEXAO, server.getName(), server.getHost(), status, "-", message);
        return new ActionResult(success, message);
    }

    private ActionResult validate(ServerInfo server) {
        if (server == null || ValidationUtil.isBlank(server.getName())) {
            return new ActionResult(false, "Informe o nome do servidor.");
        }
        if (ValidationUtil.isBlank(server.getHost())) {
            return new ActionResult(false, "Informe o IP do servidor.");
        }
        if (!ValidationUtil.isValidIpv4(server.getHost())) {
            return new ActionResult(false, "Informe um IPv4 valido para o servidor.");
        }
        return new ActionResult(true, "OK");
    }
}
