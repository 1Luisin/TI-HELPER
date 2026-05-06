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
        ActionResult validation = validate(script);
        if (!validation.success()) {
            return validation;
        }

        store.scripts().add(script);
        store.persistScripts();
        historicoService.registrar(user, TipoAcao.CADASTRAR_SCRIPT, "-", script.getName(),
                StatusExecucao.SUCESSO, "-", "Script registrado no mock local.");
        return new ActionResult(true, "Script registrado no mock local.");
    }

    @Override
    public ActionResult alterar(String originalName, ScriptDefinition script, User user) {
        if (ValidationUtil.isBlank(originalName)) {
            return new ActionResult(false, "Selecione um script para editar.");
        }
        ActionResult validation = validate(script);
        if (!validation.success()) {
            return validation;
        }

        for (int index = 0; index < store.scripts().size(); index++) {
            if (store.scripts().get(index).getName().equals(originalName)) {
                store.scripts().set(index, script);
                store.persistScripts();
                historicoService.registrar(user, TipoAcao.ALTERAR_SCRIPT, "-", script.getName(),
                        StatusExecucao.SUCESSO, "-", "Script alterado no mock local.");
                return new ActionResult(true, "Script alterado no mock local.");
            }
        }
        return new ActionResult(false, "Script selecionado nao foi encontrado.");
    }

    @Override
    public ActionResult excluir(ScriptDefinition script, User user) {
        if (script == null) {
            return new ActionResult(false, "Selecione um script para excluir.");
        }

        boolean removed = store.scripts().removeIf(item -> item.getName().equals(script.getName()));
        if (!removed) {
            return new ActionResult(false, "Script selecionado nao foi encontrado.");
        }

        store.persistScripts();
        historicoService.registrar(user, TipoAcao.EXCLUIR_SCRIPT, "-", script.getName(),
                StatusExecucao.SUCESSO, "-", "Script excluido do mock local.");
        return new ActionResult(true, "Script excluido do mock local.");
    }

    @Override
    public ActionResult executar(String scriptName, String server, String reason, User user) {
        if (ValidationUtil.isBlank(reason)) {
            return new ActionResult(false, "Informe o motivo da execucao.");
        }

        historicoService.registrar(user, TipoAcao.EXECUTAR_SCRIPT, server, scriptName, StatusExecucao.SIMULADO, reason,
                "Script simulado executado.");
        return new ActionResult(true, "Script " + scriptName + " executado com sucesso na simulacao.");
    }

    private ActionResult validate(ScriptDefinition script) {
        if (script == null || ValidationUtil.isBlank(script.getName())) {
            return new ActionResult(false, "Informe o nome do script.");
        }
        if (ValidationUtil.isBlank(script.getBody()) && ValidationUtil.isBlank(script.getFileName())) {
            return new ActionResult(false, "Informe o corpo do script ou selecione um arquivo.");
        }
        return new ActionResult(true, "OK");
    }
}
