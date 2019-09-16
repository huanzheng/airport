package com.invent.airport.requests;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.HttpStatus;

public final class AirportErrorListener implements Response.ErrorListener {
    private final Context mContext;
    private static final String TAG = "AirportError";

    public AirportErrorListener(Context context) {
        mContext = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        if (error instanceof AirportError) {
            Toast.makeText(mContext, "错误 " + ((AirportError) error).mErrorMsg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Airport Error " + ((AirportError) error).mErrorMsg);
        } else {
            if (networkResponse != null) {
                Log.e(TAG, "Status code is " + networkResponse.statusCode);
                if (networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    Toast.makeText(mContext, "无访问权限，请登录", Toast.LENGTH_SHORT).show();
                } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    Toast.makeText(mContext, "网络问题，请检查服务器配置", Toast.LENGTH_SHORT).show();
                }
            }

            Log.e(TAG, "cause class " + error.getCause().getClass().getCanonicalName());
            //if (NetworkResponse)
            Toast.makeText(mContext, "错误 " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Volley Error " + error.getMessage());
        }
    }
}
