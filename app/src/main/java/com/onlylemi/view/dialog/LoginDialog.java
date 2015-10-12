package com.onlylemi.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.onlylemi.dr.activity.ReadyActivity;
import com.onlylemi.indoor.R;
import com.onlylemi.parse.JSONUpload;
import com.onlylemi.parse.info.UserTable;
import com.onlylemi.utils.MD5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 董神 on 2015/10/10.
 * I love programming
 */
public class LoginDialog extends Dialog implements JSONUpload.OnUploadDataListener {


    private static final String TAG = LoginDialog.class.getSimpleName();
    private Activity activity;
    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String urlString;


    public LoginDialog(Activity activity) {
        super(activity);
        this.activity = activity;


    }

    public static String md5(String string) {

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_layout);

        editTextName = (EditText) findViewById(R.id.dialog_login_name);
        editTextPassword = (EditText) findViewById(R.id.dialog_login_pw);
        buttonLogin = (Button) findViewById(R.id.dialog_login_login);

        setTitle(activity.getResources().getString(R.string.login));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String pw = editTextPassword.getText().toString();
                UserTable userTable = new UserTable();
                userTable.setEmail(name);
                userTable.setPassword(MD5.getMD5Code(pw));
                JSONUpload upload = new JSONUpload(activity);
                upload.setOnUploadDataListener(LoginDialog.this);
                upload.uploadUserData("POST", userTable);
                Log.i(TAG, name + "-----" + MD5.getMD5Code(pw));
//                new UserLoginTask().execute();
            }
        });
    }

    @Override
    public void onSuccess(int success, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFail(int success, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

}
