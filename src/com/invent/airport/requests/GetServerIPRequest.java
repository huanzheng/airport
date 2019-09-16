package com.invent.airport.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

import java.io.UnsupportedEncodingException;

public final class GetServerIPRequest extends Request<GetServerIPRequest.ServerIP> {
    private static final String TAG = "GetServerIPRequest";
    Response.Listener<ServerIP> mListener;
    Gson mGson;

    private GetServerIPRequest(Response.Listener<ServerIP> listener, Response.ErrorListener errorListener) {
        super(Method.GET, Constants.getFunctionURL(Constants.FUNC_GET_SERVERIP), errorListener);
        mListener = listener;
        mGson = new Gson();
    }

    @Override
    protected Response<ServerIP> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        AirportError airportError = new AirportError(100, "Failed to find server ip");
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            L.i(TAG, "Response is " + json);
            if (json != null) {
                ServerIP ret = mGson.fromJson(json, ServerIP.class);
                if (ret != null) {
                    return Response.success(ret, getCacheEntry());
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.error(airportError);
    }

    @Override
    protected void deliverResponse(ServerIP response) {
        mListener.onResponse(response);
    }

    public static class ServerIP {
        @SerializedName("ip")
        public String mIP;
    }
}
