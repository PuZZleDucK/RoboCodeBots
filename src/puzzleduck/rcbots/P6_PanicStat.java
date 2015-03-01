package puzzleduck.rcbots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import robocode.DeathEvent;
import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;


public class P6_PanicStat extends Robot {
	static int localTime = 0;
	static HashMap<String, RobotData> enemies;
	private Random rng;

	
	private class RobotData {
		public double energy = -1;
		public double bearing = -1;
	}//RobotData
	
    public void run() {
    	while(true) {
    		if(localTime <= 0) {
    			rng = robocode.util.Utils.getRandom();
    			enemies = new HashMap<String, RobotData>();
    			out.println("Reset."); 
    			setAdjustRadarForGunTurn(true);
    			setAdjustRadarForRobotTurn(true);
    			setAdjustGunForRobotTurn(true);
    			setBodyColor(Color.green);
    		} else {
    			turnRadarRight(Rules.RADAR_TURN_RATE/2);//scan
    		}
    		fire(3);
        	localTime += 1;
    	}//loop
    }//run
    
	 public void onPaint(Graphics2D g) {
		 g.setFont((Font.decode("Monospace-Bold-14"))); 
//	     g.drawString("Hi Robocode: ", 50, 50);
	     //draw my data
	     g.setColor(new Color(0x00, 0xFF, 0x00, 0xFF));
	     g.drawString("H: "+getHeading(), (int)getX()+20, (int)getY());
	     g.drawString("V: "+getVelocity(), (int)getX()+20, (int)getY()-20); 
	     double hAngle = Math.toRadians((getHeading()) % 360);
	     // Calculate the coordinates of the robot
	     double goingX = (int)(getX() + Math.sin(hAngle) * getVelocity()*100+50);
	     double goingY = (int)(getY() + Math.cos(hAngle) * getVelocity()*100+50);
	     g.drawLine((int)getX(), (int)getY(), (int)(goingX), (int)goingY);
	     
	     
	     //draw enemy data
	     g.setColor(new Color(0xFF, 0x00, 0x00, 0xFF));
	     Set<Entry<String,RobotData>> droidz = enemies.entrySet();
	     for(Entry<String,RobotData> thisDroid : droidz) {
	    	 g.drawString("E: "+thisDroid.getKey(), 50, 50);
	    	 RobotData thisRobot = thisDroid.getValue();
	    	 g.drawString("B: "+thisRobot.bearing, 50, 30);
	    	 
	    	 
	     }
		    
	 }//paint
	 
	
	 
	public void onDeath(DeathEvent d) {
		out.println("Death !!!");
		enemies = new HashMap<String, RobotData>();
		localTime = 1;
	}//reset
	
	public void onWin(WinEvent w) {
		out.println("Victory !!!");
		enemies = new HashMap<String, RobotData>();
		localTime = 1;
	}
	 
	public void onScannedRobot(ScannedRobotEvent e) {
		turnRadarLeft(Rules.RADAR_TURN_RATE/2);//re-scan, track same robot
		RobotData thisEnemy;
		String targetName = e.getName();
		out.println("Target: "+targetName+"  |==| "+e.getEnergy()); 
		if(enemies.containsKey(targetName)) { //old foe
			thisEnemy = enemies.get(targetName);
//			out.println("Old: "+ thisEnemy.energy); 
			if(thisEnemy.energy != e.getEnergy()) { //enemy energy drop... probable fire
    			setBodyColor(Color.yellow);
    			if(rng.nextBoolean()) {
    				ahead(rng.nextInt(90)+90);
    			} else {
    				back(rng.nextInt(90)+90);
    			}
			} else { //chill
    			setBodyColor(Color.green);
			}
		} else { //new enemy
			thisEnemy = new RobotData();
		}
		//always store data
		thisEnemy.energy = e.getEnergy();
		thisEnemy.bearing = e.getBearing();
		enemies.put(targetName, thisEnemy);
		
//		Double delta = getHeading()-thisEnemy.bearing;
//		out.println("D:"+delta);
		Double thisTurn = (thisEnemy.bearing-90)%180;
		turnRight(thisTurn);  //turn 90 to enemy
		turnGunRight((thisEnemy.bearing%180)); //dead recon... dead bad
	}//scan
}//class
