from hashlib import blake2b
import random
from typing import Any, Callable, Dict, List
from RLServer import Model, ModelTrainer

class SquareWorldModel(Model):
    def __init__(self):
        self.size   = 0
        # vtable will be a 2D array, with qtable[x][y] representing
        # estimated value of the position/state (x,y).
        self.vtable = []

    def configure(self,size) :
        '''
        Initialize the model for a square-world of the specified size.
        The values in the qtable will be initialed randomly, except the V(goal-location),
        which is intialized to 0 (for a technical reason, it should be 0).
        '''
        self.size = size
        self.vtable = [ [ random.random()/100.0 for y in range(size) ] for x in range(size) ]
        # the goal/terminal state has value 0, for technical reason
        # (to prevent the v-value to increase indefinitely)
        self.vtable[size-1][size-1] = 0

    def getNextTrainedAction(self, currentState:Dict) -> str :
        ''' Return an action, that is the best according to the vtable. '''
        x = currentState["x"]
        y = currentState["y"]
        if x == self.size-1 and y == self.size-1 : 
            # already in the goal-state
            return "doNothing"
        # find an action that leads to a neighbor state with best-value:
        bestAction = ""
        bestNeighbourValue = -1
        if x>0 and self.qtable[x-1][y] > bestNeighbourValue :
            bestAction = "left"
        if x<self.size-1 and self.qtable[x+1][y] > bestNeighbourValue :
            bestAction = "right"
        if y>0 and self.qtable[x][y-1] > bestNeighbourValue :
            bestAction = "down"
        if y<self.size-1 and self.qtable[x][y+1] > bestNeighbourValue :
            bestAction = "up"

        return bestAction 

    def save(self,fname):
        print(f"> Saving a model to {fname}")

    def load(self,fname):
        print(f"> Loading a model from {fname}")    

    def __str__(self) -> str:
        s = ""
        for y in range(self.size):
            if y>0 : s += "\n"    
            for x in range(self.size):
                if y>0 : s += "|"
                s += self.vtable[x][self.size - 1 - y]
        return s


class SquareWorldModelTrainer(ModelTrainer) :
    """
    Implementing a simple Temporal Difference learning.
    """
    def __init__(self):
        self.model = None
        self.seed  = 127
        self.alpha = 0.5
        self.gamma = 0.9
        self.exploreProbability = 0.16
        self.numberOfEpisodes = 1
        self.episodeMaxLength = 16
        self.currentState = { "x":-1, "y":-1 }

    def configure(self, conf:Dict) -> None :
        # self.seed = conf["seed"]
        random.seed(self.seed)
        size = conf["size"]
        self.model = SquareWorldModel()
        self.model.configure(size)
        self.currentState["x"] = conf["x0"]
        self.currentState["y"] = conf["y0"]
    
    def getModel(self) -> Model :
        return self.model 

    def applyReward(self, action:str, newstate:Dict, reward:float) -> None :
        xnew = newstate["x"]
        ynew = newstate["y"]
        x = self.currentState["x"]
        y = self.currentState["y"]
        self.model.vtable[x][y] = self.model.vtable[x][y] \
                                    + self.alpha * (reward + self.gamma * self.model.vtable[xnew][ynew] \
                                                           - self.model.vtable[x][y])    

    def getNextAction(self, currentState:Dict, possibleActions:List[str]) -> str :
        if random.random() < self.exploreProbability :
            choices = []
            x = self.currentState["x"]
            y = self.currentState["y"]
            if x>0 : choices.append("left")
            if x<self.model.save-1 : choices.append("right")
            if y>0 : choices.append("down")
            if y<self.model.save-1 : choices.append("up")
            action = random.choice(choices)
            return action  
        else:
            self.model.getNextTrainedAction(self.currentState)
        raise

# just for testing:
if __name__ == '__main__':
    print("Baa!")