# Japyre

Japyre is a library provides a socket-based connection between Java and Python to enable Reinforcement Learning (RL) libraries in Python to be used to solve problems formulated in Java. Which RL library to use is up to you, Japyre only provides a convenient Java-Python connection, and several basic interfaces to plug-in "problems" and "RL" to the connection.

### Main concepts

A **StatefulGame** represents some stateful environment. The game allows **actions** to be executed on it, that will change its state. Some states might be _terminal_ (no further actions are possible on these states). But it is also possible that some games have no terminal state. Whenever an action is executed, the game also gives back some **reward** (which can 0, or even negative). As a general problem to solve, we are interested in figure out how to play the game (so, deciding what action to do at each step during the play) that would maximize the total reward. We leave it open, how long a play should be (should be N steps, or until a terminal state is reached?). We also leave it open whether we actually want to find the best play, or simply a good play.

A **policy** is just some formulation of "how to play". Usually it is a function that takes the current game state, and decides which action to take. A more powerful policy can take the history of some length towards the current state to decide the action to take.

A **value-function** v\*(s) gives the theoretical value of the state s, which is the total reward we would get from the best play that starts in the state s. We don't have this function :) because we don't know what this best play is (or else then we have no problem to solve), but we can try to construct some approximation of it, V(s). Another value function is q\*(s,a), which gives the total reward if we start a play in the state s, and then we do the action a to arrive in some state s', and after which we follow the best play. Again, this is a theoretical function that we don't actually have. We can try to construct some approximation of it, Q(s,a). Approximation-functions such as V() and Q() are also called **models**.

The goal of the RL side could be to "learn" a V-function or a Q-function (model), from which a policy is defined (e.g. a greedy policy that simply choose the action with the greates Q-value). Or, it could be to directly learn a policy.

### How to plug in my stuff

At the Java-side, you need to formulate your problem as an implementation of the interface `IStatefulGame`.

At the Python-side, wrap your RL algorithm as an implementation of the (abstract) classes `Model` and `ModelTrainer`. An RL algorithm is seen as a 'trainer' to train a 'model'. As a concept, a model can be seen as a function that embodies lessons learned during the training.

To train the model:

  1. At the Python-side, run the function `deployTrainingServer()`, passing to it an instance of your `ModelTrainer`.

  2. At the Java-side, run the function `JapyreTrainer()`, passing to it an instance of your StatefulGame and some configuration parameters of the training.
JapyreTrainer will make a connection to the TrainingServer at the Python-side, and then automatically start the training process for some number of episodes. At the end, the trained model will be saved in a file.

To use a trained model:

  1. At the Python-side, run the function `deployModelServer()`, passing to it an instance of `Model`; this can be just an empty model.

2. At the Java-side, run the function `JapyreDriver()`, passing to it an instance of your StatefulGame.
JapyreDriver will make a connection to the ModelServer at the Python-side, and ask the ModelServer to load a trained-model from some file.
The model is assumed to come with some policy, and then the Driver will just run this policy on the Game.
