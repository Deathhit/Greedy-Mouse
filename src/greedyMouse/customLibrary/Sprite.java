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

import android.graphics.Bitmap;

/***Sprite is a class specified for sprite animation. 
    Use update() function to get the current frame for current time.
    It is suggested to build with immutable collection of bitmaps.  ***/
public class Sprite{	
	protected Bitmap[] frames;
	
	protected long     framePeriod,
	 				   frameTicker = (long).01,
	 				   time;
	
	protected int      currentIndex;
	
	public  boolean  repeat,
					 pause;
	
	public Sprite(Bitmap[] frames, int fps){
		this.frames  = frames;
		
		framePeriod  = 1000/fps;
		time         = 0;
		
		currentIndex = 0;
		
		repeat       = false;
		pause        = false;
	}
	
	//Functions
	/***Get current frame by current index.***/
	public Bitmap getCurrentFrame(){
		return frames[currentIndex];
	}
	
	/***Check input time ,and move to the next frame if needed.***/
	public void update(long time){
		if(time - this.time > frameTicker + framePeriod){
			this.time = time;
			
			if(pause)
				return;
			
			currentIndex++;
			
			if(currentIndex >= frames.length)
				if(repeat)
					currentIndex = 0;
				else
					currentIndex--;
		}
	}
	
	/***Check system time ,and move to the next frame if needed.***/
	public void update(){
		this.update(System.currentTimeMillis());
	}
	
	/***Reset current time and current index.***/
	public Sprite reset(){
	    time         = 0;
		currentIndex = 0;
	    
	    return this;
	}
	
	//Setters
	public Sprite setFps(int fps){
		framePeriod  = 1000/fps;
		
		return this;
	}
	
	public Sprite setRepeat(boolean repeat){
		this.repeat = repeat;
		
		return this;
	}
	
	public Sprite setPause(boolean pause){
		this.pause = pause;
		
		return this;
	}
}
