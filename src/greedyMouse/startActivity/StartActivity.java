package greedyMouse.startActivity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import greedyMouse.customLibrary.BitmapSolutions;
import greedyMouse.customLibrary.CacheManager;
import greedyMouse.customLibrary.Sprite;
import greedyMouse.gameActivity.GameActivity;
import greedyMouse.predefined.Assets;
import greedyMouse.predefined.CustomDatabaseHelper;
import greedyMouse.predefined.Values;
import greedyMouse.predefined.CustomDatabaseContract.Entry;
import test.greedymouse.R;

/***The launch activity of the Android application Greedy Mouse.***/
public class StartActivity extends Activity {
	private static final CacheManager CACHE_MANAGER = CacheManager.getInstance();
	
	private static final String SQL_QUERY_ENTRY = "SELECT * FROM " + Entry.TABLE_NAME +
			    								  " ORDER BY " + Entry.COLUMN_NAME_ENTRY_SCORE + 
			    								  " DESC LIMIT 5";
	
	//UI tasks
	/***Load data before activity is ready.***/
	private final Runnable loadingTask = new Runnable(){		
		private int cacheChannel; //This channel is where cache stores initial data.
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Handler handler = new Handler(StartActivity.this.getMainLooper());
			
			//Get an unregistered channel from cache for initial data.
			cacheChannel = CACHE_MANAGER.registerChannel(
						   		CACHE_MANAGER.getUnregisteredChannel(Values.MIN_CHANNEL, 
						  										     Values.MAX_CHANNEL), 
						   true);
			
			//Initialize the application.
			CustomInitialization.run(StartActivity.this, cacheChannel);
			
			//Prepare the media player.			
			mediaPlayer = MediaPlayer.create(StartActivity.this, R.raw.surface);
			
			mediaPlayer.setLooping(true);

			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Inform UI thread to do the rest after loading.
			handler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startIntent   = new Intent(StartActivity.this, GameActivity.class);
					startIntent.putExtra(Values.CACHE_CHANNEL, cacheChannel);
					
					//Set up ranking dialog.					
					adapter = new SimpleCursorAdapter(StartActivity.this,
							                          R.layout.simple_cursor_adapter,
							                          null,
							                          new String[] {Entry.COLUMN_NAME_ENTRY_NAME,Entry.COLUMN_NAME_ENTRY_SCORE}, 
							                          new int[] {R.id.nameView ,R.id.scoreView},
					                                  0);
					
					rankingDialog = new AlertDialog.Builder(new ContextThemeWrapper(StartActivity.this, R.style.CustomDialogTheme))
							 		    .setIcon(android.R.drawable.ic_dialog_alert)
							            .setTitle("Ranking")
							            .setAdapter(adapter, null);
							 				
					//Set up credit dialog.
					creditDialog  = new AlertDialog.Builder(new ContextThemeWrapper(StartActivity.this, R.style.CustomDialogTheme))
					 		   		.setIcon(android.R.drawable.ic_dialog_alert)
					 		   		.setTitle("Credit")
					 		   		.setMessage("§@ªÌ :\n\t·¨´°¶£\n\tÃC§Ê¦w\n\tÂ²¶W \nGraphics :\n\tWatabou\nMusic :\n\tCube_Code");
					
					//Show start screen, and hide loading screen.
					startLayout    .setVisibility(View.VISIBLE);
					iconLayout     .setVisibility(View.VISIBLE);
					iconView       .setVisibility(View.VISIBLE);
					
					loadingLayout  .setVisibility(View.GONE);
					
					//Play music
					mediaPlayer.start();
				}			
			});
		}	
	};
	
	private View        loadingLayout,
						iconLayout,
						startLayout;
	
	private ImageView   iconView;
	
	private AlertDialog.Builder rankingDialog,
					            creditDialog;
	
	private Intent      startIntent;
	
	private SimpleCursorAdapter adapter;
	
	private MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_start);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		loadingLayout   = findViewById(R.id.loadingLayout);
		iconLayout      = findViewById(R.id.iconLayout);
		startLayout     = findViewById(R.id.startLayout);
		
		iconView        = (ImageView)findViewById(R.id.iconView);
		
		new Thread(loadingTask).start();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if(mediaPlayer != null)
			mediaPlayer.start();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		if(mediaPlayer != null){
			mediaPlayer.pause();
			mediaPlayer.seekTo(0);
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		//Release media player.
		mediaPlayer.release();
		mediaPlayer = null;
	}
	
	//Functions
	/***Start the game activity.***/
	public void start(View v){		
		startActivity(startIntent);
	}
	
	/***Show the ranking dialog.***/
	public void ranking(View v){
		Cursor  cursor  = new CustomDatabaseHelper(this).getReadableDatabase()
	          			  .rawQuery(SQL_QUERY_ENTRY, null);

	    adapter.changeCursor(cursor);
		
		rankingDialog.show();
	}
	
	/***Show the credit dialog.***/
	public void credit(View v){
		creditDialog.show();
	}
	
	/***Custom initialization utility class for the Android application Greedy Mouse.***/
	private static class CustomInitialization{
		private CustomInitialization(){}
		
		/***Run initialization.***/
		static void run(Context context, int cacheChannel){
			if(CACHE_MANAGER.getBoolean(Values.INITIALIZED, false, cacheChannel))	
				return;
			
			getBitmapsAndSprite(context, cacheChannel);
			
			getSounds(context, cacheChannel);
			
			//Create database for later use if it is not created.
			new CustomDatabaseHelper(context).getReadableDatabase();
			
			CACHE_MANAGER.put(Values.INITIALIZED, true, cacheChannel);
		}
		
		//Logical Methods
		/***Get bitmaps by decoding from assets, then use the bitmaps to create sprite.
		 *  Put them in the cache for further use.                                     ***/
		private static void getBitmapsAndSprite(Context context, int cacheChannel){	
			Bitmap ratJump[],
			   	   ratWLeft,
			       ratWRight,
			       ratWJump[],
			       ratWEat[],
			       ratWDie[],
			       food[],
			       wall,
			       floor,
			       fireIcon,
			       iceIcon;

			Sprite ratJumpSprite,
				   ratWJumpSprite,
			   	   ratWEatSprite,
			       ratWDieSprite;
			
			Bitmap sheet,temp[][];
		        
		    sheet = BitmapSolutions.getBitmapFromAsset(context, Assets.RAT_SHEET);
		        
		    temp  = BitmapSolutions.cutSpriteSheet(sheet,  
		        								   new Point(0,0),
		        								   16,
		        								   15,
		        								   2,
		        								   15);
		    
		    ratJump = new Bitmap[5];
		    for(int i=0;i<5;i++)
		    	ratJump[i] = temp[0][i+6];  
		    
		    ratWLeft  = temp[1][1];
		        
		    ratWRight = temp[1][0];
		        
		    ratWJump = new Bitmap[5];
		    for(int i=0;i<5;i++)
		    	ratWJump[i] = temp[1][i+6];
		    	
		    ratWEat  = new Bitmap[4];
		    for(int i=0;i<4;i++)
		    	ratWEat[i] = temp[1][i+2]; 
		    
		    ratWDie  = new Bitmap[4];
		    for(int i=0;i<4;i++)
		        ratWDie[i]  = temp[1][i+11];
		        
		    sheet.recycle();
		        
		    sheet = BitmapSolutions.getBitmapFromAsset(context, Assets.BACKGROUND_SHEET);
		        
		    temp  = BitmapSolutions.cutSpriteSheet(sheet, 
						 						   new Point(64,0), 
						 						   16,
						 						   16,
						 						   2,
						 						   1);

		    wall  = temp[0][0];
		        
		    floor = temp[1][0];
		        
		    sheet.recycle();
		        
		    sheet = BitmapSolutions.getBitmapFromAsset(context, Assets.FOOD_SHEET);
		        
		    temp  = BitmapSolutions.cutSpriteSheet(sheet, 
						  						   new Point(32,224), 
						  						   16,
						  						   16,
						  						   1,
						  						   3);
		        
		    food    = new Bitmap[3];
		        
		    food[0] = temp[0][1];
		    food[1] = temp[0][0];
		    food[2] = temp[0][2];
		        
		    sheet.recycle();
		    
		    sheet    = BitmapSolutions.getBitmapFromAsset(context, Assets.BUFFS_SHEET);
		    
		    temp     = BitmapSolutions.cutSpriteSheet(sheet, 
		    									      new Point(14,0), 
		    									      7, 
		    									      7,
		    									      1, 
		    									      1);
		    
		    fireIcon = temp[0][0];
		    
		    temp     = BitmapSolutions.cutSpriteSheet(sheet, 
		    										  new Point(105,0), 
		    										  7, 
		    										  7, 
		    										  1, 
		    										  1);
		    
		    iceIcon  = temp[0][0];
		    
		    sheet.recycle();
		    
		    ratJumpSprite  = new Sprite(ratJump,10);
	        ratWJumpSprite = new Sprite(ratWJump,10);
	        ratWEatSprite  = new Sprite(ratWEat,8);
	        ratWDieSprite  = new Sprite(ratWDie,8);
	        
	        CACHE_MANAGER.put(Values.RAT_W_LEFT_BITMAP,  ratWLeft,       cacheChannel)
	        			 .put(Values.RAT_W_RIGHT_BITMAP, ratWRight,      cacheChannel)
	        			 .put(Values.FOOD_BITMAPS,       food,           cacheChannel)
	        			 .put(Values.WALL_BITMAP,        wall,           cacheChannel)
	        			 .put(Values.FLOOR_BITMAP,       floor,          cacheChannel)
	        			 .put(Values.FIRE_ICON_BITMAP,   fireIcon,       cacheChannel)
	        			 .put(Values.ICE_ICON_BITMAP,    iceIcon,        cacheChannel)
	        			 .put(Values.RAT_JUMP_SPRITE,    ratJumpSprite,  cacheChannel)
	        			 .put(Values.RAT_W_JUMP_SPRITE,  ratWJumpSprite, cacheChannel)
	        			 .put(Values.RAT_W_EAT_SPRITE,   ratWEatSprite,  cacheChannel)
	        			 .put(Values.RAT_W_DIE_SPRITE,   ratWDieSprite,  cacheChannel);
		}
		
		/***Get sound objects, and put them into the cache.***/
		private static void getSounds(Context context, int cacheChannel){
		    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		    
		    int eatSoundId  = soundPool.load(context, R.raw.snd_eat, 1),
		        dieSoundId  = soundPool.load(context, R.raw.snd_death, 1);
		    
		    CACHE_MANAGER.put(Values.SOUND_POOL,   soundPool,  cacheChannel)
		    		     .put(Values.EAT_SOUND_ID, eatSoundId, cacheChannel)
		    		     .put(Values.DIE_SOUND_ID, dieSoundId, cacheChannel);
		}
	}
}
