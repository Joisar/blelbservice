package com.example.lbservice;

import java.util.HashMap;

public class BleNamesResolver {
	private static HashMap<String, String> mServices = new HashMap<String, String>();
	private static HashMap<String, String> mCharacteristics = new HashMap<String, String>();
	
	static public String resolveServiceName(final String uuid)
	{
		String result = mServices.get(uuid);
		if (result == null) result = "Unknown Service";
		return result;
	}
	
	static public String resolveCharacteristicName(final String uuid)
	{
		String result = mCharacteristics.get(uuid);
		if (result == null) result = "Unknown Characteristic";
		return result;
	}
	
	static public String resolveUuid(final String uuid) {
		String result = mServices.get(uuid);
		if (result != null) 
			return "Service: " + result;
		
		result = mCharacteristics.get(uuid);
		if (result != null) 
			return "Characteristic: " + result;
		
		result = "Unknown UUID";
		return result;
	}
	
	static public boolean isService(final String uuid) {
		return mServices.containsKey(uuid);
	}

	static public boolean isCharacteristic(final String uuid) {
		return mCharacteristics.containsKey(uuid);
	}	
	
	static {
		mServices.put("00001523-1212-efde-1523-785feabcd123", "LedButtonService");
		mServices.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
		mServices.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");

		mCharacteristics.put("00001524-1212-efde-1523-785feabcd123", "Button Characteristic");
		mCharacteristics.put("00001525-1212-efde-1523-785feabcd123", "LED Characteristic");
		mCharacteristics.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
		mCharacteristics.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
		mCharacteristics.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
	}
}
