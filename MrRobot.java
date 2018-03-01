package testing;
import robocode.*;
import robocode.util.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class Testbot extends AdvancedRobot
{
	static Point2D location, last;
	static Hashtable<String, Point2D> enemies;
	static final double MAXRADS = Math.PI*2;
	
	public void run()
	{
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		setColors(Color.black, Color.red, Color.red);
		enemies = new Hashtable<String, Point2D>(); //will store Key String enemy name, Value Point2d enemy location		
		Point2D next = null;

		do
		{
			location = new Point2D.Double(getX(), getY());			
			if(last == null)
				last = location;
			boolean changed = false;
			double moveAngle, distance;
			moveAngle = distance = 0.0;
			do
			{
			//	if ()//find the risk of different points, if there's a good new point, then move there
			//	{
			//		changed = true;
			//		next = p; //next equals calculated risk next point
			//	}
				moveAngle += .1;
			} while(moveAngle < MAXRADS);
			if (changed)
				last = location;
			double turn = angle(next, location)-getHeadingRadians(); //nullPointer here
			if (Math.cos(turn) < 0)
			{
				turn += Math.PI;
				distance = -distance;
			}
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(turn));						
			if(Math.abs(getTurnRemainingRadians()) > 1)
			{
				setFire(1); //setAhead(0);
			}
			else
				setAhead(distance);
			execute();
		} while(true);
	}	
	
	public double risk(Point2D point)
	{
		double riskVal = .05*location.distance(point); //greater risk at further away, zero risk when point given is current location
		double hp = getEnergy();
		if(hp<50 && (point.getX()<=0 || point.getY()<=0 || point.getX()>=getBattleFieldWidth() || point.getY()>=getBattleFieldHeight()))
			return 100; //hit a wall, risk is 100% at less than 50 HP because AdvRobots get dmg'd on wall hit
		Point2D closest = point; //assume the point given is of highest priority
		riskVal *= enemies.size(); //higher risk when more enemies on field
		if(enemies.size()<=2) //if there are few enemies on field/one-on-one, start prioritizing melee
		{
			for (Point2D enemyLoc : enemies.values()) //loop through and find if there is an enemy closer than the point given, will be fast bc 2 or less enemies only
			{
    			if(location.distance(closest)>location.distance(enemyLoc))
					closest = enemyLoc;
			}
			if(!closest.equals(point))
				riskVal -= .05*location.distance(closest); //subtract the distance of the closest robot because we WANT to move there if melee'ing
		}
		else
		{
		
		}
		return riskVal;	
	}
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		String name = e.getName();
		double enemyX = (location.getX() + Math.sin(getHeadingRadians() + e.getBearingRadians()) * e.getDistance());
       double enemyY = (location.getY() + Math.cos(getHeadingRadians() + e.getBearingRadians()) * e.getDistance());
		Point2D enemyLoc = new Point2D.Double(enemyX, enemyY); ;// point2D w calculated location of enemy based on distance + bearing/heading 
		Enemy en = new Enemy(enemyLoc, e.getEnergy());
		enemies.put(name, enemyLoc); //note here that put() will replace the previous Enemy (location/energy storage object) if the enemy is already in the hashmap
	}
	
	public void onRobotDeath(RobotDeathEvent e)
	{
		enemies.remove(e.getName());
	}
	
/*
 * This helper method uses code from Kawigi, which is released under the terms of the KPL.
 * http://robowiki.net/wiki/Coriantumr
 * returns a point dist pixels away from startPoint in the direction of angle.
 * 
 */
	public static Point2D projectPoint(Point2D startPoint, double angle, double dist) 
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
		private Point2D loc;
		private double energy;
		
		public Enemy(Point2D l, double e)
		{
			loc = l;
			energy = e;
		} 
		
		public void setE(double e)
		{ energy = e;}
		
		public void setLoc(Point2D l)
		{ loc = l;}
		
		public double getE()
		{ return energy;}
		
		public Point2D getLoc()
		{ return loc;}
	}
}
