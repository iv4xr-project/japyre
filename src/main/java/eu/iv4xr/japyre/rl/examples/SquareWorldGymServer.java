package eu.iv4xr.japyre.rl.examples;

import java.io.IOException;

import eu.iv4xr.japyre.rl.GymEnvServer;

/**
 * Run the main-method of this class to wrap an instance of {@link eu.iv4xr.japyre.rl.examples.SquareWorld}
 * Gym and deploys it as a server that responds to requests from the Python-side.
 * In this example the world-size is set to 6.
 * 
 * @author Wish
 */
public class SquareWorldGymServer {
	
	public static int WORLD_SIZE = 6 ;
	
	public static void main(String[] args) throws IOException {
		SquareWorld sw = new SquareWorld(WORLD_SIZE) ;
		//SquareWorld.debug = true ;
		int port = 9999 ;
		GymEnvServer<SquareWorld.Location> server = new GymEnvServer<>(port,sw) ;
		//server.turnDebugMode(true);
		server.start();
	}
	

}
