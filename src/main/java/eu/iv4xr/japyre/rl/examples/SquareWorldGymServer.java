package eu.iv4xr.japyre.rl.examples;

import java.io.IOException;

import eu.iv4xr.japyre.connection.GymEnvServer;

public class SquareWorldGymServer {
	
	public static void main(String[] args) throws IOException {
		SquareWorld sw = new SquareWorld(6) ;
		SquareWorld.debug = true ;
		int port = 9999 ;
		GymEnvServer<SquareWorld.Location> server = new GymEnvServer<>(port,sw) ;
		server.turnDebugMode(true);
		server.start();
	}
	

}
