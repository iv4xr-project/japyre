package eu.iv4xr.japyre.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * A class that supports sending a pair (cmd,arg) to a server and receives the
 * response the server sends. A socket is used to facilitate the connection to
 * the server. From the perspective of client-server relation, this class will
 * then acts as a client.
 * 
 * <p>
 * The pair (cmd,arg) represents some command and its argument. When sent to the
 * server, the server can interpret the command and produce some result, which
 * is then sent back to the client (this class) as a response.
 * 
 * This class provides a method that allows us to abstractly see the sending
 * such a command as a function call r = cmd(arg), where r is the the server's
 * result/response on that command.
 * 
 * <p>
 * The command (cmd,arg) is sent over as a Json string. And the server is
 * assumed to send back each response object as a Json string.
 * 
 * @author Wish
 *
 */
public class SendCommandClient {

	String host ;
	int port ;
	Socket socket;
	ObjectReaderWriter_OverSocket readerwriter ;
	
    /**
     * Constructor. Will setup the needed socket to communicate with the given server
     * hosted at the given host-id, at the given port.
     */
	public SendCommandClient(String host, int port) {
		this.host = host ;
    	this.port = port ;
        int maxWaitTime = 20000;
        System.out.println(String.format("> Trying to connect with a host on %s:%s (will time-out after %s seconds)", host, port, maxWaitTime/1000));

        long startTime = System.nanoTime();

        while (!socketReady() && millisElapsed(startTime) < maxWaitTime){
            try {
                socket = new Socket(host, port);
                readerwriter = new ObjectReaderWriter_OverSocket(socket) ;
            } 
            catch (IOException ignored) { }
        }
        if(socketReady()){
            System.out.println(String.format("> CONNECTED with %s:%s", host, port));
        }
        else{
            System.out.println(String.format("> Could NOT establish a connection with the host %s:%s.", host,port));
        }
	}
	
    static class Cmd {
    	String cmd ;
    	Object arg ;
    	Cmd(String cmd, Object arg) {
    		this.cmd = cmd ;
    		this.arg = arg ;
    	}
    }
    
    /**
     * @return true if the socket and readers are not null
     */
    public boolean socketReady(){
        return socket != null && readerwriter != null ;
    }

    /**
     * @param startTimeNano the start time in long
     * @return the elapsed time from the start time converted to milliseconds
     */
    private float millisElapsed(long startTimeNano){
        return (System.nanoTime() - startTimeNano) / 1000000f;
    }
		
	/**
	 * Send a command and an argument to the server. The pair will first be wrapped
	 * as an instance of the class Cmd, and then sent to the server as a
	 * Json-string. The server is expected to interpret the command, does some
	 * calculation, and produces some result. This will be sent back to the client
	 * (this class) as a Json-string, which this method will return as an object.
	 * The Json-string will be converted to an object of some class T, as specified
	 * in the 3rd parameter of this method.
	 */
    public <T> T sendCommand(String cmd, Object arg, Class<T> expectedClassOfResultObj) throws IOException {
    	readerwriter.write(new Cmd(cmd,arg)) ;
    	return readerwriter.read(expectedClassOfResultObj) ;
    }
    
    public void turnDebugMode(boolean on) {
		ObjectReaderWriter_OverSocket.debug = on ;
	}
    
    public void close() throws IOException {
    	readerwriter.close(); 
    	socket.close() ;
    }
    
    // just for testing:
    static class SomeClass {
    	public int id ;
    	public String name ;
    	public String toString() {
    		return "" + id + ":" + name ;
    	}
    }
    
    static class A {
    	int id ;
    	String name ;
    	List<A> children = new LinkedList<>() ;
    	A(int id, String name)  {
    		this.id = id ;
    		this.name = name ;
    	}
    	
    	void addChild(A b) {
    		children.add(b) ;
    	}
    }
    
    public static void main(String[] args) throws IOException {
    	
    	SendCommandClient commander = new SendCommandClient("127.0.0.1",9999) ;
    	commander.turnDebugMode(true);
    	
    	if (!commander.socketReady()) return ;
    	
    	A a1 = new A(0,"Sponge") ;
    	A a2 = new A(99,"Patrick") ;
    	a1.addChild(a2) ;
    	a1.addChild(a2) ;
    	
    	
    	//SomeClass x = commander.sendCommand("BLA","haha",SomeClass.class) ;
    	SomeClass x = commander.sendCommand("BLA",a1,SomeClass.class) ;
    	//Integer x = commander.sendCommand("BLA",a1,Integer.class) ;
    	System.out.println(">> " + x) ;
    	
    	commander.close() ;
    	
    	//new Scanner(System.in).nextLine();
    }
	
}
