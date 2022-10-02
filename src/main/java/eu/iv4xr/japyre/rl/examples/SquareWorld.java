package eu.iv4xr.japyre.rl.examples;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import eu.iv4xr.japyre.klad.IStatefulGame;
import eu.iv4xr.japyre.rl.IJavaGymEnv;
import eu.iv4xr.japyre.rl.RLStepData;

/**
 * A SquareWorld is a tiled NxN grid, where a robot is dropped in its center.
 * Its goal is to reach the top-right corner of the grid (location (N-1,N-1)).
 * The robot can move left/right/up/down. The robot itself has no intelligence,
 * so has no idea what to do. The method execute(a) allows you to control the
 * robot.
 * 
 * <p>Moving off the grid causes the robot to be broken.
 */
public class SquareWorld implements IJavaGymEnv<SquareWorld.Location> {
	
	public static class Location {
		int x ;
		int y ;
		public Location(int x, int y) { this.x = x ; this.y = y ; }
		@Override
		public String toString() {
			return "<" + x + "," + y + ">" ;
		}
	}
	
	public int size ;
	public Location currentLocation ;
	
	public int randomSeed = 3373 ;
	public int stepCount = 0 ;
	
	Random rnd = new Random(randomSeed) ;
	
	public SquareWorld(int size) {
		this.size = size ;
		currentLocation = new Location(size/2,size/2) ;
	}
	
	static final String UP    = "up" ;
	static final String DOWN  = "down" ;
	static final String LEFT  = "left" ;
	static final String RIGHT = "right" ;
	
	public static boolean deterministic = true ;
	public static boolean debug = false ;

	@Override
	public Location reset() {
		currentLocation.x = size/2 ;
		currentLocation.y = size/2 ;
		rnd = new Random(randomSeed) ;
		stepCount = 0 ;
		return currentLocation ;
	}
	
	@Override
	public List<String> actionSpace() {
		List<String> actions = new LinkedList<>() ;
		actions.add(LEFT) ;
		actions.add(RIGHT) ;
		actions.add(UP) ;
		actions.add(DOWN) ;
		return actions ;
		
	}

	private void crash() {
		if (debug) System.out.println("## The robot CRASHES.") ;
		currentLocation.x = size ;
		currentLocation.y = size ;
	}
	
	public boolean goalAchieved() {
		return (currentLocation.x == size-1 && currentLocation.y == size-1) ;
	}
	
	public boolean offTheGrid() {
		return currentLocation.x < 0 || currentLocation.x >= size ||
			   currentLocation.y < 0 || currentLocation.y >= size ;
	}

	public boolean isTerminalState() {
		return goalAchieved() || offTheGrid() ;
	}

	
	

	@Override
	public RLStepData<Location> step(String action) {
		if (this.isTerminalState()) {
			if (debug) {
				System.out.println("## the robot is in a terminal state. No action is possible.") ;
			}
			return null ;
		}
		switch(action) {
			case "left" :
				currentLocation.x -- ;
				break ;
			case "right" :
				currentLocation.x ++ ;
				break ;
			case "down" :
				currentLocation.y -- ;
				break ;
			case "up" :
				if (deterministic) {
					currentLocation.y ++ ;
				}
				else {
					if (rnd.nextFloat() <= 0.15) {
						currentLocation.y -- ;
					}
					else {
						currentLocation.y ++ ;
					}
				}
		}
		if (debug) {
			System.out.print("## " + stepCount + ":" + action) ;
			if (offTheGrid()) {
				System.out.println(", the robot CRASHES.") ;
			}
			else if (goalAchieved()) {
				System.out.println(", SUCCESS reaching the goal.") ;
			}
			else {
				System.out.println("") ;
			}
		}
		float reward = goalAchieved() ? 100 : (offTheGrid() ? -100 : 0) ;

		stepCount++ ;
		
		RLStepData<Location> o = new RLStepData<>(currentLocation,reward,isTerminalState()) ;
		return o ;
	}
	
	// just for testing
	public static void main(String[] args) {
		SquareWorld sw = new SquareWorld(6) ;
		SquareWorld.debug = true ;
		RLStepData<Location> o  ;
		o = sw.step(RIGHT) ; System.out.println(">> " + o);
		o = sw.step(RIGHT) ; System.out.println(">> " + o);
		o = sw.step(UP)    ; System.out.println(">> " + o);
		o = sw.step(RIGHT) ; System.out.println(">> " + o);
		o = sw.step(RIGHT) ; System.out.println(">> " + o);	
	}
	
}
