package com.onlylemi.dr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.onlylemi.indoor.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 董神 on 2015/10/10.
 * I love programming
 */
public class LoginDialog extends Dialog {


    private static final String TAG = LoginDialog.class.getSimpleName();
    private Context context;
    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String urlString;


    public LoginDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_layout);

        editTextName = (EditText) findViewById(R.id.dialog_login_name);
        editTextPassword = (EditText) findViewById(R.id.dialog_login_pw);
        buttonLogin = (Button)findViewById(R.id.dialog_login_login);

        setTitle(context.getResources().getString(R.string.login));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "跳转成功", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new UserLoginTask().execute();
            }
        });
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.i(TAG, "end");
            dismiss();
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "start");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer("");
                String s = "测试中";
                while((s = reader.readLine()) != null) {
                    stringBuffer.append(s);
                }
                Log.i(TAG, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "faile");
            return true;
        }
    }
}
