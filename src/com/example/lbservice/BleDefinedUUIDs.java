package com.example.lbservice;

import java.util.UUID;

public class BleDefinedUUIDs {
	
	public static class Service {
		final static public UUID LBS_SERVICE              = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
	};
	
	public static class Characteristic {
		final static public UUID BUTTON_CHAR              = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
		final static public UUID LED_CHAR                 = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
		final static public UUID BATTERY_LEVEL            = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
	}
	
	public static class Descriptor {
		final static public UUID CHAR_CLIENT_CONFIG       = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	}
	
}
