import CommandServer
import json
from typing import Any, Callable, Dict, List

class Model:
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
    def __init__(self, host:str, port:int, modelConstructor:Callable[[Dict],Model]):
        self.host = host
        self.port = port
        self.modelConstructor = modelConstructor
        self.model   = None
        self.trainer = None
        self.previousState = None
        self.commandServer = CommandServer(host,port)

    def interpretCommand(self,cmd,arg):
        retval = "OK__"
        arg__ = json.dumps(arg)
        if cmd == "MK_FRESH_MODEL" : 
                modelParameters = arg__
                self.model = self.modelConstructor(modelParameters)
                self.previousState = None
                return retval
        if cmd == "SAVE"  :
                filename = arg__
                self.model.save(filename)
                return retval
        if cmd == "LOAD"  :
                filename = arg__
                self.model = self.model.load(filename)  
                self.previousState = None
                return retval

        if cmd == "SET_TRAINING_CONFIG"  :
                trainingConfig = arg__
                self.model = self.model.setTrainingAlgorithm(trainingConfig)
                return retval        

        if cmd == "GET_NEXTACTION" :
                state           = arg__["st"]   # current observation/state
                action          = arg__["prev"] # the action that leads to the current state
                reward          = arg__["rew"]  # the reward given upon reaching the current state
                possibleActions = arg__["opts"] # list of next-actions that are possible on the current state
                if  action != None :
                    self.model.applyReward(self.previousState,action,state,reward)
                self.previousState = state
                nextAction =  self.model.getNextActionToTry(state,possibleActions)
                return nextAction

        if cmd == "GET_NEXT_TRAINEDACTION" :
                state     = arg__  # current observation/state
                nextAction = self.model.getNextTrainedAction(state)
                return nextAction

        raise ValueError("## receiving an unknown command: " + cmd)

    def deploy(self,receiveBufferSize=4096) -> None :
        print("> About to deploy a model-training-server.")   
        def foo(cmd,arg) :
            return self.interpretCommand(cmd,arg)   
        self.commandServer.attachInterpreter(foo)
        self.commandServer.deploy(receiveBufferSize)



# just for testing:
if __name__ == '__main__':
    print("Boo!")
