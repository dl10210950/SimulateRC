package com.example.duanlianex.simulaterc;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.List;

/**
 * Created by duanlian.ex on 2019/1/4.
 */

class BtPresenter implements Contract.Realize, BluetoothEngine.BtStateChangeListener {
    private BluetoothEngine engine;
    private Contract.View mView;

    public BtPresenter(Context context, Contract.View view) {
        engine = BluetoothEngine.getInstance(context);
        this.mView = view;
        engine.setOnBtStateChangeListener(this);
    }

    @Override
    public void openBt() {
        engine.openBt();
    }

    @Override
    public void closeBt() {
        engine.closeBt();
    }

    @Override
    public void startScanDevice() {
        engine.startScanDevice();
    }

    @Override
    public void stopScanDevice() {
        engine.stopScanDevice();
    }

    @Override
    public int getBtState() {
        return engine.getBtState();
    }

    @Override
    public void startPair(BluetoothDevice device) {
        engine.startPairing(device);
    }

    @Override
    public void setDeviceDiscoverable() {
        engine.setDeviceDiscoverable();
    }

    @Override
    public void clearListAndScan() {
        engine.clearListAndScan();
    }

    @Override
    public void unregisterBroadcast() {
        engine.unregisterBroadcast();
    }


    @Override
    public void onBtStateChangeListener(int state) {

    }

    @Override
    public void updateButtonState(String stateLabel) {
        mView.updateBtState(stateLabel);
    }

    @Override
    public void updateDeviceListVisible(boolean isVisible) {
        mView.updateDeviceListVisible(isVisible);
    }

    @Override
    public void updateBtnScanLabelChangeListener(String label, boolean isClickable) {
        mView.updateBtnScanState(label, isClickable);
    }

    @Override
    public void refreshDeviceList(List<BluetoothDevice> list) {
        mView.refreshDeviceList(list);
    }

    @Override
    public void updateBondStateAndConnectionState(String label) {
        mView.updateBondStateAndConnectionState(label);
    }


}
