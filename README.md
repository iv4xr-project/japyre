# Japyre

Japyre is a library provides a socket-based connection between Java and Python to enable Reinforcement Learning (RL) libraries in Python to be used to solve problems formulated in Java. Which RL library to use is up to you, Japyre only provides a convenient Java-Python connection, and several basic interfaces to plug-in "problems" and "RL" to the connection.

### Some basic concepts

Following an OpenAI's way of looking at RL, we formulate the 'problem' on which we apply RL as a 'gym-environment'. Because 'gym-environment' is a bit mouthful, we sometime refer to it just by the term 'gym'.


A **gym-environment** (or **gym**, to shorthand it) represents some stateful environment. The environment allows **actions** to be executed on it, that will change its state. Some states are _terminal_ (no further actions are possible on these states). Such a state can represent a goal to achieve (so, after achieving it we are done), but it can also represent a dead-state (e.g. in a game this can represent a state where the player loses the game).

Whenever an action is executed, the environment also gives back some **reward** (which can 0, or even negative). A _run_ on the environment is a series of actions executed on it (starting at its initial state).
As a general problem to solve with RL, we are interested in figure out how to run the environment (so, deciding what action to do at each step during the interactions) that would maximize the total reward. We leave open whether we actually want to find the best run, or simply a good run.

A **policy** is just some formulation of "how to run" the environment. Usually it is a function that takes the current environment state, and decides which action to take. A more powerful policy can take the history of some length towards the current state to decide the action to take.

A **value-function** v\*(s) gives the theoretical value of the state s, which is the total reward we would get from the best run that starts in the state s. We don't have this function :) because we don't know what this best run is (or else then we have no problem to solve), but we can try to construct some approximation of it, V(s). Another value function is q\*(s,a), which gives the total reward if we start a run in the state s, and then we do the action a to arrive in some state s', and after which we follow the best play. Again, this is a theoretical function that we don't actually have. We can try to construct some approximation of it, Q(s,a). Approximation-functions such as V() and Q() are also called **models**.

The goal of RL could be to train/learn a V-function or a Q-function (model), from which a policy can be defined (e.g. a greedy policy that simply choose the action with the greatest Q-value). Or, it could be to directly train a policy.

### Architecture

Imagine, you have a gym-environment (or just 'gym' to shorthand it) in _Java_, and you want to train a model using a _Python_ RL library. Generally, what you need to do is: (1) implement this gym-environment as a class G in Java, and (2) implement a 'mirror'-API, let's call it H, in Python that implements typical gym-environment methods, but forward the calls to G through a client-server scheme provided by Japyre. To train a model using some RL-algorithm A, we use A on H instead of using it on G (well, A can't directly target G, as G is in Java).

The picture below shows the general architecture:

![Architecture](./docs/architecture.png)

The RL-algorithm on the right only sees the mirror-gym. The algorithm will want to call methods like `reset()` and `step()` from this gym (1 in the picture). The mirror-gym simply forwards the call to the `GymEnvClient` (2 in the picture), which in turn forwards the call (3 in the picture) to the real Gym in Java via a TCP/IP socket connection.

The `GymEnvClient` and `GymEnvServer` components are provided by Japyre. Your part is to construct the Gym and mirror-Gym.  A link to an example of how to implement this scheme is provided below.

### How to build

The Java binary can be built using Maven. Just use `mvn compile` to build, and `mvn install` to install the Java binary in your local Maven repository.

The Python modules can be found in `python` subdir. No packaging yet :) To-do.

### How to use Japyre, explained with an example

   * See the [SquareWorld](./docs/SquareWorldExample.md) example

### Using a trained model

This assumes you used Python to train a model, and models are being managed by Python.

* Use the Python class [`ModelServer`](./python/src/japyre/modelserver.py) (in `modelserver.py`) to run a server. A Java-client can instruct this server to load a model, and subsequently to ask for the best next-action to do according to the model, given some current state.
* At the Java-side, run [`JExecutor`](./src/main/java/eu/iv4xr/japyre/rl/JExecutor.java) as the client. This can be configured to interpret next-actions sent by the Python-side model server and turn it to an actual execution.
