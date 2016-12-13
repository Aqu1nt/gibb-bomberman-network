package application.network.impl.a.server.event;

import application.network.api.Message;
import application.network.impl.a.server.ServerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.BiConsumer;
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
        final Object testLock = new Object();
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
                synchronized( testLock ){
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
                synchronized( testLock ){
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

        // It may be required in future to wait here (before asserts) so the implementation can complete in case the
        // implementation gets asynchronous.

        synchronized( testLock ){
            Assert.assertFalse( "'"+peterHandler.myName+"' must be disconnected now." , peterHandler.isConnected );
            Assert.assertFalse( "'"+hansHandler.myName+"' must be disconnected now." , hansHandler.isConnected );

            Assert.assertTrue( "'"+heidiHandler.myName+"' must not be disconnected. We never fired that event." , heidiHandler.isConnected );

            Assert.assertEquals( "There must be exactly two events. Because we fired exactly two." , 2 , counter.count );
        }

    }

    /**
     * Check if all handlers get notified even a handler throws an exception.
     */
    @Test
    public void sendAMessage(){
        final Object testLock = new Object();
        final String peterName = "Peter";
        final String paulName = "Paul";
        EventManager eventManager = serverFactory.createEventManager();

        class TestMessage implements Message {
            private final String txt;
            private TestMessage( String txt ){ this.txt=txt; }
            private String getTxt(){ return txt; }
        }
        final TestMessage msg1 = new TestMessage( "msg1" );
        final TestMessage msg2 = new TestMessage( "msg2" );

        class MsgHandler implements BiConsumer<Message,String> , Comparable<Object> {
            private boolean gotCalled = false;
            @Override public void accept( Message message , String playerName ) {
                synchronized( testLock ){
                    gotCalled = true;
                    throw new RuntimeException( "Try to bring the manager into trouble by throwing this one. Manager MUST log this exception as an error. But he MUST continue work after that." );
                }
            }
            @Override public int compareTo( Object o ) {
                return this.toString().compareTo( o.toString() );
            }
        }

        // Even the handlers above throw exceptions the manager MUST continue working so other handlers can work further.
        final MsgHandler msgHandler1 = new MsgHandler();
        final MsgHandler msgHandler2 = new MsgHandler();
        eventManager.addMessageReceivedObserver( msgHandler1 );
        eventManager.addMessageReceivedObserver( msgHandler2 );

        // TODO: How to mock the logger to check if the implementation actually logs the message.

        eventManager.fireMessageReceived( msg1 , peterName );

        // Some sleeping may be required in the future here. To also take care in case the handlers get called async.

        synchronized( testLock ){
            Assert.assertTrue( "Message handler badly didn't receive the message." , msgHandler1.gotCalled );
            Assert.assertTrue( "Message handler badly didn't receive the message." , msgHandler2.gotCalled );
        }

        // Reset and try again to test if the manager is ready for new messages even there were exceptions before.
        synchronized( testLock ){
            msgHandler1.gotCalled = false;
            msgHandler2.gotCalled = false;
        }
        eventManager.fireMessageReceived( msg2 , paulName );

        // Some sleeping may be required in the future here. To also take care in case the handlers work asynchronous
        // internally.

        // Assert again.
        synchronized( testLock ){
            Assert.assertTrue( "Message handler badly didn't receive the message." , msgHandler1.gotCalled );
            Assert.assertTrue( "Message handler badly didn't receive the message." , msgHandler2.gotCalled );
        }

    }

}
