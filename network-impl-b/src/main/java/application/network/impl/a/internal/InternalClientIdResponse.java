package application.network.impl.a.internal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Nachricht um das resultat der clientId mitzuteilen
 */
@Data
@Accessors(chain = true)
public class InternalClientIdResponse implements InternalMessage
{
    private boolean clientIdInUse = false;
    private boolean clientRejected = false;
}
