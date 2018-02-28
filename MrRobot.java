package TeamMrRobot;
import robocode.*;
import robocode.util.Utils.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

/*
 * This code uses code from Shiz by Kawigi, which is released under the terms of the KPL.
 */
public class MrRobot extends AdvancedRobot
{
	static Point2D location, last;
	static String target;
	static Hashtable enemies;
	static final MAXRADS = Math.PI*2;
	
	public void run()
	{
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		setColors(Color.black, Color.red, Color.red);
		//MAXRADS = Math.PI*2;
		enemies = new Hashtable(); //will store Key String enemy name, Value Point2d enemy location
		target = "";
		Point2D next = null;
		
		do
		{
			location = new Point2D.Double(getX(), getY());
			
				
				do
				{
					angle += .1;
				} while (angle < MAXRADS);
			execute();
		}
		while (true);
	}
	
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		
	}
	
	public void onRobotDeath(RobotDeathEvent e)
	{
		if (target.equals(e.getName()))
		{
			enemies.remove(e.getName());
			target = "";
		}
	}
	
	//METHOD TO CALC ABSBEARING BETWEEN 2 POINTS
	public static double absBearing(double x1, double y1, double x2, double y2) 
	{
  	double xAbs = x2-x1;
  	double yAbs = y2-y1;
  	double hyp = Point2D.distance(x1, y1, x2, y2);
  	double arcSin = Math.asin(xAbs / hyp);
  	double bearing = 0;
  
		if (xAbs > 0 && yAbs > 0) 
		{ 
			bearing = arcSin;
		}
		else if (xAbs < 0 && yAbs > 0) 
		{ 
			bearing = MAXRADS + arcSin; 
		}
		else if (xAbs > 0 && yAbs < 0) 
		{ 
			bearing = Math.PI - arcSin;
		}
		else if (xAbs < 0 && yAbs < 0) 
		{ 
			bearing = Math.PI + arcSin; 
		}
		return bearing;
  	}
}
