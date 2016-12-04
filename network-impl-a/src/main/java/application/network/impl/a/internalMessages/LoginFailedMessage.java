package application.network.impl.a.internalMessages;

public class LoginFailedMessage implements InternalMessage {


// types //////////////////////////////////////////////////////////////////////

    public enum LoginFailedReason {
        ACCESS_DENIED,
        LOBBY_FULL,
        NAME_ALREADY_USED,
        ;
    }


// memory /////////////////////////////////////////////////////////////////////

    private LoginFailedReason reason;


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    public LoginFailedReason getReason() {
        return reason;
    }

    public LoginFailedMessage setReason( LoginFailedReason reason ) {
        this.reason = reason;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginFailedMessage that = (LoginFailedMessage) o;

        return getReason() == that.getReason();
    }

    @Override
    public int hashCode() {
        return getReason() != null ? getReason().hashCode() : 0;
    }
}
