package puzzleduck.rcbots;

import robocode.util.*;
import java.util.Random;

import robocode.Bullet;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class P2_TaG extends Robot {

	static int[][] targetingGrid;
	
	static double targetsEnergy = 100;
	private double firePower;
	private Bullet lastShot;
	private boolean panicDir = true;
	private double panicDistance = 130;
	private double panicTurn = 0;
	
	private double gridX;
	private double gridY;
	private int instance;
	
		public void onDeath() {
			instance = 0;
		}//dead
		
	    public void run() {
	    	while(true) {
	    		if(instance == 0) { // reset
	    			gridX = getHeight()/getBattleFieldHeight();
	    			gridY = getWidth()/getBattleFieldWidth();
	    			targetingGrid = new int[(int)gridX][(int)gridY]; //pobabilistic targeting
	    			//so get.x hits index -->   x / getWidth()
	        		setAdjustGunForRobotTurn(true);
	    		}
		    	turnRadarLeft(20);
		    	if (getEnergy() > 3.0 && getGunHeat() == 0) {
		    		lastShot = fireBullet(firePower);
		    		
		    		if(lastShot != null) {
		    			lastShot.getVelocity();//not sure why
		    			lastShot.getHeadingRadians();
		    		}
		    		lastShot = null;
		    	}
		    	instance += 1;
	    	}//loop
	    }//run
	    
	 
	    public void onScannedRobot(ScannedRobotEvent e) {
//	    	moving = getDistanceRemaining() == 0.0; //not avail in simple bot... turn and go!
	    	double enemyBearing = e.getBearingRadians();
	    	double turretBearing = getGunHeading();
//	    	double turretDelta = turretBearing - enemyBearing;
//	    	if(turretDelta > 1) { //max turn rate
//	    		turretDelta = 1;
//	    	}
//	    	if(turretDelta < -1) { //max turn rate
//	    		turretDelta = -1;
//	    	}
//	    	turnGunRight(turretDelta);
	    	
	    	double enemyDistance = e.getDistance(); // shoot faster at further targets
	    	double distanceRatio = enemyDistance/((getBattleFieldHeight()+getBattleFieldWidth())/2); // should be ~0 to ~1
	    	firePower = 4 - (distanceRatio * 3); // 1~3ish
	    			
	    	double newEnergy = e.getEnergy();
	    	boolean targetFired = (targetsEnergy-newEnergy) > 0.1;// target probably fired
	    	
	    	if(targetFired) { 
	    		turnLeft(panicTurn);
	    		if(panicDir) {
		    		back(panicDistance);
	    		} else {
	    			ahead(panicDistance);
	    		}
	    		panicDir = !panicDir;
	    		Random rng = robocode.util.Utils.getRandom();
	    		panicDistance = (rng.nextDouble()*100) + 50;
	    		panicTurn = (rng.nextDouble()*90) - 45;
	    	}
	    	
	    	
	    	targetsEnergy = newEnergy; //store for next tick
	    }//scan
	    

	
	
	
}
