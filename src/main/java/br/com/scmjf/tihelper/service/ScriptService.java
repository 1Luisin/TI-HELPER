package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.User;

public interface ScriptService {

    List<String> listarNomes();

    List<ScriptDefinition> listarDefinicoes();

    ActionResult cadastrar(ScriptDefinition script, User user);

    ActionResult alterar(String originalName, ScriptDefinition script, User user);

    ActionResult excluir(ScriptDefinition script, User user);

    ActionResult executar(String scriptName, String server, String reason, User user);
}
