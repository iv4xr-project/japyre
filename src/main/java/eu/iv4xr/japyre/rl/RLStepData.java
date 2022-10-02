package eu.iv4xr.japyre.rl;

/**
 * Just a tuple of (observation,reward,...) that a GymEnv step-function needs to
 * construct; see {@link eu.iv4xr.japyre.rl.IJavaGymEnv#step(String)}.
 * 
 * <p>
 * The type parameter "Observation" represents an observation produced by a
 * (Java-side) GymEnv, see {@link eu.iv4xr.japyre.rl.IJavaGymEnv#step(String)}.
 * It can be any type that can be serialized to Json for transport over socket.
 * The Python-side is responsible for converting this observation to a data
 * structure accepted by the RL algorithm that we want to use there.
 * 
 * @author Wish
 */
public class RLStepData<Observation> {
	
	public Observation obs ;
	
	/**
	 * Reward
	 */
	public float rw ;
	
	/**
	 * To indicate episode-end (terminal state) is reached.
	 */
	public boolean end ;
	
	/** 
	 * We can pass other info ... but for now I just fix this to nothing.
	 */
	public Object etc = null ;
	
	public RLStepData(Observation obs, float reward, boolean weAreDone) {
		this.obs = obs ;
		this.rw = reward ;
		end = weAreDone ;
	}
	
	@Override
	public String toString() {
		return "Obs: " + obs + "\nReward: " + rw + "\nEpisode-end: " +  end ;
	}

}
