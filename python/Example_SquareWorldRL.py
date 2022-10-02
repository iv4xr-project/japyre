import random
from typing import Any, Callable, Dict, List

class QLearning:
    def __init__(self,numberOfStates,numberOfActions):
        self.numberOfStates  = numberOfStates
        self.numberOfActions = numberOfActions
        self.seed  = 127
        self.alpha = 0.5
        self.gamma = 0.9
        self.exploreProbability = 0.16
        self.numberOfEpisodes = 1
        self.episodeMaxLength = 16
        random.seed(self.seed)
        # qtable will be a 2D array, with qtable[x][y] is a dictionary
        # mapping actions to value
        def mkcell():
            return { "left"  : random.random()/100.0 , \
                     "right" : random.random()/100.0 , \
                     "up"    : random.random()/100.0, \
                     "down"  : random.random()/100.0 }

        self.qtable = [ [ mkcell() for y in range(size) ] for x in range(size) ]

    def setTrainingAlgorithm(self, trainingConf:Dict) -> None :
        return    

    def bestActionValue(self,currentState:Dict) -> Dict :
        x = currentState["x"]
        y = currentState["y"]
        actionValues = self.qtable[x][y]
        bestAction = "left"
        bestValue = actionValues["left"]
        v = actionValues["right"]
        if  v > bestValue :
            bestValue = v
            bestAction = "right"
        v = actionValues["up"]
        if  v > bestValue :
            bestValue = v
            bestAction = "up"
        v = actionValues["down"]
        if  v > bestValue :
            bestValue = v
            bestAction = "down"

        return { "bestAction" : bestAction, "bestValue" : bestValue }


    def getNextTrainedAction(self, currentState:Dict) -> str :
        ''' Return an action, that is the best according to the qtable. '''
        best = self.bestActionValue(currentState)
        return best["bestAction"]


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
        

    def configure(self, conf:Dict) -> None :
        # self.seed = conf["seed"]
        
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
    trainer = SquareWorldModelTrainer()
    deployTrainingServer(trainer,"127.0.0.1",9999)