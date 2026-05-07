package br.com.scmjf.tihelper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

import br.com.scmjf.tihelper.model.CloseBehavior;
import br.com.scmjf.tihelper.model.ExecutionHistory;
import br.com.scmjf.tihelper.model.PanelInfo;
import br.com.scmjf.tihelper.model.QueryDefinition;
import br.com.scmjf.tihelper.model.ScriptDefinition;
import br.com.scmjf.tihelper.model.ServerInfo;
import br.com.scmjf.tihelper.model.ServiceInfo;
import br.com.scmjf.tihelper.model.StatusExecucao;
import br.com.scmjf.tihelper.model.TipoAcao;
import br.com.scmjf.tihelper.model.UserAccount;
import br.com.scmjf.tihelper.model.UserModule;
import br.com.scmjf.tihelper.model.UserProfile;
import br.com.scmjf.tihelper.persistence.LocalJsonDataService;
import br.com.scmjf.tihelper.persistence.LocalJsonDataService.LoadedData;
import br.com.scmjf.tihelper.util.AppLogger;

public class MockDataStore {

    private final LocalJsonDataService persistenceService = new LocalJsonDataService();
    private final Map<String, UserAccount> users = new LinkedHashMap<>();
    private final List<ServerInfo> servers = new ArrayList<>();
    private final List<ServiceInfo> services = new ArrayList<>();
    private final List<PanelInfo> panels = new ArrayList<>();
    private final List<QueryDefinition> queries = new ArrayList<>();
    private final List<ScriptDefinition> scripts = new ArrayList<>();
    private final List<ExecutionHistory> history = new ArrayList<>();
    private long historySequence = 1L;

    public MockDataStore() {
        loadInitialData();
    }

    public Path getDataDirectory() {
        return persistenceService.getDataDirectory();
    }

    public CloseBehavior getCloseBehavior() {
        return persistenceService.loadConfig().closeBehavior();
    }

    public void saveCloseBehavior(CloseBehavior closeBehavior) {
        persistenceService.saveCloseBehavior(closeBehavior);
        AppLogger.info("Preferencia de fechamento salva: " + closeBehavior);
    }

    public void restoreDefaults() {
        clear();
        seedDefaults();
        refreshHistorySequence();
        persistAll();
        AppLogger.info("Dados mockados padrao restaurados.");
    }

    public void exportBackup(Path destination) throws java.io.IOException {
        persistenceService.exportBackup(destination, snapshot());
        AppLogger.info("Backup JSON exportado para: " + destination);
    }

    public void importBackup(Path source) throws java.io.IOException {
        applyLoadedData(persistenceService.importBackup(source));
        persistAll();
        AppLogger.info("Backup JSON importado de: " + source);
    }

    public void persistUsers() {
        persistenceService.saveUsers(users);
        persistenceService.saveConfig();
    }

    public void persistServers() {
        persistenceService.saveServers(servers);
        persistenceService.saveConfig();
    }

    public void persistServices() {
        persistenceService.saveServices(services);
        persistenceService.saveConfig();
    }

    public void persistPanels() {
        persistenceService.savePanels(panels);
        persistenceService.saveConfig();
    }

    public void persistQueries() {
        persistenceService.saveQueries(queries);
        persistenceService.saveConfig();
    }

    public void persistScripts() {
        persistenceService.saveScripts(scripts);
        persistenceService.saveConfig();
    }

    public void persistHistory() {
        persistenceService.saveHistory(history);
        persistenceService.saveConfig();
    }

    public void persistAll() {
        persistenceService.saveAll(users, servers, services, panels, queries, scripts, history);
    }

    private void loadInitialData() {
        seedUsers();
        seedServers();
        seedServices();
        seedPanels();
        seedQueries();
        seedScripts();
        seedHistory();

        persistenceService.load().ifPresentOrElse(data -> {
            applyLoadedData(data);
            AppLogger.info("Dados locais JSON carregados de: " + persistenceService.getDataDirectory());
        }, () -> {
            persistAll();
            AppLogger.info("Dados mockados padrao carregados e salvos em: " + persistenceService.getDataDirectory());
        });
        refreshHistorySequence();
    }

    Map<String, UserAccount> users() {
        return users;
    }

    List<ServerInfo> servers() {
        return servers;
    }

    List<ServiceInfo> services() {
        return services;
    }

    List<PanelInfo> panels() {
        return panels;
    }

    List<QueryDefinition> queries() {
        return queries;
    }

    List<ScriptDefinition> scripts() {
        return scripts;
    }

    List<ExecutionHistory> history() {
        return history;
    }

    long nextHistoryId() {
        return historySequence++;
    }

    private void seedDefaults() {
        seedUsers();
        seedServers();
        seedServices();
        seedPanels();
        seedQueries();
        seedScripts();
        seedHistory();
    }

    private void clear() {
        users.clear();
        servers.clear();
        services.clear();
        panels.clear();
        queries.clear();
        scripts.clear();
        history.clear();
        historySequence = 1L;
    }

    private void applyLoadedData(LoadedData data) {
        clear();
        users.putAll(data.users());
        servers.addAll(data.servers());
        services.addAll(data.services());
        panels.addAll(data.panels());
        queries.addAll(data.queries());
        scripts.addAll(data.scripts());
        history.addAll(data.history());
        refreshHistorySequence();
    }

