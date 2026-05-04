package br.com.scmjf.tihelper.util;

import java.util.function.Consumer;

import br.com.scmjf.tihelper.model.User;
import br.com.scmjf.tihelper.service.ActionSimulationService;
import br.com.scmjf.tihelper.service.AuthService;
import br.com.scmjf.tihelper.service.HistoryService;
import br.com.scmjf.tihelper.service.MockDataService;

public final class AppContext {

    private static final AuthService AUTH_SERVICE = new AuthService();
    private static final HistoryService HISTORY_SERVICE = new HistoryService();
    private static final MockDataService MOCK_DATA_SERVICE = new MockDataService();
    private static final ActionSimulationService ACTION_SIMULATION_SERVICE =
            new ActionSimulationService(HISTORY_SERVICE);

    private static User currentUser;
    private static Consumer<NavigationTarget> navigationHandler;

    private AppContext() {
    }

    public static AuthService authService() {
        return AUTH_SERVICE;
    }

    public static HistoryService historyService() {
        return HISTORY_SERVICE;
    }

    public static MockDataService mockDataService() {
        return MOCK_DATA_SERVICE;
    }

    public static ActionSimulationService actionSimulationService() {
        return ACTION_SIMULATION_SERVICE;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        AppContext.currentUser = currentUser;
    }

    public static void setNavigationHandler(Consumer<NavigationTarget> navigationHandler) {
        AppContext.navigationHandler = navigationHandler;
    }

    public static void navigateTo(NavigationTarget target) {
        if (navigationHandler != null) {
            navigationHandler.accept(target);
        }
    }

    public static void clearSession() {
        currentUser = null;
        navigationHandler = null;
    }
}
