package com.lyyjy.yfyb.bionicfish.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Device;
import com.lyyjy.yfyb.bionicfish.DeviceRecyclerViewAdapter;
import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.remote.IRemoteCallback;
import com.lyyjy.yfyb.bionicfish.remote.IRemoteScan;
import com.lyyjy.yfyb.bionicfish.remote.RemoteFactory;
import com.lyyjy.yfyb.bionicfish.remote.RemoteParent;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ParentActivity implements IRemoteScan, IRemoteCallback {

    private static final String TAG=SearchActivity.class.getSimpleName();

    private MenuItem mMenuItemLoading = null;
    private MenuItem mMenuItemScanning = null;

    private BroadcastReceiver mBroadcastReceiver = null;

    private final List<Device> mDevicesInfo = new ArrayList<>();
    private DeviceRecyclerViewAdapter mDeviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.device_recycler_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceArrayAdapter=new DeviceRecyclerViewAdapter(mDevicesInfo);
        recyclerView.setAdapter(mDeviceArrayAdapter);

//        ListView listViewDevices = ((ListView) findViewById(R.id.lvDevices));
//        listViewDevices.setOnItemClickListener(this);
//
//        mDeviceArrayAdapter = new DeviceAdapter(SearchActivity.this, R.layout.adapter_device, mDevicesInfo);
//        listViewDevices.setAdapter(mDeviceArrayAdapter);
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
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.action_search: {
                tryToScan(!RemoteFactory.getRemote().isScanning());
            }
            break;
            case R.id.action_refresh: {
                tryToScan(false);
                tryToScan(true);
            }
            break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateOptionItem();
        return true;
    }

    private void updateOptionItem() {
        updateProgressBarItem();
        updateScanItem();
    }

    private final int REQUEST_FINE_LOCATION = 1;
    private boolean mIsToScan=false;

    private void tryToScan(boolean enable) {
        //清空搜索记录
        if (enable) {
//            mDevicesInfo = new ArrayList<>();
            int length=mDevicesInfo.size();
            mDevicesInfo.clear();
            mDeviceArrayAdapter.notifyItemRangeRemoved(0,length);
        }

        if (!RemoteFactory.getRemote().isEnabled()) {
            return;
        }

        mIsToScan=enable;

        //若为开始搜索且sdk>=23
        if (enable && Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //如果需要向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "Android6.0以上版本需要开启定位才能搜索蓝牙设备", Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
                return;
            }
        }

        RemoteFactory.getRemote().tryScanDevice(enable, this);
        updateOptionItem();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                // 若申请被取消，则数组为空
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RemoteFactory.getRemote().tryScanDevice(mIsToScan, this);
                    updateOptionItem();
                }
                break;
        }
    }


    private void updateProgressBarItem() {
        if (mMenuItemLoading != null) {
            if (RemoteFactory.getRemote().isScanning()) {
                mMenuItemLoading.setActionView(R.layout.actionbar_search_loading);
                mMenuItemLoading.setVisible(true);
            } else {
                mMenuItemLoading.setVisible(false);
                mMenuItemLoading.setActionView(null);
            }
        }
    }

    private void updateScanItem() {
        if (mMenuItemScanning != null) {
            if (RemoteFactory.getRemote().isScanning()) {
                mMenuItemScanning.setTitle("停止");
            } else {
                mMenuItemScanning.setTitle("搜索");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        RemoteFactory.getRemote().registerRemoteCallback(this);
        mBroadcastReceiver = RemoteFactory.getRemote().registerEnableReceiver(this, this);

        if (!RemoteFactory.getRemote().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        } else {
            tryToScan(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RemoteFactory.getRemote().unregisterRemoteCallback();
        if (mBroadcastReceiver != null) {
            RemoteFactory.getRemote().unregisterEnableReceiver(this, mBroadcastReceiver);
        }
        if (RemoteFactory.getRemote().isEnabled()) {
            tryToScan(false);
        }
    }

    @Override
    public void onScan(final Device device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (device==null){
                    return;
                }
                String strName = device.getName();

                if (strName == null || strName.length() == 0) {
                    return;
                }

                byte[] byteName = strName.getBytes();

                if (byteName.length < 2) {
                    return;
                }
                //若前两个字节正确
                if (byteName[0] == 0x01 && byteName[1] == 0x02) {
                    String strNewName = strName.substring(2);
                    Device newDevice = new Device(strNewName, device.getAddress());
                    mDevicesInfo.add(newDevice);
                    mDeviceArrayAdapter.notifyItemInserted(mDevicesInfo.size()-1);
                }
            }
        });
    }

    @Override
    public void onEnableChanged() {
        if (RemoteFactory.getRemote().isEnabled()) {
            tryToScan(true);
        }
    }

    @Override
    public void onConnectChanged() {
        if (RemoteFactory.getRemote().getConnectState() == RemoteParent.ConnectState.CONNECTED) {
            finish();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {

    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        //noinspection ConstantConditions
//        if (mDevicesInfo == null || mDevicesInfo.size() <= position) {
//            Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        final Device device = mDevicesInfo.get(position);
//        if (device == null) {
//            Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
//        alertDialog.setTitle("是否连接该设备");
//        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                RemoteFactory.getRemote().connect(device);
//            }
//        });
//        alertDialog.setNegativeButton("取消", null);
//        alertDialog.show();
//    }
}
