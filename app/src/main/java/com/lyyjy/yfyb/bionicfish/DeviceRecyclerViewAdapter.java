package com.lyyjy.yfyb.bionicfish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Activity.SearchActivity;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteFactory;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder>{

    private static final String TAG=DeviceRecyclerViewAdapter.class.getSimpleName();

    private List<Device> mDevices;

    public DeviceRecyclerViewAdapter(List<Device> devices){
        mDevices=devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Device device = mDevices.get(viewHolder.getAdapterPosition());
                if (device == null) {
                    Toast.makeText(parent.getContext(), "未找到该设备", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(parent.getContext());
                alertDialog.setTitle("是否连接该设备");
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoteFactory.getRemote().connect(device);
                    }
                });
                alertDialog.setNegativeButton("取消", null);
                alertDialog.show();
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device=mDevices.get(position);
        holder.updateDevice(device);
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mNameTextView;
        private TextView mAddressTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            mNameTextView= (TextView) mView.findViewById(R.id.tvDeviceName);
            mAddressTextView = (TextView) mView.findViewById(R.id.tvDeviceAddress);
        }

        void updateDevice(Device device){
            mNameTextView.setText(device.getName());
            mAddressTextView.setText(device.getAddress());
        }
    }
}
