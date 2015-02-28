package puzzleduck.rcbots;

import java.awt.Color;
import java.util.Map;
import java.util.Random;

import robocode.Bullet;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class P3_DeD_PaniC extends Robot {
	static int[][] targetingGrid;
	static Map<String, EData> enemyData;
	private class EData{
		public double energy;
		public double heading;
		public double bearing;
	}
	
	static double targetsEnergy = 100;
	private double firePower;
	private Bullet lastShot;
	private boolean panicDir = true;
	private double panicDistance = 130;
	private double panicTurn = 0;
	private boolean scanTarget = false;
	
	private double gridX;
	private double gridY;
	private int instance;
	static double lastKnownBearing;
	private boolean scanSinceStill;

	private Random rng;

	private double lastKnownDelta;

	private boolean enemyStill;

	private double lastX;

	private double lastY;
	
		public void onDeath() {
			instance = 0;
			lastKnownBearing = -1;
			scanSinceStill = false;
			enemyStill = false;
		}//dead
		
	    public void run() {
	    	while(true) {
	    		
	    		if(instance == 0) { // reset
	    			rng = robocode.util.Utils.getRandom();
	    			gridX = getHeight()/getBattleFieldHeight();
	    			gridY = getWidth()/getBattleFieldWidth();
	    			//scale movements by field size
	    			targetingGrid = new int[(int)gridX][(int)gridY]; //pobabilistic targeting
	    			//so get.x hits index -->   x / getWidth()
	        		setAdjustGunForRobotTurn(true);

	    		}
	    		
	    		if(scanTarget) { // try to 'rescan' current target
	    			scanTarget = false;
			    	turnRadarRight(15);
			    	turnRadarLeft(30);
	    		} else {
			    	turnRadarRight(20); // or just keep looking
	    		}
		    	if ((getGunHeat() == 0)) {
	    			setBodyColor(Color.green);
//	    			if(getEnergy() > 3.0 && (getVelocity() == 0 || getOthers() > 1)) { //still for last shot
//	    				fireBullet(getEnergy());
//	    			}
	    			if(getEnergy() > 3.0 && (getVelocity() == 0 || getOthers() > 1)) { //still for last shot
	    				lastShot = fireBullet(3);// store shot bullets, not used
	    			}
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
	    		
	    	scanTarget = true; //hone in on target
//	    	moving = getDistanceRemaining() == 0.0; //not avail in simple bot... turn and go!
//	    	double enemyBearing = e.getBearingRadians();
//	    	double turretBearing = getGunHeading();
//	    	double turretDelta = turretBearing - enemyBearing;
//	    	if(turretDelta > 1) { //max turn rate
//	    		turretDelta = 1;
//	    	}
//	    	if(turretDelta < -1) { //max turn rate
//	    		turretDelta = -1;
//	    	}
//	    	turnGunRight(turretDelta);
//	    	 

	    	lastKnownBearing = e.getBearing();
	    	lastKnownDelta = getHeading() + e.getBearing();
	    	double enemyX = getX() + e.getDistance() * getHeading();
	    	double enemyY = getY() + e.getDistance() * getHeading();
	    	if (lastX == enemyX && lastX == enemyX) {
	    		enemyStill = true;
	    	} else {
	    		enemyStill = false;
	    	}
	    	lastX = enemyX;
	    	lastY = enemyY;
	    	
	    	double turnGunAmt = (getHeading() + e.getBearing()) - getGunHeading();
	    	if(getVelocity() < 0.1) {
	    		scanSinceStill = true;
	    	}
	    	boolean invert = false; //spinning less than 180 if possible
	    	while (turnGunAmt > 180) {
	    		turnGunAmt = turnGunAmt - 180;
	    		invert  = !invert;
	    	}
	    	if(invert) {
	    		turnGunAmt = -turnGunAmt;
	    	}
    		if (turnGunAmt > 0) {
    			turnGunRight(turnGunAmt);
    		} else {
    			turnGunLeft(-(turnGunAmt));
    		}

	    	
	    	double enemyDistance = e.getDistance(); // shoot faster at further targets, not used anymore
	    	double distanceRatio = enemyDistance/((getBattleFieldHeight()+getBattleFieldWidth())/2); // should be ~0 to ~1
	    	firePower = 5 - (distanceRatio * 3); // 2~3ish
	    			
	    	double newEnergy = e.getEnergy();
	    	boolean targetFired = ((targetsEnergy-newEnergy) >= 0.1 || (targetsEnergy-newEnergy) <= -0.1);// target probably fired
	    	
	    	if(enemyDistance < 200) { // don't stay close
	    		panicDistance = 300;
	    	}
	    	
	    	if(getOthers() == 1 && targetFired) { 
    			setBodyColor(Color.yellow);
    			scanSinceStill = false;
	    		if ( ((lastKnownBearing < 30) && (lastKnownBearing > -30))
	    				|| ((lastKnownBearing < -150) || (lastKnownBearing > 150)) ) { //err away from head on charges during panic?!? 
	    			
	    			setBodyColor(Color.red);
	    			panicTurn = 90;//if all else fails, sharp left
	    		}
	    		turnLeft(panicTurn);
	    		if(panicDir) {
		    		back(panicDistance);
	    		} else {
	    			ahead(panicDistance);
	    		}
	    		panicDir = !panicDir;
	    		panicDistance = (rng.nextDouble()*80) + 120;//celerity
	    		panicTurn = (rng.nextDouble()*70) - 35;//faster, tighter

	    		targetsEnergy = newEnergy; //store for next tick
	    	} else { //more than one enemy... just panic
	    		if ( ((lastKnownBearing < 40) && (lastKnownBearing > -40))
	    				|| ((lastKnownBearing < -140) || (lastKnownBearing > 140)) ) { //err away from head on charges during panic?!? 
	    			
	    			setBodyColor(Color.red);
	    			panicTurn = 90;//if all else fails, sharp left
	    		}
	    		turnLeft(panicTurn);
	    		if(panicDir) {
		    		back(panicDistance);//
	    		} else {
	    			ahead(panicDistance);
	    		}
	    		panicDir = !panicDir;
	    		panicDistance = (rng.nextDouble()*80) + 70;//
	    		panicTurn = (rng.nextDouble()*180) - 90;//
	    		
	    	}//fire
	    	
	    }//scan

}
