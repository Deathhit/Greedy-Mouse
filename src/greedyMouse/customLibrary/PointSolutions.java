package greedyMouse.customLibrary;

import android.graphics.Point;

/***A storage class of static functions for handling Point object.***/
public class PointSolutions {
	private PointSolutions(){}

	/***Get top-left coordinate by given center point.***/
	public static Point coordinateByCenter(Point center,int width,int height){	
		Point coordinate = new Point(center);
		
		coordinate.x -= width/2;
		coordinate.y -= height/2;
		
		return coordinate;
	}
}
