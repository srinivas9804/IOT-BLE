/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     This Activity scans for nearby BLE devices and displays them in a Recycler View.
 *     It also connects to a particular Bluetooth device after clicking on that item
 *
 */
package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic, readCharacteristic;
    private BluetoothGattDescriptor readDescriptor;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private List<BluetoothDevice> devices;
    private static final long SCAN_PERIOD = 2000;

    final UUID READ_WRITE_SERVICE_UUID = UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455");
    final UUID WRITE_CHARACTERISTIC_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    final UUID READ_CHARACTERISTIC_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    final UUID READ_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView uclBanner, airQualityLogo;
    private Button mRefresh;

    private static AirDataViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        uclBanner = (ImageView) findViewById(R.id.uclBanner);
        airQualityLogo = (ImageView) findViewById(R.id.airTrackerLogo);
        mHandler = new Handler();
        devices = new ArrayList<>();
        mRefresh = (Button) findViewById(R.id.refreshButton);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // Dynamically setting height and width of images
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        uclBanner.getLayoutParams().width = (int)(0.6*width) ;
        airQualityLogo.getLayoutParams().width = (int)(0.3*width);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }


        mBluetoothAdapter = bluetoothManager.getAdapter();

        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        scanLeDevice(true);


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ScanDevicesAdapter(devices,this);
        recyclerView.setAdapter(mAdapter);

        mRefresh.setOnClickListener((View view) -> {
            devices.clear();
            scanLeDevice(true);
        });

        ScanActivity.mViewModel = ViewModelProviders.of(this).get(AirDataViewModel.class);
    }
    public static void setDevice(BluetoothDevice mDevice){
        ScanActivity.mDevice = mDevice;
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(()->{
                mLEScanner.stopScan(mScanCallback);
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);
        } else {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("shaataAct callbackType", String.valueOf(callbackType));
            Log.i("shaataAct result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            Log.i("shaataAct device", btDevice.getName() + " " + btDevice.getAddress());
            boolean flag = true;
            for(BluetoothDevice bt : devices){
                if(bt.getAddress().equals(btDevice.getAddress())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                devices.add(btDevice);
                mAdapter.notifyDataSetChanged();
                Log.i("shaataAct devices", devices.toString());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("shaataAct - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("shaataAct Scan Failed", "Error Code: " + errorCode);
        }
    };

    static void connectDevice(int position, Context context){
        ScanActivity obj = (ScanActivity)context;
        obj.mBluetoothGatt = obj.devices.get(position).connectGatt(context,false,obj.mGattCallback);
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
                DisplayActivity.endActivity();
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
                Intent intent = new Intent(ScanActivity.this, DisplayActivity.class);
                intent.putExtra("MAC_Address", ScanActivity.mDevice.getAddress());
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
                String arr[] = new String(characteristic.getValue()).split(",");
                if(arr.length == 9){
                    AirData item = new AirData(ScanActivity.mDevice.getAddress(),System.currentTimeMillis(),Double.parseDouble(arr[0]),Double.parseDouble(arr[1]),
                            Double.parseDouble(arr[2]),Double.parseDouble(arr[3]),Double.parseDouble(arr[4]),Double.parseDouble(arr[5]),Double.parseDouble(arr[6]),
                            Double.parseDouble(arr[7]),Double.parseDouble(arr[8]));
                    ScanActivity.mViewModel.insert(item);
                }
            }

        }
    };
}
