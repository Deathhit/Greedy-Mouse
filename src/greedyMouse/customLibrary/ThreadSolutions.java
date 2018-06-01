package greedyMouse.customLibrary;

/***A storage class of static functions for handling Thread.***/
public class ThreadSolutions {
	private ThreadSolutions(){}
	
	/***Check the thread status before starting it. 
	 * If the thread is running, it won't be started again.***/
	public static Thread safeStart(Thread thread, Runnable runnable){
		if(thread == null || thread.getState() == Thread.State.TERMINATED)
			thread = new Thread(runnable);
		
		if(thread.getState() == Thread.State.NEW)
			thread.start();
		
		return thread;
	}
}
