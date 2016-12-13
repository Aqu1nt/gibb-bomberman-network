package application.network.impl.a.server.security;

import java.util.function.Predicate;

public class ConcreteSecurityManager implements SecurityManager {


// memory /////////////////////////////////////////////////////////////////////


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public boolean canConnect( String string ) {
        // TODO: implement this method.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addClientAcceptRestriction( Predicate<String> predicate ) {
        // TODO: implement this method.
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
