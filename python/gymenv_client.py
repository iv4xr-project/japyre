import socket
import json
from typing import Any, Callable, Dict

HOST = "127.0.0.1"  # Standard loopback interface address (localhost)
PORT = 9999         # Port to listen on (non-privileged ports are > 1023)
debug = True 

class GymEnvClient:

    def __init__(self,host,port,receiveBufferSize=4096):
        self.host = host 
        self.port = port
        self.receiveBufferSize = receiveBufferSize
        self.socket = socket.socket()
        self.socket.connect((host,port))
        self.actionSpace = self.sendCommand("GET_ACTIONSPACE") 


    def sendCommand(self,cmd,arg=None):
        pckg     = {"cmd":cmd, "arg":arg}
        jsonPckg = json.dumps(pckg) 
        self.socket.sendall(bytes(jsonPckg  + "\n",encoding="utf-8"))
        if debug :
            print(f"> sending {jsonPckg}")
        if cmd == "KILL" :
            return None
        received = self.socket.recv(self.receiveBufferSize)     
        text = received.decode("utf-8")
        receivedJson = json.loads(text)
        if debug :
            print(f"> receiving {receivedJson}")
        if cmd == "RESET" :
            return receivedJson
        if cmd == "STEP" :
            observation = receivedJson["obs"]
            reward = receivedJson["rw"]
            episodeDone = receivedJson["end"]
            return (observation,reward,episodeDone,None)
        if cmd == "GET_ACTIONSPACE" :
            return receivedJson
        return None

    def reset(self):
        o = self.sendCommand("RESET")
        return o 

    def softClose(self):
        self.sendCommand("CLOSE")    

    def close(self):
        self.sendCommand("CLOSE")    
        self.sendCommand("KILL")    
        self.socket.close()

    def step(self,action):
        o = self.sendCommand("STEP",action)
        return o  


# just for testing:
if __name__ == '__main__':
    client = GymEnvClient(HOST,PORT)
    print(f"### action space: {client.actionSpace}" )
    client.reset()
    o = client.step("up")
    print(f"### {o}" )
    client.softClose()
    client.close()
