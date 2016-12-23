package application.network.impl.a.example.commandline;

import application.network.api.Message;
import application.network.protocol.*;

import java.util.ArrayList;
import java.util.List;


class SharedFunctions {

    private final String stdoutPrefix;
    private Maze maze;
    private int nextId = 0;


    SharedFunctions( String stdoutPrefix ){
        this.stdoutPrefix = stdoutPrefix;
    }


    Message createMessage( String[] args , String playerName ){
        String msgTypeName = args[0];
        int x, y;
        switch( msgTypeName ){
            case "BombDropped":
                x = Integer.parseInt( args[1] );
                y = Integer.parseInt( args[2] );
                return new BombDropped().setId( getNextId() ).setPositionX( x ).setPositionY( y );
            case "BombExploded":
                return new BombExploded().setId( getNextId() );
            case "ClientLogin":
                if( args.length < 2 ) throw new IllegalArgumentException( "Too few arguments" );
                return new ClientLogin().setPlayerName( args[1] );
            case "ClientLogout":
                return new ClientLogout();
            case "DropBomb":
                x = Integer.parseInt( args[1] );
                y = Integer.parseInt( args[2] );
                return new DropBomb().setPlayerName( playerName ).setPositionX( x ).setPositionY( y );
            case "GameOver":
                if( args.length < 2 ) throw new IllegalArgumentException( "Too few arguments." );
                return new GameOver().setWinnerName( args[1] );
            case "LoginFailed":
                if( args.length < 2 ) throw new IllegalArgumentException( "too few arguments." );
                StringBuilder reason = new StringBuilder();
                for( int i=1 ; i<args.length ; ++i ){
                    if( i>0 ) reason.append( " " );
                    reason.append( args[i] );
                }
                return new LoginFailed().setReason( reason.toString() );
            case "LoginSucceeded":
                if( args.length < 3 ) throw new IllegalArgumentException( "Too few arguments." );
                x = Integer.parseInt( args[1] );
                y = Integer.parseInt( args[2] );
                return new LoginSucceeded().setInitalPositionX( x ).setInitalPositionY( y );
            case "PlayerHit":
                if( args.length < 2 ) throw new IllegalArgumentException( "Too few arguments." );
                return new PlayerHit().setPlayerName( args[1] );
            case "PlayerJoined":
                if( args.length < 3 ) throw new IllegalArgumentException( "Too few arguments." );
                x = Integer.parseInt( args[1] );
                y = Integer.parseInt( args[2] );
                return new PlayerJoined().setPlayerName( playerName ).setPositionX( x ).setPositionY( y );
            case "PlayerMoved":
                if( args.length < 2 ) throw new IllegalArgumentException( "Too few arguments." );
                PlayerMoved.Direction direction = PlayerMoved.Direction.valueOf( args[1].toUpperCase() );
                return new PlayerMoved().setPlayerName( playerName ).setDirection( direction );
            case "StartGame":
                maze = createMaze();
                return new StartGame().setMaze( maze );
            case "UpdateGame":
                return new UpdateGame().setMaze( maze );
            default:
                throw new IllegalArgumentException( "Failed to instantiate a message of type '"+msgTypeName+"'." );
        }
    }

    private synchronized int getNextId(){
        return nextId++;
    }

    private Maze createMaze(){
        Maze maze = new Maze();
        int rows = 20;
        int cols = 20;
        List<Field> fields = new ArrayList<>( rows*cols );
        Field.Content[] allContents = Field.Content.values();
        for( int iR=0 ; iR<rows ; ++iR ){
            for( int iC=0 ; iC<cols ; ++iC ){
                int indexToUse = (int)( Math.random()*allContents.length );
                Field.Content content = allContents[ indexToUse ];
                fields.add( new Field().setPositionX(iC).setPositionY(iR).setContent( content ) );
            }
        }
        maze.setFields( new ArrayList<>(rows*cols) );
        return maze;
    }

    String messageToString( Message msg ){
        StringBuilder ans = new StringBuilder();
        ans.append( msg.getClass().getSimpleName() ).append( ":{" );

        if( msg instanceof BombDropped ){
            BombDropped casted = (BombDropped)msg;
            ans.append( " id:" ).append( casted.getId() );
            ans.append( " , x:" ).append( casted.getPositionX() );
            ans.append( " , y:" ).append( casted.getPositionY() );
        }else if( msg instanceof BombExploded ){
            BombExploded casted = (BombExploded)msg;
            ans.append( " id:" ).append( casted.getId() );
        }else if( msg instanceof ClientLogin ){
            ClientLogin casted = (ClientLogin)msg;
            ans.append( " playerName:" ).append( casted.getPlayerName() );
        }else if( msg instanceof ClientLogout ){
            // No properties to print
        }else if( msg instanceof DropBomb ){
            DropBomb casted = (DropBomb)msg;
            ans.append( " playerName:" ).append( casted.getPlayerName() );
            ans.append( " , x:" ).append( casted.getPositionX() );
            ans.append( " , y:" ).append( casted.getPositionY() );
        }else if( msg instanceof GameOver ){
            GameOver casted = (GameOver)msg;
            ans.append( " winnerName:" ).append( casted.getWinnerName() );
            ans.append( " , highscore:[" );
            boolean isFirst = true;
            for( HiscoreEntry entry : casted.getHighscore() ){
                if( isFirst ){ isFirst=false; }else{ ans.append(","); }
                ans.append( "\n\t{ player:" ).append( entry.getPlayerName() ).append( " , score:" ).append( entry.getScore() ).append( " }" );
            }
            ans.append( " \n]" );
        }else if( msg instanceof LoginFailed ){
            LoginFailed casted = (LoginFailed)msg;
            ans.append( " reason:" ).append( casted.getReason() );
        }else if( msg instanceof LoginSucceeded ){
            LoginSucceeded casted = (LoginSucceeded)msg;
            ans.append( " x:" ).append( casted.getInitalPositionX() );
            ans.append( " y:" ).append( casted.getInitalPositionY() );
        }else if( msg instanceof PlayerHit ){
            PlayerHit casted = (PlayerHit)msg;
            ans.append( " playerName:" ).append( casted.getPlayerName() );
        }else if( msg instanceof PlayerJoined ){
            PlayerJoined casted = (PlayerJoined)msg;
            ans.append( " playerName:" ).append( casted.getPlayerName() );
            ans.append( " , x:" ).append( casted.getPositionX() );
            ans.append( " , y:" ).append( casted.getPositionY() );
        }else if( msg instanceof PlayerMoved ){
            PlayerMoved casted = (PlayerMoved)msg;
            ans.append( " playerName:" ).append( casted.getPlayerName() );
        }else if( msg instanceof StartGame ){
            StartGame casted = ((StartGame)msg);
            Maze maze = casted.getMaze();
            ans.append( " maze:" ).append( maze==null?"null":maze.getClass().getSimpleName() );
        }else if( msg instanceof UpdateGame ){
            UpdateGame casted = (UpdateGame)msg;
            Maze maze = casted.getMaze();
            ans.append( " maze:" ).append( maze==null?"null":maze.getClass().getSimpleName() );
        }

        ans.append( " }" );
        return ans.toString();
    }

    void writeToStdout( String msg ){
        System.out.println( stdoutPrefix + msg );
    }

}
