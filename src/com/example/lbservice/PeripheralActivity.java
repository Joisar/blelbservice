package com.example.lbservice;

import java.util.List;
import java.util.UUID;

import com.example.blelbservice.R;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PeripheralActivity extends Activity implements BleWrapperUiCallbacks {	
	
    private static final String TAG = "LSBervice";
    
    public static final String EXTRAS_DEVICE_NAME    = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI    = "BLE_DEVICE_RSSI";
    
    private String mDeviceName;
    private String mDeviceRSSI;
    private String mDeviceAddress;

    private BleWrapper mBleWrapper;
    
    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceRssiView;
    private TextView mDeviceStatus; 
    private TextView mLedValue;
    private TextView mButtonValue;
    
    BluetoothGattService         mLbService = null;
    BluetoothGattCharacteristic  mLedCharacteristic = null;
    BluetoothGattCharacteristic  mButtonCharacteristic = null;
    
    public void uiDeviceConnected(final BluetoothGatt gatt, 
    		                      final BluetoothDevice device)
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mDeviceStatus.setText("connected");
				invalidateOptionsMenu();
			}
    	});
    }
    
    public void uiDeviceDisconnected(final BluetoothGatt gatt, 
    		                         final BluetoothDevice device)
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mDeviceStatus.setText("disconnected");
				
				invalidateOptionsMenu();
			}
    	});    	
    }
    
    public void uiNewRssiAvailable(final BluetoothGatt gatt, 
    		                       final BluetoothDevice device, 
    		                       final int rssi)
    {
    	runOnUiThread(new Runnable() {
	    	@Override
			public void run() {
				mDeviceRSSI = rssi + " db";
				mDeviceRssiView.setText(mDeviceRSSI);
			}
		});    	
    }
    
    
    public void uiAvailableServices(final BluetoothGatt gatt,
	                                final BluetoothDevice device,
		                            final List<BluetoothGattService> services)
    {
        configureLBService(gatt, device, services);  	
    }    
    
    public void uiNewValueForCharacteristic(final BluetoothGatt gatt,
			                                final BluetoothDevice device,
			                                final BluetoothGattService service,
			                                final BluetoothGattCharacteristic characteristic,
			                                final String strValue,
			                                final int intValue,
			                                final byte[] rawValue,
			                                final String timestamp)
    {	
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {

    	        UUID uuid = characteristic.getUuid();
    	       
    	        if (uuid.equals(BleDefinedUUIDs.Characteristic.LED_CHAR)) {
    	        	mLedValue = (TextView) findViewById(R.id.led_characteristic_value);
    	        	mLedValue.setText(((rawValue[0] & 0x01) == 1) ? "on" : "off");
    	        }
    	        else if (uuid.equals(BleDefinedUUIDs.Characteristic.BUTTON_CHAR)) {
    	        	mButtonValue = (TextView) findViewById(R.id.button_characteristics_value);
    	        	mButtonValue.setText(((rawValue[0] & 0x01) == 1) ? "pressed" : "released");
    	        }   
    		}
    	});
    }
    
    
	@Override
	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
		// no need to handle that in this Activity (here, we are not scanning)
	}  	
	
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peripheral);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		connectViewsVariables();
		
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0) + " db";
        mDeviceNameView.setText(mDeviceName);
        mDeviceAddressView.setText(mDeviceAddress);
        mDeviceRssiView.setText(mDeviceRSSI);
        getActionBar().setTitle(mDeviceName);
        
        addListenerOnToggleButton();
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mBleWrapper == null) mBleWrapper = new BleWrapper(this, this);
		
		if (mBleWrapper.initialize() == false) {
			finish();
		}

		// start automatically connecting to the device
    	mDeviceStatus.setText("connecting ...");
    	mBleWrapper.connect(mDeviceAddress);
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mBleWrapper.stopMonitoringRssiValue();
		mBleWrapper.disconnect();
		mBleWrapper.close();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.peripheral, menu);
		if (mBleWrapper.isConnected()) {
	        menu.findItem(R.id.device_connect).setVisible(false);
	        menu.findItem(R.id.device_disconnect).setVisible(true);
	    } else {
	        menu.findItem(R.id.device_connect).setVisible(true);
	        menu.findItem(R.id.device_disconnect).setVisible(false);
	    }		
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.device_connect:
            	mDeviceStatus.setText("connecting ...");
            	mBleWrapper.connect(mDeviceAddress);
                return true;
            case R.id.device_disconnect:
            	mBleWrapper.disconnect();
                return true;
            case android.R.id.home:
            	mBleWrapper.disconnect();
            	mBleWrapper.close();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }	
    
    private void connectViewsVariables() {
    	mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
		mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
		mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
		mDeviceStatus = (TextView) findViewById(R.id.peripheral_status);
    }   
    
    private void addListenerOnToggleButton() {
    	final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.led_characteristic_toggle);
    	
    	toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				String value = toggleButton.getText().toString();
				byte[] data;
				if (value.equals("turn LED on".toString())) // FIXME
					data = new byte[] {0x01};
				else 
					data = new byte[] {0x00};
				
				mBleWrapper.writeDataToCharacteristic(mLedCharacteristic, data);
			}
		} );    	
      }    

    private void configureLBService(final BluetoothGatt gatt,
                                    final BluetoothDevice device,
                                    final List<BluetoothGattService> services) 
    {
    	Log.d(TAG, "configureLBService");
    	
    	mLbService = null;
    	mLedCharacteristic = null;
    	mButtonCharacteristic = null;
    	
    	/* Find LBS Service in Service list for LBService device. */
		for (BluetoothGattService service : mBleWrapper.getCachedServices()) {
			
			String uuid = BleNamesResolver.resolveServiceName(service.getUuid().toString());
			
			if (uuid.equals("LedButtonService") == false) 
				continue;
			
	    	Log.d(TAG, "found LedButtonService");
	    	mLbService = service;
	    	
	    	break;
		}	
		
		/* If LBService was found, then find the interesting characteristics */
		if (mLbService != null) {
			
			for (BluetoothGattCharacteristic characteristic : mLbService.getCharacteristics()) {
				
				String uuid = BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString());
				
				if (uuid.equals("LED Characteristic")) {
			    	Log.d(TAG, "found LED characteristic");
			    	mLedCharacteristic = characteristic;
				}
				
				if (uuid.equals("Button Characteristic")) {
			    	Log.d(TAG, "found Button characteristic");
			    	mButtonCharacteristic = characteristic;
				}
			}				
		}
		
		/* If all interesting characteristics identified, then setup access to them. */
		if ((mLedCharacteristic != null) && (mButtonCharacteristic != null)) {
				 	
			/* Enable notification for Button characteristics */
			mBleWrapper.setNotificationForCharacteristic(mButtonCharacteristic, true);

		}
    }
    
}
