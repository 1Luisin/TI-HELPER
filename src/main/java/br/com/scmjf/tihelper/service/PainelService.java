package br.com.scmjf.tihelper.service;

import java.util.List;

import br.com.scmjf.tihelper.model.ActionResult;
import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.model.User;

public interface PainelService {

    List<PanelInfo> listar();

    ActionResult cadastrar(PanelInfo panel, User user);
}
