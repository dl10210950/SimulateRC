package com.example.duanlianex.simulaterc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.duanlianex.simulaterc.custom.RemovePairPop;

import java.util.ArrayList;
import java.util.List;

import static com.example.duanlianex.simulaterc.R.id.btn_state;

public class MainActivity extends AppCompatActivity implements Contract.View, View.OnClickListener,
        DeviceListRecyclerAdapter.OnUnpairedItemClickListener, DeviceListPairedRecyclerAdapter.OnPairedItemClickListener, OnLongClickListener {


    private Button btnState;
    private Button btnScan;
    private LinearLayout llDeviceList;
    private List<BluetoothDevice> deviceList;
    private RecyclerView rvUnpaired;
    private RecyclerView rvPaired;
    private BtPresenter btPresenter;
    private DeviceListRecyclerAdapter adapterUnpaired;
    private DeviceListPairedRecyclerAdapter adapterPaired;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        btnState = (Button) findViewById(btn_state);
        btnScan = (Button) findViewById(R.id.btn_start_scan);
        rvUnpaired = (RecyclerView) findViewById(R.id.recycler);
        rvPaired = (RecyclerView) findViewById(R.id.recycler_paired);
        llDeviceList = (LinearLayout) findViewById(R.id.ll_device_list);
        btnState.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnScan.setOnLongClickListener(this);
        btPresenter = new BtPresenter(this, this);
        deviceList = new ArrayList<>();
        rvUnpaired.setLayoutManager(new LinearLayoutManager(this));
        rvPaired.setLayoutManager(new LinearLayoutManager(this));
        adapterUnpaired = new DeviceListRecyclerAdapter(this, deviceList);
        rvUnpaired.setAdapter(adapterUnpaired);

        adapterUnpaired.setOnUnpairedItemClickListener(this);
        adapterPaired = new DeviceListPairedRecyclerAdapter(this, deviceList);
        rvPaired.setAdapter(adapterPaired);

        adapterPaired.setOnPairedItemClickListener(this);
        if (btPresenter.getBtState() == 12) {
            startScanDevice();
            btPresenter.getPairedDevice();
            btPresenter.getConnectedDevice();
        }
    }


    private void startScanDevice() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            btPresenter.startScanDevice();
            btPresenter.setDeviceDiscoverable();
        } else {
            todoRequestPermission();
        }
    }

    private void todoRequestPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
        int requestCode = 1;
        ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(MainActivity.this, "权限拒绝", Toast.LENGTH_SHORT).show();

                    } else {
                        btPresenter.startScanDevice();
                    }
                }
                break;

        }
    }


    @Override
    public void updateBtState(String state) {
        btnState.setText(state);
    }

    @Override
    public void updateDeviceListVisible(boolean isVisible) {
        if (isVisible) {
            llDeviceList.setVisibility(View.VISIBLE);
        } else {
            llDeviceList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateBtnScanState(String label, boolean isClickable) {
        btnScan.setText(label);
        btnScan.setClickable(isClickable);
    }

    @Override
    public void refreshPairedDeviceList(List<BluetoothDevice> pairedList) {
        adapterPaired.refreshPairedDeviceList(pairedList);
    }

    @Override
    public void refreshUnpairedDeviceList(List<BluetoothDevice> unpairedList) {
        adapterUnpaired.refreshUnpairedDeviceList(unpairedList);
    }

    @Override
    public void refreshConnectedDeviceList(List<BluetoothDevice> connectedList) {
        adapterPaired.refreshConnectedDeviceList(connectedList);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_state:
                if (btPresenter.getBtState() == BluetoothAdapter.STATE_OFF) {
                    btPresenter.openBt();
                } else if (btPresenter.getBtState() == BluetoothAdapter.STATE_ON) {
                    btPresenter.closeBt();
                }
                break;
            case R.id.btn_start_scan:
                startScanDevice();
                break;
        }
    }

    @Override
    public void onUnpairedItemClickListener(int position, BluetoothDevice device) {
        btPresenter.startPair(device);
    }

    @Override
    public void onPairedItemClickListener(int position, final BluetoothDevice device, View view) {
        final RemovePairPop mPopwindow = new RemovePairPop(MainActivity.this);
        mPopwindow.showAtLocation(view,
                Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopwindow.setOnClickListener(new RemovePairPop.ClickListener() {
            @Override
            public void confirmClickListener() {
                btPresenter.removeBond(device);
                mPopwindow.dismiss();
            }

        });

    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_scan:
                btPresenter.clearListAndScan();
                break;
        }

        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        btPresenter.unregisterBroadcast();
    }


}
