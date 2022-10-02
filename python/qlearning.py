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


    def getTrainedAction(self, currentState:int) -> int :
        ''' Return an action, that is the best according to the qtable. '''
        best = self.maxActionValue(currentState)
        return best["bestAction"]

    def applyReward(self, oldstate:int, action:int, newstate:int, reward:float) -> None :
        valNewState = self.maxActionValue(newstate)["bestValue"]
        self.qtable[oldstate][action] = self.qtable[oldstate][action] \
                + self.alpha * (reward + self.gamma * valNewState \
                                       - self.qtable)
        
    def getNextAction(self, currentState:int) -> int :
        if random.random() < self.exploreProbability :
            action = random.randint(0,self.numberOfActions)
            return action  
        else:
            self.model.getTrainedAction(currentState)
        raise



    def save(self,fname):
        print(f"> Saving a model to {fname}")

    def load(self,fname):
        print(f"> Loading a model from {fname}")    

    def __str__(self) -> str:
        s = f"{self.qtable}"
        return s


class SquareWorldModelTrainer(ModelTrainer) :
    """
    Implementing a simple Temporal Difference learning.
    """
    def __init__(self):
        

    def configure(self, conf:Dict) -> None :
        # self.seed = conf["seed"]
        
        size = conf["size"]
        self.model = SquareWorldModel()
        self.model.configure(size)
        self.currentState["x"] = conf["x0"]
        self.currentState["y"] = conf["y0"]
    
    def getModel(self) -> Model :
        return self.model 

    
    
# just for testing:
if __name__ == '__main__':
    print("Baa!")
    trainer = SquareWorldModelTrainer()
    deployTrainingServer(trainer,"127.0.0.1",9999)