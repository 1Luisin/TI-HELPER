package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.User;

public interface ServicoServidorService {

    List<ServiceInfo> listar();

    ActionResult cadastrar(ServiceInfo service, User user);

    ActionResult finalizarReinicio(ServiceInfo service, String reason, User user);
}
