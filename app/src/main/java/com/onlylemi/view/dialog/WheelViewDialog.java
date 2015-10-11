package com.onlylemi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.onlylemi.indoor.R;
import com.onlylemi.view.WheelView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by only乐秘 on 2015-10-07.
 */
public class WheelViewDialog extends Dialog implements View.OnClickListener, WheelView.OnWheelViewListener {

    private static final String TAG = "WheelViewDialog";

    private Context context;
    private static final String[] PLANETS = new String[]{"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Uranus", "Neptune", "Pluto"};

    private WheelView wv;
    private Button cancleBut;
    private Button okBut;

    private OnClickOkListener listener;

    private int selectedIndex;
    private String item;

    public WheelViewDialog(Context context, List<String> items) {
        super(context);

        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.wheel_view, null);
        wv = (WheelView) view.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(items);
        wv.setSeletion(3);
        okBut = (Button) view.findViewById(R.id.select_view_ok);
        cancleBut = (Button) view.findViewById(R.id.select_view_cancle);

        wv.setOnWheelViewListener(this);
        okBut.setOnClickListener(this);
        cancleBut.setOnClickListener(this);

        setContentView(view);
    }


    @Override
    public void onSelected(int selectedIndex, String item) {
        this.setTitle(item);
        this.selectedIndex = selectedIndex;
        this.item = item;
    }

    @Override
    public void onClick(View v) {
        if (v == okBut) {
            if (listener != null) {
                listener.onOk(selectedIndex - wv.getOffset(), item);
            }
            this.cancel();
        } else if (v == cancleBut) {
            this.cancel();
        }
    }


    public void setOnClickOkListener(OnClickOkListener listener) {
        this.listener = listener;
    }

    /**
     * 点击ok之后的回调方法
     */
    public interface OnClickOkListener {
        void onOk(int position, String item);
    }

}
