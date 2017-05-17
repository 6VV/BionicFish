package com.lyyjy.yfyb.bionicfish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class DeviceAdapter extends ArrayAdapter<Device> {
    private final int mResourceId;

    @SuppressWarnings("SameParameterValue")
    public DeviceAdapter(Context context, int resource, List<Device> objects) {
        super(context, resource, objects);
        mResourceId=resource;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(int position, View convertView, @SuppressWarnings("NullableProblems") ViewGroup parent) {
        Device device=getItem(position);
        if (device==null){
            //noinspection ConstantConditions
            return null;
        }

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        ImageView deviceIcon = (ImageView) view.findViewById(R.id.ivDeviceIcon);
        TextView deviceName = (TextView) view.findViewById(R.id.tvDeviceName);
        TextView deviceAddress= (TextView) view.findViewById(R.id.tvDeviceAddress);
        deviceIcon.setImageResource(R.mipmap.logo);
        deviceName.setText(device.getName());
        deviceAddress.setText(device.getAddress());
        return view;
    }
}
