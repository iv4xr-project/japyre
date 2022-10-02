package eu.iv4xr.japyre.klad;

/**
 * Representing parameters to be sent to the Python-side RL before we run
 * a training session. These are e.g. the number of episodes that the training
 * will last, the maximum length of an episode, alpha, and gamma.
 * 
 * <p>Extend this class (through subclassing) to add more parameters.
 */
public class TrainingConfiguration {
	
	public int numberOfEpisodes = 1 ;
	public int maxEpisodeLength = 10 ;
	public float alpha = 0.5f ; // learning rate
	public float gamma = 0.9f ; // reward temporal discount factor
	
	public TrainingConfiguration() { }
	public TrainingConfiguration(int numberOfEpisodes, int maxEpisodeLength, float alpha, float gamma) {
		this.numberOfEpisodes = numberOfEpisodes ;
		this.maxEpisodeLength = maxEpisodeLength ;
		this.alpha = alpha ;
		this.gamma = gamma ;
	}

}
