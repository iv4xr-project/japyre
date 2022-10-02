# Japyre

Japyre is a library provides a socket-based connection between Java and Python to enable Reinforcement Learning (RL) libraries in Python to be used to solve problems formulated in Java. Which RL library to use is up to you, Japyre only provides a convenient Java-Python connection, and several basic interfaces to plug-in "problems" and "RL" to the connection.

### Main concepts

Following an OpenAI's way of looking at RL, we formulate the 'problem' on which we apply RL as a 'gym-environment'.
A **gym-environment** represents some stateful environment. The environment allows **actions** to be executed on it, that will change its state. Some states are _terminal_ (no further actions are possible on these states). Such a state can represent a goal to achieve (so, after achieving it we are done), but it can also represent a dead-state (e.g. in a game this can represent a state where the player loses the game).

Whenever an action is executed, the environment also gives back some **reward** (which can 0, or even negative). A _run_ on the environment is a series of actions executed on it (starting at its initial state).
As a general problem to solve with RL, we are interested in figure out how to run the environment (so, deciding what action to do at each step during the interactions) that would maximize the total reward. We leave open whether we actually want to find the best run, or simply a good run.

A **policy** is just some formulation of "how to run" the environment. Usually it is a function that takes the current environment state, and decides which action to take. A more powerful policy can take the history of some length towards the current state to decide the action to take.

A **value-function** v\*(s) gives the theoretical value of the state s, which is the total reward we would get from the best run that starts in the state s. We don't have this function :) because we don't know what this best run is (or else then we have no problem to solve), but we can try to construct some approximation of it, V(s). Another value function is q\*(s,a), which gives the total reward if we start a run in the state s, and then we do the action a to arrive in some state s', and after which we follow the best play. Again, this is a theoretical function that we don't actually have. We can try to construct some approximation of it, Q(s,a). Approximation-functions such as V() and Q() are also called **models**.

The goal of RL could be to "learn" a V-function or a Q-function (model), from which a policy is defined (e.g. a greedy policy that simply choose the action with the greatest Q-value). Or, it could be to directly learn a policy.

### How to plug in my stuff  ... to do: completing this part

At the Java-side, you need to formulate your problem as an implementation of the interface [`IJavaGymEnv`](src/main/java/eu/iv4xr/japyre/rl/IJavaGymEnv.java).

At the Python-side, write a subclass of OpenAI (or StableBaselines) `gym.Env`, implementing its main methods (constructor, reset(), step(), close()) so that they call the corresponding functions at the Java-side implementation of IJavaGymEnv. To call the Java-side, a socket-based client-server programs will be provided.

To train the model:

  1. At the Java-side, run the function `JapyreTrainer()`, passing to it an instance of your StatefulGame and some configuration parameters of the training.
JapyreTrainer will make a connection to the TrainingServer at the Python-side, and then automatically start the training process for some number of episodes. At the end, the trained model will be saved in a file.

To use a trained model:

  1. At the Python-side, run the function `deployModelServer()`, passing to it an instance of `Model`; this can be just an empty model.

2. At the Java-side, run the function `JapyreDriver()`, passing to it an instance of your StatefulGame.
JapyreDriver will make a connection to the ModelServer at the Python-side, and ask the ModelServer to load a trained-model from some file.
The model is assumed to come with some policy, and then the Driver will just run this policy on the Game.
