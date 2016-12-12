package application.network.impl.a.server.event;

import application.network.impl.a.server.ServerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;


public class ConcreteEventHandlerTest {


// memory /////////////////////////////////////////////////////////////////////

    private static final ServerFactory serverFactory = new ServerFactory();


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    /**
     * Registers some disconnected handlers and then fires some disconnected messages. The handlers memorize some state
     * So we then can validate if the right handlers got called.
     */
    @Test
    public void disconnectAClient(){
        final String peterName = "Peter";
        final String hansName = "Hans";
        final String heidiName = "Heidi";
        EventManager eventManager = serverFactory.createEventManager();

        class DisconnectedHandler implements Consumer<String>,Comparable<Object> {
            private final String myName;
            private boolean isConnected = true;
            private DisconnectedHandler( String myName ){
                this.myName = myName;
            }
            @Override public void accept( String s ) {
                synchronized( this ){
                    if( myName.equals(s)){
                        if( isConnected ){
                            isConnected = false;
                        }else{
                            Assert.fail( "'"+myName+"' got disconnected twice. But is expected to disconnect only once." );
                        }
                    }
                }
            }
            @Override public int compareTo( Object o ) {
                return this.toString().compareTo( o.toString() );
            }
        }

        class Counter implements Consumer<String>,Comparable<Object> {
            private int count;
            @Override public void accept( String s ) {
                synchronized( this ){
                    count++;
                }
            }
            @Override public int compareTo( Object o ) {
                return this.toString().compareTo( o.toString() );
            }
        }

        // Instantiate and add all handlers.
        DisconnectedHandler peterHandler = new DisconnectedHandler( peterName );
        DisconnectedHandler hansHandler = new DisconnectedHandler( hansName );
        DisconnectedHandler heidiHandler = new DisconnectedHandler( heidiName );
        Counter counter = new Counter();
        eventManager.addClientDisconnectedObserver( peterHandler );
        eventManager.addClientDisconnectedObserver( hansHandler );
        eventManager.addClientDisconnectedObserver( heidiHandler );
        eventManager.addClientDisconnectedObserver( counter );

        // Fire both names once to ensure both are disconnected after the calls.
        eventManager.fireClientDisconnected( peterName );
        eventManager.fireClientDisconnected( hansName );

        Assert.assertFalse( "'"+peterHandler.myName+"' must be disconnected now." , peterHandler.isConnected );
        Assert.assertFalse( "'"+hansHandler.myName+"' must be disconnected now." , hansHandler.isConnected );

        Assert.assertTrue( "'"+heidiHandler.myName+"' must not be disconnected. We never fired that event." , heidiHandler.isConnected );

        Assert.assertEquals( "There must be exactly two events. Because we fired exactly two." , 2 , counter.count );

    }

}
