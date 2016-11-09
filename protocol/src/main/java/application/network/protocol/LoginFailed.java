package application.network.protocol;

import application.network.api.Message;

/**
 * Die Nachricht wird verwendet um dem Client ein Login Fehler mitzuteilen.
 */
public class LoginFailed implements Message {

    private String reason;

    public String getReason() {
        return reason;
    }

    public LoginFailed setReason(String reason) {
        this.reason = reason;
        return this;
    }
}
