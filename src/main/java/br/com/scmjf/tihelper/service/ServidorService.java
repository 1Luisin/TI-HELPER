package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.User;

public interface ServidorService {

    List<ServerInfo> listar();

    ActionResult cadastrar(ServerInfo server, User user);

    ActionResult testarConexao(ServerInfo server, User user);
}
