package com.onlylemi.dr.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onlylemi.indoor.R;

/**
 * Created by 董神 on 2015/10/14.
 * I love programming
 */
public class IntrodruceDialog extends Dialog {

    public static final String TAG = IntrodruceDialog.class.getSimpleName();
    private Activity activity;
    private TextView textView;

    public IntrodruceDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public IntrodruceDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_introduce);
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = this.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        p.alpha = 0.8f;
        this.getWindow().setAttributes(p);
        textView = (TextView) findViewById(R.id.dialog_introduce_school);

    }
}
