package com.invent.airport.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.RequestLocationQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Search location
 */
public final class ActivitySearchLocation extends AirportBaseActivity {
    Button mSearch;
    ImageButton mSearchIcon;
    EditText mBarcode;
    private static final int PAGE_SIZE = 100;
    ArrayList<RequestLocationQuery.LocationInfo> mLocationInfos =
            new ArrayList<RequestLocationQuery.LocationInfo>();
    public static final String LOCATION_INFOS = "location_infos";
    public static final String LOCATION_INFO = "location_info";

    @Override
    protected void onEnter() {
        initSearch();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_location);
        mSearch = (Button) findViewById(R.id.aty_search_search);
        mSearchIcon = (ImageButton) findViewById(R.id.button_search);
        mBarcode = (EditText) findViewById(R.id.aty_search_barcode);

        mBarcode.setOnKeyListener(mOnKeyListener);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSearch();
            }
        });
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSearch();
            }
        });
    }

    void initSearch() {
        mLocationInfos.clear();
        searchStarted();
        locationQuery(0);
    }

    Response.Listener<RequestLocationQuery.QueryLocationResult> mQueryLocationResultListener =
            new Response.Listener<RequestLocationQuery.QueryLocationResult>() {
                @Override
                public void onResponse(RequestLocationQuery.QueryLocationResult response) {
                    if (response.mLocations == null && mLocationInfos.isEmpty()) {
                        toastInfo("库位信息未找到");
                        return;
                    }
                    if (response.mLocations != null) {
                        mLocationInfos.addAll(response.mLocations);
                    }
                    if (response.mLocations != null
                            && response.mLocations.size() == PAGE_SIZE) {
                        // load more
                        locationQuery(response.mCurPageNum + 1);
                        return;
                    }
                    searchStopped();
                    if (!mLocationInfos.isEmpty()) {
                        toastInfo("找到" + mLocationInfos.size() + "个库位信息");
                        jumpToSearchResult();
                    }
                }
            };

    void searchStarted() {
        mSearch.setEnabled(false);
        mSearchIcon.setEnabled(false);
        mBarcode.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), "获取库位信息中");
    }

    void searchStopped() {
        mSearch.setEnabled(true);
        mSearchIcon.setEnabled(true);
        mBarcode.setEnabled(true);
        dismissProgressDialog();
    }

    void locationQuery(int page) {
        String keyword = mBarcode.getText().toString();
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8").replace(" ", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            toastInfo(e.toString());
            return;
        }
        RequestLocationQuery requestLocationQuery = new RequestLocationQuery(keyword,
                page, mQueryLocationResultListener, mErrorListener);
        mRequestQueue.add(requestLocationQuery);
    }

    private void jumpToSearchResult() {
        Intent i = new Intent(this, ActivitySearchLocationResult.class);
        i.putExtra(LOCATION_INFOS, mLocationInfos);
        startActivity(i);
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        searchStopped();
    }
}
