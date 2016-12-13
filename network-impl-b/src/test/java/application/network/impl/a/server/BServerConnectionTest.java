package application.network.impl.a.server;

import application.network.api.Message;
import application.network.impl.a.internal.InternalClientIdMessage;
import application.network.impl.a.internal.InternalClientIdResponse;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BServerConnectionTest
{
    private BServer server;
    private BServerConnection realConnection;
    private BServerConnection connection;

    @Before
    public void create_connection()
    {
        server = spy(new BServer());
        realConnection = new BServerConnection(null, server, false);
        connection = spy(realConnection);
        willDoNothing().given(connection).send(any());
    }

    @Test
    public void should_reponse_with_okay_when_everything_fine()
    {
        connection.handleClientIdMessage(new InternalClientIdMessage().setClientId("1234"));
        verify(connection).send(eq(new InternalClientIdResponse()));
        verify(server).registerConnection(connection);
    }

    @Test
    public void should_response_with_client_id_in_use_when_in_use()
    {
        given(server.isClientIdInUse("1234")).willReturn(true);
        connection.handleClientIdMessage(new InternalClientIdMessage().setClientId("1234"));
        verify(connection).send(eq(new InternalClientIdResponse().setClientIdInUse(true)));
        verify(server, never()).registerConnection(connection);
    }

    @Test
    public void should_response_with_client_rejected_when_rejected()
    {
        given(server.isClientAccepted("1234")).willReturn(false);
        connection.handleClientIdMessage(new InternalClientIdMessage().setClientId("1234"));
        verify(connection).send(eq(new InternalClientIdResponse().setClientRejected(true)));
        verify(server, never()).registerConnection(connection);
    }

    @Test
    public void should_not_unregister_itself_on_close_without_client_id()
    {
        connection.close();
        verify(server, never()).unregisterConnection(connection);
    }

    @Test
    public void should_unregister_itself_on_close_when_client_id_set()
    {
        given(connection.getClientId()).willReturn("1234");
        connection.close();
        verify(server).unregisterConnection(connection);
    }

    @Test
    public void should_forward_public_message_to_server()
    {
        Message msg = mock(Message.class);
        connection.dispatch(msg);
        verify(server).handleMessage(realConnection, msg);
    }
}