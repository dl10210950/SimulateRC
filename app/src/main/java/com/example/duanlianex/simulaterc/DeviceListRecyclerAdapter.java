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
 */

public class DeviceListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BluetoothDevice> list;
    private String bondConnectionState = "";
    private int currentClickPosition;

    public DeviceListRecyclerAdapter(Context context, List<BluetoothDevice> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setFocusable(true);
        final BluetoothDevice device = list.get(position);
        int bondState = device.getBondState();
        String bond = "";
        if (bondState == BluetoothDevice.BOND_BONDED) {
            bond = "已绑定";
        } else if (bondState == BluetoothDevice.BOND_BONDING) {
            bond = "正在绑定。。。";
        } else {
            bond = "未绑定";
        }
        viewHolder.tvName.setText("名称：" + device.getName() + ";分类:" + device.getBluetoothClass().getMajorDeviceClass());
        viewHolder.tvAddress.setText("地址：" + device.getAddress());
        if (position >= 0 && position == currentClickPosition) {
            viewHolder.tvBond.setText("绑定状态： " + bondConnectionState);
        } else {
            viewHolder.tvBond.setText("绑定状态： " + bond);

        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(position, device);
                }
            }
        });
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ViewCompat.animate(view).scaleX(1f).scaleY(1.5f).translationX(1).start();
                        view.setBackgroundColor(Color.RED);
                    }
                } else {
                    ViewCompat.animate(view).scaleX(1).scaleY(1).translationX(1).start();
                    view.setBackgroundColor(Color.WHITE);
                    ViewGroup parent = (ViewGroup) view.getParent();
                    parent.requestLayout();
                    parent.invalidate();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvBond;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvBond = itemView.findViewById(R.id.tv_bond);
        }
    }

    public void refreshData(List<BluetoothDevice> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void refreshState(String label, int clickPosition) {
        this.bondConnectionState = label;
        this.currentClickPosition = clickPosition;
        if (clickPosition >= 0) {
            notifyItemChanged(clickPosition);
        }
    }

    private OnItemClickListener onItemClickListener;

    interface OnItemClickListener {
        void onItemClickListener(int position, BluetoothDevice device);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


}
