package com.lyyjy.yfyb.bionicfish.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Device;
import com.lyyjy.yfyb.bionicfish.DeviceAdapter;
import com.lyyjy.yfyb.bionicfish.Remote.IRemoteCallback;
import com.lyyjy.yfyb.bionicfish.Remote.IRemoteScan;
import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteFactory;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteParent;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ParentActivity implements IRemoteScan,IRemoteCallback, AdapterView.OnItemClickListener {
    private MenuItem mMenuItemLoading=null;
    private MenuItem mMenuItemScanning=null;

    private BroadcastReceiver mBroadcastReceiver=null;

    private ListView mListViewDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListViewDevices=((ListView)findViewById(R.id.lvDevices));
        mListViewDevices.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mMenuItemLoading = menu.findItem(R.id.action_loading);
        mMenuItemScanning = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
            case R.id.action_search:{
                tryToScan(!RemoteFactory.getRemote().isScanning());
            }break;
            case R.id.action_refresh:{
                tryToScan(false);
                tryToScan(true);
            }break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateProgressBarItem();
        updateScanItem();
        return true;
    }

    private final int REQUEST_FINE_LOCATION=1;

    private void tryToScan(boolean enable){
        //清空搜索记录
        if(enable){
            mDevicesInfo=new ArrayList<>();
//            mDevicesInfo.clear();
            updateDeviceList();
        }
        RemoteFactory.getRemote().tryScanDevice(enable,this,this,REQUEST_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                // 若申请被取消，则数组为空
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan(true);
                }
                break;
        }
    }


    private void updateProgressBarItem(){
        if (mMenuItemLoading!=null){
            if (RemoteFactory.getRemote().isScanning()) {
                mMenuItemLoading.setActionView(R.layout.actionbar_search_loading);
                mMenuItemLoading.setVisible(true);
            }
            else {
                mMenuItemLoading.setVisible(false);
                mMenuItemLoading.setActionView(null);
            }
        }
    }
    private void updateScanItem(){
        if (mMenuItemScanning!=null){
            if (RemoteFactory.getRemote().isScanning()) {
                mMenuItemScanning.setTitle("停止");
            }else {
                mMenuItemScanning.setTitle("搜索");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RemoteFactory.getRemote().registerRemoteCallback(this);
        mBroadcastReceiver=RemoteFactory.getRemote().registerEnableReceiver(this,this);

        if (!RemoteFactory.getRemote().isEnabled()){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("蓝牙未开启");
            builder.setMessage("是否打开蓝牙");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RemoteFactory.getRemote().changeEnabled();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }else{
            tryToScan(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RemoteFactory.getRemote().unregisterRemoteCallback(this);
        if (mBroadcastReceiver!=null){
            RemoteFactory.getRemote().unregisterEnableReceiver(this,mBroadcastReceiver);
        }
        if(RemoteFactory.getRemote().isEnabled()){
            tryToScan(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private List<Device> mDevicesInfo=new ArrayList<>();

    @Override
    public void scan(boolean enable) {
        RemoteFactory.getRemote().scanDevice(enable,this);
        updateProgressBarItem();
        updateScanItem();
    }

    @Override
    public void onScan(final Device device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String strName = device.getName();
                byte[] byteName = strName.getBytes();

                if(byteName.length<2){
                    return;
                }
                //若前两个字节正确
                if (byteName[0] == 0x01 && byteName[1] == 0x02) {
                    String strNewName = strName.substring(2);
                    Device newDevice = new Device(strNewName, device.getAddress());
                    mDevicesInfo.add(newDevice);
                    updateDeviceList();
                }
            }
        });
}

    private void updateDeviceList() {
//        DeviceAdapter arrayAdapter = new DeviceAdapter(SearchActivity.this, R.layout.adapter_device, mDevicesInfo);
//        mListViewDevices.setAdapter(arrayAdapter);

        mHandler.sendEmptyMessage(0);
    }

    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DeviceAdapter arrayAdapter = new DeviceAdapter(SearchActivity.this, R.layout.adapter_device, mDevicesInfo);
            mListViewDevices.setAdapter(arrayAdapter);
        }
    };

    @Override
    public void onEnableChanged() {
        if (RemoteFactory.getRemote().isEnabled()){
            tryToScan(true);
        }
    }

    @Override
    public void onConnectChanged() {
        if (RemoteFactory.getRemote().getConnectState()== RemoteParent.ConnectState.CONNECTED){
            finish();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDevicesInfo==null || mDevicesInfo.size()<=position){
            Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
            return;
        }
        final Device device = mDevicesInfo.get(position);
        if (device == null) {
            Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
        alertDialog.setTitle("是否连接该设备");
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoteFactory.getRemote().connect(device);
//                mHandler.postDelayed(runnableConnectTimeout, CONNECT_TIMEOUT);
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
    }
}
