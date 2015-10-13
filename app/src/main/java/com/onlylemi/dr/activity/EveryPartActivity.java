package com.onlylemi.dr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.onlylemi.dr.custom_adapter.EveryPartAdapter;
import com.onlylemi.dr.custom_view.MyGridView;
import com.onlylemi.dr.util.PlacePhotoDownloadTask;
import com.onlylemi.indoor.R;
import com.onlylemi.indoor.TestActivity;


/**
 * @author 董神
 */
public class EveryPartActivity extends AppCompatActivity {

    public static final String TAG = "EveryPartActivity:";
    private ImageView imageView;
    private TextView textViewDescription;
    private Button button;
    private MyGridView gridView;
    private TextView tvName;
    private TextView tvAddress;
    private EveryPartAdapter adapter;
    private int pid;
    private String imageUrl;
    private CheckBox checkbox;
    private String name;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_every_part);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_every_part_refresh);
        imageView = (ImageView) findViewById(R.id.activity_every_part_image_view);
        textViewDescription = (TextView) findViewById(R.id.activity_every_part_description_text_view);
        gridView = (MyGridView) findViewById(R.id.activity_every_part_grid_view);
        tvAddress = (TextView) findViewById(R.id.activity_every_part_address);
        tvName = (TextView) findViewById(R.id.activity_every_part_name);


        // 下拉刷新数据
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.custom_test_five_title_background)
                , getResources().getColor(R.color.custom_test_four)
                , getResources().getColor(R.color.custom_test_one)
                , getResources().getColor(R.color.custom_test_three));
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.gray));
        refreshLayout.setSize(SwipeRefreshLayout.LARGE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "on refreshing");
                update();
                refreshLayout.setRefreshing(true);
                ReadyActivity.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        // 从上个activity得到数据
        Intent intent = getIntent();
        pid = intent.getIntExtra("PlaceID", pid);
        imageUrl = intent.getStringExtra("MainPlace");
        String intro = intent.getStringExtra("intro");
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");


        textViewDescription.setText(intro);
        tvName.setText(name);
        tvAddress.setText(address);
        this.name = name;
        new PlacePhotoDownloadTask(imageView).execute(imageUrl);
        this.initAdapter();
        gridView.setAdapter(adapter);

        gridView.post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) findViewById(R.id.id_scroll)).scrollTo(0, 0);

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkbox = (CheckBox) view.findViewById(R.id.id_checkbox);
                if (!checkbox.isChecked()) {
                    checkbox.setChecked(true);
                    adapter.integerList.add(Integer.valueOf(adapter.viewsTableList.get(position).getId()));
                } else {
                    checkbox.setChecked(false);
                    adapter.integerList.remove(Integer.valueOf(adapter.viewsTableList.get(position).getId()));
                }
            }
        });


        button = (Button) findViewById(R.id.activity_every_part_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(EveryPartActivity.this, TestActivity.class));
            }
        });

    }

    /**
     * 初始化数据适配器
     */
    private void initAdapter() {
        adapter = new EveryPartAdapter(this);
        Log.e(TAG, "size == " + adapter.viewsTableList.size());
    }

    /**
     * 创建菜单
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(name);
        return true;
    }

    /**
     * 更新数据
     */
    public void update() {
        adapter.update();
        adapter.notifyDataSetChanged();
    }
}
