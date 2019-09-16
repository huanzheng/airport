package com.invent.airport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestPreCheckInsByLocationBarcode;
import com.invent.airport.requests.RequestPreCheckInsByNameModel;

import java.util.ArrayList;

public final class ActivityPreCheckInBigInfoSearchResult extends ActivitySearchResultBase {
    ListView mListView;
    BaseAdapter mFinalAdapter;

    ArrayList<CookieGsonRequest.ComponentInfo> mData = new ArrayList<CookieGsonRequest.ComponentInfo>();
    boolean mIsLoading;
    boolean mMoreDataToLoad;
    int mCurPageNum;

    String mControlledType;
    String mNoneControlledType;
    private static final String TAG = "PreCheckInSearchResult";

    private Response.Listener<CookieGsonRequest.QueryPreCheckInBigInfoResult> mQueryResultListener = new Response.Listener<CookieGsonRequest.QueryPreCheckInBigInfoResult>() {
        @Override
        public void onResponse(CookieGsonRequest.QueryPreCheckInBigInfoResult response) {
            Log.i(TAG, "load finished, current page number is " + response.mCurPageNum);
            if (response.mCurPage != null) {
                Log.i(TAG, "response contained " + response.mCurPage.size() + " results");
            }
            if (response.mCurPage != null && response.mCurPage.size() == Constants.QUERY_PAGE_SIZE) {
                Log.i(TAG, "More data to load");
                mMoreDataToLoad = true;
            } else {
                Log.i(TAG, "NO More data to load");
                mMoreDataToLoad = false;
            }
            mCurPageNum = response.mCurPageNum;
            addData(response.mCurPage);
        }
    };

    private void addData(ArrayList<CookieGsonRequest.PreCheckInBigInfo> newData) {
        loadingEnd();
        if (newData != null && newData.size() > 0) {
            for(CookieGsonRequest.PreCheckInBigInfo preCHeckInBigInfo : newData) {
                CookieGsonRequest.ComponentInfo componentInfo = new CookieGsonRequest.ComponentInfo();
                preCHeckInBigInfo.fillComponentInfo(componentInfo);
                mData.add(componentInfo);
            }
            mFinalAdapter.notifyDataSetChanged();
        }
    }

    private boolean shouldLoadMoreData(ArrayList<CookieGsonRequest.ComponentInfo> data, int position) {
        // If showing the last set of data, request for the next set of data
        boolean scrollRangeReached = (position > (data.size() - Constants.QUERY_PAGE_SIZE + 5));
        boolean ret = (scrollRangeReached && !mIsLoading && mMoreDataToLoad);
        if (ret) {
            Log.i(TAG, "Determined to load more data, list view position is now " + position);
        }
        return ret;
    }

