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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;

/***A storage class of static functions for handling Bitmaps.***/
public class BitmapSolutions{
	private BitmapSolutions(){}
	
	/***Create separated bitmaps by cutting the sheet.***/
	public static Bitmap[][] cutSpriteSheet(Bitmap sheet,Point start,int spriteWidth,int spriteHeight,int row,int column){
		Bitmap[][] sprites      = new Bitmap[row][column];
		Canvas     canvas       = new Canvas();
		
		for(int i = 0;i<row;i++){
			for(int j=0;j<column;j++){
				sprites[i][j] = Bitmap.createBitmap(spriteWidth,spriteHeight,sheet.getConfig());
				canvas.setBitmap(sprites[i][j]);
				canvas.drawBitmap(sheet, 
								  new Rect(start.x+j*spriteWidth,
										   start.y+i*spriteHeight,
										   start.x+j*spriteWidth+spriteWidth,
										   start.y+i*spriteHeight+spriteHeight), 
								  new Rect(0,
										   0,
										   spriteWidth,
										   spriteHeight), 
								  null);
			}
		}
		
		return sprites;
	}
	
	/***Create bitmap by decoding asset.***/
	public static Bitmap getBitmapFromAsset(Context context, String filePath) {
	    AssetManager assetManager = context.getAssets();

	    InputStream inputStream;
	    Bitmap bitmap = null;
	    
	    try {
	    	inputStream = assetManager.open(filePath);
	    	
	        bitmap = BitmapFactory.decodeStream(inputStream);
	    } catch (IOException e) {
	        return null;
	    }

	    return bitmap;
	}
	
	/***Create bitmap by decoding drawable resource.***/
	public static Bitmap getBitmapFromDrawable(Context context, int id, Options options){	
		return BitmapFactory.decodeResource(context.getResources(), id, options); 
	}
	
	/***Create bitmap by decoding drawable resource.***/
	public static Bitmap getBitmapFromDrawable(Context context, int id){
		BitmapFactory.Options options = new Options();
		
        options.inScaled = false;
		
		return getBitmapFromDrawable(context,id,options); 
	}
	
	/***Create bitmap by flipping source bitmap. Set horizontal true to flip horizontally, and set it false to flip vertically.***/
	public static Bitmap getFlippedBitmap(Bitmap src, boolean horizontal){
		Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		
		Canvas canvas = new Canvas(bitmap);
		
		Matrix matrix = new Matrix();

		if(horizontal){
			matrix.setScale(-1,1);
			matrix.postTranslate(src.getWidth(), 0);
		}else{
			matrix.setScale(1,-1);
			matrix.postTranslate(0, src.getHeight());
		}
		
		canvas.drawBitmap(src, matrix, null);
		
		return bitmap;
	}
	
	/***Multiply source bitmap to fill target bitmap.***/
	public static Bitmap multiply(Bitmap src, Bitmap dst){	//Repeatedly draw dst to src until dst is completely drawn.
		Canvas c = new Canvas(dst);
		int srcW = src.getWidth(),
			srcH = src.getHeight();
		
		for(int i=0;i<dst.getHeight();i+=srcH){
			for(int j=0;j<dst.getWidth();j+=srcW)
				c.drawBitmap(src, j, i, null);
		}
		return dst;
	}
	
	/***Create bitmap by multiplying source bitmap.***/
	public static Bitmap multiply(Bitmap src, int dstWidth, int dstHeight){
		return multiply(src, Bitmap.createBitmap(dstWidth, dstHeight, src.getConfig()));
	}
	
	public static int randomColor(){
		return Color.rgb((int)(Math.random()*256), 
						 (int)(Math.random()*256), 
						 (int)(Math.random()*256));
	}
}