package puzzleduck.rcbots;

import java.awt.Color;
import java.math.*;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class P1 extends Robot {
	static int counter = 0;
	private double h;
	private double w;
	private double longMove;
	private double shortMove;
	private double enemyHeading;
	
	public void onDeath() {
		counter = 0;
	}
	
    public void run() {
    	if(counter == 0) { //init
    		h = this.getBattleFieldHeight();
    		w = this.getBattleFieldWidth();
    		shortMove = ((h+w)/40);
    		longMove = ((h+w)/20);
    		out.print("Setup  h:" + h + "  w:" + w + "  long:" + longMove + "  short:" + shortMove );
    		setAdjustGunForRobotTurn(true);
    		setAdjustRadarForRobotTurn(false);
    		setAdjustRadarForGunTurn(true);

        	setRadarColor(Color.white);
    	}
    	
    	
    	
        while (true) {
//        	turnRadarRight(50); //swivel your head
        	
//        	this.getGunCharge()
//        	this.getGunHeat()
//        	this.getLife()
//        	this.getOthers()
//        	this.getVelocity()
        	
//        	this.;
//        	this.onHitByBullet(arg0);
//        	this.onHitRobot(arg0);
//        	this.onHitWall(arg0);
//        	this.onBulletHit(arg0);
//        	this.onBulletHitBullet(arg0);
//        	this.onBulletMissed(arg0);
//        	this.scan();
//        	
        	
        	if (counter%3 == 0) {
                turnLeft(40);
            	setBodyColor(Color.green);
            	setRadarColor(Color.white);
        	} else {
                turnRadarLeft(40);
        	}

        	
            ahead(shortMove);
            //turnGunRight(180);
            counter += 1;
        }
    }
    
 
    public void onScannedRobot(ScannedRobotEvent e) {
//    	turnGunRight(e.getBearingRadians());
    	enemyHeading = e.getBearingRadians();
    	double delta = (getGunHeading()-enemyHeading);
    	if(delta < 0) {
    		delta = -delta;
    	}
    	setRadarColor(Color.red);
    	turnGunLeft(delta);
    	

    	if(getEnergy() > 3) {
        	setBodyColor(Color.red);
            fire(3);
    	}
    }
    
    
}
