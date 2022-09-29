/**
 * Providing the underlying socket-based communication with a server.
 * Classes like {@link SocketReaderWriter} and {@link SendCommandOverSocket}
 * provide the client. These clients assume data are packaged as Json
 * strings to be sent and received over the socket.
 */
package eu.iv4xr.japyre.connection;