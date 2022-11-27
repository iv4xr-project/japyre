/**
 * Provide classes to enable the use of Python Reinforcement Learning (RL)
 * libraries to solve a problem in the Java-side. There are two main classes:
 * 
 * <ul>
 * <li>{@link eu.iv4xr.japyre.rl.IJavaGymEnv}: implement this interface to
 * create a Java-side RL Gym-environment.
 * <li>{@link eu.iv4xr.japyre.rl.GymEnvServer}: use this to deploy a GymEnv as a
 * server so that it can be targeted by a Python-side RL algorithm.
 * </ul>
 */
package eu.iv4xr.japyre.rl;