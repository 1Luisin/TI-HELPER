package br.com.scmjf.tihelper.service;

import java.util.List;
import java.util.Map;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.QueryExecutionResult;
import br.com.scmjf.tihelper.model.User;

public interface QueryService {

    List<String> listarNomes();

    List<QueryDefinition> listarDefinicoes();

    List<String> listarParametros(String queryName);

    ActionResult cadastrar(QueryDefinition query, User user);

    QueryExecutionResult executar(String queryName, Map<String, String> parameters, User user);
}
