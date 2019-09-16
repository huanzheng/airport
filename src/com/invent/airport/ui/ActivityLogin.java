package com.invent.airport.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.GetServerIPRequest;
import com.invent.airport.requests.LoginRequest;
import com.invent.airport.requests.RequestGetUserDetail;

public final class ActivityLogin extends AirportBaseActivity {
    Button mLogin;
    EditText mName;
    EditText mPasswd;

    Response.Listener<GetServerIPRequest.ServerIP> mServerIPResponseHandler = new Response.Listener<GetServerIPRequest.ServerIP>() {
        @Override
        public void onResponse(GetServerIPRequest.ServerIP response) {
            dismissProgressDialog();
            Toast.makeText(mContext, "服务器地址为" + response.mIP, Toast.LENGTH_SHORT).show();
            AirportApp.getInstance().saveAPIIP(response.mIP);
            getUserDetail();
        }
    };

    @Override
    protected void onEnter() {
        startLogin();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        dismissProgressDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        mName = (EditText) findViewById(R.id.aty_login_user_name);
        mPasswd = (EditText) findViewById(R.id.aty_login_passwd);
        mLogin = (Button) findViewById(R.id.aty_login_login);
        mName.setOnKeyListener(mOnKeyListener);
        mPasswd.setOnKeyListener(mOnKeyListener);
        
        
        KeyguardManager keyguardManager 
        = (KeyguardManager)getSystemService(KEYGUARD_SERVICE); 
        KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE); 
          
        lock.disableKeyguard();

        mName.setOnKeyListener(mOnKeyListener);
        mPasswd.setOnKeyListener(mOnKeyListener);
        //mName.setText("admin");
        //mPasswd.setText("admin");

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

       // getServerIP();
    }


    /*
    private void getServerIP() {
        GetServerIPRequest getServerIPRequest = new GetServerIPRequest(mServerIPResponseHandler, mErrorListener);
        showProgressDialog(mOPInProgress, "获取服务器地址中");
        mRequestQueue.add(getServerIPRequest);
    }*/

    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //toastInfo("尝试打开网络连接");
        //InternetControl.EnableInternet(mContext);
    }

    private void startLogin() {
        String name = mName.getText().toString();
        String password = mPasswd.getText().toString();
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(mContext, getString(R.string.login_need_input), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            name = URLEncoder.encode(name, "UTF-8").replace(" ", "%20");
            password = URLEncoder.encode(password, "UTF-8").replace(" ", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            toastInfo(e.toString());
            return;
        }
        LoginRequest.Login login = new LoginRequest.Login(name, password);
        LoginRequest loginRequest = new LoginRequest(login,
                new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        getUserDetail();
                    }
                }, mErrorListener);
        showProgressDialog(mOPInProgress, getString(R.string.login_login_in_progress));
        mRequestQueue.add(loginRequest);
    }

    private void getUserDetail() {
        RequestGetUserDetail requestGetUserDetail = new RequestGetUserDetail(new Response.Listener<RequestGetUserDetail.User>() {
            @Override
            public void onResponse(RequestGetUserDetail.User response) {
                dismissProgressDialog();
                Toast.makeText(mContext, getString(R.string.login_welcome_back) + response.mRealname, Toast.LENGTH_SHORT).show();
                AirportApp.getInstance().setUser(response);
                Intent i = new Intent(mContext, ActivityEntry.class);
                mContext.startActivity(i);
                ((Activity) mContext).finish();
            }
        }, mErrorListener);
        showProgressDialog(mOPInProgress, getString(R.string.login_retrieving_user_info));
        mRequestQueue.add(requestGetUserDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(mContext, ActivityConfig.class);
                mContext.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
