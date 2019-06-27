package com.example.duanlianex.simulaterc;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by duanlian.ex on 2019/1/7.
 */

public class BluetoothEngine {
    public static final String TAG = "SimulateRC";
    //INPUT_DEVICE在bluetoothProfile中是hide的
    private static final int INPUT_DEVICE = 4;
    private BluetoothAdapter adapter;
    private static Context mContext;
    private String noSupportBluetooth = "不支持蓝牙";
    private List<BluetoothDevice> deviceList;
    private List<BluetoothDevice> unpairedDeviceList;
    private List<BluetoothDevice> pairedDeviceList;
    private BluetoothDevice currentDevice;
    //input device连接状态改变时候发送的广播（键盘，遥控器，鼠标等）
    private static final String INPUT_DEVICE_CONNECTION_STATE_CHANGED_ACTION = "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";


    private static BluetoothEngine singleton;

    private BluetoothEngine() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = new ArrayList<>();
        unpairedDeviceList = new ArrayList<>();
        pairedDeviceList = new ArrayList<>();
        initReceiver();
        updateScState();
    }

    public static BluetoothEngine getInstance(Context context) {
        mContext = context;
        if (singleton == null) {
            synchronized (BluetoothEngine.class) {
                singleton = new BluetoothEngine();
            }
        }
        return singleton;
    }

    /**
     * 打开蓝牙
     */
    public void openBt() {
        if (adapter == null) {
            if (btStateChangeListener != null)
                btStateChangeListener.updateButtonState(noSupportBluetooth);
            return;
        }
        if (btStateChangeListener != null)
            btStateChangeListener.updateButtonState("正在开。。。");
        Log.i(TAG, "开启蓝牙");
        adapter.enable();
    }

    /**
     * 关闭蓝牙
     */
    public void closeBt() {
        if (adapter == null) {
            if (btStateChangeListener != null)
                btStateChangeListener.updateButtonState(noSupportBluetooth);
            return;
        }
        if (btStateChangeListener != null)
            btStateChangeListener.updateButtonState("正在关。。。");
        Log.i(TAG, "开启蓝牙");
        adapter.disable();

    }

    /**
     * 开始扫描设备
     */
    public void startScanDevice() {
        if (adapter == null) return;
        Log.i(TAG, "开始扫描");
        adapter.startDiscovery();
        if (btStateChangeListener != null) {
            btStateChangeListener.updateBtnScanLabelChangeListener("扫描中。。。", false);
        }
    }

    /**
     * 关闭扫描
     */
    public void stopScanDevice() {
        if (adapter == null) return;
        if (adapter.isDiscovering()) {
            Log.i(TAG, "取消扫描");
            adapter.cancelDiscovery();
            if (btStateChangeListener != null) {
                btStateChangeListener.updateBtnScanLabelChangeListener("扫描停止", false);
            }
        }
    }

    /**
     * 开始配对
     */
    public void startPairing(BluetoothDevice device) {
        stopScanDevice();
        createBond(device);
    }

    /**
     * 绑定设备
     */
    private void createBond(BluetoothDevice device) {
        try {
            Method method = BluetoothDevice.class.getMethod("createBond");
            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解绑设备
     */
    public void removeBond(BluetoothDevice device) {
        Method method = null;
        try {
            method = BluetoothDevice.class.getMethod("removeBond");
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 连接设备
     * 具体实现 int deviceType = device.getBluetoothClass().getMajorDeviceClass() = BITMASK & 下面的类型;
     * 不同设备类型该值不同，比如computer蓝牙为256、phone 蓝牙为512、打印机蓝牙为1536等等
     * public static class Major {
     * private static final int BITMASK           = 0x1F00;
     * <p>
     * public static final int MISC              = 0x0000;
     * public static final int COMPUTER          = 0x0100;
     * public static final int PHONE             = 0x0200;
     * public static final int NETWORKING        = 0x0300;
     * public static final int AUDIO_VIDEO       = 0x0400;
     * public static final int PERIPHERAL        = 0x0500;
     * public static final int IMAGING           = 0x0600;
     * public static final int WEARABLE          = 0x0700;
     * public static final int TOY               = 0x0800;
     * public static final int HEALTH            = 0x0900;
     * public static final int UNCATEGORIZED     = 0x1F00;
     * }
     */
    public void connect(final BluetoothDevice device) {
        currentDevice = device;
        //测试,实际需要判断设备类型创建不同的profile
        final int deviceType = device.getBluetoothClass().getMajorDeviceClass();
//            BluetoothProfile.A2DP
        new Thread() {
            @Override
            public void run() {
                if (deviceType == (0x1F00 & 0x0500)) {//4:input_device
                    Log.i(TAG, "connect input device:" + device);
                    adapter.getProfileProxy(mContext, mProfileListener, INPUT_DEVICE);
                } else if (deviceType == (0x1F00 & 0x0400)) {//AUDIO_VIDEO
                    Log.i(TAG, "connect a2dp device:" + device);
                    adapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.A2DP);
                } else if (deviceType == (0x1F00 & 0x0200)) {//PHONE

                }
            }
        }.start();


    }

    private BluetoothProfile mBluetoothProfile;
    private BluetoothA2dp mA2dp;
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.i(TAG, "mConnectListener onServiceConnected");
            mBluetoothProfile = proxy;
            //判断连接的profile
            switch (profile) {
                case BluetoothProfile.A2DP:
                    mA2dp = (BluetoothA2dp) proxy;
                    setPriority(currentDevice,profile);
                    try {
                        //通过反射获取BluetoothA2dp中connect方法（hide的），进行连接。
                        Method connectMethod =BluetoothA2dp.class.getMethod("connect",
                                BluetoothDevice.class);
                        connectMethod.invoke(mA2dp, currentDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case INPUT_DEVICE:
                    try {
                        //得到BluetoothInputDevice然后反射connect连接设备
//                        Method method = mBluetoothProfile.getClass().getMethod("connect",
//                                new Class[]{BluetoothDevice.class});
//                        method.invoke(mBluetoothProfile, currentDevice);
                        Class mInputDevice = Class.forName("android.bluetooth.BluetoothInputDevice");
                        Method method = mInputDevice.getMethod("connect", String.class);
                        method.invoke(mInputDevice.newInstance(),currentDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }


        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.i(TAG, "mConnectListener onServiceConnected");
        }
    };

    /**
     * 断开连接
     */
    public void disConnect(BluetoothDevice device) {
        Log.i(TAG, "disConnect device:" + device);
        try {
            if (device != null) {
                Method method = mBluetoothProfile.getClass().getMethod("disconnect",
                        new Class[]{BluetoothDevice.class});
                method.invoke(mBluetoothProfile, device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置优先级
     * @param device
     * @param priority PRIORITY_OFF 0
     *                 PRIORITY_ON 100
     *                 PRIORITY_AUTO_CONNECTION 1000
     *                 PRIORITY_UNDEFINED -1
     *
     */
    private void setPriority(BluetoothDevice device, int priority) {
        if (mA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod =BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class,int.class);
            connectMethod.invoke(mA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 设置能否被扫描到
     * 有问题
     */
    public void setDeviceDiscoverable() {
//        //开启显示，让本机可以被搜索到
//        if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
//            mContext.startActivity(discoverableIntent);
//        }
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 100000);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 100000);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 清除device集合，重新扫描
     */
    public void clearListAndScan() {
        deviceList.clear();
        unpairedDeviceList.clear();
        if (!adapter.isDiscovering()) {
            adapter.startDiscovery();
        } else {
            adapter.cancelDiscovery();
            adapter.startDiscovery();
        }
        if (btStateChangeListener != null) {
            btStateChangeListener.updateBtnScanLabelChangeListener("扫描中。。。", false);
        }
    }


    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(INPUT_DEVICE_CONNECTION_STATE_CHANGED_ACTION);//input device 连接状态改变的广播
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);//a2dp设备连接状态改变广播
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "receiver = " + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
//                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
//                    int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
//                    if (state == BluetoothProfile.STATE_CONNECTED) {
//                        if (btStateChangeListener != null) {
//                            btStateChangeListener.updateBtnScanLabelChangeListener("连接成功，点击继续扫描（长按重新扫描）", true);
//                            btStateChangeListener.updateBondStateAndConnectionState("已连接");
//                        }
//                        Toast.makeText(context, "蓝牙设备:" + device.getName() + "已链接", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "蓝牙设备:" + device.getName() + "已链接");
//                    } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
//                        Log.e(TAG, "蓝牙设备:" + device.getName() + "已断开");
//                        Toast.makeText(context, "蓝牙设备:" + device.getName() + "已断开", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                case INPUT_DEVICE_CONNECTION_STATE_CHANGED_ACTION:
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                    if (state == BluetoothProfile.STATE_CONNECTED) {
                        //从未配对列表去掉
                        if (unpairedDeviceList.contains(device)) {
                            unpairedDeviceList.remove(device);
                        }
                        //加到已配对列表
                        if (!pairedDeviceList.contains(device)) {
                            pairedDeviceList.add(device);
                        }
                        if (btStateChangeListener != null) {
                            btStateChangeListener.refreshPairedDeviceList(pairedDeviceList);
                            btStateChangeListener.refreshUnpairedDeviceList(unpairedDeviceList);
                        }
                        Toast.makeText(context, "蓝牙设备:" + device.getName() + "已连接", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "蓝牙设备:" + device.getName() + "已链接");
                    } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                        //从已配对列表去掉
                        if (pairedDeviceList.contains(device)) {
                            pairedDeviceList.remove(device);
                            if (btStateChangeListener != null) {
                                btStateChangeListener.refreshPairedDeviceList(pairedDeviceList);
                            }
                        }
                        Log.i(TAG, "蓝牙设备:" + device.getName() + "已断开");
                        Toast.makeText(context, "蓝牙设备:" + device.getName() + "已断开", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    Log.i(TAG, "蓝牙状态 state = " + blueState);
                    updateScState();
                    switch (blueState) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(context, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.i(TAG, "扫描到设备:name = " + device.getName());
                    if (!deviceList.contains(device)) {
                        deviceList.add(device);
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            pairedDeviceList.add(device);
                        } else {
                            unpairedDeviceList.add(device);
                        }
                        if (btStateChangeListener != null) {
                            btStateChangeListener.refreshUnpairedDeviceList(unpairedDeviceList);
                            btStateChangeListener.refreshPairedDeviceList(pairedDeviceList);
                        }
                    }

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (btStateChangeListener != null) {
                        btStateChangeListener.updateBtnScanLabelChangeListener("扫描结束，点击继续扫描（长按重新扫描）", true);
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 10);
                    Log.i(TAG, "bond state change :bond state is " + bondState);
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDING:
                            if (btStateChangeListener != null) {
                                btStateChangeListener.updateBtnScanLabelChangeListener("配对中，禁止扫描", false);
                            }
                            break;
                        case BluetoothDevice.BOND_NONE:
                            if (btStateChangeListener != null) {
                                btStateChangeListener.updateBtnScanLabelChangeListener("配对失败，点击继续扫描（长按重新扫描）", true);
                            }
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            if (btStateChangeListener != null) {
                                btStateChangeListener.updateBtnScanLabelChangeListener("配对成功，点击继续扫描（长按重新扫描）", true);
                            }
                            //绑定成功，去连接设备
                            connect(device);

                            break;

                    }
                    break;
            }

        }
    };

    /**
     * 更新开关按钮的状态以及事后显示扫描按钮
     */
    private void updateScState() {
        if (adapter == null) {
            if (btStateChangeListener != null) {
                btStateChangeListener.updateButtonState(noSupportBluetooth);
                btStateChangeListener.updateDeviceListVisible(false);
            }
            return;
        }
        int state = adapter.getState();
        Log.e(TAG, "更新蓝牙状态 state = " + state);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                if (btStateChangeListener != null) {
                    btStateChangeListener.updateDeviceListVisible(false);
                    btStateChangeListener.updateButtonState("关");
                    btStateChangeListener.onBtStateChangeListener(BluetoothAdapter.STATE_OFF);
                }
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                if (btStateChangeListener != null) {
                    btStateChangeListener.updateDeviceListVisible(false);
                    btStateChangeListener.updateButtonState("正在关。。。");
                    btStateChangeListener.onBtStateChangeListener(BluetoothAdapter.STATE_TURNING_OFF);
                }
                break;
            case BluetoothAdapter.STATE_ON:
                if (btStateChangeListener != null) {
                    btStateChangeListener.updateDeviceListVisible(true);
                    btStateChangeListener.updateButtonState("开");
                    btStateChangeListener.onBtStateChangeListener(BluetoothAdapter.STATE_ON);
                }
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                if (btStateChangeListener != null) {
                    btStateChangeListener.updateDeviceListVisible(true);
                    btStateChangeListener.updateButtonState("正在开。。。");
                    btStateChangeListener.onBtStateChangeListener(BluetoothAdapter.STATE_TURNING_ON);
                }
                break;

        }

    }

    private BtStateChangeListener btStateChangeListener;

    public void setOnBtStateChangeListener(BtStateChangeListener listener) {
        this.btStateChangeListener = listener;
    }

    public int getBtState() {
        if (adapter != null) {
            return adapter.getState();
        } else {
            return 0;
        }
    }

    public void unregisterBroadcast() {
        mContext.unregisterReceiver(mReceiver);
    }

    /**
     * 获取已经连接的设备
     */
    public void getConnectedDevice() {
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                Log.i(TAG, "devices:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.i(TAG, "connected:" + device.getName());
                        if (!pairedDeviceList.contains(device)) {
                            pairedDeviceList.add(device);
                            if (btStateChangeListener != null) {
                                btStateChangeListener.refreshPairedDeviceList(pairedDeviceList);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BtStateChangeListener {

        void onBtStateChangeListener(int state);

        void updateButtonState(String stateLabel);

        void updateDeviceListVisible(boolean isVisible);

        void updateBtnScanLabelChangeListener(String label, boolean isClickable);

        void refreshPairedDeviceList(List<BluetoothDevice> pairedList);

        void refreshUnpairedDeviceList(List<BluetoothDevice> unpairedList);
    }
}
