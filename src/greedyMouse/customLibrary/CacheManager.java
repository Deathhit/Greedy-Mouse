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

import android.util.ArrayMap;

/***A Singleton cache system that provides caching in different channels. 
    Race condition is possible, and must be considered.                  ***/
public class CacheManager extends ChannelManager{
	private static CacheManager instance = new CacheManager();

	private CacheManager(){
		super();
	}
	
	/***Get the instance of CacheManager.***/
	public static CacheManager getInstance(){
		return instance;
	}
	
	//Functions
	/***Put value to key slot of default channel.***/
	public CacheManager put(String key, Object value){
		synchronized(defaultChannel){
			defaultChannel.put(key, value);
		}
		
		return this;
	}
	
	/***Put value to key slot of target channel.***/
	public CacheManager put(String key, Object value, int channel){	
		if(channelMap.get(channel) == null){
			synchronized(channelMap){
				if(channelMap.get(channel) == null){
					channelMap.put(channel, new ArrayMap<String, Object>());
				}
			}
		}
		
		synchronized(channelMap.get(channel)){
			channelMap.get(channel).put(key, value);
		}
		
		return this;
	}
	
	/***Get the object from the key slot in default channel.***/
	public Object get(String key){
		if(defaultChannel.get(key) == null)
			return null;
		
		return defaultChannel.get(key);
	}
	
	/***Get the object from the key slot in target channel.***/
	public Object get(String key,int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return null;
		
		return channelMap.get(channel).get(key);
	}
	
	/***Get double from the key slot in default channel. Return defaultValue if the value was null.***/
	public double getDouble(String key, double defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Double)defaultChannel.get(key);
	}
	
	/***Get double from the key slot in target channel. Return defaultValue if the value was null.***/
	public double getDouble(String key, double defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Double)channelMap.get(channel).get(key);
	}
	
	/***Get float from the key slot in default channel. Return defaultValue if the value was null.***/
	public float getFloat(String key, float defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Float)defaultChannel.get(key);
	}
	
	/***Get float from the key slot in target channel. Return defaultValue if the value was null.***/
	public float getFloat(String key, float defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Float)channelMap.get(channel).get(key);
	}
	
	/***Get long from the key slot in default channel. Return defaultValue if the value was null.***/
	public long getLong(String key, long defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Long)defaultChannel.get(key);
	}
	
	/***Get long from the key slot in target channel. Return defaultValue if the value was null.***/
	public long getLong(String key, long defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Long)channelMap.get(channel).get(key);
	}
	
	/***Get int from the key slot in default channel. Return defaultValue if the value was null.***/
	public int getInt(String key, int defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Integer)defaultChannel.get(key);
	}
	
	/***Get int from the key slot in target channel. Return defaultValue if the value was null.***/
	public int getInt(String key, int defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Integer)channelMap.get(channel).get(key);
	}
	
	/***Get short from the key slot in default channel. Return defaultValue if the value was null.***/
	public short getShort(String key, short defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Short)defaultChannel.get(key);
	}
	
	/***Get short from the key slot in target channel. Return defaultValue if the value was null.***/
	public short getShort(String key, short defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Short)channelMap.get(channel).get(key);
	}
	
	/***Get byte from the key slot in default channel. Return defaultValue if the value was null.***/
	public byte getByte(String key, byte defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Byte)defaultChannel.get(key);
	}
	
	/***Get byte from the key slot in target channel. Return defaultValue if the value was null.***/
	public byte getByte(String key, byte defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Byte)channelMap.get(channel).get(key);
	}
	
	/***Get char from the key slot in default channel. Return defaultValue if the value was null.***/
	public char getChar(String key, char defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Character)defaultChannel.get(key);
	}
	
	/***Get char from the key slot in target channel. Return defaultValue if the value was null.***/
	public char getChar(String key, char defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Character)channelMap.get(channel).get(key);
	}
	
	/***Get boolean from the key slot in default channel. Return defaultValue if the value was null.***/
	public boolean getBoolean(String key, boolean defaultValue){
		if(defaultChannel.get(key) == null)
			return defaultValue;
					
		return (Boolean)defaultChannel.get(key);
	}
	
	/***Get boolean from the key slot in target channel. Return defaultValue if the value was null.***/
	public boolean getBoolean(String key, boolean defaultValue, int channel){
		if(channelMap.get(channel) == null || channelMap.get(channel).get(key) == null)
			return defaultValue;
					
		return (Boolean)channelMap.get(channel).get(key);
	}
}