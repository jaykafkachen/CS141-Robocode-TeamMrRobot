package trstamp_siennachen;
import robocode.*;
import robocode.util.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class MrRobot extends AdvancedRobot
{
	static Point2D.Double location, next;
	static Hashtable<String, Enemy> enemies;
	static Enemy target;
	static final double MAXRADS = Math.PI*2;
	int moveDirection = 1;
	
public void run()
	{
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		setColors(Color.black, Color.red, Color.red);
		enemies = new Hashtable<String, Enemy>(); //will store Key String enemy name, Value Enemy obj (enemy location + energy storage)		
		target = null;
		location = new Point2D.Double(getX(), getY());	
		double moveAngle, distance, turn;
		moveAngle = distance = turn = 0.0;
		do
		{			
			if(next == null)
				next = location;
			Rectangle2D.Double area  = new Rectangle2D.Double(getX(), getY(), getBattleFieldWidth()-50, getBattleFieldHeight()-50);
			Point2D.Double pt = null;
			do
			{
				if(target!=null)
				{
					distance = location.distance(target.getLoc()); //distance to target
					pt = projectPoint(location, moveAngle, Math.max(distance/2, 100));
					if(pt.getX()<=10)
						pt.setLocation(30, pt.getY());
					else if(pt.getX()>=getBattleFieldWidth()-10)
						pt.setLocation(pt.getX()-20, pt.getY());
					if(pt.getY()<=10)
						pt.setLocation(pt.getY(), 30);
					else if(pt.getY()>=getBattleFieldHeight()-10)
						pt.setLocation(pt.getX(), pt.getY()-20);
					if (area.contains(pt) && risk(pt)<=risk(next))//find the risk of different points, if there's a good new point, then move there
					{
						next = pt; //next equals calculated risk next point
					}
				}
				moveAngle += .1;
			} while(moveAngle < MAXRADS);
			turn = angle(next, location)-getHeadingRadians(); 
			if (Math.cos(turn) < 0 || getX()==0 || getY()==0 || getX()==getBattleFieldWidth() || getY()==getBattleFieldHeight())
			{
				turn += Math.PI;
				distance = -distance;
			}
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(turn));						
			setAhead(distance);
			if(Math.abs(getTurnRemainingRadians()) > 1 && getGunHeat()==0) //we will waste a turn if the gun goes off when gun heat is not zero
 			{
 				setFire(1); //setAhead(0);
 			}
			location = new Point2D.Double(getX(), getY());
			execute();
		} while(true);
	}	
	
		public double risk(Point2D.Double point)
	{
		double riskVal = 1;//location.distance(point); //greater risk at further away, zero risk when point given is current location
		if(getEnergy()<50 && (point.getX()<=10 || point.getY()<=10 || point.getX()>=getBattleFieldWidth()-10 || point.getY()>=getBattleFieldHeight()-10))
			return 100000; //hit a wall, risk is 100% at less than 50 HP because AdvRobots get dmg'd on wall hit
		if(point.equals(target.getLoc()))
			return 0;
		Enemy closest = new Enemy(new Point2D.Double(1000,1000), 100); 
		double hp = target.getE();
		Point2D.Double riskPoint = point;
		riskVal *= enemies.size(); //higher risk when more enemies on field
			for (Enemy enemyLoc : enemies.values()) //loop through and find if there is an enemy closer than the point given, will be fast bc 2 or less enemies only
			{
    			if(location.distance(riskPoint)>location.distance(enemyLoc.getLoc()) && enemyLoc.getE()<=hp) //if the last enemy's location is farther than the next enemy
				{
					closest = enemyLoc;
					riskPoint = enemyLoc.getLoc();
					hp = closest.getE();
				}
			}
			riskVal *= hp*location.distance(closest.getLoc());
		return riskVal;	
	}
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		

		double absBearing=e.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
		double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//enemies later velocity
		double gunTurnAmt;//amount to turn our gun
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
		if(Math.random()>.9){
			setMaxVelocity((12*Math.random())+12);//randomly change speed
		}
		if (e.getDistance() > 150) {//if distance is greater than 150
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//amount to turn our gun, lead just a little bit
			setTurnGunRightRadians(gunTurnAmt); //turn our gun
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
			setAhead((e.getDistance() - 140)*moveDirection);//move forward
			setFire(3);//fire
		}
		else{//if we are close enough...
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//amount to turn our gun, lead just a little bit
			setTurnGunRightRadians(gunTurnAmt);//turn our gun
			setTurnLeft(-90-e.getBearing()); //turn perpendicular to the enemy
			setAhead((e.getDistance() - 140)*moveDirection);//move forward
			setFire(3);//fire
		}	
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		double dir = getHeadingRadians()+e.getBearingRadians();	
		next = projectPoint(location, dir+Math.PI/2, 100);
		double turn = angle(next, location)-getHeadingRadians(); 
		if (Math.cos(turn) < 0)
			{
				turn += Math.PI;
			}
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(turn));						
			setAhead(50);
	}
	
	public void onHitRobot(HitRobotEvent e)
	{
		double dir = getHeadingRadians()+e.getBearingRadians();	
		next = projectPoint(location, dir+Math.PI/2, 100);
	}
	
	public void onHitWall(HitWallEvent e) 
	{
		double dir = getHeadingRadians()-e.getBearingRadians();	
		next = projectPoint(location, dir+Math.PI/2, 100);
		double turn = angle(next, location)-getHeadingRadians(); 
		if (Math.cos(turn) < 0)
			{
				turn += Math.PI;
			}
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(turn));						
			setAhead(50);
	}
	
	public void onRobotDeath(RobotDeathEvent e)
	{
		enemies.remove(e.getName());
		Enemy newtarget = new Enemy(new Point2D.Double(1000,1000),100);
		for (Enemy enemyLoc : enemies.values()) //loop through and find if there is an enemy closer than the point given, will be fast bc 2 or less enemies only
			{
    			if(location.distance(newtarget.getLoc())>location.distance(enemyLoc.getLoc())) //if the last enemy's location is farther than the next enemy
				{
					newtarget = enemyLoc; //set this enemy to be the closest
				}
			}
		target = newtarget;
		
	}
	
