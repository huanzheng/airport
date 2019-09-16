package com.invent.airport.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;
import com.invent.airport.ui.AirportApp;

public final class LoginRequest extends Request<CookieGsonRequest.Succeed> {

    private static final String USER_NAME = "j_username";
    private static final String USER_PASSWD = "j_password";
    private static final String REMEMBER_ME = "_spring_security_remember_me";
    private static final String ON = "on";

    Response.Listener<CookieGsonRequest.Succeed> mListener;
    Login mLogin;
    Gson mGson;

    public LoginRequest(Login login, Response.Listener<CookieGsonRequest.Succeed> listener, Response.ErrorListener errorListener) {
        super(Method.POST, getLoginURL(login), errorListener);
        mListener = listener;
        mLogin = login;
        mGson = new Gson();
    }

    @Override
    protected Response<CookieGsonRequest.Succeed> parseNetworkResponse(NetworkResponse response) {
        // since we don't know which of the two underlying network vehicles
        // will Volley use, we have to handle and store session cookies manually
        AirportApp.getInstance().checkSessionCookie(response.headers);
        CookieGsonRequest.Succeed ret = new CookieGsonRequest.Succeed();
        return Response.success(ret, getCacheEntry());
    }

    @Override
    protected void deliverResponse(CookieGsonRequest.Succeed response) {
        mListener.onResponse(response);
    }

    private static String getLoginURL(Login login) {
        String url = Constants.getFunctionURL(Constants.FUNC_LOGIN);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.QUESTION);
        stringBuilder.append(USER_NAME).append(Constants.EQUAL).append(login.mUser);
        stringBuilder.append(Constants.AND);
        stringBuilder.append(USER_PASSWD).append(Constants.EQUAL).append(login.mPassword);
        stringBuilder.append(Constants.AND);
        stringBuilder.append(REMEMBER_ME).append(Constants.EQUAL).append(ON);
        url = url + stringBuilder.toString();
        L.i(Constants.URL_TAG, "LoginRequest URL is " + url);
        return url;
    }

    public static class Login {
        public Login(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        @SerializedName("user")
        String mUser;
        @SerializedName("password")
        String mPassword;
    }
}
