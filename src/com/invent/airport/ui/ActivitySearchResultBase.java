package com.invent.airport.ui;

import android.widget.TextView;

import com.invent.airport.R;
import com.invent.airport.requests.QueryRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ActivitySearchResultBase extends AirportBaseActivity {

    protected TextView mSearchResultTitle;
    protected int mQueryType;
    protected QueryRequest.Query mQuery;
    protected String mLocationBarcode;
    protected String mPreCheckInfoName;
    protected String mPreCheckInfoModel;

    protected void getSearchTypeAndSetTitle() {
        mSearchResultTitle = (TextView) findViewById(R.id.aty_search_result_title);
        mQueryType = getIntent().getIntExtra(ActivitySearch.KEY_QUERY_TYPE, ActivitySearch.QUERY_TYPE_ASSET_BARCODE);
        switch (mQueryType) {
            case ActivitySearch.QUERY_TYPE_ASSET_BARCODE:
            case ActivitySearch.QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO:
                mQuery = getIntent().getParcelableExtra(ActivitySearch.KEY_QUERY_INFO);
                mSearchResultTitle.setText(getString(R.string.barcode) + ": " + mQuery.mBarcode + " " + getString(R.string.search_result));
                break;
            case ActivitySearch.QUERY_TYPE_ASSET_INFO:
                mQuery = getIntent().getParcelableExtra(ActivitySearch.KEY_QUERY_INFO);
                String name = null;
                String model = null;
                try {
                    name = URLDecoder.decode(mQuery.mName, "UTF-8");
                    model = URLDecoder.decode(mQuery.mModel, "UTF-8");;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mSearchResultTitle.setText(getString(R.string.asset_name) + ": " + name + " " + getString(R.string.asset_model) + ": " + model + " " + getString(R.string.search_result));
                break;
            case ActivitySearch.QUERY_TYPE_LOCATION_BARCODE:
            case ActivitySearch.QUERY_TYPE_PRECHECKIN_BY_LOCATION:
                mLocationBarcode = getIntent().getStringExtra(ActivitySearch.KEY_QUERY_LOCATION_BARCODE);
                mSearchResultTitle.setText(getString(R.string.barcode) + ": " + mLocationBarcode + " " + getString(R.string.search_result));
                break;
            case ActivitySearch.QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL:
                mPreCheckInfoName = getIntent().getStringExtra(ActivitySearch.KEY_QUERY_NAME);
                mPreCheckInfoModel = getIntent().getStringExtra(ActivitySearch.KEY_QUERY_MODEL);
                if (mPreCheckInfoName == null)
                    mPreCheckInfoName = "";
                if (mPreCheckInfoModel == null)
                    mPreCheckInfoModel = "";
                String name1 = null;
                String model1 = null;
                try {
                    name1 = URLDecoder.decode(mPreCheckInfoName, "UTF-8");
                    model1 = URLDecoder.decode(mPreCheckInfoModel, "UTF-8");;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mSearchResultTitle.setText(getString(R.string.asset_name) + ": " + name1 + " " + getString(R.string.asset_model) + ": " + model1 + " " + getString(R.string.search_result));
                break;
        }
    }

}
