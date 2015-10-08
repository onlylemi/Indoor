package com.onlylemi.indoor.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onlylemi.indoor.R;
import com.onlylemi.map.core.PMark;
import com.onlylemi.parse.JSONUpload;
import com.onlylemi.parse.info.ActivityTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by only乐秘 on 2015-10-07.
 */
public class AddViewsActivityDialog extends Dialog implements View.OnClickListener,
        WheelViewDialog.OnClickOkListener, DatePickerDialog.OnDateSetListener {

    public final static String TAG = "AddViewsActivityDialog:";

    private Context context;
    private List<PMark> views;

    private Button cancleBut;
    private Button okBut;

    private TextView addViewsActivityName;
    private TextView addViewsActivityStartName;
    private TextView addViewsActivityEndName;
    private EditText addViewsActivityIntro;

    private DatePickerDialog datePickerDialogStart;
    private DatePickerDialog datePickerDialogEnd;

    private ActivityTable activityTable;

    private Handler handler;

    public AddViewsActivityDialog(Context context, List<PMark> views, Handler handler) {
        super(context);

        this.context = context;
        this.views = views;
        this.activityTable = new ActivityTable();
        this.handler = handler;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_views_activity_dialog, null);

        addViewsActivityName = (TextView) view.findViewById(R.id.add_views_activity_name);
        addViewsActivityStartName = (TextView) view.findViewById(R.id.add_views_activity_start_time);
        addViewsActivityEndName = (TextView) view.findViewById(R.id.add_views_activity_end_time);
        addViewsActivityIntro = (EditText) view.findViewById(R.id.add_views_activity_intro);
        okBut = (Button) view.findViewById(R.id.add_views_activity_ok);
        cancleBut = (Button) view.findViewById(R.id.add_views_activity_cancle);


        addViewsActivityName.setOnClickListener(this);
        addViewsActivityStartName.setOnClickListener(this);
        addViewsActivityEndName.setOnClickListener(this);
        okBut.setOnClickListener(this);
        cancleBut.setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        if (v == okBut) {
            addViewsActivity();
        } else if (v == cancleBut) {
            this.cancel();
        } else if (v == addViewsActivityName) {
            List<String> items = new ArrayList<>();
            for (PMark pMark : views) {
                items.add(pMark.name);
            }
            WheelViewDialog wheelViewDialog = new WheelViewDialog(context, items);
            wheelViewDialog.setTitle("选择店铺");
            wheelViewDialog.setOnClickOkListener(this);
            wheelViewDialog.show();
        } else if (v == addViewsActivityStartName) {
            datePickerDialogStart = new DatePickerDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT, this,
                    mYear, mMonth, mDay);
            datePickerDialogStart.setTitle("选择优惠活动开始时间");
            datePickerDialogStart.show();
        } else if (v == addViewsActivityEndName) {
            datePickerDialogEnd = new DatePickerDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT, this,
                    mYear, mMonth, mDay);
            datePickerDialogEnd.setTitle("选择优惠活动结束时间");
            datePickerDialogEnd.show();
        }
    }

    @Override
    public void onOk(int position, String item) {
        this.addViewsActivityName.setText(item);
        activityTable.setVid(views.get(position).id);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;

        if (view == datePickerDialogStart.getDatePicker()) {
            this.addViewsActivityStartName.setText(year + "-" + month + "-" + day);
        } else if (view == datePickerDialogEnd.getDatePicker()) {
            this.addViewsActivityEndName.setText(year + "-" + month + "-" + day);
        }
    }

    /**
     * ok 响应事件
     */
    private void addViewsActivity() {
        String viewActivityName = this.addViewsActivityName.getText().toString();
        String viewActivityStartTime = this.addViewsActivityStartName.getText().toString();
        String viewActivityEndTime = this.addViewsActivityEndName.getText().toString();
        String viewActivityIntro = this.addViewsActivityIntro.getText().toString();

        if ("".equals(viewActivityName)) {
            Toast.makeText(context, "请选择商铺！", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(viewActivityStartTime)) {
            Toast.makeText(context, "请选择优惠活动开始时间！", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(viewActivityEndTime)) {
            Toast.makeText(context, "请选择优惠活动结束时间！", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(viewActivityIntro)) {
            Toast.makeText(context, "请输入优惠活动介绍详细介绍内容！", Toast.LENGTH_SHORT).show();
            return;
        }

        activityTable.setName(viewActivityName);
        activityTable.setStartTime(viewActivityStartTime);
        activityTable.setEndTime(viewActivityEndTime);
        activityTable.setIntro(viewActivityIntro);

        Log.i(TAG, activityTable.toString());

        JSONUpload upload = new JSONUpload(handler);
        upload.setOnUploadDataListener(new JSONUpload.OnUploadDataListener() {
            @Override
            public void onSuccess(int success, final String message) {
                Log.i(TAG, message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(int success, final String message) {
                Log.i(TAG, message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
        upload.uploadViewsActivityData("POST", activityTable);


        this.cancel();
    }

}
