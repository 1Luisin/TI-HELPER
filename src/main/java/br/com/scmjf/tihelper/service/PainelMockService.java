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

        store.panels().add(panel);
        historicoService.registrar(user, TipoAcao.CADASTRAR_PAINEL, panel.getIpAddress(), panel.getName(),
                StatusExecucao.SUCESSO, "-", "Painel registrado no mock em memória.");
        return new ActionResult(true, "Painel registrado no mock em memória.");
    }
}
