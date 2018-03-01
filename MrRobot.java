package TeamMrRobot;
import robocode.*;
import robocode.util.Utils.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class MrRobot extends AdvancedRobot
{
	static Point2D location, last;
	static Hashtable enemies;
	static final double MAXRADS = Math.PI*2;
	
	public void run()
	{
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		setColors(Color.black, Color.red, Color.red);
		enemies = new Hashtable(); //will store Key String enemy name, Value Point2d enemy location		
		Point2D next = null;

		do
		{
			location = new Point2D.Double(getX(), getY());			
			if(last == null)
				last = location;
			boolean changed = false;
			double moveAngle, distance;
			angle = distance = 0.0;
			do
			{
				if ()
				{
					changed = true;
					next = p; //next equals calculated risk next point
				}
				moveAngle += .1;
			} while(moveAngle < MAXRADS);
			if (changed)
				last = location;
			double turn = angle(next, location)-getHeadingRadians();
			if (Math.cos(turn) < 0)
			{
				turn += Math.PI;
				distance = -distance;
			}
			setTurnRightRadians(normalRelativeAngle(turn));						
			if(Math.abs(getTurnRemainingRadians()) > 1)
				setAhead(0);
			else
				setAhead(distance);
			execute();
		} while(true);
	}	
	private double findRisk(Point2D point)
	{
	
	}
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		String name = e.getName();
		Point2D enemyLoc = 
		enemies.put(name, //put point2D w calculated location of enemy based on distance + bearing/heading 
		//new Point2D.Double(getX(), getY()); 
		//write method to generate Point2D obj and return it
	}
	
	public void onRobotDeath(RobotDeathEvent e)
	{
		enemies.remove(e.getName());
	}
	
	/*
 	* This helper method uses code from Shiz by Kawigi, which is released under the terms of the KPL.
 	*/
	private static Point2D projectPoint(Point2D startPoint, double theta, double dist)
	{
		return new Point2D.Double(startPoint.getX() + dist * Math.sin(theta), startPoint.getY() + dist * Math.cos(theta));
	}
	
	/*
 	* This helper method uses code from Shiz by Kawigi, which is released under the terms of the KPL.
 	*/
	public static double angle(Point2D point1, Point2D point2)
	{
		return Math.atan2(point1.getX()-point2.getX(), point1.getY()-point2.getY());
	}
	
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

