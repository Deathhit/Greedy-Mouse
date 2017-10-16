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
