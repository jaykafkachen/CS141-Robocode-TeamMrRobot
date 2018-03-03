package name;
import robocode.*;
import robocode.util.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class Name extends AdvancedRobot
{
	static Point2D.Double location, next;
	static Hashtable<String, Enemy> enemies;
	static Enemy target;
	static final double MAXRADS = Math.PI*2;
	
	public void run()
	{
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		setColors(Color.black, Color.red, Color.red);
		enemies = new Hashtable<String, Enemy>(); //will store Key String enemy name, Value Enemy obj (enemy location + energy storage)		
		target = null;
		location = new Point2D.Double(getX(), getY());	
		double moveAngle, distance;
		moveAngle = distance = 0.0;
		do
		{			
			if(next == null)
				next = location;
			Rectangle2D.Double area  = new Rectangle2D.Double(40, 40, getBattleFieldWidth()-10, getBattleFieldHeight()-10);
			Point2D.Double pt = null;
			do
			{
				if(target!=null)
				{
					distance = location.distance(target.getLoc()); //distance to target
					pt = projectPoint(location, moveAngle, Math.max(distance/2, 300));
					if (area.contains(pt) && risk(pt)<risk(next))//find the risk of different points, if there's a good new point, then move there
					{
						next = pt; //next equals calculated risk next point
					}
				}
				moveAngle += 1;
			} while(moveAngle < MAXRADS);
			double turn = angle(next, location)-getHeadingRadians(); 
			if (Math.cos(turn) < 0)
			{
				turn += Math.PI;
				distance = -distance;
			}
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(turn));						
			setAhead(distance);
			location = new Point2D.Double(getX(), getY());
			execute();
		} while(true);
	}	
	
	public double risk(Point2D.Double point)
	{
		double riskVal = .05*location.distance(point); //greater risk at further away, zero risk when point given is current location
		if(getEnergy()<50 && (point.getX()<=0 || point.getY()<=0 || point.getX()>=getBattleFieldWidth() || point.getY()>=getBattleFieldHeight()))
			return 100; //hit a wall, risk is 100% at less than 50 HP because AdvRobots get dmg'd on wall hit
		Enemy closest = null; 
		Point2D.Double riskPoint = point;
		riskVal *= enemies.size(); //higher risk when more enemies on field
			for (Enemy enemyLoc : enemies.values()) //loop through and find if there is an enemy closer than the point given, will be fast bc 2 or less enemies only
			{
    			if(location.distance(riskPoint)>location.distance(enemyLoc.getLoc())) //if the last enemy's location is farther than the next enemy
				{
					closest = enemyLoc; //set this enemy to be the closest
					riskPoint = enemyLoc.getLoc();
				}
			}
			if(!riskPoint.equals(point)) //if the closest enemy is not the current point projected
			{
				riskVal -= .5*location.distance(closest.getLoc()); //subtract the distance of the closest robot because we WANT to move there if melee'ing
				riskVal = Math.max(riskVal, 10);
			}	
		return riskVal;	
	}
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		String name = e.getName();
		double enemyX = (location.getX() + Math.sin(getHeadingRadians() + e.getBearingRadians()) * e.getDistance());
       	double enemyY = (location.getY() + Math.cos(getHeadingRadians() + e.getBearingRadians()) * e.getDistance());
		Point2D.Double enemyLoc = new Point2D.Double(enemyX, enemyY); ;// point2D w calculated location of enemy based on distance + bearing/heading 
		Enemy en = new Enemy(enemyLoc, e.getEnergy());
		if(target==null || target.getE()>en.getE()) //if we dont have a target or the target is not the weakest/only enemy on the field, target this enemy
			target = en;
		enemies.put(name, en); //note here that put() will replace the previous Enemy (location/energy storage object) if the enemy is already in the hashmap
 		setTurnRadarRightRadians(Utils.normalRelativeAngle(turn));
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		double dir = getHeadingRadians()+e.getBearingRadians();	
		next = projectPoint(location, dir+Math.PI/2, 100);
	}
	
	public void onHitWall(HitWallEvent event) 
	{
		turnRight(Math.PI-getHeadingRadians());
		ahead(50);
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
	}
}
