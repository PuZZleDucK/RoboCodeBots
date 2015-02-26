package puzzleduck.rcbots;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.*;
import robocode.BattleRules;
import robocode.util.*;
import robocode.Rules;

import java.util.Random;

public class P4_StaT_MeM extends Robot {

	private int myCount = 0;
	private Random rng;
	private int gridX, gridY;
	static int[][] targetingGrid;
	static Map<String, EData> enemyData;
	static ArrayList<Coords> myHistory;
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


    public void run() {
    	while(true) {  //isNear(d,d)
    		if(myCount == 0) { // reset... now not running :(
    			out.println("Reset.");
    			rng = robocode.util.Utils.getRandom();
    			gridX = (int)(getHeight()/getBattleFieldHeight());
    			gridY = (int)(getWidth()/getBattleFieldWidth());
        	    out.println("Enemy Grid Size ("+gridX+","+gridY+")");
    			if( targetingGrid == null ) {
            	    out.println("Initializing grid!");
        			targetingGrid = new int[(int)gridX][(int)gridY]; //pobabilistic targeting
        			for(int x = 0; x < (int)gridX; x++) {
        				for(int y = 0; y < (int)gridY; y++) {
        					targetingGrid[x][y] = 0;
        				}
        			}
    			}
    			//so get.x hits index -->   x / getWidth()
        		setAdjustGunForRobotTurn(true);
        		setAdjustRadarForGunTurn(true);
        		setAdjustRadarForRobotTurn(true);
        		if(enemyData == null) {
            		enemyData = new HashMap<String, EData>();
            		myHistory = new ArrayList<Coords>();
        		}
    		}//first
    		myCount += 1;
    		Coords myCoords = new Coords();
    		myCoords.x = getX();
    		myCoords.y = getY();
    		myHistory.add(myCoords);
    		
    		ahead(35);
    		turnRight(15);
    		ajustTargets(enemyData, 10);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		turnRadarRight(Rules.RADAR_TURN_RATE);
    		setBodyColor(Color.blue);

    	}
    }//run


    private void ajustTargets(Map<String, EData> enemyData, int adjust) {
    	for(String name : enemyData.keySet()) {
    		EData e = enemyData.get(name);
    		e.bearing = e.bearing-adjust;
    		enemyData.put(name, e);
    	}
		
	}


	public void onScannedRobot(ScannedRobotEvent e) {
    	out.println("xy:("+getX()+""+getY()+")");
		setBodyColor(Color.green);
		EData thisEnemy = new EData();
		if(e.getDistance() < 300) { //close
			out.print("..CLOSE!!");
			setBodyColor(Color.black);
    		turnLeft(Rules.MAX_TURN_RATE);
    		ahead(300);
		}
    	if(enemyData.containsKey(e.getName())) {
    		thisEnemy = enemyData.get(e.getName());
    		if(thisEnemy.energy > e.getEnergy()) { //has shot
    			out.println("..PANIC!!");
    			setBodyColor(Color.yellow);
    			turnLeft((e.getBearing() + (rng.nextDouble()*180 + 180)) % 360);
	    		if(rng.nextBoolean()) {
		    		back((rng.nextDouble()*150)+100);
	    		} else {
	    			ahead((rng.nextDouble()*150)+100);
	    		}
    		}	
    	} else {//new enemy
    		enemyData.put(e.getName(), thisEnemy);
    		out.println("New eney:" + e.getName());
    	}

		thisEnemy.energy = e.getEnergy(); //store for next tick
		thisEnemy.bearing = e.getBearing();
		thisEnemy.distance = e.getDistance();
		enemyData.put(e.getName(), thisEnemy);
		
		//statistical targeting & crashing...
//	    double eAngle = Math.toRadians((getHeading()+thisEnemy.bearing) % 360);
//	    double enemyX = (int)(getX() + Math.sin(eAngle) * thisEnemy.distance);
//	    double enemyY = (int)(getY() + Math.cos(eAngle) * thisEnemy.distance);
//	    out.println("Enemy location ("+(int)(enemyX/getHeight())+","+(int)(enemyY/getWidth())+")");
//	    if((enemyX/getHeight()) >= 0 && (int)(enemyY/getWidth()) >= 0) {
////			targetingGrid[(int)(enemyX/getHeight())][(int)(enemyY/getWidth())] += 1;
//	    }
		
		
    }//scan
    
    
    


	public void onDeath() {
		out.println("DEAD!!!");
	}//reset
	
	

	
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
	     
	     //draw battle data
	     for(String name : enemyData.keySet()) {
	    	 EData e = enemyData.get(name);
		     g.setColor(new Color(0x00, 0x00, 0xff, 0x80));
		     if(e != null) {
			     double eAngle = Math.toRadians((getHeading()+e.bearing) % 360);
			     double enemyX = (int)(getX() + Math.sin(eAngle) * e.distance);
			     double enemyY = (int)(getY() + Math.cos(eAngle) * e.distance);
			     g.drawLine((int)getX(), (int)getY(), (int)enemyX, (int)enemyY);
			     g.drawString("E: "+name, (int)getX(), (int)getY());
		     }	    	 
		     
	     }
	     
	     //history
	     for(int i = 0; i < myHistory.size(); i++) {
	    	 if(i != 0) {
	    		 Coords last = myHistory.get(i-1);
	    		 Coords now = myHistory.get(i);
			     g.setColor(new Color(0x00, 0xff, 0xff, 0x80));
	    		 g.drawLine((int)last.x, (int)last.y, (int)now.x, (int)now.y);
	    	 }
	    	 
	     }
	     
	 }
	
	
	
	
	
	
	
	
}
