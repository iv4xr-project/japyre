package eu.iv4xr.japyre.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import eu.iv4xr.japyre.rl.IJavaGymEnv;
import eu.iv4xr.japyre.rl.RLStepData;

/**
 * Deploy an instance of JavaGymEnv as a server that can respond to requests
 * from a Python-side client. At the Python-side we will need another GymEnv
 * that twins the Java-side Gym. When applying an RL algorithm it is the Python
 * GymEnv that we will give to the RL-algorithm. But this Python-side Env will
 * then call this Java GymEnv through this server.
 *
 * @param <Observation> Some type representing observations that Java GymEnv
 *                      produces. These will be sent to the Python-side as JSon
 *                      strings. The python-side is responsible for tranlsating
 *                      this type to some representation suitable for whatever
 *                      the RL-algorithm to use.
 *
 * @author Wish
 */
public class GymEnvServer<Observation> {
	
    int port ;
    ServerSocket serversocket ;
    ObjectReaderWriter_OverSocket readerwriter ;
    public IJavaGymEnv<Observation> gymEnv ;
    
    public GymEnvServer(int port, IJavaGymEnv<Observation> gymEnv) throws IOException  {
    	this.port = port ;
    	serversocket = new ServerSocket(port) ;
    	this.gymEnv = gymEnv ;
    }
    
    public static class TrainingCommand {
    	public String cmd  ;
    	public String arg  ;
    }
    
    static final String OK_  = "OK__" ;
    
    public void start() throws IOException {
    	System.out.println(String.format("> Starting a GynEnv-server at port %s.", port));
 
    	// accept a connection request from a client; for our purpose
    	// a single-client setup is enough:
    	Socket clientsocket = serversocket.accept() ;
    	readerwriter = new ObjectReaderWriter_OverSocket(clientsocket) ;
    	boolean keepRunning = true ;
    	while (keepRunning) {
    		TrainingCommand cmd =  readerwriter.read(TrainingCommand.class) ;
    		if (cmd == null) {
    			System.out.println(String.format("> The client left."));
    			// for now, we will close the server as well, but probably we don't
    			// have to. 
    			// TODO.
    			keepRunning = false ;
    			break ;
    		}
    		switch (cmd.cmd) {
    		  case "RESET" : // Python wants the GymEnv to reset its state
    			  Observation o = gymEnv.reset(); 
    			  readerwriter.write(o) ;
    			  break ;
    		  case "CLOSE" : // Python wants the GymEnv to close
    			  gymEnv.close(); 
    			  readerwriter.write(OK_) ;
    			  break ;
    		  case "GET_ACTIONSPACE" : // Python wants the GymEnv to send back its action-space
    			  List<String> actions = gymEnv.actionSpace() ;
    			  readerwriter.write(actions) ;
    			  break ;	  
    		  case "STEP"  :  // Python wants the GymEnv to do one step and sends back new observation, reward etc
    			  String action = cmd.arg ;
    			  RLStepData<Observation>  r = gymEnv.step(action) ;
    			  readerwriter.write(r) ;
    			  break ;
    		  case "KILL" : // Python wants to close this server :|
    			  keepRunning = false ;
    		}	
    	}
    	System.out.println(String.format("> Closing GynEnv-server..."));
    	readerwriter.close(); 
    	clientsocket.close();
    	serversocket.close();
    	System.out.println(String.format("> The GynEnv-server is closed."));

    }
    
    public void turnDebugMode(boolean on) {
		ObjectReaderWriter_OverSocket.debug = on ;
	}

}
