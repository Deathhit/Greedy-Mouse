package greedyMouse.customLibrary;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;

/***A greedy snake game system. reset() and setRunnable(true) before start.***/
public class GreedySnake implements Runnable{
	protected static final long DEFAULT_DELAY = 1000;
	
	public static final byte WEST    = 0, 
	     					 NORTH   = 1, 
	     					 EAST    = 2, 
	     					 SOUTH   = 3, 
	     					 BLOCKED = 4,
	     					 FOOD    = 5,
	     					 EMPTY   = 6;
	
	protected final int    mapWidth,
    			           mapHeight,
    			           startLength;
	
	protected LinkedList<Point> snake;

	protected Point delta, //Temporary point for getNext() method
	 				food;
	
	protected byte  map[][]; 				 
	
	protected byte  direction;
	
	protected boolean readyToTurn;
	
	public long     delay;

	public boolean  runnable;
	
	public GreedySnake(int mapWidth, int mapHeight, int startLength){
		this.mapWidth    = mapWidth;
		this.mapHeight   = mapHeight;
		this.startLength = startLength;
		
		snake         = new LinkedList<Point>();

		delta         = new Point();
		food          = new Point();
		
		map           = new byte[mapHeight][mapWidth];

		direction     = EAST;
		
		readyToTurn   = false;
		
		delay         = DEFAULT_DELAY;
		
		runnable      = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(runnable){
			//Update rate control
			try {
				Thread.sleep(delay);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
				
			//Run the game
			move();
		}
	}

	//Functions
	/***Initialize game system for next run.***/
	public void reset(){
		//Initialize direction to EAST.
		direction = EAST;

		//Allow changing direction.
		readyToTurn = true;
		
		//Fill map with EMPTY.
		for(int i=1;i<mapHeight-1;i++){
			for(int j=1;j<mapWidth-1;j++)
				map[i][j] = EMPTY;
		}

		//Fill map boundaries with BLOCKED.
		for(int i=0;i<mapHeight;i++){
			map[i][0]           = BLOCKED;
			map[i][mapWidth-1] = BLOCKED;
		}
		for(int i=1;i<mapWidth-1;i++){
			map[0][i]            = BLOCKED;
			map[mapHeight-1][i] = BLOCKED;
		}

		//Initialize positions.
		int x = mapWidth /2 - 1,
			y = mapHeight/2 - 1;

		snake.clear();

		for(int i=0;i<startLength;i++){
			map[y][x-i] = EAST;
			snake.add(new Point(x-i,y));
		}

		//Generate first food.
		generateFood();
	}
	
	/***Turn direction by 90 degree. Set clockwise true to turn clockwise.***/ 
	public boolean turn(boolean clockwise){
		if(!readyToTurn)
			return false;
			
		if(clockwise){
			if(direction == WEST)
				direction = NORTH;
			else if(direction == NORTH)
				direction = EAST;
			else if(direction == EAST)
				direction = SOUTH;
			else
				direction = WEST;
		}else{
			if(direction == WEST)
				direction = SOUTH;
			else if(direction == NORTH)
				direction = WEST;
			else if(direction == EAST)
				direction = NORTH;
			else
				direction = EAST;
		}
		
		//Disable turn until next move.
		readyToTurn = false;
		
		return true;
	}
	
	//Logical Methods
	/***This method is called when food is generated.***/
	protected void generateFood(){
		int x,y;

		while(true){
			x = (int)(Math.random()*mapWidth);
			y = (int)(Math.random()*mapHeight);
			if(map[y][x] == EMPTY){
				food.set(x, y);
				map[y][x] = FOOD;
				break;
			}
		}
	}
	
	/***This method is called every time snake moves.***/
	protected void move(){
		byte status;

		int nextX = snake.getFirst().x,
			nextY = snake.getFirst().y,
			lastX = snake.getLast().x,
			lastY = snake.getLast().y;

		getNext(direction);

		status = map[nextY+=delta.y][nextX+=delta.x];

		if(status <= BLOCKED && !(nextX == lastX && nextY == lastY)){
			die();
			
			return;
		}else{
			byte next = direction;

			map[nextY][nextX] = next;

			for(Point point : snake){			
				//Update status for each position
				next = map[point.y][point.x];

				//Update node position
				point.x += delta.x;
				point.y += delta.y;

				//Update delta by direction
				getNext(next);
			}

			if(status == FOOD)
				eat(lastX,lastY);
			else if(status == EMPTY)
				map[lastY][lastX] = EMPTY;
		}
		
		//Allow changing direction.
		readyToTurn = true;
	}
	
	/***This method is called when snake dies.***/
	protected void die(){
		runnable = false;
		
		return;
	}
	
	/***This method is called when snake eats.***/
	protected void eat(int lastX,int lastY){
		snake.add(new Point(lastX, lastY));
		generateFood();
	}
	
	//Utility Methods
	private void getNext(byte direction){
		if(direction == EAST){
			delta.x = 1;
			delta.y = 0;
		}else if(direction == WEST){
			delta.x = -1;
			delta.y = 0;
		}else if(direction == SOUTH){
			delta.x = 0;
			delta.y = 1;
		}else if(direction == NORTH){
			delta.x = 0;
			delta.y = -1;
		}
	}
	
	//Getters
	public List<Point> getSnake(){
		return Collections.unmodifiableList(snake);
	}
	
	public Point getFood(){
		return food;
	}
	
	public byte getMapValue(int x, int y){
		return map[y][x];
	}
	
	public byte getDirection(){
		return direction;
	}
	
	public boolean isRunnable(){
		return runnable;
	}
	
	//Setters
	public GreedySnake setDelay(long delay){
		this.delay = delay;
		
		return this;
	}
	
	public GreedySnake setRunnable(boolean runnable){
		this.runnable = runnable;

		return this;
	}
}
