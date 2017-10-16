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

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import greedyMouse.customLibrary.BitmapSolutions;

/***A wrapper class of bitmap. 
 * It helps specify where the bitmap should be drawn, 
 * and scale automatically depends on the size of the Grids.
 * This class can be used as a dynamic layout for a single surface.***/
public class Grids{
	private Vector<Grids>  slaves;    //Slave objects
	
	private Bitmap	 draw;			  //Temporary bitmap for draw() method.
	
	private Canvas   canvas;          //Temporary canvas for draw() method.
	
	private Rect     rect,			  //Temporary rectangle for draw() method.
					 src,			  //Temporary rectangle for draw() method.
					 dst;			  //Temporary rectangle for draw() method.
	
	public  Bitmap   bitmap;		  //Bitmap that stores the frame of grids.
	
	public int       x,y,             //Top left coordinate in its x-y axis
					 offsetX,		  //Coordinate offset in axis X.
					 offsetY,		  //Coordinate offset in axis Y.
				     gridSize,	      //Length of a grid //Unit is gridSize of its master grids
					 widthInGrids,
					 heightInGrids,
					 color;           //Color for drawGrids()
	
	public boolean	 visible,         //Become invisible if visible is false;
				     drawGrids;       //The grids will not be drawn if drawGrids is false;
	
	public Grids(int x,int y,int widthInGrids,int heightInGrids){
		slaves  = new Vector<Grids>();
		
		rect    = new Rect();
		
		offsetX = 0;
		offsetY = 0;
		
		this.widthInGrids  = widthInGrids;
		this.heightInGrids = heightInGrids;
		
		color   = Color.TRANSPARENT;
		
		visible   = true;
		drawGrids = false;
		
		this.x  = x;
		this.y  = y;
	}
	
	public Grids(int x,int y,int widthInGrids,int heightInGrids,int gridSize){
		this(x,y,widthInGrids,heightInGrids);
		
		this.gridSize = gridSize;
	}
	
	//Functions
	/***Add sub Grids with the same grid size to current Grids to target coordinate within the domain of current Grids.***/
	public Grids addSlave(int x,int y,int widthInGrid,int heightInGrid){ //Add slave-grids with same grid size to calling grids.
		Grids grids    = new Grids(x,y,widthInGrid,heightInGrid,this.gridSize);
		
		slaves.add(grids);
		
		return grids;
	}
	
	/***Add sub Grids with target grid size to current Grids to target coordinate within the domain of current Grids.***/
	public Grids addSlave(int x,int y,int widthInGrid,int heightInGrid,int newGridSize){ //Add slave-grids with new grid size to calling grids.
		Grids grids    = new Grids(x,y,widthInGrid,heightInGrid,newGridSize);
		
		slaves.add(grids);
		
		return grids;
	}
	
	/***Draw all the contents of sub Grids and current Grids. Draw grids if drawGrids is true.***/
	public Bitmap draw(boolean drawGrids,Config config){	//Initialize the objects that the draw method needs.
		int width  = widthInGrids *gridSize,				//Grid size must be set before invoking this method.
			height = heightInGrids*gridSize;
		
		if(!visible)
			return null;
		if(draw == null)
			draw = Bitmap.createBitmap(width, height, config);
		else if(draw.getWidth() != width || draw.getHeight() != height){
			draw.recycle();
			draw = Bitmap.createBitmap(width, height, config);
		}
		if(canvas == null)
			canvas = new Canvas();
		if(src == null)
			src = new Rect();
		if(dst == null)
			dst = new Rect();
		
		canvas.setBitmap(draw);
		draw.eraseColor(Color.TRANSPARENT);
		
		rect.set(0,					//This rectangle represents the area of its owner.
				 0,
				 width,
				 height);
		
		if(bitmap != null)
			canvas.drawBitmap(bitmap,null,rect,null);
		
		if(drawGrids && this.drawGrids)				//Draw grids if drawGrids is true
		{
			if(color == Color.TRANSPARENT)
				color = BitmapSolutions.randomColor();
			drawGrids(canvas,0,0);
		}
		
		return draw(drawGrids, src, dst, canvas);
	}
	
	//Logical Methods.
	/***Draw grids lines.***/
	private void drawGrids(Canvas canvas,int x,int y){   //Draw grids at (x,y).			
		Paint paint      = new Paint();

		int width  = widthInGrids *gridSize,
			height = heightInGrids*gridSize;

		paint.setColor(color);
		paint.setStrokeWidth(3);
		
		for(int j = 0;j<height/gridSize;j++){ //Draw horizontal lines
			canvas.drawLine(x, 
							y+j*gridSize, 
							x+width,
							y+j*gridSize, 
							paint);
		}
		for(int j = 0;j<width/gridSize;j++){ //Draw vertical lines
			canvas.drawLine(x+j*gridSize,
							y,
							x+j*gridSize,
							y+height,
							paint);
		}
	}
	
