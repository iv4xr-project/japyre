import socket
import json
from typing import Any, Callable, Dict

HOST = "127.0.0.1"  # Standard loopback interface address (localhost)
PORT = 9999        # Port to listen on (non-privileged ports are > 1023)

debug = True 

def deployCommandServer(host:str,
                        port:int,
                        commandInterpreter : Callable[[Dict,Dict],Any],
                        receiveBufferSize=4096) -> None :
    '''
    Deploy a command-server at the given ip-address and port. The server
    accept commands sent by a client. A command is a pair (cmd,arg) where
    cmd is a string specifying the command name, and arg is a Json-string
    representing the argument of the command.

    This function takes a command-interpreter f as an argument. Upon receiving
    a pair (cmd,arg) from the client, f(cmd,arg) is invoked. The function is
    assumed to produce a Json-string, which is then sent back to the client.

    The server closes when the client asks it to close. 

    Parameters:
      host (string): the ip when this server will be hosted
      port (int):    the port number
      commandInterpreter: is a function that takes two string cmd and arg as arguments.
                          The arg is assumed to be a Json-string.
                          The function is assumed to return a Json-string.
    '''
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((host,port))
        s.listen()
        print(f"> Starting a sever at {host}:{port}")
        clientSocket, addr = s.accept()
        with clientSocket:
            print(f"> CONNECTED by {addr}")
            while True:
                received = clientSocket.recv(receiveBufferSize) 
                if not received:
                    print("> CLOSING the server.")
                    break
                text = received.decode("utf-8")
                myjson = json.loads(text)
                cmd = myjson["cmd"] # the command-string
                arg = myjson["arg"] # the arg-object, represented as a nested Dictionary
                if debug :
                    #print(f"> receiving {text}")
                    print(f"> receiving cmd: {cmd}")
                    print(f">           arg: {arg}")
                    
                # interpret the command:
                result =  commandInterpreter(cmd,arg)   
                resultJson = json.dumps(result) 
                # send result back to the client:
                # need to add that newline-char at the end, else the client does not
                # know that the string that was sent has ended:
                clientSocket.sendall(bytes(resultJson  + "\n",encoding="utf-8"))
                if debug :
                    print(f"> sending {resultJson}")
        s.close()


# just for testing:
if __name__ == '__main__':
    def testCmdInterpreter(cmd,arg):
        xxx = { "id":0, "name":"Batman" }
        return xxx

    deployCommandServer(HOST,PORT,testCmdInterpreter)        
