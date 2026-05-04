package br.com.scmjf.tihelper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;

public class MockDataService {

    private final List<ServerInfo> servers = new ArrayList<>();
    private final List<ServiceInfo> services = new ArrayList<>();
    private final List<String> queries = List.of(
            "Buscar paciente por CPF",
            "Buscar atendimento por número",
            "Consultar respostas de pesquisa",
            "Verificar integração pendente");
    private final List<String> scripts = List.of(
            "Instalar VNC remoto",
            "Limpar pasta temporária",
            "Atualizar atalho do sistema");

    public MockDataService() {
        seedServers();
        seedServices();
    }

    public List<ServerInfo> getServers() {
        return List.copyOf(servers);
    }

    public List<ServiceInfo> getServices() {
        return List.copyOf(services);
    }

    public List<String> getQueries() {
        return queries;
    }

    public List<String> getScripts() {
        return scripts;
    }

    private void seedServers() {
        servers.add(new ServerInfo("SRV-APP-01", "10.10.1.11", "Windows Server 2019", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-BD-01", "10.10.1.20", "Oracle Linux 8", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-HML-01", "hml-scmjf.local", "Windows Server 2022", "Homologação", "Manutenção"));
        servers.add(new ServerInfo("SRV-ARQ-01", "files.scmjf.local", "Windows Server 2016", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-INT-01", "int.scmjf.local", "Ubuntu Server 22.04", "Integração", "Instável"));
    }

    private void seedServices() {
        services.add(new ServiceInfo(
                "SRV-APP-01",
                "SCMJF Web",
                "Aplicação principal do sistema.",
                "Em execução",
                LocalDateTime.now().minusMinutes(5)));
        services.add(new ServiceInfo(
                "SRV-APP-01",
                "Pesquisa Tablet",
                "Serviço de coleta de respostas da pesquisa.",
                "Em execução",
                LocalDateTime.now().minusMinutes(8)));
        services.add(new ServiceInfo(
                "SRV-BD-01",
                "Oracle Listener",
                "Canal de escuta para conexões Oracle.",
                "Em execução",
                LocalDateTime.now().minusMinutes(11)));
        services.add(new ServiceInfo(
                "SRV-INT-01",
                "IntegradorSCMJF",
                "Fila de integração entre sistemas internos.",
                "Atenção",
                LocalDateTime.now().minusMinutes(22)));
        services.add(new ServiceInfo(
                "SRV-HML-01",
                "SCMJF Homolog",
                "Ambiente de homologação para validações.",
                "Parado",
                LocalDateTime.now().minusHours(1)));
    }
}
