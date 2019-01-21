package com.example.duanlianex.simulaterc;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by duanlian.ex on 2019/1/7.
 */

public interface Contract {
    interface View {
        void updateBtState(String state);

        void updateDeviceListVisible(boolean isVisible);

        void updateBtnScanState(String label, boolean isClickable);

        void refreshPairedDeviceList(List<BluetoothDevice> pairedList);

        void refreshUnpairedDeviceList(List<BluetoothDevice> unpairedList);

    }

    interface Realize {
        void openBt();

        void closeBt();

        void startScanDevice();

        void stopScanDevice();

        int getBtState();

        void startPair(BluetoothDevice device);

        void setDeviceDiscoverable();

        void clearListAndScan();

        void unregisterBroadcast();

        void getConnectedDevice();

    }
}
