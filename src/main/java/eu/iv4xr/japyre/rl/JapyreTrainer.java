package eu.iv4xr.japyre.rl;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import eu.iv4xr.japyre.connection.SendCommandOverSocket;

public class JapyreTrainer<Configuration,State> {

	SendCommandOverSocket sendcommand ;
	IStatefulGame<State> statefulgame ;
	int stepCount = 0 ;
	
	JapyreTrainerState trainerInternalState = JapyreTrainerState.IDLE ;
	
	public enum JapyreTrainerState {
		IDLE, TRAINING, READY_FOR_DRIVING, DRIVING
	}
	
	
	public JapyreTrainer(String host, int port) {
		sendcommand = new SendCommandOverSocket(host,port) ;
	}

	public JapyreTrainer<Configuration,State> attachProblem(IStatefulGame<State> statefulgame) {
		this.statefulgame = statefulgame ;
		return this ;
	}
	
	void resetTheStatefulGame() {
		statefulgame.reset();
		stepCount = 0 ;
	}
	
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
	
	public void closeConnection() throws IOException {
		sendcommand.close();
	}
	
	public void train(Configuration trainerCofig, 
			          String fileToSaveTrainedModel) 
			          throws IOException 
	{	
		System.out.println("> Start training...") ; 
		String ok = sendcommand.sendCommand("START_TRAINING", trainerCofig, String.class) ;
		if (! ok.equals("OK__")) {
			System.out.println("> The START command does not return an OK.") ;
			throw new IOException() ;
		}
		this.trainerInternalState = JapyreTrainerState.TRAINING ;
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
		if(fileToSaveTrainedModel != null) {
			System.out.println("> Saving the trained-model to " + fileToSaveTrainedModel) ; 
			sendcommand.sendCommand("SAVE", fileToSaveTrainedModel, String.class) ;
		}
		this.trainerInternalState = JapyreTrainerState.IDLE ;
	}
	
	public void loadTrainedModel(String fileToLoadTrainedModel) throws IOException {
		String ok = sendcommand.sendCommand("LOAD", fileToLoadTrainedModel, String.class) ;
		if (! ok.equals("OK__")) {
			System.out.println("> The LOAD command does not return an OK.") ;
			throw new IOException() ;
		}
		this.resetTheStatefulGame();
		System.out.println("> A trained-model is loaded from " + fileToLoadTrainedModel) ; 	
		this.trainerInternalState = JapyreTrainerState.READY_FOR_DRIVING ;
	}

	public void doNextTrainedAction() throws IOException {
		if (this.trainerInternalState == JapyreTrainerState.READY_FOR_DRIVING) {
			trainerInternalState = JapyreTrainerState.DRIVING ;
		}
		if (trainerInternalState != JapyreTrainerState.DRIVING) {
			System.out.println("> You need to load a trained-model first before driving.") ;
			throw new IOException() ;
		}
		String action = sendcommand.sendCommand("GET_NEXT_TRAINEDACTION", statefulgame.getState() , String.class) ;
		statefulgame.execute(action) ;
		System.out.println("> " + stepCount + ":" + action) ;
		stepCount++ ;
	}
	
}
