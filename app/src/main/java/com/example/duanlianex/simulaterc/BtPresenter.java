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
    public void getPairedDevice() {
        engine.getPairedDevice();
    }

    @Override
    public void getConnectedDevice() {
        engine.getConnectedDevice();
    }

    @Override
    public void removeBond(BluetoothDevice device) {
        engine.removeBond(device);
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
    public void refreshPairedDeviceList(List<BluetoothDevice> pairedList) {
        mView.refreshPairedDeviceList(pairedList);
    }

    @Override
    public void refreshUnpairedDeviceList(List<BluetoothDevice> unpairedList) {
        mView.refreshUnpairedDeviceList(unpairedList);
    }

    @Override
    public void refreshConnectedDeviceList(List<BluetoothDevice> connectedList) {
        mView.refreshConnectedDeviceList(connectedList);
    }


}
