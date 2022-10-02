import random
import numpy as np
from typing import Any, Callable, Dict, List

class Qlearning:
    def __init__(self,numberOfStates,numberOfActions):
        self.numberOfStates  = numberOfStates
        self.numberOfActions = numberOfActions
        self.seed  = 127
        self.alpha = 0.5
        self.gamma = 0.9
        self.exploreProbability = 0.16
        random.seed(self.seed)
        a = [ [ random.random()/100.0 for y in range(numberOfActions) ] for x in range(numberOfStates) ]
        self.qtable = np.array(a)

    def maxActionValue(self,currentState:int) -> Dict :
        bestAction = 0
        bestValue = self.qtable[currentState][0]
        for a in range(1,self.numberOfActions) :
            v = self.qtable[currentState][a]
            if v > bestValue :
               bestAction = a
               bestValue = v
        return { "bestAction" : bestAction, "bestValue" : bestValue }


    def getNextTrainedAction(self, currentState:int) -> int :
        ''' Return an action, that is the best according to the qtable. '''
        best = self.maxActionValue(currentState)
        return best["bestAction"]

    def applyReward(self, oldstate:int, action:int, newstate:int, reward:float) -> None :
        valOldState = self.qtable[oldstate][action]
        valNewState = self.maxActionValue(newstate)["bestValue"]
        self.qtable[oldstate][action] = valOldState + self.alpha * (reward + self.gamma * valNewState - valOldState)
        
    def getNextAction(self, currentState:int) -> int :
        if random.random() < self.exploreProbability :
            action = random.randint(0,self.numberOfActions-1)
            return action  
        else:
            return self.getNextTrainedAction(currentState)
        

    def save(self,fname):
        print(f">>> implement this --> Saving a model to {fname}")
        raise 

    def load(self,fname):
        print(f">>> implement this --> Loading a model from {fname}")    
        raise

    def __str__(self) -> str:
        s = f"{self.qtable}"
        return s
    
    
# just for testing:
if __name__ == '__main__':
    print("Baa!")
