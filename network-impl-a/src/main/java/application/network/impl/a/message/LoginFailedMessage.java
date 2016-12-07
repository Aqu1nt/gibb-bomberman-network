package application.network.impl.a.message;

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

}
