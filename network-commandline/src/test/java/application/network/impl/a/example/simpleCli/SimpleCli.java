package application.network.impl.a.example.simpleCli;

import java.io.IOException;
import java.util.function.Predicate;

public class SimpleCli {


// memory /////////////////////////////////////////////////////////////////////

    private Predicate<String> handler;
    private String prompt = "simpleCli> ";


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////


    public void run(){
        while( true ){
            final StringBuilder line = new StringBuilder();
            System.out.print( prompt );
            while( true ){
                int c32;
                try{
                    c32 = System.in.read();
                }catch( IOException e ){
                    throw new RuntimeException(e); // TODO: handle
                }
                if( c32 > 65535 ){
                    throw new RuntimeException( "This character is not handleable." ); // TODO: Handle
                }else if( c32 == -1 ){
                    System.out.println( "Stdin closed." );
                    return;
                }else if( c32 == '\n' ){
                    break;
                }else{
                    line.append( (char)c32 );
                }
            }
            if( line.length() > 0 ){
                if( handler == null ) throw new IllegalStateException( "No handler to handle received command available." );
                boolean cont = handler.test( line.toString() );
                if( !cont ){
                    break;
                }
            }
        }
    }

    public void setHandler( Predicate<String> handler ){
        this.handler = handler;
    }

    public void setPrompt( String prompt ){
        this.prompt = prompt;
    }

}
