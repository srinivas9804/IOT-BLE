/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     Recycler View adapter to display scanned devices in the Scan Activity.
 *     Connects to a device on clicking a list item.
 *
 */
package com.example.airquality;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScanDevicesAdapter extends RecyclerView.Adapter<ScanDevicesAdapter.MyViewHolder> {

    static List<BluetoothDevice> devices;
    static Context context;
    //String array[][];//just for testing


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mDeviceName;
        public TextView mMacAddress;
        CardView cv;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            cv = (CardView) view.findViewById(R.id.card_view);
            mDeviceName = (TextView) view.findViewById(R.id.deviceName);
            mMacAddress = (TextView) view.findViewById(R.id.macAddress);

        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            System.out.println(pos);
            Toast.makeText(context,
                    "Connecting to the device: " + devices.get(pos).getAddress(), Toast.LENGTH_SHORT).show();
            ScanActivity.setDevice(devices.get(pos));
            ScanActivity.connectDevice(pos,context);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScanDevicesAdapter(List<BluetoothDevice> devices, Context context) {
        this.devices = devices;
        ScanDevicesAdapter.context = context;
    }

//    public ScanDevicesAdapter(String data[][]) { // test
//        this.array = data;
//    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScanDevicesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ble_device_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mDeviceName.setText(devices.get(position).getName());
        if(devices.get(position).getName() == null){
            holder.mDeviceName.setText("Null");
        }
        holder.mMacAddress.setText(devices.get(position).getAddress());
//        holder.mMacAddress.setText(array[position][0]);
//        holder.mDeviceName.setText(array[position][1]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        return array.length;//test
        return devices.size();
    }


}