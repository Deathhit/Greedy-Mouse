package greedyMouse.customLibrary;

import java.lang.ref.WeakReference;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/***A renderer for surface. Set runnable(true) before start.***/
public abstract class SurfaceRenderer implements Runnable{
	private static final int DEFAULT_FPS = 60;
	
	private WeakReference<SurfaceHolder> holder;
	
	private long     targetFrameTime;
	
	public  boolean  runnable;
	
	public SurfaceRenderer(SurfaceView surfaceView){
		holder = new WeakReference<SurfaceHolder>(surfaceView.getHolder());
		
		targetFrameTime = 1000/DEFAULT_FPS;
		
		runnable  = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Canvas canvas;
		
		long currentTime = System.currentTimeMillis(),
			 newTime;
		
		while(runnable){	
			//Render Rate Control		
			if((newTime = System.currentTimeMillis()) - currentTime < targetFrameTime){
				currentTime = newTime;
				
				try {
					Thread.sleep(targetFrameTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Check and render
			if((canvas = holder.get().lockCanvas()) == null || holder.get() == null)
				continue;
				
			onRender(canvas);	//The abstract method to draw current frame.
					
			holder.get().unlockCanvasAndPost(canvas);
		}
	}
	
	//Setters
	public SurfaceRenderer setFps(int fps){
		targetFrameTime = 1000/fps;
		
		return this;
	}
	
	public SurfaceRenderer setRunnable(boolean runnable){
		this.runnable = runnable;
		
		return this;
	}
	
	//Abstract Methods
	/***Implement your render method here.***/
	protected abstract void onRender(Canvas canvas);
}
