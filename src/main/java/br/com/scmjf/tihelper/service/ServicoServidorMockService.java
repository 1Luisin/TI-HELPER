package br.com.scmjf.tihelper.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class ServicoServidorMockService implements ServicoServidorService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public ServicoServidorMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public List<ServiceInfo> listar() {
        return List.copyOf(store.services());
    }

    @Override
    public ActionResult cadastrar(ServiceInfo service, User user) {
        if (service == null || ValidationUtil.isBlank(service.getServer())) {
            return new ActionResult(false, "Informe o servidor do serviço.");
        }
        if (ValidationUtil.isBlank(service.getName())) {
            return new ActionResult(false, "Informe o nome do serviço.");
        }

        store.services().add(service);
        historicoService.registrar(user, TipoAcao.CADASTRAR_SERVICO, service.getServer(), service.getName(),
                StatusExecucao.SUCESSO, "-", "Serviço registrado no mock em memória.");
        return new ActionResult(true, "Serviço registrado no mock em memória.");
    }

    @Override
    public ActionResult finalizarReinicio(ServiceInfo service, String reason, User user) {
        if (service == null) {
            return new ActionResult(false, "Selecione um serviço para reiniciar.");
        }
        if (ValidationUtil.isBlank(reason)) {
            return new ActionResult(false, "Informe um motivo para reiniciar o serviço.");
        }

        service.setStatus("Em execução");
        service.setLastVerification(LocalDateTime.now());
        historicoService.registrar(user, TipoAcao.REINICIAR_SERVICO, service.getServer(), service.getName(),
                StatusExecucao.SIMULADO, reason, "Reinício de serviço simulado.");
        return new ActionResult(true, "Serviço " + service.getName() + " reiniciado com sucesso na simulação.");
    }
}
