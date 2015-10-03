package com.onlylemi.indoor;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity implements IndoorFragment.OnFragmentInteractionListener {

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().add(R.id.indoor_frament, IndoorFragment.newInstance()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
