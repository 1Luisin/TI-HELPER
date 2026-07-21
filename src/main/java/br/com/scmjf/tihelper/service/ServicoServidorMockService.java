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
        ActionResult validation = validate(service);
        if (!validation.success()) {
            return validation;
        }

        store.services().add(service);
        store.persistServices();
        historicoService.registrar(user, TipoAcao.CADASTRAR_SERVICO, service.getServer(), service.getName(),
                StatusExecucao.SUCESSO, "-", "Serviço registrado no mock local.");
        return new ActionResult(true, "Serviço registrado no mock local.");
    }

    @Override
    public ActionResult alterar(ServiceInfo originalService, ServiceInfo service, User user) {
        if (originalService == null) {
            return new ActionResult(false, "Selecione um serviço para editar.");
        }
        ActionResult validation = validate(service);
        if (!validation.success()) {
            return validation;
        }

        for (int index = 0; index < store.services().size(); index++) {
            ServiceInfo current = store.services().get(index);
            if (sameService(current, originalService)) {
                store.services().set(index, service);
                store.persistServices();
                historicoService.registrar(user, TipoAcao.ALTERAR_SERVICO, service.getServer(), service.getName(),
                        StatusExecucao.SUCESSO, "-", "Serviço alterado no mock local.");
                return new ActionResult(true, "Serviço alterado no mock local.");
            }
        }
        return new ActionResult(false, "Serviço selecionado não foi encontrado.");
    }

    @Override
    public ActionResult excluir(ServiceInfo service, User user) {
        if (service == null) {
            return new ActionResult(false, "Selecione um serviço para excluir.");
        }

        boolean removed = store.services().removeIf(item -> sameService(item, service));
        if (!removed) {
            return new ActionResult(false, "Serviço selecionado não foi encontrado.");
        }

        store.persistServices();
        historicoService.registrar(user, TipoAcao.EXCLUIR_SERVICO, service.getServer(), service.getName(),
                StatusExecucao.SUCESSO, "-", "Serviço excluído do mock local.");
        return new ActionResult(true, "Serviço excluído do mock local.");
    }

    @Override
    public ActionResult finalizarReinicio(ServiceInfo service, String reason, User user) {
        if (service == null) {
            return new ActionResult(false, "Selecione um serviço para reiniciar.");
        }
        if (ValidationUtil.isBlank(reason)) {
            return new ActionResult(false, "Informe um motivo para reiniciar o serviço.");
        }
        if (!service.isRestartAllowed()) {
            historicoService.registrar(user, TipoAcao.REINICIAR_SERVICO, service.getServer(), service.getName(),
                    StatusExecucao.NEGADO, "-", "Serviço configurado para não permitir reinício.");
            return new ActionResult(false, "Este serviço está configurado para não permitir reinício.");
        }

        service.setStatus("Em execução");
        service.setLastVerification(LocalDateTime.now());
        store.persistServices();
        historicoService.registrar(user, TipoAcao.REINICIAR_SERVICO, service.getServer(), service.getName(),
                StatusExecucao.SIMULADO, reason, "Reinício de serviço simulado.");
        return new ActionResult(true, "Serviço " + service.getName() + " reiniciado com sucesso na simulação.");
    }

    private ActionResult validate(ServiceInfo service) {
        if (service == null || ValidationUtil.isBlank(service.getServer())) {
            return new ActionResult(false, "Informe o servidor do serviço.");
        }
        if (ValidationUtil.isBlank(service.getName())) {
            return new ActionResult(false, "Informe o nome do serviço.");
        }
        return new ActionResult(true, "OK");
    }

    private boolean sameService(ServiceInfo left, ServiceInfo right) {
        return left.getServer().equals(right.getServer()) && left.getName().equals(right.getName());
    }
}
