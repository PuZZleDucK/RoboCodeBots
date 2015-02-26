package puzzleduck.rcbots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import robocode.Bullet;
import robocode.Robot;

public class P5_SeeShot extends Robot {
	static ArrayList<Coords> myHistory;
	static ArrayList<Bullet> myBullets;
	private class Coords{
		public double x;
		public double y;
	}

	private class EData{
		public double energy;
		public double heading;
		public double bearing;
		public double distance;
	}
	static Random rng;
	static Map<String, EData> enemyData;
	
    public void run() {
    	while(true) {  //isNear(d,d)
    		if(getTime() == 0) { // reset... now not running :(
    			out.println("Reset.");
    			rng = robocode.util.Utils.getRandom();
        		setAdjustGunForRobotTurn(false);
        		setAdjustRadarForGunTurn(true);
        		setAdjustRadarForRobotTurn(true);
        		if(enemyData == null) {
            		enemyData = new HashMap<String, EData>();
            		myHistory = new ArrayList<Coords>();
        		}
    		}//first
    		
    		
    		

    		Coords myCoords = new Coords();
    		myCoords.x = getX();
    		myCoords.y = getY();
    		myHistory.add(myCoords);
    		
//    		turnRight(5);
//    		fire(0.01);
    		while(getGunHeat() > 0.0) {
        		turnGunRight(10);
        		ahead(5);
    		}
    		fireBullet(0.1);
    		
    		
    	}//true
    }//run

	
	
	 public void onPaint(Graphics2D g) {
		 //g.setFont();
		  g.setFont((Font.decode("Monospace-Bold-14")));
	     
	     //draw my data
	     g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
	     g.drawString("H: "+getHeading(), (int)getX()+20, (int)getY());
	     g.drawString("V: "+getVelocity(), (int)getX()+20, (int)getY()-20); 
	     double hAngle = Math.toRadians((getHeading()) % 360);
	     // Calculate the coordinates of the robot
	     double goingX = (int)(getX() + Math.sin(hAngle) * getVelocity()*100);
	     double goingY = (int)(getY() + Math.cos(hAngle) * getVelocity()*100);
	     g.drawLine((int)getX(), (int)getY(), (int)(goingX), (int)goingY);
	    
	     
	     //history
	     for(int i = 0; i < myHistory.size(); i++) {
	    	 if(i != 0) {
	    		 Coords last = myHistory.get(i-1);
	    		 Coords now = myHistory.get(i);
			     g.setColor(new Color(0x00, 0xff, 0xff, 0x80));
	    		 g.drawLine((int)last.x, (int)last.y, (int)now.x, (int)now.y);
	    	 }
	    	 
	     }//his
	     
	     for(Bullet b : myBullets) {
		     g.setColor(new Color(0xFF, 0x00, 0x00, 0x80));
	    	 g.drawLine((int)getY(), (int)getX(),(int)b.getX(),(int)b.getY());
	     }//bullets
	     
	     
	     
	 }//paint
    
    
	
	
}//class
