package application.network.protocol;

import application.network.protocol.client.ServerProxy;
import application.network.protocol.server.Server;

/**
 * Container welcher ein Netzwerkmodul spezifiziert
 */
public interface NetworkModule
{
    Server createServer();
    ServerProxy createClient();
}
