package application.network.impl.a.internal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Nachricht um den client namen zu senden
 */
@Data
@Accessors(chain = true)
public class InternalClientIdMessage implements InternalMessage
{
    private String clientId;
}
