package eu.iv4xr.japyre.klad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provide a reader/writer to a socket to communicate with a server.
 * This class only provides raw communication over socket, where we can send
 * and receive an object. When an object is sent, it will be serialized to 
 * a Json string. Likewise, the server is assumed to send each response
 * object as a Json string.
 * 
 * <p>Note: this class was taken over from iv4xrDemo. 
 */
public class SocketJsonReaderWriterzzz {
	
	public static boolean debug = false ;
	
	
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    
    private String host ;
    private int port ;
        
    // Configuring the json serializer/deserializer. Register custom serializers
    // here.
    // Transient modifiers should be excluded, otherwise they will be send with json
    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
    
    /**
     * Constructor. Will setup the needed socket to communicate with the given host
     * at the given port.
     */
    public SocketJsonReaderWriterzzz(String host, int port) {
    	this.host = host ;
    	this.port = port ;
        int maxWaitTime = 20000;
        System.out.println(String.format("> Trying to connect with a host on %s:%s (will time-out after %s seconds)", host, port, maxWaitTime/1000));

        long startTime = System.nanoTime();

        while (!socketReady() && millisElapsed(startTime) < maxWaitTime){
            try {
                socket = new Socket(host, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException ignored) { }
        }
        if(socketReady()){
            System.out.println(String.format("> CONNECTED with %s:%s", host, port));
        }
        else{
            System.out.println(String.format("> Could NOT establish a connection with the host %s:%s.", host,port));
        }
    }
    
    /**
     * @return true if the socket and readers are not null
     */
    public boolean socketReady(){
        return socket != null && reader != null && writer != null;
    }

    /**
     * @param startTimeNano the start time in long
     * @return the elapsed time from the start time converted to milliseconds
     */
    private float millisElapsed(long startTimeNano){
        return (System.nanoTime() - startTimeNano) / 1000000f;
    }
    
    /**
     * Send an object to the host. The object will first be serialized
     * to a json string, so it is assumed that the json serializer knows how to
     * handle the object.
     */
    public void write(Object packageToSend) {
    	String json = gson.toJson(packageToSend) ;
    	if (debug) {
        	System.out.println("** SENDING: " + json);
        }
    	writer.println(json);
    }
    
    /**
     * Read an object that was sent by the host. The object will be
     * received as a json string, which is then converted into an instance of the
     * given class. It is assumed that the json deserializer knows how to do this.
     * The resulting object is then returned.
     */
    public <T> T read(Class<T> expectedClassOfResultObj) throws IOException {
    	//System.out.println("** waiting for answer....")  ;
    	//reader.ready() ;
    	String response = reader.readLine() ; 
        // we do not have to cast to T, since req.responseType is of type Class<T>
        if (debug) {
        	System.out.println("** RECEIVING: " + response);
        }
        //return null ;
    	return gson.fromJson(response,expectedClassOfResultObj);
    }

    /**
     * Close the socket/reader/writer
     * @throws IOException 
     */
    public void close() throws IOException {
         if (reader != null) reader.close();
         if (writer != null) writer.close();
         if (socket != null) socket.close();
         System.out.println(String.format("> Disconnected from the host %s:%s", host, port));
    }
 
}
