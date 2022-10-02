package eu.iv4xr.japyre.rl;

import java.util.List;

/**
 * An interface allowing an AI-gym-env a la OpenAI Gym-env to be implemented in Java. You can implement
 * this interface to have your Java-Gym, which can be targeted by Python RL
 * libraries. The interface is pretty simple, and quite similar to OpenAI Gym
 * interface. To actually target a Java-Gym from Python, you need to deploy it
 * as a server using the helper class {@link eu.iv4xr.japyre.rl.GymEnvServer}.
 * 
 * <p> Since Python cannot directly target Java, you will also need a Python-side
 * Gym-env that provides the actual Env, which internally calls the Java-side
 * Gym-env by sending requests to a Java-side GymEnvServer.
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