    private LoadedData snapshot() {
        return new LoadedData(
                new LinkedHashMap<>(users),
                List.copyOf(servers),
                List.copyOf(services),
                List.copyOf(panels),
                List.copyOf(queries),
                List.copyOf(scripts),
                List.copyOf(history));
    }

    private void refreshHistorySequence() {
        historySequence = history.stream()
                .mapToLong(ExecutionHistory::getId)
                .max()
                .orElse(0L) + 1L;
    }

    private void seedUsers() {
        users.put("admin", new UserAccount("admin", "admin", UserProfile.ADMIN,
                "Administrador TI", "admin@scmjf.local", "TI", null, UserModule.defaultsFor(UserProfile.ADMIN)));
        users.put("ti", new UserAccount("ti", "ti", UserProfile.OPERADOR_TI,
                "Operador TI", "ti@scmjf.local", "TI", null, UserModule.defaultsFor(UserProfile.OPERADOR_TI)));
        users.put("consulta", new UserAccount("consulta", "consulta", UserProfile.CONSULTA,
                "Usuario Consulta", "consulta@scmjf.local", "Atendimento", null, UserModule.defaultsFor(UserProfile.CONSULTA)));
    }

    private void seedServers() {
        servers.add(new ServerInfo("SRV-APP-01", "10.10.1.11", "Windows Server 2019", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-BD-01", "10.10.1.20", "Oracle Linux 8", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-HML-01", "hml-scmjf.local", "Windows Server 2022", "Homologação", "Manutenção"));
        servers.add(new ServerInfo("SRV-ARQ-01", "files.scmjf.local", "Windows Server 2016", "Produção", "Online"));
        servers.add(new ServerInfo("SRV-INT-01", "int.scmjf.local", "Ubuntu Server 22.04", "Integração", "Instável"));
    }

    private void seedServices() {
        services.add(new ServiceInfo("SRV-APP-01", "SCMJF Web", "Aplicação principal do sistema.", "Em execução", LocalDateTime.now().minusMinutes(5)));
        services.add(new ServiceInfo("SRV-APP-01", "Pesquisa Tablet", "Serviço de coleta de respostas da pesquisa.", "Em execução", LocalDateTime.now().minusMinutes(8)));
        services.add(new ServiceInfo("SRV-BD-01", "Oracle Listener", "Canal de escuta para conexões Oracle.", "Em execução", LocalDateTime.now().minusMinutes(11)));
        services.add(new ServiceInfo("SRV-INT-01", "IntegradorSCMJF", "Fila de integração entre sistemas internos.", "Atenção", LocalDateTime.now().minusMinutes(22)));
        services.add(new ServiceInfo("SRV-HML-01", "SCMJF Homolog", "Ambiente de homologação para validações.", "Parado", LocalDateTime.now().minusHours(1)));
    }

    private void seedPanels() {
        panels.add(new PanelInfo("Painel Recepção", "172.18.10.21", "Recepção", "Ligado"));
        panels.add(new PanelInfo("Painel PA", "172.18.10.22", "Pronto atendimento", "Ligado"));
        panels.add(new PanelInfo("Painel Internação", "172.18.10.23", "Internação", "Desligado"));
    }

    private void seedQueries() {
        queries.add(new QueryDefinition("Buscar paciente por CPF", List.of("CPF")));
        queries.add(new QueryDefinition("Buscar atendimento por número", List.of("Número")));
        queries.add(new QueryDefinition("Consultar respostas de pesquisa", List.of("Data inicial", "Data final")));
        queries.add(new QueryDefinition("Verificar integração pendente", List.of("Integração")));
    }

    private void seedScripts() {
        scripts.add(new ScriptDefinition("Instalar VNC remoto", "Mock", "", ""));
        scripts.add(new ScriptDefinition("Limpar pasta temporária", "Mock", "", ""));
        scripts.add(new ScriptDefinition("Atualizar atalho do sistema", "Mock", "", ""));
    }

    private void seedHistory() {
        history.add(new ExecutionHistory(nextHistoryId(), LocalDateTime.now().minusMinutes(18), "ti", UserProfile.OPERADOR_TI,
                TipoAcao.EXECUTAR_QUERY, "-", "Buscar paciente por CPF", StatusExecucao.SIMULADO, "-",
                "Consulta simulada retornou 1 registro."));
        history.add(new ExecutionHistory(nextHistoryId(), LocalDateTime.now().minusHours(1), "admin", UserProfile.ADMIN,
                TipoAcao.EXECUTAR_SCRIPT, "SRV-APP-01", "Limpar pasta temporária", StatusExecucao.SIMULADO, "Manutenção preventiva",
                "Execução mockada concluída."));
        history.add(new ExecutionHistory(nextHistoryId(), LocalDateTime.now().minusHours(2), "ti", UserProfile.OPERADOR_TI,
                TipoAcao.REINICIAR_SERVICO, "SRV-INT-01", "IntegradorSCMJF", StatusExecucao.ERRO, "Teste operacional",
                "Serviço indisponível na simulação."));
        history.add(new ExecutionHistory(nextHistoryId(), LocalDateTime.now().minusDays(1), "consulta", UserProfile.CONSULTA,
                TipoAcao.EXECUTAR_QUERY, "-", "Consultar respostas de pesquisa", StatusExecucao.SIMULADO, "-",
                "Consulta simulada retornou 12 registros."));
    }
}
