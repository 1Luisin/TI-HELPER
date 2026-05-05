package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.util.ValidationUtil;

public class ScriptMockService implements ScriptService {

    private final MockDataStore store;
    private final HistoricoService historicoService;

    public ScriptMockService(MockDataStore store, HistoricoService historicoService) {
        this.store = store;
        this.historicoService = historicoService;
    }

    @Override
    public List<String> listarNomes() {
        return store.scripts().stream()
                .map(ScriptDefinition::getName)
                .toList();
    }

    @Override
    public List<ScriptDefinition> listarDefinicoes() {
        return List.copyOf(store.scripts());
    }

    @Override
    public ActionResult cadastrar(ScriptDefinition script, User user) {
        if (script == null || ValidationUtil.isBlank(script.getName())) {
            return new ActionResult(false, "Informe o nome do script.");
        }
        if (ValidationUtil.isBlank(script.getBody()) && ValidationUtil.isBlank(script.getFileName())) {
            return new ActionResult(false, "Informe o corpo do script ou selecione um arquivo.");
        }

        store.scripts().add(script);
        historicoService.registrar(user, TipoAcao.CADASTRAR_SCRIPT, "-", script.getName(),
                StatusExecucao.SUCESSO, "-", "Script registrado no mock em memória.");
        return new ActionResult(true, "Script registrado no mock em memória.");
    }

    @Override
    public ActionResult executar(String scriptName, String server, String reason, User user) {
        if (ValidationUtil.isBlank(reason)) {
            return new ActionResult(false, "Informe o motivo da execução.");
        }

        historicoService.registrar(user, TipoAcao.EXECUTAR_SCRIPT, server, scriptName, StatusExecucao.SIMULADO, reason,
                "Script simulado executado.");
        return new ActionResult(true, "Script " + scriptName + " executado com sucesso na simulação.");
    }
}
