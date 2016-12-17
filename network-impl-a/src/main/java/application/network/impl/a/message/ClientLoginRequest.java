package application.network.impl.a.message;

/**
 * Created by nschaefli on 12/15/16.
 */
public class ClientLoginRequest implements InternalMessage{

    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public ClientLoginRequest setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientLoginRequest that = (ClientLoginRequest) o;

        return getClientId() != null ? getClientId().equals(that.getClientId()) : that.getClientId() == null;
    }

    @Override
    public int hashCode() {
        return getClientId() != null ? getClientId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientLoginRequest{" +
                "clientId='" + clientId + '\'' +
                '}';
    }
}
