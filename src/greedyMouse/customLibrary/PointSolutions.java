/*
Copyright 2017 YANG-TUN-HUNG

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
