package com.onlylemi.dr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.onlylemi.dr.custom_adapter.EveryPartAdapter;
import com.onlylemi.dr.custom_view.MyGridView;
import com.onlylemi.dr.util.PlacePhotoDownloadTask;
import com.onlylemi.indoor.IndoorActivity;
import com.onlylemi.indoor.R;
import com.onlylemi.user.Assist;
import com.onlylemi.utils.Share;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 董神
 */
public class EveryPartActivity extends AppCompatActivity {

    public static final String TAG = "EveryPartActivity:";
    private ImageView imageView;
    private TextView textViewDescription;
    private String string;
    private Button button;
    private Button videoButton;
    private MyGridView gridView;
    private TextView tvName;
    private TextView tvAddress;
    private EveryPartAdapter adapter;
    private int pid;
    private boolean[] mlist;
    private List<Integer> selectViewsList;
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

        final Intent intent = getIntent();
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
                    mlist[position] = true;
                    checkbox.setChecked(true);
                } else {
                    mlist[position] = false;
                    checkbox.setChecked(false);
                }
            }
        });


        button = (Button) findViewById(R.id.activity_every_part_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectViewsList = new ArrayList<>();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i] == true) {
                        selectViewsList.add(i);
                    }
                }
                Log.v(TAG, selectViewsList.toString());

                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("select_views_list", (ArrayList<Integer>) selectViewsList);
                Intent intent1 = new Intent();
                intent1.putExtras(bundle);
                if (pid == Assist.tangjiuId) {
                    intent1.setClass(EveryPartActivity.this, IndoorActivity.class);
                    startActivity(intent1);
                } else {
                    Toast.makeText(EveryPartActivity.this, "该地区正在后期开发中，敬请期待！请先游览中北大学唐久便利", Toast.LENGTH_LONG).show();
                }
            }
        });


        /*videoButton = (Button) findViewById(R.id.activity_every_part_video_button);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    *//*Intent intent = new Intent(EveryPartActivity.this, VitamioActivity.class);
                    startActivity(intent);*//*
            }
        });*/
    }

    private void initAdapter() {
        adapter = new EveryPartAdapter(this);
        mlist = new boolean[adapter.viewsTableList.size()];
        Log.e(TAG, "size == " + adapter.viewsTableList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_every_part, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(name);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.every_part_activity_share) {
            Share.shareApp(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void update() {
        adapter.update();
        adapter.notifyDataSetChanged();
    }
}
