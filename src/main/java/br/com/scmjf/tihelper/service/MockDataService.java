package br.com.scmjf.tihelper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;

public class MockDataService {

    private final List<ServerInfo> servers = new ArrayList<>();
    private final List<ServiceInfo> services = new ArrayList<>();
    private final List<QueryDefinition> queries = new ArrayList<>();
    private final List<ScriptDefinition> scripts = new ArrayList<>();

    public MockDataService() {
        seedServers();
        seedServices();
        seedQueries();
        seedScripts();
    }

    public List<ServerInfo> getServers() {
        return List.copyOf(servers);
    }

    public List<ServiceInfo> getServices() {
        return List.copyOf(services);
    }

    public List<String> getQueries() {
        return queries.stream()
                .map(QueryDefinition::getName)
                .toList();
    }

    public List<QueryDefinition> getQueryDefinitions() {
        return List.copyOf(queries);
    }

    public List<String> getQueryFields(String queryName) {
        return queries.stream()
                .filter(query -> query.getName().equals(queryName))
                .findFirst()
                .map(QueryDefinition::getParameters)
                .orElse(List.of());
    }

    public List<String> getScripts() {
        return scripts.stream()
                .map(ScriptDefinition::getName)
                .toList();
    }

    public List<ScriptDefinition> getScriptDefinitions() {
        return List.copyOf(scripts);
    }

    public void addServer(ServerInfo server) {
        servers.add(server);
    }

    public void addService(ServiceInfo service) {
        services.add(service);
    }

    public void addQuery(QueryDefinition query) {
        queries.add(query);
    }

    public void addScript(String scriptName) {
        scripts.add(new ScriptDefinition(scriptName, "Mock", "", ""));
    }

    public void addScript(ScriptDefinition script) {
        scripts.add(script);
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

    private void seedQueries() {
        queries.add(new QueryDefinition("Buscar paciente por CPF", List.of("CPF")));
        queries.add(new QueryDefinition("Buscar atendimento por número", List.of("Número")));
        queries.add(new QueryDefinition("Consultar respostas de pesquisa", List.of("Data inicial", "Data final")));
        queries.add(new QueryDefinition("Verificar integração pendente", List.of("Integração")));
    }

    private void seedScripts() {
        addScript("Instalar VNC remoto");
        addScript("Limpar pasta temporária");
        addScript("Atualizar atalho do sistema");
    }
}
