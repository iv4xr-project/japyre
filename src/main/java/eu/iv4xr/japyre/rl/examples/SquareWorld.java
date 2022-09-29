package eu.iv4xr.japyre.rl.examples;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import eu.iv4xr.japyre.rl.IStatefulGame;

public class SquareWorld implements IStatefulGame {
	
	public static class Location {
		int x ;
		int y ;
		public Location(int x, int y) { this.x = x ; this.y = y ; }
	}
	
	public int size ;
	public Location currentLocation ;
	
	public int randomSeed = 3373 ;
	
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

	@Override
	public void reset() {
		currentLocation.x = size/2 ;
		currentLocation.y = size/2 ;
		rnd = new Random(randomSeed) ;
	}

	@Override
	public Object getState() {
		return currentLocation ;
	}

	@Override
	public boolean isTerminalState() {
		return currentLocation.x == size-1 && currentLocation.y == size-1 ;
	}

	@Override
	public List<String> getCurrentlyPossibleActions() {
		List<String> options = new LinkedList<>() ;
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
		int x = currentLocation.x ;
		int y = currentLocation.y ;
		switch(action) {
			case "LEFT" :
				x-- ;
				if (x<0) throw new Error("Illegal Move!") ;
				break ;
			case "RIGHT" :
				x++ ;
				if (x>=size) throw new Error("Illegal Move!") ;
				break ;
			case "DOWN" :
				y-- ;
				if (y<0) throw new Error("Illegal Move!") ;
				break ;
			case "UP" :
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
				if (y>=size) throw new Error("Illegal Move!") ;
				break ;
		}
		currentLocation.x = x ;
		currentLocation.y = y ;
		float reward = 0f ;
		if (isTerminalState()) {
			reward = 100f ;
		}
		return reward ;
	}
	
}
