package br.com.scmjf.tihelper.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import br.com.scmjf.tihelper.util.AppInfo;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.AppPaths;

public class LocalJsonDataService {

    private static final String USERS_FILE = "usuarios.json";
    private static final String SERVERS_FILE = "servidores.json";
    private static final String SERVICES_FILE = "servicos.json";
    private static final String PANELS_FILE = "paineis.json";
    private static final String QUERIES_FILE = "queries.json";
    private static final String SCRIPTS_FILE = "scripts.json";
    private static final String HISTORY_FILE = "historico.json";
    private static final String CONFIG_FILE = "config.json";

    private final ObjectMapper mapper;
    private final Path dataDirectory;

    public LocalJsonDataService() {
        this(AppPaths.dataDirectory());
    }

    LocalJsonDataService(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Optional<LoadedData> load() {
        if (!hasAnyDataFile()) {
            return Optional.empty();
        }

        try {
            Map<String, UserAccount> users = new LinkedHashMap<>();
            for (UserAccountDto user : readList(USERS_FILE, new TypeReference<List<UserAccountDto>>() {
            })) {
                users.put(user.username(), new UserAccount(
                        user.username(),
                        user.password(),
                        user.profile(),
                        nullToEmpty(user.fullName()),
                        nullToEmpty(user.email()),
                        nullToEmpty(user.sector()),
                        user.profilePhotoUri(),
                        normalizeModules(user.profile(), user.modules())));
            }

            List<ServerInfo> servers = readList(SERVERS_FILE, new TypeReference<List<ServerInfoDto>>() {
            }).stream()
                    .map(dto -> new ServerInfo(dto.name(), dto.host(), dto.operatingSystem(), dto.environment(), dto.status()))
                    .toList();

            List<ServiceInfo> services = readList(SERVICES_FILE, new TypeReference<List<ServiceInfoDto>>() {
            }).stream()
                    .map(dto -> new ServiceInfo(dto.server(), dto.name(), dto.description(), dto.status(), dto.lastVerification()))
                    .toList();

            List<PanelInfo> panels = readList(PANELS_FILE, new TypeReference<List<PanelInfoDto>>() {
            }).stream()
                    .map(dto -> new PanelInfo(dto.name(), dto.ipAddress(), dto.location(), dto.status()))
                    .toList();

            List<QueryDefinition> queries = readList(QUERIES_FILE, new TypeReference<List<QueryDefinitionDto>>() {
            }).stream()
                    .map(dto -> new QueryDefinition(dto.name(), dto.queryText(), nullToEmpty(dto.parameters())))
                    .toList();

            List<ScriptDefinition> scripts = readList(SCRIPTS_FILE, new TypeReference<List<ScriptDefinitionDto>>() {
            }).stream()
                    .map(dto -> new ScriptDefinition(dto.name(), dto.sourceType(), dto.body(), dto.fileName()))
                    .toList();

            List<ExecutionHistory> history = readList(HISTORY_FILE, new TypeReference<List<ExecutionHistoryDto>>() {
            }).stream()
                    .map(dto -> new ExecutionHistory(dto.id(), dto.dateTime(), dto.username(), dto.profile(), dto.tipoAcao(),
                            dto.server(), dto.target(), dto.status(), dto.reason(), dto.message()))
                    .toList();

            return Optional.of(new LoadedData(users, servers, services, panels, queries, scripts, history));
        } catch (IOException | RuntimeException exception) {
            AppLogger.error("Erro ao carregar dados JSON locais.", exception);
            return Optional.empty();
        }
    }

    public AppConfigDto loadConfig() {
        Path file = dataDirectory.resolve(CONFIG_FILE);
        if (!Files.exists(file)) {
            return defaultConfig(CloseBehavior.ASK);
        }

        try {
            return normalizeConfig(mapper.readValue(file.toFile(), AppConfigDto.class));
        } catch (IOException | RuntimeException exception) {
            AppLogger.error("Erro ao carregar configuracao JSON local.", exception);
            return defaultConfig(CloseBehavior.ASK);
        }
    }

    public void saveAll(
            Map<String, UserAccount> users,
            List<ServerInfo> servers,
            List<ServiceInfo> services,
            List<PanelInfo> panels,
            List<QueryDefinition> queries,
            List<ScriptDefinition> scripts,
            List<ExecutionHistory> history) {
        saveUsers(users);
        saveServers(servers);
        saveServices(services);
        savePanels(panels);
        saveQueries(queries);
        saveScripts(scripts);
        saveHistory(history);
        saveConfig();
    }

    public void saveUsers(Map<String, UserAccount> users) {
        write(USERS_FILE, users.values().stream()
                .map(user -> new UserAccountDto(
                        user.getUsername(),
                        user.getPassword(),
                        user.getProfile(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getSector(),
                        user.getProfilePhotoUri(),
                        List.copyOf(user.getModules())))
                .toList());
    }

    public void saveServers(List<ServerInfo> servers) {
        write(SERVERS_FILE, servers.stream()
                .map(server -> new ServerInfoDto(server.getName(), server.getHost(), server.getOperatingSystem(), server.getEnvironment(), server.getStatus()))
                .toList());
    }

    public void saveServices(List<ServiceInfo> services) {
        write(SERVICES_FILE, services.stream()
                .map(service -> new ServiceInfoDto(service.getServer(), service.getName(), service.getDescription(), service.getStatus(), service.getLastVerification()))
                .toList());
    }

    public void savePanels(List<PanelInfo> panels) {
        write(PANELS_FILE, panels.stream()
                .map(panel -> new PanelInfoDto(panel.getName(), panel.getIpAddress(), panel.getLocation(), panel.getStatus()))
                .toList());
    }

    public void saveQueries(List<QueryDefinition> queries) {
        write(QUERIES_FILE, queries.stream()
                .map(query -> new QueryDefinitionDto(query.getName(), query.getQueryText(), query.getParameters()))
                .toList());
    }

    public void saveScripts(List<ScriptDefinition> scripts) {
        write(SCRIPTS_FILE, scripts.stream()
                .map(script -> new ScriptDefinitionDto(script.getName(), script.getSourceType(), script.getBody(), script.getFileName()))
                .toList());
    }

    public void saveHistory(List<ExecutionHistory> history) {
        write(HISTORY_FILE, history.stream()
                .map(record -> new ExecutionHistoryDto(record.getId(), record.getDateTime(), record.getUsername(),
                        record.getProfile(), record.getTipoAcao(), record.getServer(), record.getTarget(),
                        record.getStatusExecucao(), record.getReason(), record.getMessage()))
                .toList());
    }

    public void saveConfig() {
        saveConfig(loadConfig().closeBehavior());
    }

    public void saveCloseBehavior(CloseBehavior closeBehavior) {
        saveConfig(closeBehavior);
    }

    public void exportBackup(Path destination, LoadedData data) throws IOException {
        ensureDirectory();
        Path parent = destination.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        BackupDto backup = new BackupDto(
                defaultConfig(loadConfig().closeBehavior()),
                data.users().values().stream()
                        .map(user -> new UserAccountDto(
                                user.getUsername(),
                                user.getPassword(),
                                user.getProfile(),
                                user.getFullName(),
                                user.getEmail(),
                                user.getSector(),
                                user.getProfilePhotoUri(),
                                List.copyOf(user.getModules())))
                        .toList(),
                data.servers().stream()
                        .map(server -> new ServerInfoDto(server.getName(), server.getHost(), server.getOperatingSystem(), server.getEnvironment(), server.getStatus()))
                        .toList(),
                data.services().stream()
                        .map(service -> new ServiceInfoDto(service.getServer(), service.getName(), service.getDescription(), service.getStatus(), service.getLastVerification()))
                        .toList(),
                data.panels().stream()
                        .map(panel -> new PanelInfoDto(panel.getName(), panel.getIpAddress(), panel.getLocation(), panel.getStatus()))
                        .toList(),
                data.queries().stream()
                        .map(query -> new QueryDefinitionDto(query.getName(), query.getQueryText(), query.getParameters()))
                        .toList(),
                data.scripts().stream()
                        .map(script -> new ScriptDefinitionDto(script.getName(), script.getSourceType(), script.getBody(), script.getFileName()))
                        .toList(),
                data.history().stream()
                        .map(record -> new ExecutionHistoryDto(record.getId(), record.getDateTime(), record.getUsername(),
                                record.getProfile(), record.getTipoAcao(), record.getServer(), record.getTarget(),
                                record.getStatusExecucao(), record.getReason(), record.getMessage()))
                        .toList());
        mapper.writeValue(destination.toFile(), backup);
    }

    public LoadedData importBackup(Path source) throws IOException {
        BackupDto backup = mapper.readValue(source.toFile(), BackupDto.class);
        saveConfig(normalizeConfig(backup.config()).closeBehavior());
        Map<String, UserAccount> users = new LinkedHashMap<>();
        for (UserAccountDto user : nullToEmpty(backup.users())) {
            users.put(user.username(), new UserAccount(
                    user.username(),
                    user.password(),
                    user.profile(),
                    nullToEmpty(user.fullName()),
                    nullToEmpty(user.email()),
                    nullToEmpty(user.sector()),
                    user.profilePhotoUri(),
                    normalizeModules(user.profile(), user.modules())));
        }

        return new LoadedData(
                users,
                nullToEmpty(backup.servers()).stream()
                        .map(dto -> new ServerInfo(dto.name(), dto.host(), dto.operatingSystem(), dto.environment(), dto.status()))
                        .toList(),
                nullToEmpty(backup.services()).stream()
                        .map(dto -> new ServiceInfo(dto.server(), dto.name(), dto.description(), dto.status(), dto.lastVerification()))
                        .toList(),
                nullToEmpty(backup.panels()).stream()
                        .map(dto -> new PanelInfo(dto.name(), dto.ipAddress(), dto.location(), dto.status()))
                        .toList(),
                nullToEmpty(backup.queries()).stream()
                        .map(dto -> new QueryDefinition(dto.name(), dto.queryText(), nullToEmpty(dto.parameters())))
                        .toList(),
                nullToEmpty(backup.scripts()).stream()
                        .map(dto -> new ScriptDefinition(dto.name(), dto.sourceType(), dto.body(), dto.fileName()))
                        .toList(),
                nullToEmpty(backup.history()).stream()
                        .map(dto -> new ExecutionHistory(dto.id(), dto.dateTime(), dto.username(), dto.profile(), dto.tipoAcao(),
                                dto.server(), dto.target(), dto.status(), dto.reason(), dto.message()))
                        .toList());
    }

    private boolean hasAnyDataFile() {
        return Files.exists(dataDirectory.resolve(USERS_FILE))
                || Files.exists(dataDirectory.resolve(SERVERS_FILE))
                || Files.exists(dataDirectory.resolve(SERVICES_FILE))
                || Files.exists(dataDirectory.resolve(PANELS_FILE))
                || Files.exists(dataDirectory.resolve(QUERIES_FILE))
                || Files.exists(dataDirectory.resolve(SCRIPTS_FILE))
                || Files.exists(dataDirectory.resolve(HISTORY_FILE));
    }

    private <T> List<T> readList(String fileName, TypeReference<List<T>> typeReference) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        if (!Files.exists(file)) {
            return List.of();
        }
        return nullToEmpty(mapper.readValue(file.toFile(), typeReference));
    }

    private void write(String fileName, Object value) {
        try {
            ensureDirectory();
            mapper.writeValue(dataDirectory.resolve(fileName).toFile(), value);
        } catch (IOException exception) {
            AppLogger.error("Erro ao salvar arquivo JSON local: " + fileName, exception);
        }
    }

    private void ensureDirectory() throws IOException {
        Files.createDirectories(dataDirectory);
    }

    private void saveConfig(CloseBehavior closeBehavior) {
        write(CONFIG_FILE, defaultConfig(closeBehavior));
    }

    private AppConfigDto defaultConfig(CloseBehavior closeBehavior) {
        return new AppConfigDto(1, AppInfo.VERSION, AppInfo.ENVIRONMENT, LocalDateTime.now(),
                closeBehavior == null ? CloseBehavior.ASK : closeBehavior);
    }

    private AppConfigDto normalizeConfig(AppConfigDto config) {
        if (config == null) {
            return defaultConfig(CloseBehavior.ASK);
        }

        return new AppConfigDto(
                config.schemaVersion() <= 0 ? 1 : config.schemaVersion(),
                AppInfo.VERSION,
                AppInfo.ENVIRONMENT,
                config.savedAt() == null ? LocalDateTime.now() : config.savedAt(),
                config.closeBehavior() == null ? CloseBehavior.ASK : config.closeBehavior());
    }

    private static <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static Set<UserModule> normalizeModules(UserProfile profile, List<UserModule> modules) {
        return modules == null ? UserModule.defaultsFor(profile) : Set.copyOf(modules);
    }

    public record LoadedData(
            Map<String, UserAccount> users,
            List<ServerInfo> servers,
            List<ServiceInfo> services,
            List<PanelInfo> panels,
            List<QueryDefinition> queries,
            List<ScriptDefinition> scripts,
            List<ExecutionHistory> history) {
    }

    public record AppConfigDto(int schemaVersion, String appVersion, String environment, LocalDateTime savedAt,
            CloseBehavior closeBehavior) {
    }

    private record BackupDto(
            AppConfigDto config,
            List<UserAccountDto> users,
            List<ServerInfoDto> servers,
            List<ServiceInfoDto> services,
            List<PanelInfoDto> panels,
            List<QueryDefinitionDto> queries,
            List<ScriptDefinitionDto> scripts,
            List<ExecutionHistoryDto> history) {
    }

    private record UserAccountDto(
            String username,
            String password,
            UserProfile profile,
            String fullName,
            String email,
            String sector,
            String profilePhotoUri,
            List<UserModule> modules) {
    }

    private record ServerInfoDto(String name, String host, String operatingSystem, String environment, String status) {
    }

    private record ServiceInfoDto(String server, String name, String description, String status, LocalDateTime lastVerification) {
    }

    private record PanelInfoDto(String name, String ipAddress, String location, String status) {
    }

    private record QueryDefinitionDto(String name, String queryText, List<String> parameters) {
    }

    private record ScriptDefinitionDto(String name, String sourceType, String body, String fileName) {
    }

    private record ExecutionHistoryDto(long id, LocalDateTime dateTime, String username, UserProfile profile,
            TipoAcao tipoAcao, String server, String target, StatusExecucao status, String reason, String message) {
    }
}
