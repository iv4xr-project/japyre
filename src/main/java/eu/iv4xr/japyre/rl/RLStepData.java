package eu.iv4xr.japyre.rl;

/**
 * Just a tuple of (observation,reward,...) that a GymEnv step-function needs to
 * construct.
 * 
 * <p>The type parameter "Observation" is usually an array (1D/2D, etc) of numerics.
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
