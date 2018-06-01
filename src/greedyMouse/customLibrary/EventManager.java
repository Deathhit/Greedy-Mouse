package greedyMouse.customLibrary;

import android.util.ArrayMap;

/***A passive Singleton event system that supports multiple channels.  
    Use probeEvent() or consumeEvent() to get notified. 
    Race condition is possible.                                      ***/
public class EventManager extends ChannelManager{
	private static EventManager instance = new EventManager();
	
	private EventManager(){		
		super();
	}
	
	/***Get the instance of EventManager.***/
	public static EventManager getInstance(){			
		return instance;
	}
	
	//Functions
	/***Post a event named eventName to default channel.***/
	public void post(String eventName){
		synchronized(defaultChannel){
			if(defaultChannel.get(eventName) == null)
				defaultChannel.put(eventName, Integer.valueOf(1));
			else
				defaultChannel.put(eventName, (Object)(Integer.valueOf((Integer)(defaultChannel.get(eventName))+1)));
		}
	}
	
	/***Post a event named eventName to target channel.***/
	public void post(String eventName, int channel){
		if(channelMap.get(channel) == null){
			synchronized(channelMap){
				if(channelMap.get(channel) == null){
					channelMap.put(channel, new ArrayMap<String, Object>());
				}
			}
		}

		synchronized(channelMap.get(channel)){
			if(channelMap.get(channel).get(eventName) == null)
				channelMap.get(channel).put(eventName, Integer.valueOf(1));
			else
				channelMap.get(channel).put(eventName, (Object)(Integer.valueOf((Integer)(channelMap.get(channel).get(eventName))+1)));
		}
	}
	
	/***Check if there is at least one event named eventName in default channel.***/
	public boolean probe(String eventName){
		if(defaultChannel.get(eventName) == null)
			return false;
		else if((Integer)defaultChannel.get(eventName) <= 0)
			return false;
		
		return true;
	}
	
	/***Check if there is at least one event named eventName in target channel.***/
	public boolean probe(String eventName,int channel){
		if(channelMap.get(channel) == null)
			return false;
		
		if(channelMap.get(channel).get(eventName) == null)
			return false;
		else if((Integer)channelMap.get(channel).get(eventName) <= 0)
			return false;
		
		return true;
	}
	
	/***Consume a event named eventName in default channel.***/
	public boolean consume(String eventName){
		return consume(eventName, false);
	}
	
	/***Consume events named eventName in default channel if consumeAll is true.***/
	public boolean consume(String eventName, boolean consumeAll){
		synchronized(defaultChannel){
			if(probe(eventName)){
				if(consumeAll)
					defaultChannel.put(eventName, 0);
				else
					defaultChannel.put(eventName, (Object)(Integer.valueOf((Integer)(defaultChannel.get(eventName))-1)));
					
				return true;
			}
		}
		return false;
	}
	
	/***Consume a event named eventName in target channel.***/
	public boolean consume(String eventName, int channel){
		return consume(eventName, channel, false);
	}
	
	/***Consume events named eventName in target channel if consumeAll is true.***/
	public boolean consume(String eventName, int channel, boolean consumeAll){
		synchronized(channelMap){
			if(channelMap.get(channel) == null)
				return false;
		}	
		
		synchronized(channelMap.get(channel)){
			if(probe(eventName, channel)){
				if(consumeAll)
					channelMap.get(channel).put(eventName, 0);
				else
					channelMap.get(channel).put(eventName, (Object)(Integer.valueOf((Integer)(channelMap.get(channel).get(eventName))-1)));
				
				return true;
			}
		}
		
		return false;
	}
}
