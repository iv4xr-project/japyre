package eu.iv4xr.japyre.rl.examples;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import eu.iv4xr.japyre.rl.IStatefulGame;

/**
 * A SquareWorld is a tiled NxN grid, where a robot is dropped in its center.
 * Its goal is to reach the top-right corner of the grid (location (N-1,N-1)).
 * The robot can move left/right/up/down. The robot itself has no intelligence,
 * so has no idea what to do. The method execute(a) allows you to control the
 * robot.
 * 
 * <p>Moving off the grid causes the robot to be broken, which we will represent
 * by a terminal position (N,N).
 */
public class SquareWorld implements IStatefulGame<SquareWorld.Location> {
	
	public static class Location {
		int x ;
		int y ;
		public Location(int x, int y) { this.x = x ; this.y = y ; }
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
	public void reset() {
		currentLocation.x = size/2 ;
		currentLocation.y = size/2 ;
		rnd = new Random(randomSeed) ;
		stepCount = 0 ;
	}

	@Override
	public Location getState() {
		return currentLocation ;
	}
	
	private void crash() {
		if (debug) System.out.println("## The robot CRASHES.") ;
		currentLocation.x = size ;
		currentLocation.y = size ;
	}

	@Override
	public boolean isTerminalState() {
		return (currentLocation.x == size-1 && currentLocation.y == size-1)
				|| (currentLocation.x == size && currentLocation.y == size) ;
	}

	@Override
	public List<String> getCurrentlyPossibleActions() {
		List<String> options = new LinkedList<>() ;
		if (isTerminalState()) return options ;
		if (currentLocation.x > 0) 
			options.add(LEFT) ;
		if (currentLocation.x < size-1)
			options.add(RIGHT) ;
		if (currentLocation.y > 0)
			options.add(DOWN) ;
		if (currentLocation.y < size-1)
			options.add(UP) ;
		return options ;
	}
	

	@Override
	public float execute(String action) {
		if (this.isTerminalState()) {
			if (debug) System.out.println("## the robot is in a terminal state. No action is possible.") ;
			return 0 ;
		}
		if (debug) System.out.println("## " + stepCount + ":" + action) ;
		int x = currentLocation.x ;
		int y = currentLocation.y ;
		stepCount++ ;
		switch(action) {
			case "left" :
				x-- ;
				if (x<0) {
					crash() ; return -100 ;
				}
				break ;
			case "right" :
				x++ ;
				if (x>=size) {
					crash() ; return -100 ;
				}
				break ;
			case "down" :
				y-- ;
				if (y<0) {
					crash() ; return -100 ;
				}
				break ;
			case "up" :
				if (deterministic) {
					y++ ;
				}
				else {
					if (y>0 && rnd.nextFloat() <= 0.15) {
						y-- ;
					}
					else {
						y++ ;
					}
				}
				if (y>=size) {
					crash() ; return -100 ;
				}
				break ;
		}
		currentLocation.x = x ;
		currentLocation.y = y ;
		float reward = 0f ;
		if (x==size-1 && y==size-1) {
			if (debug) System.out.println("## robot SUCCEEDS to get to the goal-location.") ;	
			reward = 100f ;
		}
		return reward ;
	}
	
	// just for testing
	public static void main(String[] args) {
		SquareWorld sw = new SquareWorld(6) ;
		SquareWorld.debug = true ;
		float reward = 0 ;
		reward = sw.execute(RIGHT) ; System.out.println(">> rw=" + reward);
		reward = sw.execute(RIGHT) ; System.out.println(">> rw=" + reward);
		reward = sw.execute(UP) ; System.out.println(">> rw=" + reward);
		reward = sw.execute(RIGHT) ; System.out.println(">> rw=" + reward);
		reward = sw.execute(RIGHT) ; System.out.println(">> rw=" + reward);	
	}
	
}
