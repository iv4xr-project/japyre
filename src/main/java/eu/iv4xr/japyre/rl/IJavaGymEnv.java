package eu.iv4xr.japyre.rl;

import java.util.List;

/**
 * An interface allowing an AI-gym to be implemented in Java. You can implement
 * this interface to have your Java-Gym, which can be targeted by Python RL
 * libraries. The interface is pretty simple, and quite similar to OpenAI Gym
 * interface.
 * 
 * <p> Since Python cannot directly target Java, you will still need a Python-side
 * Gym, but since the methods are almost the same, implementing that should be
 * simple.
 * 
 * <p> The type parameter "Observation" is used to represent the Gym's current state
 * in a data-structure suitable for RL algorithms. Usually it is an array (1D/2D, etc) 
 * of numerics.
 * 
 * @author Wish
 *
 */
public interface IJavaGymEnv<Observation> {
	
	/**
	 * Reset the environment back to its initial state.
	 */
	public Observation reset() ;
	
	/**
	 * Close and clean-up the environment, if needed. The default implementation
	 * is to do nothing.
	 */
	public default void close() { }
	
	/**
	 * Specify the set of possible actions this environment can do.
	 * (so... this implies that only an environment with discrete actions
	 * can be handled). 
	 */
	public List<String> actionSpace() ;
	
	/**
	 * Perform the specified, return a tuple of (observation,reward,episode-done-flag),
	 * wrapped as an instance of RLStepData.
	 */
	public RLStepData<Observation> step(String action) ;
		
}
