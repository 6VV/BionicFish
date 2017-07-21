package com.lyyjy.yfyb.bionicfish.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Device;
import com.lyyjy.yfyb.bionicfish.DeviceRecyclerViewAdapter;
import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.remote.RemoteFactory;
import com.lyyjy.yfyb.bionicfish.WifiAp;

import java.util.ArrayList;
import java.util.List;

public class SearchWifiActivity extends ParentActivity implements AdapterView.OnItemClickListener {

    private final String TAG="SearchWifiActivity";

//    private ListView mListViewDevices;
    private List<Device> mDevicesInfo=new ArrayList<>();
    private DeviceRecyclerViewAdapter mDeviceArrayAdapter;

    private WifiAp mWifiAp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.device_recycler_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceArrayAdapter=new DeviceRecyclerViewAdapter(mDevicesInfo);
        recyclerView.setAdapter(mDeviceArrayAdapter);

        mWifiAp = new WifiAp(this);
//        mListViewDevices = (ListView) findViewById(R.id.lvDevices);
//        mListViewDevices.setOnItemClickListener(this);

        updateDeviceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
            case R.id.action_refresh:{
                updateDeviceList();
            }break;
        }
        return true;
    }

    private void updateDeviceList() {
        mDevicesInfo.clear();

        ArrayList<String> list=WifiAp.getConnectedHotIP();
        for (int i=1;i<list.size();++i){
            mDevicesInfo.add(new Device("",list.get(i)));
        }

        mDeviceArrayAdapter.notifyDataSetChanged();
//        DeviceAdapter arrayAdapter = new DeviceAdapter(SearchWifiActivity.this, R.layout.adapter_device, mDevicesInfo);
//        mListViewDevices.setAdapter(arrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mWifiAp.isWifiApEnabled()){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
//            builder.setTitle("WIFI热点未打开");
            builder.setTitle("请打开WIFI热点");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void changeWifiApState() {
        boolean enable=mWifiAp.isWifiApEnabled();

        mWifiAp.setWifiApEnabled(!enable);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isExist(position)){
            Toast.makeText(SearchWifiActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
            return;
        }

        final Device device=mDevicesInfo.get(position);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchWifiActivity.this);
        alertDialog.setTitle("是否连接该设备");
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoteFactory.getRemote().connect(device);
//                if (!WifiAp.ping(device.getAddress())){
//                    Toast.makeText(SearchWifiActivity.this,"连接失败，请选择其它设备", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    finish();
//                }
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private boolean isExist(int position){
        if (mDevicesInfo==null || mDevicesInfo.size()<=position){
            return false;
        }
        Device device = mDevicesInfo.get(position);
        if (device == null) {
            return false;
        }

        ArrayList<String> list=WifiAp.getConnectedHotIP();

        return list.contains(device.getAddress());
    }
}
