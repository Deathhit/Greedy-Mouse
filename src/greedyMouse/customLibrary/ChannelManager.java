package greedyMouse.customLibrary;

import java.util.LinkedHashSet;

import android.util.ArrayMap;

/***A data-sharing framework. 
    Extend it and implement your own data access methods to use it.***/
public class ChannelManager{	
	protected ArrayMap<Integer, ArrayMap<String, Object>> channelMap;
	
	protected ArrayMap<String, Object> defaultChannel;
	
	protected LinkedHashSet<Integer> registeredChannels;
	
	protected ChannelManager(){		
		channelMap     = new ArrayMap<Integer, ArrayMap<String, Object>>();
		
		defaultChannel = new ArrayMap<String, Object>();
		
		registeredChannels = new LinkedHashSet<Integer>();
	}
	
	//Functions
	/***Clear all the elements in default channel.
	 	Does not clear default channel itself.    ***/
	public ChannelManager clear(){
		synchronized(defaultChannel){
			defaultChannel.clear();
		}
		
		return this;
	}
	
	/***Clear all the elements in target channel.
		Does not clear the channel itself.       ***/
	public ChannelManager clear(int channel){
		if(channelMap.get(channel) == null){
			synchronized(channelMap){
				if(channelMap.get(channel) == null)
					return this;
			}
		}
		
		synchronized(channelMap.get(channel)){
			channelMap.get(channel).clear();
		}
		
		return this;
	}
	
	/***Clear all the elements in all channels. 
	    Does not clear the channels themselves.***/
	public ChannelManager clearAllChannels(boolean clearDefaultChannel){
		if(clearDefaultChannel){
			synchronized(defaultChannel){
				defaultChannel.clear();
			}
		}
		
		synchronized(channelMap){
			for(int channel : channelMap.keySet()){
				synchronized(channelMap.get(channel)){
					channelMap.get(channel).clear();
				}
			}
		}
		
		return this;
	}
	
	/***Register or unregister channel in the static field of ChannelManager, and return the channel.***/
	public int registerChannel(int channel, boolean register){
		synchronized(registeredChannels){
			if(register)
				registeredChannels.add(channel);
			else
				registeredChannels.remove(channel);
		}
		
		return channel;
	}
	
	/***Get a random unregistered channel within the range.***/
	public int getUnregisteredChannel(int min, int max){
		int channel;
		
		synchronized(registeredChannels){
			do{
				channel = (int)(Math.random()*(max-min+1)+min);
			}while(registeredChannels.contains(channel));
		}
		
		return channel;
	}
}
