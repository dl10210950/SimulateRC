package com.example.duanlianex.simulaterc.custom;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.duanlianex.simulaterc.R;

/**
 * Created by duanlian.ex on 2019/1/22.
 */

public class RemovePairPop extends PopupWindow {
    private Context mContext;


    public RemovePairPop(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_remove_pair, null);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.confirmClickListener();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
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
        btnCancel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
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
        setContentView(view);
        //设置非PopupWindow区域是否可触摸
        this.setOutsideTouchable(false);
        this.setFocusable(true);
    }

    private ClickListener clickListener;

    public void setOnClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

    public interface ClickListener {
        void confirmClickListener();

    }
}
