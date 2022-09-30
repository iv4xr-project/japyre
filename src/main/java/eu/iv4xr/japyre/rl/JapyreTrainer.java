package eu.iv4xr.japyre.rl;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import eu.iv4xr.japyre.connection.SendCommandOverSocket;

/**
 * A trainer is used to train a model (or a policy, depending of the used training
 * algorithm) on a "problem" formulated as a StatefulGame. The trainer will need
 * to hold a reference to this StatefulGame. We can then invoke methods like
 * train() to train a model. A "training" will involve trying out different actions
 * on the StatefulGame (this will be controlled by the method train()).
 * 
 * <p>The model will be held at the Python-side, and will be refined using some
 * training algorithm at the Python-side as well. This Java-side trainer also
 * allows a trained-model to saved to a file, and loaded again to be used to
 * drive the StatefulGame.
 *
 * @param <ModelConfiguration> The type representing the parameters of the model
 *       to train. E.g. if it is a Q-table, how many entries it has. This 
 *       ModelConfiguration-type should be serializable to Json.
 *       
 * @param <State> The type representing the state of the StatefulGame. It should be 
 *                serializable to Json.
 *                
 * @author Wish              
 */
public class JapyreTrainer<ModelConfiguration,State> {

	SendCommandOverSocket sendcommand ;
	IStatefulGame<State> statefulgame ;
	int stepCount = 0 ;
	boolean modelPresent = false ;
	
	/**
	 * Create an instance of a Trainer. It will connect to the Python-side
	 * training-server at the specified ip-address and port.
	 */
	public JapyreTrainer(String host, int port) {
		sendcommand = new SendCommandOverSocket(host,port) ;
	}

	/**
	 * Attach an instance of StatefulGame to this trainer. 
	 */
	public JapyreTrainer<ModelConfiguration,State> attachProblem(IStatefulGame<State> statefulgame) {
		this.statefulgame = statefulgame ;
		return this ;
	}
	
	void resetTheStatefulGame() {
		statefulgame.reset();
		stepCount = 0 ;
	}
	
	/**
	 * A class to wrap state-information to be sent to the Python-side training-server.
	 */
	public static class StateInfo<St> {
		
		/**
		 * The current state
		 */
		St st ;
		
		/**
		 * The action that leads to the current state.
		 */
		String prev ;
		
		/**
		 * The reward given to the previous action.
		 */
		float rew ;
		
		/**
		 * Possible actions that are available in the current state.
		 */
		List<String> opts  ;
		
		public StateInfo(St currentState, String previousAction, float reward) {
			st = currentState ;
			prev = previousAction ;
			rew = reward ;
		}
	}
	
	/**
	 * Close the connection to the Python-side training-server.
	 */
	public void closeConnection() throws IOException {
		sendcommand.close();
	}
	
	/**
	 * Create a fresh untrained model with the specified configuration. The model
	 * is created at the 
	 * @param modelConfig
	 * @throws IOException
	 */
	public void createUntrainedModel(ModelConfiguration modelConfig) 
	          throws IOException  {
		System.out.println("> Creating a fresh model ...") ; 
		String ok = sendcommand.sendCommand("MK_FRESH_MODEL", modelConfig, String.class) ;
		if (! ok.equals("OK__")) {
			System.out.println("> The MK_FRESH_MODEL command does not return an OK.") ;
			throw new IOException() ;
		}
		modelPresent = true ;
	}
	
	/**
	 * Run a training session. This will train the model (at the Python-side).
	 * The training-configuration specifies e.g. how many episodes does the
	 * training consist, the max-length of each episode, etc.
	 */
	public void train(TrainingConfiguration trainingCofig) 
			          throws IOException 
	{	
		System.out.println("> Start training...") ; 
		this.resetTheStatefulGame();
		int episodeCount = 0 ;
		List<Integer> stepCounts = new LinkedList<>() ;
		String previousAction = null ;
		float reward = 0 ;
		boolean done = false ;
		while (!done) {			
			StateInfo<State> arg = new StateInfo<>(statefulgame.getState(),previousAction,reward) ;	
			arg.opts = statefulgame.getCurrentlyPossibleActions() ;
			String nextAction = sendcommand.sendCommand("GET_NEXTACTION", arg, String.class) ;
			stepCount++ ;
			switch(nextAction)  {
			    case "TRAINING_END" :  
			    case "EPISODE_END" :   
			    	System.out.println("    epidose " + episodeCount + " ends, #actions=" + stepCount) ; 
					stepCounts.add(stepCount) ;
					this.resetTheStatefulGame();
					previousAction = null ;
					reward = 0 ;
					episodeCount++ ;
					if (nextAction.equals("TRAINING_END"))   
						done = true ;
					break ;
				default :
					reward = statefulgame.execute(nextAction) ;
					previousAction = nextAction ;	
			}
		}
		int totalNumOfSteps = stepCounts.stream().reduce(0, (x,r) -> x+r) ;
		System.out.println("> Training ended; #episodes=" 
				+ episodeCount + ", tot. #actions=" 
				+ totalNumOfSteps) ;
	}
	
	public void saveModel(String filename) throws IOException {
		if(filename != null && filename.length()>0) {
			System.out.println("> Saving the trained-model to " + filename) ; 
			sendcommand.sendCommand("SAVE", filename, String.class) ;
		}
		else {
			System.out.println("> Trying to save the model, but the filename is empty.") ; 
		}
	}
	
	public void loadTrainedModel(String fileToLoadTrainedModel) throws IOException {
		String ok = sendcommand.sendCommand("LOAD", fileToLoadTrainedModel, String.class) ;
		if (! ok.equals("OK__")) {
			System.out.println("> The LOAD command does not return an OK.") ;
			throw new IOException() ;
		}
		this.resetTheStatefulGame();
		modelPresent = true ;
		System.out.println("> A trained-model is loaded from " + fileToLoadTrainedModel) ; 	
	}

	public void doNextTrainedAction() throws IOException {
		if (!modelPresent) {
			System.out.println("> You need first have a model to be able to train it.") ;
			throw new IOException() ;
		}
		String action = sendcommand.sendCommand("GET_NEXT_TRAINEDACTION", statefulgame.getState() , String.class) ;
		statefulgame.execute(action) ;
		System.out.println("> " + stepCount + ":" + action) ;
		stepCount++ ;
	}
	
}
