package application.network.api;

import application.network.api.client.ServerProxy;
import application.network.api.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * Stellt eine beliebige Implementation der Netzwerk API zur Verfügung
 */
public final class Network
{
    /**
     * Die Wähl Strategie welche standardmässig verwendet wird um das benutzte Modul zu wählen
     */
    public final static Function<List<NetworkModule>,NetworkModule> DEFAULT_MODULE_EVALUATION_STRATEGY = list -> {
        if (list.isEmpty()) {
            throw new IllegalStateException("No provided NetworkModule found!");
        }
        if (list.size() > 1) {
            throw new IllegalStateException("Multiple available NetworkModules found!");
        }
        return list.get(0);
    };

    /**
     * Hier wird das benutzte {@link NetworkModule} gespeichert welches
     * benutzt wird um die Instanzen von {@link Server} und {@link ServerProxy}
     * zu erstellen
     */
    protected static NetworkModule usedModule = null;

    /**
     * Die Strategie welche benutzt wird um aus mehreren verfügbaren Modulen das richtige auszuwählen
     */
    protected static Function<List<NetworkModule>, NetworkModule> moduleEvaluationStrategy = DEFAULT_MODULE_EVALUATION_STRATEGY;

    /**
     * @param module das Modul welches verwendet werden soll
     * @throws IllegalStateException wenn bereits ein anderes Modul registriert wurde
     */
    public synchronized static void setNetworkModule(NetworkModule module)
    {
        if (Network.usedModule != null) {
            throw new IllegalStateException("There is already another NetworkModule registerd!");
        }
        if (module == null) {
            throw new IllegalArgumentException("NetworkModule cannot be null!");
        }
        Network.usedModule = module;
    }

    /**
     * Setzt die strategie welche benutzt wird um das richtige {@link NetworkModule} auszuwählen
     * @param strategy die Stragie
     */
    public synchronized static void setModuleEvaluationStrategy(Function<List<NetworkModule>, NetworkModule> strategy)
    {
        if (strategy == null) {
            throw new IllegalArgumentException("Provided custom NetworkModule evaluation strategy cannot be null!");
        }
        Network.moduleEvaluationStrategy = strategy;
    }

    /**
     * @return eine neue {@link Server} instanz welche abhängig vom gegebenen Networking Modul ist
     */
    public synchronized static Server createServer()
    {
        if (usedModule == null) {
            autoDetectNetworkModule();
        }
        return usedModule.createServer();
    }

    /**
     * @return eine neue {@link ServerProxy} instanz welche abhängig vom gegebenen Networking Modul ist
     */
    public synchronized static ServerProxy createClient()
    {
        if (usedModule == null) {
            autoDetectNetworkModule();
        }
        return usedModule.createClient();
    }


    /**
     * Lädt alle verfügbaren NetworkModule Klassen, wirft einen Fehler wenn mehrere verfügbar sind
     * Wie mache ich mein NetworkModule benutzbar?
     * 1. Erstelle den Ordner META-INF/services (src/main/resources/META-INF/services bei gradle|maven)
     * 2. Erstelle die Datei application.network.protocol.NetworkModule
     * 3. Die Datei muss den voll qualifizierten Namen deines NetworkModules beinhalten
     * @throws IllegalStateException wenn kein modul gefunden wurde
     */
    private static void autoDetectNetworkModule()
    {
        List<NetworkModule> modules = new ArrayList<>();
        ServiceLoader.load(NetworkModule.class).forEach(modules::add);
        setNetworkModule(moduleEvaluationStrategy.apply(modules));
    }
}