    private void loadMoreData(int pageNum) {
        Log.i(TAG, "Start to load more data, page number is " + pageNum);
        mCurPageNum = pageNum;
        switch (mQueryType) {
            case ActivitySearch.QUERY_TYPE_PRECHECKIN_BY_LOCATION:
                RequestPreCheckInsByLocationBarcode requestPreCheckInsByLocationBarcode = new RequestPreCheckInsByLocationBarcode(
                        mLocationBarcode, mCurPageNum, mQueryResultListener, mErrorListener);
                loadingStart();
                mRequestQueue.add(requestPreCheckInsByLocationBarcode);
                break;
            case ActivitySearch.QUERY_TYPE_PRECHECKIN_BY_NAME_MODEL:
                RequestPreCheckInsByNameModel requestPreCheckInsByNameModel = new RequestPreCheckInsByNameModel(mPreCheckInfoName,
                        mPreCheckInfoModel, mCurPageNum, mQueryResultListener, mErrorListener);
                loadingStart();
                mRequestQueue.add(requestPreCheckInsByNameModel);
                break;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_result);
        getSearchTypeAndSetTitle();

        mControlledType = getString(R.string.controlled_asset);
        mNoneControlledType = getString(R.string.none_controlled_asset);

        mListView = (ListView) findViewById(R.id.aty_search_result_list);

        //if (mOP != Constants.OP_NONE && mOP != Constants.OP_CHONGHONG) {
            // OP_NONE has four choices, OP_CHONGHONG has two choices
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(i);
                    Intent intent;
                    intent = new Intent(mContext, ActivityPreCheckInOutNew.class);
                    intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    intent.putExtra(ActivityPreCheckInOutNew.KEY_PRECHECK_OP, ActivityPreCheckInOutNew.PRECHECKOUT_NEW);
                    startActivity(intent);
                }
            });
       // }
    }

    void loadingStart() {
        mIsLoading = true;
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.loading));
    }

    void loadingEnd() {
        mIsLoading = false;
        dismissProgressDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadAllInfo();
    }

    void reloadAllInfo() {
        mFinalAdapter = new SearchResultNoButtonsAdapter();
        mData.clear();
        mListView.setAdapter(mFinalAdapter);
        loadMoreData(1);
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        loadingEnd();
    }

    protected void viewHolderAssetInfoFindIDs(ViewHolderAssetInfo viewHolder, View v) {
        viewHolder.mNetworkImageView = (NetworkImageView) v.findViewById(R.id.ast_info_image);
        viewHolder.mBarcode = (EditText) v.findViewById(R.id.ast_info_barcode);
        viewHolder.mName = (EditText) v.findViewById(R.id.ast_info_name);
        viewHolder.mModel = (EditText) v.findViewById(R.id.ast_info_model);
        viewHolder.mQuantity = (EditText) v.findViewById(R.id.ast_info_quantity);
        viewHolder.mLocationBarcode = (EditText) v.findViewById(R.id.ast_info_location_barcode);
        viewHolder.mLocationName = (EditText) v.findViewById(R.id.ast_info_location_name);
        viewHolder.mControlType = (EditText) v.findViewById(R.id.ast_info_control_type);
        viewHolder.mBarcode.setKeyListener(null);
        viewHolder.mName.setKeyListener(null);
        viewHolder.mModel.setKeyListener(null);
        viewHolder.mQuantity.setKeyListener(null);
        viewHolder.mLocationBarcode.setKeyListener(null);
        viewHolder.mLocationName.setKeyListener(null);
        viewHolder.mControlType.setKeyListener(null);
    }

    protected void viewHolderAssetInfoSetInfo(ViewHolderAssetInfo viewHolder, CookieGsonRequest.ComponentInfo assetInfo) {
        if (assetInfo.mPhotoURL != null && !assetInfo.mPhotoURL.isEmpty()) {
            Log.i(TAG, "Relative Photo url of barcode " + assetInfo.mBarcode + " is " + assetInfo.mPhotoURL);
            viewHolder.mNetworkImageView.setImageUrl(Constants.getImageURL(assetInfo.mPhotoURL), mImageLoader);
        }
        viewHolder.mBarcode.setText(assetInfo.mBarcode);
        viewHolder.mName.setText(assetInfo.mName);
        viewHolder.mModel.setText(assetInfo.mModel);
        if (assetInfo.mTotalAvailable != Constants.INVALID_QUANT) {
            viewHolder.mQuantity.setText("" + assetInfo.mQuantIn + "/" + assetInfo.mTotalAvailable);
        } else {
            viewHolder.mQuantity.setText("");
        }
        viewHolder.mLocationBarcode.setText(assetInfo.mLocationBarcode);
        viewHolder.mLocationName.setText(assetInfo.mLocation);
        if (assetInfo.mType != null && assetInfo.mType == Constants.ASSET_TYPE_CONTROLLED) {
            viewHolder.mControlType.setText(mControlledType);
        } else {
            viewHolder.mControlType.setText(mNoneControlledType);
        }
    }

    private final class SearchResultNoButtonsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolderAssetInfo viewHolder;

            //check to see if we need to load more data
            if (shouldLoadMoreData(mData, position)) {
                loadMoreData(mCurPageNum + 1);
            }

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_asset_info, null);
                viewHolder = new ViewHolderAssetInfo();
                viewHolderAssetInfoFindIDs(viewHolder, v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderAssetInfo) v.getTag();
            }
            CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
            viewHolderAssetInfoSetInfo(viewHolder, assetInfo);
            return v;
        }
    }

    public class ViewHolderAssetInfo {
        public NetworkImageView mNetworkImageView;
        public EditText mBarcode;
        public EditText mName;
        public EditText mModel;
        public EditText mQuantity;
        public EditText mLocationBarcode;
        public EditText mLocationName;
        public EditText mControlType;
    }
}
