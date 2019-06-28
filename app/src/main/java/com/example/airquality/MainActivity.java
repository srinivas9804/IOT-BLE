package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.UUID;


/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,github.com/srinivas9804>
 *
 *     Application to connect to a Bluetooth Low Energy(BLE) module.
 *     Connects to a microchip RN4870 chip and uses the transparent UART mode to
 *     read data asynchronously.
 *
 *     Note: The UUIDs are hard coded based on the datasheet, so make sure that they are
 *     appropriate for other chips.
 *
 */

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic, readCharacteristic;
    private BluetoothGattDescriptor readDescriptor;

    final UUID READ_WRITE_SERVICE_UUID = UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455");
    final UUID WRITE_CHARACTERISTIC_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    final UUID READ_CHARACTERISTIC_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    final UUID READ_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    Button mConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnect = (Button) findViewById(R.id.connectButton);
        mConnect.setOnClickListener((View view)->{
            mBluetoothGatt = mDevice
                    .connectGatt(MainActivity.this, false, mGattCallback);
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BluetoothLE not supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,0);
        }
        mDevice = mBluetoothAdapter.getRemoteDevice("D8:80:39:F6:15:2F");
        if(mDevice == null){
            Log.w("BLEDevice","Not found");
            finish();
        }
        else{
            Log.w("BLEDevice","found");
        }
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public final String ACTION_GATT_CONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
        public final String ACTION_GATT_DISCONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
        public final String ACTION_GATT_SERVICES_DISCOVERED =
                "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
        public final String ACTION_DATA_AVAILABLE =
                "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
        public final String EXTRA_DATA =
                "com.example.bluetooth.le.EXTRA_DATA";
        private int mConnectionState = STATE_DISCONNECTED;

        private static final int STATE_DISCONNECTED = 0;
        private static final int STATE_CONNECTING = 1;
        private static final int STATE_CONNECTED = 2;

        String TAG = "BGatt";
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "GATT SUCCESS");

                /**
                 * The following block is to log the services, characteristics and descriptors of the BLE server
                 * Use this to find the appropriate UUIDs and update the final vars
                 */
                /*
                List<BluetoothGattService> services = mBluetoothGatt.getServices();
                for(BluetoothGattService service : services){
                    Log.i(TAG, "service " + service.getUuid().toString());
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for(BluetoothGattCharacteristic characteristic : characteristics){
                        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                        for(BluetoothGattDescriptor descriptor: descriptors){
                            Log.i(TAG, "    "+service.getUuid()+","+ characteristic.getUuid()+","+descriptor.getUuid());
                        }
                    }
                }
                */

                BluetoothGattService Service = mBluetoothGatt.getService(READ_WRITE_SERVICE_UUID);
                if (Service == null) {
                    Log.e("BGatt", "service not found!");
                }
                else {
                    writeCharacteristic = Service.getCharacteristic(WRITE_CHARACTERISTIC_UUID);
                    if (writeCharacteristic == null) {
                        Log.e("BGatt", "char not found!");
                    }
                }
                readCharacteristic = Service.getCharacteristic(READ_CHARACTERISTIC_UUID);
                if (readCharacteristic == null) {
                    Log.e("BGatt", "char not found!");
                }
                mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);
                readDescriptor = readCharacteristic.getDescriptor(READ_DESCRIPTOR_UUID);
                readDescriptor.setValue(
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                boolean flag = mBluetoothGatt.writeDescriptor(readDescriptor);
                Log.i("BGatt", "status " + flag);

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this,
//                                "Transparent UART link successful", Toast.LENGTH_SHORT).show();
//                    }
//                });
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                startActivity(intent);

            } else {
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "GATT SUCCESS");
            }
            else{
                Log.i(TAG, "NOT GATT SUCCESS");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Received text = " +  new String(characteristic.getValue()));
            //MainActivity.mReceiveText.setText(new String(characteristic.getValue()));
            if(characteristic.equals(readCharacteristic)) {
                DisplayActivity.update(new String(characteristic.getValue()));
            }
        }
    };
}
