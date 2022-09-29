import CommandServer
import json
from typing import Any, Callable, Dict, List

class Model:
    def save(self, filename:str) -> None :
        print(">>> Implement this method.")
        raise

    def load(self, filename:str) -> None :
        print(">>> Implement this method.")
        raise

    def getNextTrainedAction(self, currentState:Dict) -> str :
        print(">>> Implement this method.")
        raise

class ModelTrainer:
    def getModel(self) -> Model :
        print(">>> Implement this method.")
        raise
    
    def configure(self, conf:Dict) -> None :
        print(">>> Implement this method.")
        raise

    def applyReward(self, action:str, newstate:Dict, reward:float) -> None :
        print(">>> Implement this method.")
        raise

    def getNextAction(self, currentState:Dict, possibleActions:List[str]) -> str :
        print(">>> Implement this method.")
        raise


def deployTrainingServer(trainer:ModelTrainer, host:str , port:int,receiveBufferSize=4096) -> None :
    def interpret(cmd,arg):
        retval = "OK__"
        if cmd == "START_TRAINING" : 
                trainer.configure(arg)
        elif cmd == "SAVE"  :
                filename = json.dumps(arg)["filename"]
                trainer.getModel().save(filename)
        elif cmd == "GET_NEXTACTION" :
                observation     = arg["st"]  # current observation/state
                previousAction  = arg["prev"] # the previous action that leads to the current state
                reward          = arg["rew"]  # the reward given upon reaching the current state
                possibleActions = arg["opts"] # list of next-actions that are possible on the current state
                trainer.applyReward(previousAction,observation,reward)
                nextAction = trainer.getNextAction(observation,possibleActions)
                retval = nextAction

        return retval
    print("> About to deploy a model-training-server.")        
    cmdServer = CommandServer.deployCommandServer(host,port,interpret,receiveBufferSize)


def deployModelServer(model:Model, host:str, port:int, receiveBufferSize=4096) -> None :
    def interpret(cmd,arg):
        retval = "OK__"
        if cmd == "LOAD"  :
                filename = json.dumps(arg)["filename"]
                model.load(filename)
        elif cmd == "GET_NEXT_TRAINEDACTION" :
                observation     = arg["obs"]  # current observation/state
                nextAction = model.getNextTrainedAction(observation)
                retval = nextAction
        return retval

    print("> About to deploy a model-server.")                
    cmdServer = CommandServer.deployCommandServer(host,port,interpret,receiveBufferSize)


# just for testing:
if __name__ == '__main__':
    print("Boo!")
