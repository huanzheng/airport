package com.invent.airport.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.AirportError;
import com.invent.airport.requests.QueryRequest;
import com.invent.airport.requests.RequestAssetsByLocationBarcode;
import com.invent.airport.requests.RequestPreCheckInsByLocationBarcode;
import com.invent.airport.requests.RequestPreCheckInsByNameModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class ActivitySearch extends AirportBaseActivity {
    Button mSearch;
    Button mCheckInNew;
    ImageButton mSearchIcon;
    EditText mAssetName;
    EditText mAssetModel;
    EditText mBarcode;
    Button mBackToMain;

    public static final int QUERY_TYPE_ASSET_BARCODE = 1;
    public static final int QUERY_TYPE_ASSET_INFO = 2;
    public static final int QUERY_TYPE_LOCATION_BARCODE = 3;
    public static final int QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO = 4;
    public static final int QUERY_TYPE_PRECHECKIN_BY_LOCATION = 5;
    public static final int QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL = 6;

    public static final String KEY_QUERY_TYPE = "key_query_type";
    public static final String KEY_QUERY_INFO = "key_query_info";
    public static final String KEY_QUERY_LOCATION_BARCODE = "key_query_location_barcode";
    public static final String KEY_QUERY_NAME = "key_query_name";
    public static final String KEY_QUERY_MODEL = "key_query_model";

    // As for query type, firstly will search by asset barcode, if found, then query type is asset barcode
    // then will search by location barcode, if found, then query type is location barcode
    // then will search by asset info, if found, then query type is asset info
    // finally if nothing found, will enter search result nothing
    private int mQueryType = -1;
    private String mLocationBarcodeInSearch;
    private QueryRequest.Query mQueryInSearch;

    @Override
    protected void onEnter() {
        startSearch();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search);
        mAssetName = (EditText) findViewById(R.id.aty_search_asset_name);
        mAssetModel = (EditText) findViewById(R.id.aty_search_asset_model);
        mSearch = (Button) findViewById(R.id.aty_search_search);
        mCheckInNew = (Button) findViewById(R.id.aty_search_checkin_new);
        mSearchIcon = (ImageButton) findViewById(R.id.button_search);
        mBarcode = (EditText) findViewById(R.id.aty_search_barcode);
        mBarcode.setOnKeyListener(mOnKeyListener);
        mAssetName.setOnKeyListener(mOnKeyListener);
        mAssetModel.setOnKeyListener(mOnKeyListener);
        mBackToMain = (Button) findViewById(R.id.back_to_main);

        //mAssetName.setText("传动");
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });
        
        mBackToMain.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
        	
        });

        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });

        mCheckInNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivityPreCheckInOutNew.class);
                // only shown when pre check in
                i.putExtra(ActivityPreCheckInOutNew.KEY_PRECHECK_OP, ActivityPreCheckInOuts.PRE_CHECK_INS);
                mContext.startActivity(i);
                finish();
            }
        });
        if (mOP == Constants.OP_PRECHECKIN) {
            mCheckInNew.setVisibility(View.VISIBLE);
        }
    }

    void startSearch() {
        String barcode = mBarcode.getText().toString();
        String assetName = mAssetName.getText().toString();
        String assetModel = mAssetModel.getText().toString();
        try {
            barcode = URLEncoder.encode(barcode, "UTF-8").replace(" ", "%20");
            assetName = URLEncoder.encode(assetName, "UTF-8").replace(" ", "%20");
            assetModel = URLEncoder.encode(assetModel, "UTF-8").replace(" ", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            toastInfo(e.toString());
            return;
        }

        if (barcode.isEmpty() && assetName.isEmpty() && assetModel.isEmpty()) {
            toastInfo(getString(R.string.pls_input_at_least_one_info));
            return;
        }
        if (!barcode.isEmpty()) {
            searchByAssetBarcode(barcode);
        } else {
            if (assetName.isEmpty() && assetModel.isEmpty()) {
                toastInfo("需输入备件名称或者备件型号");
                return;
            } else {
                searchByAssetInfo(assetName, assetModel);
            }
        }
    }

    void searchByAssetBarcodeForPureAssetInfo(String assetBarcode) {
        mToastAirportError = false;
        mQueryType = QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO;
        QueryRequest.Query query = new QueryRequest.Query();
        query.mQueryType = QueryRequest.QUERY_WITH_BARCODE_PURE_ASSET_INFO;
        query.mBarcode = assetBarcode;
        query.mPage = 1;
        mQueryInSearch = query;

        QueryRequest queryRequest = new QueryRequest(query, new Response.Listener<QueryRequest.QueryResult>() {
            @Override
            public void onResponse(QueryRequest.QueryResult response) {
                // Successfully found asset with asset barcode, let's jump to SearchResult
                dismissProgressDialog();
                jumpToSearchResult(mQueryType, mQueryInSearch, null);
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), getString(R.string.asset_barcode) + " " + assetBarcode + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(queryRequest);
    }

    void searchByAssetBarcode(String assetBarcode) {
        mToastAirportError = false;
        mQueryType = QUERY_TYPE_ASSET_BARCODE;
        QueryRequest.Query query = new QueryRequest.Query();
        query.mQueryType = QueryRequest.QUERY_WITH_BARCODE;
        query.mBarcode = assetBarcode;
        query.mPage = 1;
        mQueryInSearch = query;

        QueryRequest queryRequest = new QueryRequest(query, new Response.Listener<QueryRequest.QueryResult>() {
            @Override
            public void onResponse(QueryRequest.QueryResult response) {
                // Successfully found asset with asset barcode, let's jump to SearchResult
                dismissProgressDialog();
                jumpToSearchResult(mQueryType, mQueryInSearch, null);
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), getString(R.string.asset_barcode) + " " + assetBarcode + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(queryRequest);
    }

    void searchByLocationBarcode(String locationBarcode) {
        mToastAirportError = false;
        mQueryType = QUERY_TYPE_LOCATION_BARCODE;
        mLocationBarcodeInSearch = locationBarcode;
        RequestAssetsByLocationBarcode requestAssetsByLocationBarcode = new RequestAssetsByLocationBarcode(locationBarcode, 1, new Response.Listener<RequestAssetsByLocationBarcode.QueryLocationResult>() {
            @Override
            public void onResponse(RequestAssetsByLocationBarcode.QueryLocationResult response) {
                // Successfully found asset with location barcode, let's jump to searchResult
                dismissProgressDialog();
                mSearch.setEnabled(true);
                jumpToSearchResult(mQueryType, null, mLocationBarcodeInSearch);
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), getString(R.string.location_barcode) + locationBarcode + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(requestAssetsByLocationBarcode);
    }

    void searchByAssetInfo(String assetName, String assetModel) {
        if (mOP != Constants.OP_PRECHECKOUT)
            mToastAirportError = true;
        else
            mToastAirportError = false;
        mQueryType = QUERY_TYPE_ASSET_INFO;
        QueryRequest.Query query = new QueryRequest.Query();
        query.mQueryType = QueryRequest.QUERY_WITHOUT_BARCODE;
        query.mName = assetName;
        query.mModel = assetModel;
        query.mPage = 1;
        mQueryInSearch = query;
        QueryRequest queryRequest = new QueryRequest(query, new Response.Listener<QueryRequest.QueryResult>() {
            @Override
            public void onResponse(QueryRequest.QueryResult response) {
                dismissProgressDialog();
                mSearch.setEnabled(true);
                jumpToSearchResult(mQueryType, mQueryInSearch, null);
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), "备件信息 " + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(queryRequest);
    }

    void searchPreCheckInsByLocationBarcode(String locationBarcode) {
        mToastAirportError = true;
        mQueryType = QUERY_TYPE_PRECHECKIN_BY_LOCATION;
        mLocationBarcodeInSearch = locationBarcode;

        RequestPreCheckInsByLocationBarcode requestPreCheckInsByLocationBarcode = new RequestPreCheckInsByLocationBarcode(locationBarcode, 1, new Response.Listener<RequestPreCheckInsByLocationBarcode.QueryPreCheckInBigInfoResult>() {
            @Override
            public void onResponse(RequestPreCheckInsByLocationBarcode.QueryPreCheckInBigInfoResult response) {
                dismissProgressDialog();
                mSearch.setEnabled(true);
                //jumpToSearchResult(mQueryType, null, mLocationBarcodeInSearch);
                jumpToPreCheckInBigInfoResult(QUERY_TYPE_PRECHECKIN_BY_LOCATION, mLocationBarcodeInSearch, "", "");
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), getString(R.string.location_barcode) + locationBarcode + "预入库记录" + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(requestPreCheckInsByLocationBarcode);
    }

    void searchPreCheckInsByNameModel(String name, String model) {
        mToastAirportError = true;
        mQueryType = QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL;
        //mLocationBarcodeInSearch = locationBarcode;

        RequestPreCheckInsByNameModel requestPreCheckInsByNameModel = new RequestPreCheckInsByNameModel(name, model, 1, new Response.Listener<RequestPreCheckInsByNameModel.QueryPreCheckInBigInfoResult>() {
            @Override
            public void onResponse(RequestPreCheckInsByNameModel.QueryPreCheckInBigInfoResult response) {
                dismissProgressDialog();
                mSearch.setEnabled(true);
                //jumpToSearchResult(mQueryType, null, mLocationBarcodeInSearch);
                jumpToPreCheckInBigInfoResult(QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL, "", mQueryInSearch.mName, mQueryInSearch.mModel);
            }
        }, mErrorListener);
        showProgressDialog(getString(R.string.searching), "预入库记录" + getString(R.string.searching));
        mSearch.setEnabled(false);
        mRequestQueue.add(requestPreCheckInsByNameModel);
    }

    private void jumpToSearchResult(int queryType, QueryRequest.Query query, String locationbarcode) {
        jumpToResult(queryType, query, locationbarcode, ActivitySearchResult.class);
    }

    private void jumpToSearchNoResult(int queryType, QueryRequest.Query query, String locationbarcode) {
        jumpToResult(queryType, query, locationbarcode, ActivitySearchNoResult.class);
    }

    @SuppressWarnings("rawtypes")
    private void jumpToResult(int queryType, QueryRequest.Query query, String locationbarcode, Class resultClass) {
        Intent i = new Intent(mContext, resultClass);
        i.putExtra(KEY_QUERY_TYPE, queryType);
        switch (queryType) {
            case QUERY_TYPE_ASSET_BARCODE:
            case QUERY_TYPE_ASSET_INFO:
            case QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO:
                i.putExtra(KEY_QUERY_INFO, query);
                break;
            case QUERY_TYPE_LOCATION_BARCODE:
                i.putExtra(KEY_QUERY_LOCATION_BARCODE, locationbarcode);
                break;
        }
        startActivityWithOP(mContext, i, mOP);
    }

    private void jumpToPreCheckInBigInfoResult(int queryType, String locationBarcode, String name, String model) {
        Intent i = new Intent(mContext, ActivityPreCheckInBigInfoSearchResult.class);
        i.putExtra(KEY_QUERY_TYPE, queryType);
        switch (queryType) {
            case QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL:
                i.putExtra(KEY_QUERY_NAME, name);
                i.putExtra(KEY_QUERY_MODEL, model);
                break;
            case QUERY_TYPE_PRECHECKIN_BY_LOCATION:
                i.putExtra(KEY_QUERY_LOCATION_BARCODE, locationBarcode);
                break;
        }
        startActivityWithOP(mContext, i, mOP);
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        mSearch.setEnabled(true);
        if (error instanceof AirportError) {
            {
                // found nothing
                switch (mQueryType) {
                    case QUERY_TYPE_ASSET_BARCODE:
                        // continue try to search with location barcdoe
                        if (((AirportError) error).mErrorCode == Constants.ERR_ASSET_NOT_FOUND) {
                            searchByLocationBarcode(mQueryInSearch.mBarcode);
                        }
                        break;
                    case QUERY_TYPE_LOCATION_BARCODE:
                        // continue try to search with asset barcode for pure asset info
                        if (((AirportError) error).mErrorCode == Constants.ERR_ASSET_NOT_FOUND
                                || ((AirportError) error).mErrorCode == Constants.ERR_INVALID_LOCATION_BARCODE) {
                            searchByAssetBarcodeForPureAssetInfo(mQueryInSearch.mBarcode);
                        }
                        break;
                    case QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO:
                        // if it is precheckout, final resort is here
                        if (mOP == Constants.OP_PRECHECKOUT) {
                            searchPreCheckInsByLocationBarcode(mQueryInSearch.mBarcode);
                        } else {
                            // stop search, found nothing
                            jumpToSearchNoResult(mQueryType, mQueryInSearch, mLocationBarcodeInSearch);
                        }
                        break;
                    case QUERY_TYPE_ASSET_INFO:
                        // if it is precheckout, final resort is here
                        if (mOP == Constants.OP_PRECHECKOUT) {
                            searchPreCheckInsByNameModel(mQueryInSearch.mName, mQueryInSearch.mModel);
                        } else {
                            // stop search, found nothing
                            jumpToSearchNoResult(mQueryType, mQueryInSearch, mLocationBarcodeInSearch);
                        }
                        break;
                    case QUERY_TYPE_PRECHECKIN_BY_LOCATION:
                    case QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL:
                        // already in PRECHECKOUT mode, found nothing
                        jumpToSearchNoResult(mQueryType, mQueryInSearch, mLocationBarcodeInSearch);
                        break;

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_printbarcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_printbarcode:
                String astName = mAssetName.getText().toString();
                String barcode = mBarcode.getText().toString();
                if (barcode.isEmpty() && astName.isEmpty()) {
                    toastInfo("请至少输入条码或者备件名称");
                    return true;
                }
                Intent i = new Intent(mContext, ActivityPrint.class);
                i.putExtra(ActivityPrint.KEY_PRINT_AST_BARCODE, barcode);
                i.putExtra(ActivityPrint.KEY_PRINT_AST_NAME, astName);
                mContext.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
