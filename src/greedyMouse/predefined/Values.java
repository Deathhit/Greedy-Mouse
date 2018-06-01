package greedyMouse.predefined;

/***A storage class of predefined values for the Android application Greedy Mouse.***/
public class Values {
	private Values(){}
	
	//Channel Range
	public static final int
		MIN_CHANNEL = 0,
		MAX_CHANNEL = 1073741824;	//2^30
	
	//Intent Extra Keys
	public static final String
		//int
		CACHE_CHANNEL      = "cacheChannel"; //Key to the channel value.
	
	//Cache Keys
	public static final String 
		//boolean
		INITIALIZED        = "initialized",
	
		//Bitmap[]
		FOOD_BITMAPS       = "foodBitmaps",
		
		//Bitmap
		RAT_W_LEFT_BITMAP  = "ratWLeftBitmap",
		RAT_W_RIGHT_BITMAP = "ratWRightBitmap",
		WALL_BITMAP        = "wallBitmap",
		FLOOR_BITMAP       = "floorBitmap",
		FIRE_ICON_BITMAP   = "fireIconBitmap",
		ICE_ICON_BITMAP    = "iceIconBitmap",
		
		//Sprite
		RAT_JUMP_SPRITE    = "ratJumpSprite",
		RAT_W_JUMP_SPRITE  = "ratWJumpSprite",
		RAT_W_EAT_SPRITE   = "ratWEatSprite",
		RAT_W_DIE_SPRITE   = "ratWDieSprite",
	
		//SoundPool
		SOUND_POOL         = "soundPool",
		
		//int
		EAT_SOUND_ID       = "eatSoundId",
		DIE_SOUND_ID       = "dieSoundId";
	
	//Events
	public static final String
		//Game activity
		GAME_START   = "start",
		GAME_RESET   = "reset",
		SNAKE_MOVE   = "move",
		SNAKE_DIE    = "die",
		SNAKE_EAT    = "eat",
		GRILLED_MEAT = "grilledMeat",
		FROZEN_MEAT  = "frozenMeat";
		
}
