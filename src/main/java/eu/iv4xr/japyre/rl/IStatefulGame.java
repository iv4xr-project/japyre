package eu.iv4xr.japyre.rl;

import java.util.List;

/**
 * A StatefulGame is just a generic representation of a stateful environment.
 * On this environment we can do actions, that may change the environment
 * state. Some states may be terminal. On a terminal state, no further action
 * is possible.
 * 
 * <p>Executing an action gives some reward (which can be 0, or even negative).
 * The goal of such a game is to play it (by giving it a sequence of actions)
 * such that we get as much total reward as possible.
 * 
 * <p>An algorithm that autonomously plays the game can also be seen as a "policy",
 * which is essentially a function that decides what the next action to do, based
 * on the current state (or, a more powerful policy might use the knowledge on the 
 * trace of past states until the current state). 
 * 
 * <p>E.g. a machine learning technique, e.g. RL, can be used to train a policy
 * with the goal of playing the game so that it gets as much total reward as
 * possible.
 * 
 * @param <State> The type of the state of this game. It should be serializable to Json.
 *    E.g. primitive types, or a class whose fields are all primitive types are
 *    Json-serialization.
 * 
 * @author Wish
 */
public interface IStatefulGame<State> {
	
	/**
	 * Reset the Game to its initial state.
	 */
	public void reset() ;
	
	/**
	 * Get the Game's current state.
	 */
	public State getState() ;
	
	/**
	 * True if the current state is a terminal state; else false.
	 */
	public boolean isTerminalState() ;
	
	/**
	 * The list of actions which are currently possible. An action is
	 * identified by its name (a string).
	 */
	public List<String> getCurrentlyPossibleActions() ;
	
	/**
	 * Execute the given action. This may move the Game to a new state.
	 * The method also returns the reward of doing that action.
	 */
	public float execute(String action) ;

}
