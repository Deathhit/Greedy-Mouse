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
package greedyMouse.gameActivity;

import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import greedyMouse.customLibrary.BitmapSolutions;
import greedyMouse.customLibrary.CacheManager;
import greedyMouse.customLibrary.EventManager;
import greedyMouse.customLibrary.GreedySnake;
import greedyMouse.customLibrary.Grids;
import greedyMouse.customLibrary.PointSolutions;
import greedyMouse.customLibrary.Sprite;
import greedyMouse.customLibrary.SurfaceRenderer;
import greedyMouse.customLibrary.ThreadSolutions;
import greedyMouse.predefined.CustomDatabaseContract.Entry;
import greedyMouse.predefined.CustomDatabaseHelper;
import greedyMouse.predefined.Values;
import test.greedymouse.R;

/***The game activity of the Android application Greedy Mouse.***/
public class GameActivity extends Activity implements OnClickListener,
													  OnLongClickListener{
	private static final EventManager EVENT_MANAGER = EventManager.getInstance();
	
	private static final CacheManager CACHE_MANAGER = CacheManager.getInstance();
	
	private static final int RENDER_CHANNEL    = EVENT_MANAGER.registerChannel(
					 				 			 	EVENT_MANAGER.getUnregisteredChannel(Values.MIN_CHANNEL, Values.MAX_CHANNEL), 
					 				 			 	true),
							 BACKGROUND_WIDTH  = 12,
							 BACKGROUND_HEIGHT = 12,
							 MAP_WIDTH         = 10,
							 MAP_HEIGHT        = 10,
							 START_LENGTH      = 3;
	
	//UI tasks
	/***Load data before activity is ready.***/
	private final Runnable loadingTask = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Handler handler = new Handler(GameActivity.this.getMainLooper());
			
			//Get data from cache.
			foodBitmaps     = (Bitmap[])CACHE_MANAGER.get(Values.FOOD_BITMAPS,       cacheChannel);
			ratWLeftBitmap  = (Bitmap)CACHE_MANAGER  .get(Values.RAT_W_LEFT_BITMAP,  cacheChannel);
			ratWRightBitmap = (Bitmap)CACHE_MANAGER  .get(Values.RAT_W_RIGHT_BITMAP, cacheChannel);
			wallBitmap      = (Bitmap)CACHE_MANAGER  .get(Values.WALL_BITMAP,        cacheChannel);
			floorBitmap     = (Bitmap)CACHE_MANAGER  .get(Values.FLOOR_BITMAP,		 cacheChannel);
			fireIconBitmap  = (Bitmap)CACHE_MANAGER  .get(Values.FIRE_ICON_BITMAP,   cacheChannel);
			iceIconBitmap   = (Bitmap)CACHE_MANAGER  .get(Values.ICE_ICON_BITMAP,    cacheChannel);

			ratJumpSprite   = (Sprite)CACHE_MANAGER.get(Values.RAT_JUMP_SPRITE,      cacheChannel);
	        ratWJumpSprite  = (Sprite)CACHE_MANAGER.get(Values.RAT_W_JUMP_SPRITE,    cacheChannel);
			ratWEatSprite   = (Sprite)CACHE_MANAGER.get(Values.RAT_W_EAT_SPRITE,     cacheChannel);
			ratWDieSprite   = (Sprite)CACHE_MANAGER.get(Values.RAT_W_DIE_SPRITE,     cacheChannel);
			
			soundPool       = (SoundPool)CACHE_MANAGER.get(Values.SOUND_POOL, cacheChannel);
			
			eatSoundId      = CACHE_MANAGER.getInt(Values.EAT_SOUND_ID, -1, cacheChannel);
			dieSoundId      = CACHE_MANAGER.getInt(Values.DIE_SOUND_ID, -1, cacheChannel);
			
	        //Establish render system. 	    
	        map = new Grids(0,0,BACKGROUND_WIDTH,BACKGROUND_HEIGHT)
					.setBitmap(BitmapSolutions.multiply(wallBitmap, 
														Bitmap.createBitmap(wallBitmap.getWidth()*BACKGROUND_WIDTH, 
																	  		wallBitmap.getHeight()*BACKGROUND_HEIGHT, 
																	  		wallBitmap.getConfig())));
	        
	        map	.addSlave(1, 1, MAP_WIDTH, MAP_HEIGHT)
				 	.setBitmap(BitmapSolutions.multiply(floorBitmap,
				 										Bitmap.createBitmap(floorBitmap.getWidth()*MAP_WIDTH,
				 														  	floorBitmap.getHeight()*MAP_HEIGHT,
				 															floorBitmap.getConfig())));

	        food  = map.addSlave(0, 0, 1, 1);

	        floor = map.addSlave(1, 1, MAP_WIDTH, MAP_HEIGHT);

	        fireIcon  = map.addSlave(0, 0, 1, 1).setBitmap(fireIconBitmap);
	        
	        iceIcon   = map.addSlave(1, 0, 1, 1).setBitmap(iceIconBitmap);
	        
	        rats  = floor.getSlaves();      
	        
			renderer = new CustomSurfaceRenderer(gameView);
			
			renderThread = new Thread(renderer);
			
	        //Establish game system.
	        game       = new CustomGreedySnake(GameActivity.this,BACKGROUND_WIDTH,BACKGROUND_HEIGHT,START_LENGTH);
	        gameThread = new Thread(game);
	        
	        snake      = game.getSnake();
	        foodPoint  = game.getFood();
			
	        game.reset();
	        game.setDelay(500);
			
	        //Inform UI thread to do the rest after loading.
			handler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//Set bitmap to views.
					for(int i=0;i<foodView.length;i++)
						foodView[i].setImageBitmap(foodBitmaps[i]);
					
					leftButton .setImageBitmap(ratWLeftBitmap);
					rightButton.setImageBitmap(ratWRightBitmap);
					
					//Set up dialogs.
					startDialog    = new AlertDialog.Builder(new ContextThemeWrapper(GameActivity.this, R.style.CustomDialogTheme))
							 	     .setIcon(android.R.drawable.ic_dialog_alert)
							 	     .setTitle("Get Ready!")
							 	     .setMessage("1. Cheese provides 1 pt. Eating it doesn't give you sdie effect.\n\n"+
							 	    		 	 "2. Grilled Meat provides 2 pts. Eating it makes you faster in 16 turns.\n\n"+
							 	    		 	 "3. Frozen Meat provides 3 pts. Eating it makes you change direction only by long click in 16 turns.\n")
							 	     .setCancelable(false)
							 	     .setNegativeButton("Start", new DialogInterface.OnClickListener() {
							 	    	 @Override
							 	    	 public void onClick(DialogInterface dialog, int which) {
							 	    		 // TODO Auto-generated method stub
							 	    		 startGame();
									
							 	    		 //Update status.
							 	    		 isPaused        = false;
							 	    		 isDialogRunning = false;
							 	    	 }
							 	     });
			
					pauseDialog    = new AlertDialog.Builder(new ContextThemeWrapper(GameActivity.this, R.style.CustomDialogTheme))
				     		 		 .setIcon(android.R.drawable.ic_dialog_alert)
				     		 		 .setTitle("Game Paused!")
				     		 		 .setMessage("Quit current session?")
				     		 		 .setCancelable(false)
				     		 		 .setNegativeButton("Back", new DialogInterface.OnClickListener(){
				     		 			 @Override
				     		 			 public void onClick(DialogInterface dialog, int which) {
				     		 				 // TODO Auto-generated method stub
				     		 				 resumeGame();
					
				     		 				 //Update status.
				     		 				 isPaused        = false;
				     		 				 isDialogRunning = false;
				    		 
				     		 				 dialog.cancel();
				     		 			 }
				     		 		 })
				     		 		 .setPositiveButton("Quit", new DialogInterface.OnClickListener(){	
				     		 			 @Override
				     		 			 public void onClick(DialogInterface dialog, int which) {
				     		 				 // TODO Auto-generated method stub
				     		 				 finish();
				     		 			 }
				     		 		 });
			
					gameOverDialog = new AlertDialog.Builder(new ContextThemeWrapper(GameActivity.this, R.style.CustomDialogTheme))
		    		         			 .setIcon(android.R.drawable.ic_dialog_alert)
		    		         			 .setTitle("Game Over!")
		    		         			 .setCancelable(false)
		    		         			 .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
		    		         				 @Override
		    		         				 public void onClick(DialogInterface dialog, int which) { 
		    		         					 // TODO Auto-generated method stub    		        		 
		    		         					 startDialog.show();
		    		         				 }
		    		         			 })
		    		         			 .setNeutralButton("Record", new DialogInterface.OnClickListener() {
		    		         				 @Override
		    		         				 public void onClick(DialogInterface dialog, int which) {
		    		         					 // TODO Auto-generated method stub
		    		         					 recordDialog.show();
		    		         				 }
		    		         			 })
		    		         			 .setPositiveButton("Quit", new DialogInterface.OnClickListener(){
		    		         				 @Override
		    		         				 public void onClick(DialogInterface dialog, int which) {
		    		         					 // TODO Auto-generated method stub
		    		         					 finish();
		    		         				 }
		    		         			 });
					
					final EditText input = new EditText(GameActivity.this);
					
					input.setTextColor(Color.WHITE);
					
					recordDialog = new AlertDialog.Builder(new ContextThemeWrapper(GameActivity.this, R.style.CustomDialogTheme))
		         			 	   .setIcon(android.R.drawable.ic_dialog_alert)
		         			 	   .setTitle("Enter your name!")
		         			 	   .setCancelable(false)
		         			 	   .setView(input)
	    		         		   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    		         			   @Override
	    		         			   public void onClick(DialogInterface dialog, int which) { 
	    		         				   // TODO Auto-generated method stub  
	    		         				   ((ViewGroup)input.getParent()).removeView(input);
	    		         				   
	    		         				   gameOverDialog.show();
	    		         			   }
	    		         		   })
	    		         		   .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
	    		         				@Override
	    		         				public void onClick(DialogInterface dialog, int which) {
	    		         					// TODO Auto-generated method stub
	    		         					CustomDatabaseHelper helper = new CustomDatabaseHelper(GameActivity.this);
	    		         					
	    		         					ContentValues values = new ContentValues();
	    		         					
	    		         					values.put(Entry.COLUMN_NAME_ENTRY_NAME, input.getText().toString());
	    		         					values.put(Entry.COLUMN_NAME_ENTRY_SCORE, totalScore);
	    		         					
	    		         					helper.getWritableDatabase().insert(Entry.TABLE_NAME, null, values);
	    		         					
	    		         					finish();
	    		         				}
	    		         			});
					
					//Show game screen, and hide loading screen.
					gameView  .setVisibility(View.VISIBLE);	//Surface starts to be created.
					gameLayout.setVisibility(View.VISIBLE);
					
					loadingLayout  .setVisibility(View.GONE);
					
					//Show start dialog.
					startDialog.show();					
				}});
		}	
	};
	
	/***Allow or stop button click from changing snake direction.***/
	private final Runnable enableClickTask = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isClickAvailable)
				isClickAvailable = false;
			else
				isClickAvailable = true;
		}
	};
	
	/***Update score board.***/
	private final Runnable scoreUpdateTask = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(int i=0;i<scoreView.length;i++)
				scoreView[i].setText(String.valueOf(game.getScore(i)));
		}
    };
	
    /***Pause the game, and show pause dialog.***/
    private final Runnable pauseGameTask   = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
	    	stopGame();
	    	
			isPaused = true;
			
			if(isPaused && !isDialogRunning){
				isDialogRunning = true;
				pauseDialog.show();
			}
		}
    };
    
    /***Sum up score, stop game system ,and show game over dialog.***/
	private final Runnable gameOverTask    = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub		
			//Sum up the score.
			totalScore = 0;
			
			for(int i=0;i<scoreView.length;i++)
				totalScore += game.getScore(i)*(i+1);
			
			//Make sure the game thread is terminated.
			while(true){
				try {
					gameThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			
			//Update status.
			isPaused        = true;
			isDialogRunning = true;
			
			gameOverDialog.setMessage("Your final score is " + totalScore + " pts!").show();
			
		}
    };
    
    //Channels
	private int         cacheChannel;
    
	//View System
    private View        loadingLayout,
    					gameLayout;
    
	private TextView[]  scoreView;
	
	private ImageView[] foodView;
	
	private SurfaceView gameView;
	
	private ImageButton leftButton,
						rightButton;
	
	private AlertDialog.Builder startDialog,
							    pauseDialog,
								gameOverDialog,
								recordDialog;
								
	
	private boolean     isPaused,
					    isDialogRunning,
					    isClickAvailable;
	
	//Animation System		
	private Bitmap[] foodBitmaps;
	
	private Bitmap ratWLeftBitmap,
				   ratWRightBitmap,
				   wallBitmap,
				   floorBitmap,
				   fireIconBitmap,
				   iceIconBitmap;
	
	private Sprite ratJumpSprite,
				   ratWJumpSprite,
				   ratWEatSprite,
				   ratWDieSprite,
				   ratWSprite;
	
	private Grids  map,			//Animation layout
	   			   floor,
	   			   food,
	   			   fireIcon,
	   			   iceIcon;

	private Vector<Grids> rats;

	private Point  mapPosition;
	
	private SurfaceRenderer renderer;
	
	private Thread renderThread;
	
	//Game System
	private CustomGreedySnake game;
	
	private Thread      gameThread;
	
	private List<Point> snake;
	
	private Point       foodPoint;
	
	private int         totalScore;
	
	//Sound System
	private SoundPool soundPool;
	
	private int eatSoundId,
				dieSoundId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        //Check if activity was recycled. Return to launching activity if true.
        cacheChannel = getIntent().getIntExtra(Values.CACHE_CHANNEL, -1);
        
        if(!CACHE_MANAGER.getBoolean(Values.INITIALIZED, false, cacheChannel)){
        	finish();
        	
        	return;
        }
        
        setContentView(R.layout.activity_game);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Establish view system.      	
		loadingLayout = findViewById(R.id.loadingLayout);
		gameLayout    = findViewById(R.id.gameLayout);
		
        scoreView    = new TextView[3];
        scoreView[0] = (TextView)findViewById(R.id.scoreView0);
        scoreView[1] = (TextView)findViewById(R.id.scoreView1);
        scoreView[2] = (TextView)findViewById(R.id.scoreView2);
        
        foodView     = new ImageView[3];
        foodView[0]  = (ImageView)findViewById(R.id.foodView0);
        foodView[1]  = (ImageView)findViewById(R.id.foodView1);
        foodView[2]  = (ImageView)findViewById(R.id.foodView2);
               
		gameView     = (SurfaceView)findViewById(R.id.gameView);
		
	    leftButton   = (ImageButton)findViewById(R.id.leftButton);
		rightButton  = (ImageButton)findViewById(R.id.rightButton);

		leftButton .setOnClickListener(this);
		rightButton.setOnClickListener(this);
		
		leftButton .setOnLongClickListener(this);
		rightButton.setOnLongClickListener(this);

		gameView.getHolder().addCallback(new Callback(){
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub		
				//Set grid size.
				map.setGridSize(gameView.getWidth()/BACKGROUND_WIDTH, true);
				
				//Set map position.
				mapPosition = PointSolutions.coordinateByCenter(new Point(gameView.getWidth()/2,gameView.getHeight()/2), 
														         map.getWidth(), 
														         map.getHeight());
				
				//Start renderer.
				renderer.runnable = true;
				renderThread      = ThreadSolutions.safeStart(renderThread, renderer);
					
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub				
				//Stop game and renderer.
				stopGame();
				
				renderer.runnable = false;
				
				while(true){
					try {
						renderThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
			}	
		});	

	    isPaused         = true;
	    isDialogRunning  = true;
	    isClickAvailable = true;

        //Start loading task.
        new Thread(loadingTask).start();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	
		pauseGameTask.run();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	//Clear and unregister local channel.
    	EVENT_MANAGER.clear(RENDER_CHANNEL)
	                 .registerChannel(RENDER_CHANNEL, false);
    }
	
    //Control
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(!isClickAvailable)
			return;
		
		if(v.getId() == leftButton.getId()){
			game.turn(false);		
		}else if(v.getId() == rightButton.getId()){
			game.turn(true);		
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		if(!game.runnable)
			return false;
		
		if(v.getId() == leftButton.getId())
			game.turn(false);		
		else if(v.getId() == rightButton.getId())
			game.turn(true);		
		
		return true;
	}   
	
	@Override
	public void onBackPressed(){
		pauseGameTask.run();
	}
	
	//Utility Method
	/***Reset and launch game system.***/
	private void startGame(){	
		game.reset();
		
		resumeGame();
	}
	
	/***Launch game system without resetting it.***/
	private void resumeGame(){
		game.runnable = true;
		
		gameThread = ThreadSolutions.safeStart(gameThread, game);
	}
	
	/***Stop game system by terminating its thread.***/
	private void stopGame(){	
		game.runnable = false;
		
		while(true){
			try {
				gameThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		}
	}
	
	/***Custom surface renderer system for Greedy Mouse the Android application project.***/
	private class CustomSurfaceRenderer extends SurfaceRenderer{

		public CustomSurfaceRenderer(SurfaceView surfaceView) {
			super(surfaceView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onRender(Canvas canvas) {
			// TODO Auto-generated method stub
			//Update Sprite frame
			long time = System.currentTimeMillis();
			
			ratJumpSprite  .update(time);
			ratWJumpSprite .update(time);
			ratWEatSprite  .update(time);
			ratWDieSprite  .update(time);
			
			//Check if there are more rats on the view than the actual number.
			if(rats.size() > snake.size()){
				for(int i = snake.size();i < rats.size();i++)
					rats.get(i).setVisible(false);
			}
			
			//Check the game map and update sprite positions.
			for(int i=0;i<snake.size();i++){
				int x = snake.get(i).x,
					y = snake.get(i).y;
				
				//Add new rat on the view.
				if(rats.size() < snake.size())
					floor.addSlave(0, 0, 1, 1);

				//Update position for each rat.
				rats.get(i).x = x-1;
				rats.get(i).y = y-1;
				
				//Set each frame.
				rats.get(i).setBitmap(ratJumpSprite.getCurrentFrame());
				
				rats.get(i).setVisible(true);
			}
			
			//White rat reacts to events.
			if(EVENT_MANAGER.consume(Values.GAME_RESET, RENDER_CHANNEL, true)){
				ratJumpSprite  .reset().setPause(true).setRepeat(true);
				ratWJumpSprite .reset().setPause(true).setRepeat(true);
		        ratWEatSprite  .reset().setPause(true);
		        ratWDieSprite  .reset().setPause(true);
		        
		        ratWSprite = ratWJumpSprite;
			}else if(EVENT_MANAGER.consume(Values.GAME_START, RENDER_CHANNEL, true)){
				ratJumpSprite  .setPause(false);
				ratWJumpSprite .setPause(false);
			}else if(EVENT_MANAGER.consume(Values.SNAKE_DIE, RENDER_CHANNEL, true)){
				ratWDieSprite  .setPause(false);
				ratJumpSprite  .setRepeat(false);
				
				ratWSprite = ratWDieSprite;
			}else if(EVENT_MANAGER.consume(Values.SNAKE_EAT, RENDER_CHANNEL, true)){
				ratWEatSprite  .setPause(false);
				
				ratWSprite = ratWEatSprite;
			}else if(EVENT_MANAGER.consume(Values.SNAKE_MOVE, RENDER_CHANNEL, true)){
				ratWEatSprite  .reset().setPause(true);
				ratWDieSprite  .reset().setPause(true);
				
				ratWSprite = ratWJumpSprite;
			}
			
			EVENT_MANAGER.consume(Values.SNAKE_MOVE, RENDER_CHANNEL, true);
			
			//Icon reacts to events
			if(EVENT_MANAGER.probe(Values.GRILLED_MEAT, RENDER_CHANNEL))
				fireIcon.setVisible(true);
			else
				fireIcon.setVisible(false);

			if(EVENT_MANAGER.probe(Values.FROZEN_MEAT, RENDER_CHANNEL))
				iceIcon.setVisible(true);
			else
				iceIcon.setVisible(false);
			
			//Set frame for the first rat.			
			rats.firstElement().setBitmap(ratWSprite.getCurrentFrame());
			
			//Set frame for the food.
			food.setBitmap(foodBitmaps[game.getFoodType()]);
		
			//Update the food position.
			food.x = foodPoint.x;
			food.y = foodPoint.y;
			
			//Draw the frame.
			canvas.drawBitmap(map.draw(true, wallBitmap.getConfig()), mapPosition.x, mapPosition.y, null);
		}
		
	}
	
	/***Custom greedy snake game system for Greedy Mouse the Android application project.***/
	private class CustomGreedySnake extends GreedySnake{
		private static final int EFFECT_TIME  = 16,
								 EFFECT_TYPES = 3,
								 CHEESE       = 0,
								 GRILLED_MEAT = 1,
								 FROZEN_MEAT  = 2;
		
		private final Handler  handler;
		
		private Effect[]  effects;
		
		private int[]     score;
		
		private int       foodType;
		
		private boolean   gameOver;
		
		public CustomGreedySnake(Context context, int mapWidth, int mapHeight, int startLength) {
			super(mapWidth, mapHeight, startLength);
			
			handler = new Handler(context.getMainLooper());
			
			effects = new Effect[EFFECT_TYPES];
			
			for(int i=0;i<effects.length;i++)
				effects[i] = new Effect(0,i);
			
			score   = new int[EFFECT_TYPES];
			
			gameOver  = false;
		}
		
		@Override
		public void run(){
			EVENT_MANAGER.post(Values.GAME_START, RENDER_CHANNEL);
			
			super.run();
			
			if(gameOver)
				handler.post(gameOverTask);
		}
		
		@Override
		public void reset(){
			//Kill remaining effects
			for(Effect effect : effects)
				effect.timeLeft = 0;
			
			updateEffects();
			
			foodType = 0;	//Set before super.reset() because super.reset() calls generateFood()
			
			super.reset();
			
			for(int i=0;i<score.length;i++)
				score[i] = 0;
			
			gameOver = false;
			
			handler.post(scoreUpdateTask);
			
			EVENT_MANAGER.post(Values.GAME_RESET, RENDER_CHANNEL);
		}
		
		@Override
		protected void move(){	
			updateEffects();
			
			super.move();
			
			EVENT_MANAGER.post(Values.SNAKE_MOVE, RENDER_CHANNEL);
		}
		
		@Override
		protected void die(){
			super.die();
			
			gameOver = true;
			
			EVENT_MANAGER.post(Values.SNAKE_DIE, RENDER_CHANNEL);
			
			soundPool.play(dieSoundId, 1f, 1f, 1, 0, 1f);
		}
		
		@Override 
		protected void eat(int lastX,int lastY){
			int temp;
			
			super.eat(lastX, lastY);;
			
			score[foodType]++;

			addEffect();
			
			//1/2 for cheese , 3/10 for grilled meat, and 2/10 for frozen meat.
			if((temp = (int)(Math.random()*11)) <= 5)
				foodType = CHEESE;
			else if(temp <= 8)
				foodType = GRILLED_MEAT;
			else
				foodType = FROZEN_MEAT;
			
			handler.post(scoreUpdateTask);
			
			EVENT_MANAGER.post(Values.SNAKE_EAT, RENDER_CHANNEL);
			
			soundPool.play(eatSoundId, 1f, 1f, 1, 0, 1f);
		}
		
		//Logical Methods
		/***Add or refresh food effect.***/
		private void addEffect(){
			if(!effects[foodType].valid){
				if(foodType == GRILLED_MEAT){
					delay /= 2;
					
					EVENT_MANAGER.post(Values.GRILLED_MEAT, RENDER_CHANNEL);
				}else if(foodType == FROZEN_MEAT){
					handler.post(enableClickTask);
				
					EVENT_MANAGER.post(Values.FROZEN_MEAT, RENDER_CHANNEL);
				}
				effects[foodType].valid = true;
			}
			
			effects[foodType].timeLeft = EFFECT_TIME;
		}
		
		/***Update the status of each food effect.***/
		private void updateEffects(){
			//Remove effect if time is out
			for(Effect effect : effects){			
				effect.timeLeft--;
				
				//Kill effects
				if(effect.valid){
					if(effect.timeLeft <= 0){
						if(effect.type == GRILLED_MEAT){
							delay *= 2;
							
							EVENT_MANAGER.consume(Values.GRILLED_MEAT, RENDER_CHANNEL, true);
						}else if(effect.type == FROZEN_MEAT){
							handler.post(enableClickTask);
						
							EVENT_MANAGER.consume(Values.FROZEN_MEAT, RENDER_CHANNEL, true);
						}
						
						effect.valid = false;
					}
				}
			}
		}
		
		//Getters
		public int getScore(int index){
			return score[index];
		}
		
		public int getFoodType(){
			return foodType;
		}
		
		private class Effect{
			int timeLeft,
				type;
			
			boolean valid;
			
			Effect(int timeLeft, int type){
				this.timeLeft = timeLeft;
				this.type     = type;
				
				valid         = false;
			}
		}
	}
}