	/***The recursive part of the draw method.***/
	private Bitmap draw(boolean drawGrids,Rect src,Rect dst,Canvas canvas){	//Draw all the contents including its slaves
		for(int i = 0;i<slaves.size();i++){
			Grids slave;
			
			slave = slaves.get(i);
			if(slave.visible){
				/***Finding the rectangle that represents the slave***/
				int slaveLeft = rect.left+slave.x*gridSize+slave.offsetX,
					slaveTop  = rect.top +slave.y*gridSize+slave.offsetY;
				
				slave.rect.set(slaveLeft,
							   slaveTop,
							   slaveLeft + slave.widthInGrids *slave.gridSize,
							   slaveTop  + slave.heightInGrids*slave.gridSize);
				
				if(dst.setIntersect(rect, slave.rect)){  
					if(slave.bitmap != null){
						/***Calculation to get the source rectangle for source bitmap***/
						src.set(dst);						
						if(rect.left > slave.rect.left){
							src.left  -= slave.rect.left;
							src.right -= slave.rect.left;
						}else{
							src.left  -= dst.left;
							src.right -= dst.left;
						}
						if(rect.top > slave.rect.top){
							src.top    -= slave.rect.top;
							src.bottom -= slave.rect.top;
						}else{
							src.top    -= dst.top;
							src.bottom -= dst.top;
						}
						
						slave.rect.set(dst);
						
						float xRatio = (float)slave.bitmap.getWidth() /(slave.widthInGrids *slave.gridSize),
						      yRatio = (float)slave.bitmap.getHeight()/(slave.heightInGrids*slave.gridSize);
						
						src.left   = (int)(src.left  *xRatio + 0.5);
						src.top    = (int)(src.top   *yRatio + 0.5);
						src.right  = (int)(src.right *xRatio + 0.5);
						src.bottom = (int)(src.bottom*yRatio + 0.5);
	
						canvas.drawBitmap(slave.bitmap, 
										  src,
										  dst,
										  null);	
					}
					
					if(drawGrids && slave.drawGrids){				//Draw grids if drawGrids is true
						if(slave.color == Color.TRANSPARENT)
							slave.color = BitmapSolutions.randomColor();
						slave.drawGrids(canvas,slave.rect.left,slave.rect.top);
					}
					
					slave.draw(drawGrids, src, dst, canvas);
				}
			}
		}
		
		return draw;
	}
	
	/***Recycle the bitmaps.***/
	public void recycle(boolean recycleSourceBitmap){	//Call this method to release memory;
		Grids slave;
		
		if(recycleSourceBitmap && bitmap != null)
			bitmap.recycle();
		if(draw != null)
			draw.recycle();
		
		for(int i = 0;i<slaves.size();i++){
			slave = slaves.get(i);
			if(recycleSourceBitmap && slave.bitmap != null)
				slave.bitmap.recycle();
			if(slave.draw != null)
				slave.draw.recycle();
			slave.recycle(recycleSourceBitmap);
		}
	}
	
	//Setters
	public Grids setBitmap(Bitmap bitmap){	
		this.bitmap = bitmap;	
		
		return this;
	}
	
	public Grids setCoordinate(Point point){
		x = point.x;
		y = point.y;
		
		return this;
	}
	
	public Grids setOffset(int offsetX,int offsetY){	//Coordinate offset of current grids
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
		return this;
	}
	
	public Grids setWidthInGrids(int widthInGrids){
		this.widthInGrids = widthInGrids;
		
		return this;
	}
	
	public Grids setHeightInGrids(int heightInGrids){
		this.heightInGrids = heightInGrids;
		
		return this;
	}
	
	public Grids setGridSize(int gridSize,boolean setSlave){	//Set the same grid size for slave if setSlave is true
		this.gridSize = gridSize;
		
		if(setSlave){
			for(int i=0;i<slaves.size();i++)
				slaves.get(i).setGridSize(gridSize,setSlave);
		}
		
		return this;
	}

	public Grids setGridsColor(int color){	//The color is used in drawGrids() method.
		this.color = color;
		
		return this;
	}
	
	public Grids setVisible(boolean visible){
		this.visible = visible;
		
		return this;
	}
	
	public Grids setDrawGrids(boolean drawGrids){	
		this.drawGrids = drawGrids;
		
		return this;
	}
	
	//Getters
	public Vector<Grids> getSlaves(){
		return slaves;
	}
	
	public Bitmap getBitmap(){
		return bitmap;
	}
	
	public Point  getCoordinate(){
		return new Point(x,y);
	}
	
	public int getWidth(){

		return widthInGrids*gridSize;
	}
	
	public int getWidthInGrids(){

		return widthInGrids;
	}
	
	public int getHeight(){

		return heightInGrids*gridSize;
	}
	
	public int getHeightInGrid(){

		return heightInGrids;
	}
	
	public int getGridSize(){
		return gridSize;
	}
	
	public int getGridsColor(){ //Color of grids.
		return color;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public boolean isDrawGrids(){
		return drawGrids;
	}
}