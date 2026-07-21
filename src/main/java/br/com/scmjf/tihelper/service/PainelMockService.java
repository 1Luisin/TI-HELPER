package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class PainelMockService implements PainelService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public PainelMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public List<PanelInfo> listar() {
        return List.copyOf(store.panels());
    }

    @Override
    public ActionResult cadastrar(PanelInfo panel, User user) {
        ActionResult validation = validate(panel);
        if (!validation.success()) {
            return validation;
        }

        store.panels().add(panel);
        store.persistPanels();
        historicoService.registrar(user, TipoAcao.CADASTRAR_PAINEL, panel.getIpAddress(), panel.getName(),
                StatusExecucao.SUCESSO, "-", "Painel registrado no mock local.");
        return new ActionResult(true, "Painel registrado no mock local.");
    }

    @Override
    public ActionResult alterar(String originalName, PanelInfo panel, User user) {
        if (ValidationUtil.isBlank(originalName)) {
            return new ActionResult(false, "Selecione um painel para editar.");
        }
        ActionResult validation = validate(panel);
        if (!validation.success()) {
            return validation;
        }

        for (int index = 0; index < store.panels().size(); index++) {
            if (store.panels().get(index).getName().equals(originalName)) {
                store.panels().set(index, panel);
                store.persistPanels();
                historicoService.registrar(user, TipoAcao.ALTERAR_PAINEL, panel.getIpAddress(), panel.getName(),
                        StatusExecucao.SUCESSO, "-", "Painel alterado no mock local.");
                return new ActionResult(true, "Painel alterado no mock local.");
            }
        }
        return new ActionResult(false, "Painel selecionado não foi encontrado.");
    }

    @Override
    public ActionResult excluir(PanelInfo panel, User user) {
        if (panel == null) {
            return new ActionResult(false, "Selecione um painel para excluir.");
        }

        boolean removed = store.panels().removeIf(item -> item.getName().equals(panel.getName()));
        if (!removed) {
            return new ActionResult(false, "Painel selecionado não foi encontrado.");
        }

        store.persistPanels();
        historicoService.registrar(user, TipoAcao.EXCLUIR_PAINEL, panel.getIpAddress(), panel.getName(),
                StatusExecucao.SUCESSO, "-", "Painel excluído do mock local.");
        return new ActionResult(true, "Painel excluído do mock local.");
    }

    private ActionResult validate(PanelInfo panel) {
        if (panel == null || ValidationUtil.isBlank(panel.getName())) {
            return new ActionResult(false, "Informe o nome do painel.");
        }
        if (ValidationUtil.isBlank(panel.getIpAddress())) {
            return new ActionResult(false, "Informe o IP do painel.");
        }
        if (!ValidationUtil.isValidIpv4(panel.getIpAddress())) {
            return new ActionResult(false, "Informe um IPv4 válido para o painel.");
        }
        if (ValidationUtil.isBlank(panel.getLocation())) {
            return new ActionResult(false, "Informe o local do painel.");
        }
        return new ActionResult(true, "OK");
    }
}
