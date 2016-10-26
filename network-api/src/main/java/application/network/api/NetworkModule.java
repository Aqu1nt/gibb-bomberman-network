package application.network.api;

import application.network.api.client.ServerProxy;
import application.network.api.server.Server;

/**
 * Container welcher ein Netzwerkmodul spezifiziert
 */
public interface NetworkModule
{
    Server createServer();
    ServerProxy createClient();
}
