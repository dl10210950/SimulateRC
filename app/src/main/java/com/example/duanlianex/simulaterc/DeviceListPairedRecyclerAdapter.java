package com.example.duanlianex.simulaterc;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duanlian.ex on 2019/1/8.
 * 已经配对的设备列表
 */

public class DeviceListPairedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BluetoothDevice> pairedList;
    private List<BluetoothDevice> connectedList = new ArrayList<>();

    public DeviceListPairedRecyclerAdapter(Context context, List<BluetoothDevice> pairedList) {
        this.context = context;
        this.pairedList = pairedList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_paired_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setFocusable(true);
        final BluetoothDevice device = pairedList.get(position);
        viewHolder.tvName.setText("名称：" + device.getName() + ";分类:" + device.getBluetoothClass().getMajorDeviceClass());
        viewHolder.tvAddress.setText("地址：" + device.getAddress());
        for (BluetoothDevice connectedDevice : connectedList) {
            if(connectedDevice.equals(device)){
                viewHolder.tvState.setText("已连接");
            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPairedItemClickListener != null) {
                    onPairedItemClickListener.onPairedItemClickListener(position, device, view);
                }
            }
        });
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ViewCompat.animate(view).scaleX(1f).scaleY(1.5f).translationX(1).start();
                        view.setBackgroundColor(Color.parseColor("#FFC300FF"));
                    }
                } else {
                    ViewCompat.animate(view).scaleX(1).scaleY(1).translationX(1).start();
                    view.setBackgroundColor(Color.WHITE);
                    ViewGroup parent = (ViewGroup) view.getParent();
                    if (parent != null) {
                        parent.requestLayout();
                        parent.invalidate();
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pairedList != null ? pairedList.size() : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvState;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvState = itemView.findViewById(R.id.tv_state);
        }
    }

    public void refreshPairedDeviceList(List<BluetoothDevice> list) {
        this.pairedList = list;
        notifyDataSetChanged();
    }

    public void refreshConnectedDeviceList(List<BluetoothDevice> list) {
        this.connectedList = list;
        notifyDataSetChanged();
    }


    private OnPairedItemClickListener onPairedItemClickListener;

    interface OnPairedItemClickListener {
        void onPairedItemClickListener(int position, BluetoothDevice device, View view);
    }

    public void setOnPairedItemClickListener(OnPairedItemClickListener listener) {
        this.onPairedItemClickListener = listener;
    }


}