/*
 * This helper method uses code from Kawigi, which is released under the terms of the KPL.
 * http://robowiki.net/wiki/Coriantumr
 * returns a point dist pixels away from startPoint in the direction of angle.
 * 
 */
	public static Point2D.Double projectPoint(Point2D.Double startPoint, double angle, double dist) 
	{
		return new Point2D.Double(startPoint.getX() + dist * Math.sin(angle), startPoint.getY() + dist * Math.cos(angle));
	}

/*
 * This helper method uses code from Kawigi, which is released under the terms of the KPL.
 * http://robowiki.net/wiki/Shiz
 * returns the angle between 2 points in order to move from one to another
 * uses Math.atan2(x,y) here to correct for possibility of denom being 0 when you calc arctan
 * 
 */
	public static double angle(Point2D point1, Point2D point2) 
	{
		return Math.atan2(point1.getX()-point2.getX(), point1.getY()-point2.getY()); 
	}
	
	public class Enemy //nested class to store enemy energy and location for reference in risk() method
	{
		private Point2D.Double loc;
		private double energy;
		
		public Enemy(Point2D.Double l, double e)
		{
			loc = l;
			energy = e;
		} 
		
		public void setE(double e)
		{ energy = e;}
		
		public void setLoc(Point2D.Double l)
		{ loc = l;}
		
		public double getE()
		{ return energy;}
		
		public Point2D.Double getLoc()
		{ return loc;}
		
		public boolean equals(Enemy e)
		{
			return (this.loc.equals(e.loc) && this.energy==e.energy);
		} 
	}
}