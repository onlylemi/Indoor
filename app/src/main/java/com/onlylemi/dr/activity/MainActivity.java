package com.onlylemi.dr.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.onlylemi.view.dialog.IntrodruceDialog;
import com.onlylemi.view.dialog.LoginDialog;
import com.onlylemi.dr.fragment.FindFragment;
import com.onlylemi.dr.fragment.MapFragment;
import com.onlylemi.dr.fragment.NavigationDrawerFragment;
import com.onlylemi.dr.util.BaiduLocate;
import com.onlylemi.indoor.R;
import com.onlylemi.parse.Data;
import com.onlylemi.user.Assist;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MapFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity:";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Fragment managing the behaviors, interactions and presentation of the map.
     */
    private MapFragment mMapFragment;
    /**
     * fragment managing the
     */
    private FindFragment findFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //init map
        mMapFragment = MapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.activity_main_map, mMapFragment).commit();

        findFragment = (FindFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_find);
        findFragment.setHandler(ReadyActivity.handler);

        if (BaiduLocate.getCurrentCity() != null) {
            mTitle = BaiduLocate.getCurrentCity();
            Assist.currentCity = (String) mTitle;
            Assist.currentCityID = Data.getCityId(BaiduLocate.getCurrentCity());
            for (int i = 0; i < Data.getCityTableList().size(); i++) {
                Log.i(TAG, mTitle + "==" + Data.getCityTableList().get(i));
                if (mTitle.equals(Data.getCityTableList().get(i).getName())) {
                    findFragment.setCityID(Data.getCityTableList().get(i).getId());
                    Log.i(TAG, "开始更新");
                    findFragment.upDate();
                    break;
                }
            }
        } else {
            mTitle = getTitle();
        }
    }

    /**
     * 监听城市导航事件
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        String city = "";
        if (Data.getOnlyCity() != null && mMapFragment != null) {
            city = Data.getOnlyCity().get(position);
            Log.i(this.TAG, "select of city : " + city);
            mMapFragment.updateMap(city, city);
            mTitle = city;
            for (int i = 0; i < Data.getCityTableList().size(); i++) {
                Log.i(TAG, city + "==" + Data.getCityTableList().get(i).getName());
                if (city.equals(Data.getCityTableList().get(i).getName())) {
                    findFragment.setCityID(Data.getCityTableList().get(i).getId());
                    Log.i(TAG, "开始更新");
                    findFragment.upDate();
                    Assist.currentCity = city;
                    Assist.currentCityID = Data.getCityTableList().get(i).getId();
                    break;
                }
            }
        }
    }

    /**
     * 地图响应事件
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.map_switch:
                mMapFragment.switchModel();
                break;
            case R.id.intro_team:
                IntrodruceDialog dialog = new IntrodruceDialog(this);
                dialog.show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.exit(1);
        return true;
    }
}
