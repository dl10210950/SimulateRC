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

import java.util.List;

/**
 * Created by duanlian.ex on 2019/1/8.
 * 未配对的设备列表
 */

public class DeviceListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BluetoothDevice> unpairedList;
    private String bondConnectionState = "";
    private int currentClickPosition;

    public DeviceListRecyclerAdapter(Context context, List<BluetoothDevice> list) {
        this.context = context;
        this.unpairedList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unpaired_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setFocusable(true);
        final BluetoothDevice device = unpairedList.get(position);
        viewHolder.tvName.setText("名称：" + device.getName() + ";分类:" + device.getBluetoothClass().getMajorDeviceClass());
        viewHolder.tvAddress.setText("地址：" + device.getAddress());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onUnpairedItemClickListener != null) {
                    onUnpairedItemClickListener.onUnpairedItemClickListener(position, device);
                }
            }
        });
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ViewCompat.animate(view).scaleX(1f).scaleY(1.5f).translationX(1).start();
                        view.setBackgroundColor(Color.RED);
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
        return unpairedList != null ? unpairedList.size() : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }

    public void refreshUnpairedDeviceList(List<BluetoothDevice> unpairedList) {
        this.unpairedList = unpairedList;
        notifyDataSetChanged();
    }

    public void refreshState(String label, int clickPosition) {
        this.bondConnectionState = label;
        this.currentClickPosition = clickPosition;
        if (clickPosition >= 0) {
            notifyItemChanged(clickPosition);
        }
    }

    private OnUnpairedItemClickListener onUnpairedItemClickListener;

    interface OnUnpairedItemClickListener {
        void onUnpairedItemClickListener(int position, BluetoothDevice device);
    }

    public void setOnUnpairedItemClickListener(OnUnpairedItemClickListener listener) {
        this.onUnpairedItemClickListener = listener;
    }


}
