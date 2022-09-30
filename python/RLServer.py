import CommandServer
import json
from typing import Any, Callable, Dict, List

class Model:
    def mkFreshModel(self,modelConfig:Dict) :
        ''' 
        Make a fresh untrained model with the given model-configuration. 
        This method returns thus an instance of Model.
        '''
        print(">>> Implement this method.")
        raise

    def save(self, filename:str) -> None :
        ''' Save this model to a file. '''
        print(">>> Implement this method.")
        raise

    def load(self, filename:str) :
        ''' Load a model from a file and return the model. '''
        print(">>> Implement this method.")
        raise

    def setTrainingAlgorithm(self, trainingConf:Dict) -> None :
        ''' 
        Associate a training algorithm to this model, configured to the given 
        configuration. Alternatively, if the model already comes with its own
        training algorithm, this method reconfigures to according to the given
        config.
        '''
        print(">>> Implement this method.")
        raise
  
    def getNextTrainedAction(self, currentState:Dict) -> str :
        ''' 
        Given the current state, this method consults the model to return the best action 
        to do next (according to the model, and whatever its action selection policy is).
        '''
        print(">>> Implement this method.")
        raise

    def getNextActionToTry(self, currentState:Dict, possibleActions:List[str]) -> str :
        '''
        This method is for training. It asks the model to decide which actions it wants
        to do next for the purpose of training/learning.
        '''
        print(">>> Implement this method.")
        raise

    def applyReward(self, oldState:Dict, action:str, newstate:Dict, reward:float) -> None :
        '''
        Incoporate the learning of executing the given action, on the oldstate, leading
        to the newstate, and receiving the given reward in doing so.
        '''
        print(">>> Implement this method.")
        raise


class TrainingServer:
    def __init__(self, host:str, port:int):
        self.host = host
        self.port = port
        self.model   = None
        self.trainer = None

    def deployTrainingServer(trainer:ModelTrainer,receiveBufferSize=4096) -> None :
        def interpret(cmd,arg):
            retval = "OK__"
            if cmd == "MK_FRESH_MODEL" : 
                    trainer.configure(arg)

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
