package com.invent.airport.ui;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.KeyEvent;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.intel.aware.utils.CrashLogger;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.GetServerIPRequest;
import com.invent.airport.requests.RequestGetUserDetail;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class AirportApp extends Application {
    private static AirportApp sApplication = null;
    public static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    //private static final String SESSION_COOKIE = "sessionid";
    private static final String JSESSION_COOKIE = "JSESSIONID";
    private static final String REMEMBER_ME_COOKIE = "grails_remember_me";
    public static final String PLUS = "&";
    private static final String TAG = "AirportApp";
    private RequestQueue mRequestQueue;
    private String mJsessionCookie;
    private String mRememberMeCookie;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageCache mImageCache;
    private final LruCache<String, Bitmap> mImageCacheLRU = new LruCache<String, Bitmap>(20);
    private SharedPreferences mSharedPreferences;
    private RequestGetUserDetail.User mUser;
    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    private BroadcastReceiver mReceiver = null;
    private final String mstrScreenOn = "android.intent.action.SCREEN_ON";
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    GetServerIPRequest mGetServerIPRequest = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashLogger(this));
        HttpURLConnection.setFollowRedirects(false);
        mSharedPreferences = getSharedPreferences("AirportSetting", MODE_PRIVATE);

        sApplication = this;
        mRequestQueue = Volley.newRequestQueue(this);

        mImageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                AirportApp.this.mImageCacheLRU.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return AirportApp.this.mImageCacheLRU.get(key);
            }
        };

        mImageLoader = new ImageLoader(mRequestQueue, mImageCache);
        // Start to update server ip periodically
        /*
        mScheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                startPeriodicalUpdate();
            }
        }, 10, mSharedPreferences.getInt(ServerConfig.KEY_UPDATE_PERIOD, Constants.PERIOD), TimeUnit.SECONDS);*/
        
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    
                        if (mstrScreenOn.equals(intent.getAction())) {
                            
                        } else {
                            
                    }
                }
            };
        }
        registerReceiver(mReceiver, mFilter);
    }

    /*
    private void startPeriodicalUpdate() {
        if (mGetServerIPRequest == null) {
            mGetServerIPRequest = new GetServerIPRequest(new Response.Listener<GetServerIPRequest.ServerIP>() {
                @Override
                public void onResponse(GetServerIPRequest.ServerIP response) {
                    Log.i(TAG, "Server IP is " + response.mIP);
                    saveAPIIP(response.mIP);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                }
            }
            );
        }
        mGetServerIPRequest.setShouldCache(false);
        mRequestQueue.add(mGetServerIPRequest);
    }*/

/*
    private void reSchedule() {
        mScheduledThreadPoolExecutor.shutdownNow();
        mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        mScheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                startPeriodicalUpdate();
            }
        }, 10, mSharedPreferences.getInt(ServerConfig.KEY_UPDATE_PERIOD, Constants.PERIOD), TimeUnit.SECONDS);
    }*/

    @Override
    public void onTerminate() {
        mScheduledThreadPoolExecutor.shutdown();
        super.onTerminate();
    }

    public void setUser(RequestGetUserDetail.User user) {
        mUser = user;
    }

    public RequestGetUserDetail.User getUser() {
        return mUser;
    }

    public String getHttp() {
        return mSharedPreferences.getString(ServerConfig.KEY_HTTP, Constants.HTTP);
    }

    public String getPort() {
        return mSharedPreferences.getString(ServerConfig.KEY_PORT, Constants.PORT);
    }

    public String getAPIIP() {
        return mSharedPreferences.getString(ServerConfig.KEY_UPDATE_SERVER_IP, Constants.UPDATE_SERVER_IP);
    }

    public String getRootPath() {
        return mSharedPreferences.getString(ServerConfig.KEY_ROOTPATH, Constants.ROOT_PATH);
    }

    public String getUpdateServerIP() {
        return mSharedPreferences.getString(ServerConfig.KEY_UPDATE_SERVER_IP, Constants.UPDATE_SERVER_IP);
    }

    public int getUpdatePeriod() {
        return mSharedPreferences.getInt(ServerConfig.KEY_UPDATE_PERIOD, Constants.PERIOD);
    }

    public void saveConfig(ServerConfig config) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        //int previousPeriod = mSharedPreferences.getInt(ServerConfig.KEY_UPDATE_PERIOD, Constants.PERIOD);
        editor.putString(ServerConfig.KEY_HTTP, config.mHTTPProtocal);
        editor.putString(ServerConfig.KEY_UPDATE_SERVER_IP, config.mUpdateServerIP);
        editor.putString(ServerConfig.KEY_PORT, config.mPort);
        editor.putString(ServerConfig.KEY_ROOTPATH, config.mRootPath);
        editor.putInt(ServerConfig.KEY_UPDATE_PERIOD, config.mPeriod);
        editor.commit();
       /* if (previousPeriod != config.mPeriod) {
            reSchedule();
        }*/
    }

    public void saveAPIIP(String ip) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ServerConfig.KEY_API_SERVER_IP, ip);
        editor.commit();
    }

    /**
     * @return instance of aware hub application
     */
    public static AirportApp getInstance() {
        return sApplication;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public ImageLoader.ImageCache getImageCache() {
        return mImageCache;
    }

    public LruCache<String, Bitmap> getImageCacheLRU() {
        return mImageCacheLRU;
    }

    // workaround
    public void removeCache(String url) {
        for (int i = 0; i < 400; i++) {
            String key = ImageLoader.getCacheKey(url, i, i);
            mImageCacheLRU.remove(key);
        }
    }

    public int getUserPrivilege() {
        if (mUser != null) {
            return mUser.mGroupId;
        } else {
            return Constants.GROUP_WORKER;
        }
    }

    public static boolean hardwareEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
    }

    public static final class ServerConfig {
        public static final String KEY_HTTP = "Http";
        public static final String KEY_UPDATE_SERVER_IP = "Update_Server_IP";
        public static final String KEY_PORT = "Port";
        public static final String KEY_ROOTPATH = "RootPath";
        public static final String KEY_UPDATE_PERIOD = "Period";
        public static final String KEY_API_SERVER_IP = "API_Server_IP";
        public String mHTTPProtocal;
        public String mUpdateServerIP;
        public String mPort;
        public String mRootPath;
        public int mPeriod;
    }

    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     *
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        mJsessionCookie = null;
        mRememberMeCookie = null;
        if (headers.containsKey(SET_COOKIE_KEY)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                String[] splitCookies = cookie.split(PLUS);

                for (String potentialCookie : splitCookies) {
                    String[] splitCookie = potentialCookie.split(";");
                    for (String JCookie : splitCookie) {
                        if (JCookie.contains("=")) {
                            String[] sessionID = JCookie.split("=");
                            if (sessionID.length == 2) {
                                if (sessionID[0].equals(JSESSION_COOKIE)) {
                                    mJsessionCookie = sessionID[1];
                                } else if (sessionID[0].equals(REMEMBER_ME_COOKIE)) {
                                    mRememberMeCookie = sessionID[1];
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds session cookie to headers if exists.
     *
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers) {
        if (mJsessionCookie != null && mJsessionCookie.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(JSESSION_COOKIE);
            builder.append("=");
            builder.append(mJsessionCookie);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
        if (mRememberMeCookie != null && mRememberMeCookie.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(REMEMBER_ME_COOKIE);
            builder.append("=");
            builder.append(mRememberMeCookie);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
        Log.i(TAG, "Final Cookie is " + headers.get(COOKIE_KEY));
    }
}

