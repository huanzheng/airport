package com.invent.airport.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.invent.airport.R;

public final class ActivityConfig extends Activity {
    Button mConfig;
    EditText mHTTP;
    EditText mIP;
    EditText mPort;
    EditText mRootPath;
    //EditText mPeriod;
    Context mContext;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_config);
        mContext = this;
        mHTTP = (EditText) findViewById(R.id.aty_config_http);
        mIP = (EditText) findViewById(R.id.aty_config_ip);
        mPort = (EditText) findViewById(R.id.aty_config_port);
        mRootPath = (EditText) findViewById(R.id.aty_config_rootpath);
        //mPeriod = (EditText) findViewById(R.id.aty_config_period);
        mConfig = (Button) findViewById(R.id.aty_config_config);

        mHTTP.setText(AirportApp.getInstance().getHttp());
        mIP.setText(AirportApp.getInstance().getUpdateServerIP());
        mPort.setText(AirportApp.getInstance().getPort());
        mRootPath.setText(AirportApp.getInstance().getRootPath());
        //mPeriod.setText("" + AirportApp.getInstance().getUpdatePeriod());

        mConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void save() {
        String http = mHTTP.getText().toString();
        String ip = mIP.getText().toString();
        String port = mPort.getText().toString();
        String rootPath = mRootPath.getText().toString();
        //String period = mPeriod.getText().toString();
        if (http.isEmpty() || ip.isEmpty()
                || port.isEmpty() || rootPath.isEmpty()) {
            Toast.makeText(mContext, getString(R.string.config_need_value), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!"http".equals(http) && !"https".equals(http)) {
            Toast.makeText(mContext, getString(R.string.config_need_http), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            @SuppressWarnings("unused")
            Integer portInt = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, getString(R.string.config_need_port), Toast.LENGTH_SHORT).show();
            return;
        }
        /*try {
            Integer periodInteger = Integer.valueOf(period);
            if (periodInteger <= 0) {
                Toast.makeText(mContext, "更新间隔需为正整数", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, getString(R.string.config_need_port), Toast.LENGTH_SHORT).show();
            return;
        }*/
        AirportApp.ServerConfig config = new AirportApp.ServerConfig();
        config.mHTTPProtocal = http;
        config.mUpdateServerIP = ip;
        config.mPort = port;
        config.mRootPath = rootPath;
        config.mPeriod = Integer.valueOf(100);
        AirportApp.getInstance().saveConfig(config);
        finish();
    }

}
