package application.network.impl.scala

import application.network.api.NetworkModule
import application.network.api.client.ServerProxy
import application.network.api.server.Server

class ScalaNetworkModule extends NetworkModule
{
    override def createServer(): Server = new ScalaServer
    override def createClient(): ServerProxy = new ScalaServerProxy
}