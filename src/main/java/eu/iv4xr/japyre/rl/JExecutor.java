package eu.iv4xr.japyre.rl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.function.Function;

import eu.iv4xr.japyre.connection.ObjectReaderWriter_OverSocket;
import eu.iv4xr.japyre.connection.SendCommandClient;
import eu.iv4xr.japyre.connection.SendCommandClient.A;
import eu.iv4xr.japyre.connection.SendCommandClient.SomeClass;

/**
 * This class implement a Java-client that can be used to query a trained model at the
 * Python-side. Such a model could be a reinforcement-learning model that has been 
 * trained. To use this client, the Python-side needs to launch an instance of ModelServer,
 * and configure it properly.
 * 
 * <p>This client can then connect to such a ModelServer. The command {@link #loadModel(String)}
 * instructs the Python-side to load with the given id.
 * 
 * <p>Through {@link #observeFunction} and {@link #actionExecutor} this client is 
 * connected to some program (e.g. some SUT). Let's call this program Target. Through
 * those functions, we can observe the state of the Target, and execute actions 
 * on the Target.
 * 
 * <p>The method {@link #getAndExecuteAction()} send the current Target's state to the 
 * ModelServer (which will pass it to the loaded model) to obtain the next action to
 * do. The method then executes the action on the Target.
 * 
 * @author Wish
 *
 */
public class JExecutor<Observation> {
	
	String host ;
	int port ;
    //ServerSocket serversocket ;
	SendCommandClient commander ;
    public Function<Void,Observation> observeFunction ;
    public Function<String[],Void> actionExecutor ;
    public boolean debug = false ;
    
    /**
     * Constructor
     * @param host  the host where the corresponding ModelServer runs
     * @param port  and the port of that host
     * @param observeFunction  a function that obtains the current state of the Target.
     * @param actionExecutor   a function that executes an action on the Target.
     */
    public JExecutor(String host, 
    		int port, 
    		Function<Void,Observation> observeFunction,
    		Function<String[],Void> actionExecutor) {
    	this.host = host ;
    	this.port = port ;
    	this.observeFunction = observeFunction ;
    	this.actionExecutor = actionExecutor ;
    }
    
    /**
     * Start the client (it will then make connection to the ModelServer on
     * the specified host and port).
     */
    public void start() {
    	commander = new SendCommandClient(host,port) ;
    	commander.turnDebugMode(debug);
    }
    
    /**
     * Instruct the connected ModelServer to load the model with the given id.
     */
    public void loadModel(String modelId) throws IOException {
    	Boolean ok = commander.sendCommand("LOAD", modelId, Boolean.class) ;
    	//System.out.println(">>>") ;
    	if (ok==null || !ok)
    		throw new IOException("Cannot load model " + modelId) ;
    }
    
    /**
     * Send the current Target's state to the ModelServer to obtain what the best
     * next action to do, and then execute this action on the Target.
     */
    public void getAndExecuteAction() throws IOException {
    	Observation obs = observeFunction.apply(null) ;
    	String[] dummy = {""} ;
    	Object action_ = commander.sendCommand("GETNEXTACTION", obs, dummy.getClass()) ;
    	if (action_ == null || Array.getLength(action_)<1)
    		throw new IOException("Cannot obtain next action...") ;
    	String[] action = new String[Array.getLength(action_)] ;
    	for (int k=0; k < action.length; k++) {
    		action[k] = (String) Array.get(action_, k) ;
    	}
    	actionExecutor.apply(action) ;
    }
    
    public void close() throws IOException {
    	commander.close() ;
    }
    
    // just for testing
    public static void main(String[] args) throws IOException {
    	
    	Function<Void,A> observe = v -> {
    		A a1 = new A(0,"Sponge") ;
        	A a2 = new A(99,"Patrick") ;
        	a1.addChild(a2) ;
        	a1.addChild(a2) ;
        	return a1 ;
    	} ;
    	
    	Function<String[],Void> actionExecutor = argz -> {
    		for(int k=0; k<argz.length; k++) {
    			System.out.println(">> arg " + k + "=" + argz[k]) ;
    		} ;
    		return null ;
    	} ;
    	
    	
    	JExecutor<A> jexecutor = new JExecutor("127.0.0.1",9999,observe,actionExecutor) ;
    	jexecutor.debug = true ;
    	jexecutor.start();
    	
    	jexecutor.loadModel("model super");
    	jexecutor.getAndExecuteAction();
    	
    	jexecutor.close() ;
    	
    }

}
