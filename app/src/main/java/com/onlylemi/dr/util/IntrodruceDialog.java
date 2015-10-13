package com.onlylemi.dr.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.onlylemi.indoor.R;

/**
 * Created by 董神 on 2015/10/14.
 * I love programming
 */
public class IntrodruceDialog extends Dialog {

    private Activity activity;
    private TextView textView;

    public IntrodruceDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_introduce);
        textView = (TextView) findViewById(R.id.dialog_introduce_tv);
        textView.setText("");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() > textView.getX() || event.getX() < textView.getX()) {
                if(event.getY() > textView.getY() || event.getY() < textView.getY()) {
                    dismiss();
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
