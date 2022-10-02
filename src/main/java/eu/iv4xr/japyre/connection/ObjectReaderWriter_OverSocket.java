package eu.iv4xr.japyre.connection;

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
 * Provide convenient reader/writer to read and write objects over a socket.
 * This allows an object to be send over the socket (to a recipient on the other
 * side of the socket-connection), encoded as a Json-string. Similarly, the
 * reader can receive an object, encoded as a Json-string, that was sent to this
 * class over the socket. Note that this implies that the object sent like this
 * must be serializable to Json.
 * 
 * <p>
 * Note: this class was taken over from iv4xrDemo.
 */
public class ObjectReaderWriter_OverSocket {

	public static boolean debug = false;

	Socket socket;
	BufferedReader reader;
	PrintWriter writer;

	// Configuring the json serializer/deserializer. Register custom serializers
	// here.
	// Transient modifiers should be excluded, otherwise they will be send with json
	private static Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT)
			.create();

	public ObjectReaderWriter_OverSocket(Socket socket) throws IOException {
		this.socket = socket;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
	}

	/**
	 * Send an object to the host. The object will first be serialized to a json
	 * string, so it is assumed that the json serializer knows how to handle the
	 * object.
	 */
	public void write(Object packageToSend) {
		String json = gson.toJson(packageToSend);
		if (debug) {
			System.out.println("** SENDING: " + json);
		}
		writer.println(json);
	}

	/**
	 * Read an object that was sent by the host. The object will be received as a
	 * json string, which is then converted into an instance of the given class. It
	 * is assumed that the json deserializer knows how to do this. The resulting
	 * object is then returned.
	 */
	public <T> T read(Class<T> expectedClassOfResultObj) throws IOException {
		// System.out.println("** waiting for answer....") ;
		// reader.ready() ;
		String response = reader.readLine();
		// we do not have to cast to T, since req.responseType is of type Class<T>
		if (debug) {
			System.out.println("** RECEIVING: " + response);
		}
		// return null ;
		return gson.fromJson(response, expectedClassOfResultObj);
	}

	/**
	 * Close the reader/writer. This does NOT close the socket.
	 */
	public void close() throws IOException {
		if (reader != null)
			reader.close();
		if (writer != null)
			writer.close();
	}

}
