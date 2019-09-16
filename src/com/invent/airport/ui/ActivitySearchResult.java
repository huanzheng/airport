package com.invent.airport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.QueryRequest;
import com.invent.airport.requests.RequestAssetsByLocationBarcode;
import com.invent.airport.requests.RequestLocationQuery;
import com.invent.airport.ui.ActivitySearchLocationResult.ViewHodlerLocationInfo;

import java.util.ArrayList;

public final class ActivitySearchResult extends ActivitySearchResultBase {
    ListView mListView;
    //private static final String TAG = "ActivitySearchResult";
    //SearchResultNoButtonsAdapter mSearchResultAdapter = new SearchResultNoButtonsAdapter();
    //SearchResultWithButtonsAdapter mSearchResultWithButtonsAdapter = new SearchResultWithButtonsAdapter();
    BaseAdapter mFinalAdapter;

    ArrayList<CookieGsonRequest.ComponentInfo> mData = new ArrayList<CookieGsonRequest.ComponentInfo>();
    boolean mIsLoading;
    boolean mMoreDataToLoad;
    int mCurPageNum;

    String mControlledType;
    String mNoneControlledType;
    private static final String TAG = "SearchResult";
    private static final int MENU_PRINT = 1;

    private Response.Listener<QueryRequest.QueryResult> mQueryResultListener = new Response.Listener<QueryRequest.QueryResult>() {
        @Override
        public void onResponse(QueryRequest.QueryResult response) {
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
    private Response.Listener<RequestAssetsByLocationBarcode.QueryLocationResult> mQueryLocationResultListener = new Response.Listener<RequestAssetsByLocationBarcode.QueryLocationResult>() {
        @Override
        public void onResponse(RequestAssetsByLocationBarcode.QueryLocationResult response) {
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

    private void addData(ArrayList<CookieGsonRequest.ComponentInfo> newData) {
        loadingEnd();
        if (newData != null && newData.size() > 0) {
            mData.addAll(newData);
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
            case ActivitySearch.QUERY_TYPE_ASSET_BARCODE:
            case ActivitySearch.QUERY_TYPE_ASSET_INFO:
            case ActivitySearch.QUERY_TYPE_ASSET_BARCODE_PURE_ASSET_INFO:
                // Search with incoming query, just change page number
                mQuery.mPage = pageNum;
                QueryRequest queryRequest = new QueryRequest(mQuery, mQueryResultListener, mErrorListener);
                loadingStart();
                mRequestQueue.add(queryRequest);
                break;
            case ActivitySearch.QUERY_TYPE_LOCATION_BARCODE:
                RequestAssetsByLocationBarcode requestAssetsByLocationBarcode = new RequestAssetsByLocationBarcode(mLocationBarcode,
                        pageNum, mQueryLocationResultListener, mErrorListener);
                loadingStart();
                mRequestQueue.add(requestAssetsByLocationBarcode);
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
        registerForContextMenu(mListView);
        if (mOP != Constants.OP_NONE && mOP != Constants.OP_CHONGHONG) {
            // OP_NONE has four choices, OP_CHONGHONG has two choices
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(i);
                    Intent intent;
                    switch (mOP) {
                        case Constants.OP_CHECKIN:
                            intent = new Intent(mContext, ActivityCheckInWithBarcode.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);
                            break;
                        case Constants.OP_CHECKOUT:
                            intent = new Intent(mContext, ActivityCheckOut.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);
                            break;
                        case Constants.OP_MOVE:
                            intent = new Intent(mContext, ActivityMove.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);
                            break;
                        case Constants.OP_PRECHECKIN:
                            intent = new Intent(mContext, ActivityPreCheckIn.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);
                            break;
                        case Constants.OP_PRECHECKOUT:
                            /*
                            intent = new Intent(mContext, ActivityPreCheckOut.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);*/
                            intent = new Intent(mContext, ActivityPreCheckInOutNew.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            intent.putExtra(ActivityPreCheckInOutNew.KEY_PRECHECK_OP, ActivityPreCheckInOutNew.PRECHECKOUT_NEW);
                            startActivity(intent);
                            break;
                        case Constants.OP_CREATE_CHECKOUT_TASK:
                            intent = new Intent(mContext, ActivityCreateCheckOutTask.class);
                            intent.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                            startActivityWithOP(mContext, intent, mOP);
                            break;
                    }
                }
            });
        }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("打印");
        menu.add(0, MENU_PRINT, 1, "打印此备件条码");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        ViewHolderAssetInfo holder = (ViewHolderAssetInfo) info.targetView.getTag();
        String assetName = holder.mName.getText().toString();
        String barCode = holder.mBarcode.getText().toString();
        switch (item.getItemId()) {
            case MENU_PRINT:
                Intent i = new Intent(this, ActivityPrint.class);
                
                i.putExtra(ActivityPrint.KEY_PRINT_AST_BARCODE, barCode);
                i.putExtra(ActivityPrint.KEY_PRINT_AST_NAME, assetName);
                mContext.startActivity(i);

                break;
        }
        return true;
    }
    
    void reloadAllInfo() {
        if (mOP == Constants.OP_NONE) {
            mFinalAdapter = new SearchResultWithButtonsAdapter();
        } else if (mOP == Constants.OP_CHONGHONG) {
            mFinalAdapter = new SearchResultWithChongHongButtonsAdapter();
        } else {
            mFinalAdapter = new SearchResultNoButtonsAdapter();
        }
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
            viewHolder.mQuantity.setText("" + assetInfo.mQuantIn);
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

    private final class SearchResultWithButtonsAdapter extends BaseAdapter {

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

        private int getItemPositionOfButton(View v) {
            PositionTag positionTag = (PositionTag) v.getTag();
            if (positionTag != null) {
                Log.i(TAG, "Position of button is " + positionTag.mPosition);
                return positionTag.mPosition;
            } else {
                Log.i(TAG, "Position of button is -1!!!!");
                return -1;
            }
        }

        private void setItemPositionOfButton(View v, int position) {
            PositionTag positionTag = new PositionTag();
            positionTag.mPosition = position;
            Log.i(TAG, "Set position of button to " + position);
            v.setTag(positionTag);
        }

        View.OnClickListener mCheckInListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityCheckInWithBarcode.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        View.OnClickListener mPreCheckInListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityPreCheckIn.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        View.OnClickListener mCheckOutListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityCheckOut.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        View.OnClickListener mPreCheckOutListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityPreCheckOut.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolderAssetInfoWithButtons viewHolder;

            //check to see if we need to load more data
            if (shouldLoadMoreData(mData, position)) {
                loadMoreData(mCurPageNum + 1);
            }

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_asset_info_with_buttons, null);
                viewHolder = new ViewHolderAssetInfoWithButtons();
                viewHolderAssetInfoFindIDs(viewHolder, v);
                // Continue with buttons
                viewHolder.mCheckIn = (Button) v.findViewById(R.id.list_item_check_in);
               // viewHolder.mCheckOut = (Button) v.findViewById(R.id.list_item_check_out);
                viewHolder.mPreCheckIn = (Button) v.findViewById(R.id.list_item_pre_checkin);
                viewHolder.mPreCheckOut = (Button) v.findViewById(R.id.list_item_pre_checkout);
                /*setItemPositionOfButton(viewHolder.mCheckIn, position);
                setItemPositionOfButton(viewHolder.mCheckOut, position);
                setItemPositionOfButton(viewHolder.mPreCheckIn, position);
                setItemPositionOfButton(viewHolder.mPreCheckOut, position);*/

                viewHolder.mCheckIn.setOnClickListener(mCheckInListener);
               // viewHolder.mCheckOut.setOnClickListener(mCheckOutListener);
                viewHolder.mPreCheckIn.setOnClickListener(mPreCheckInListener);
                viewHolder.mPreCheckOut.setOnClickListener(mPreCheckOutListener);
                setDisableEnableWithRole(AirportApp.getInstance().getUserPrivilege(), viewHolder);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderAssetInfoWithButtons) v.getTag();
            }
            CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
            setItemPositionOfButton(viewHolder.mCheckIn, position);
            //setItemPositionOfButton(viewHolder.mCheckOut, position);
            setItemPositionOfButton(viewHolder.mPreCheckIn, position);
            setItemPositionOfButton(viewHolder.mPreCheckOut, position);
            viewHolderAssetInfoSetInfo(viewHolder, assetInfo);
            return v;
        }

        private void setDisableEnableWithRole(int userType, ViewHolderAssetInfoWithButtons viewHolder) {
            viewHolder.mCheckIn.setEnabled(false);
            //viewHolder.mCheckOut.setEnabled(false);
            viewHolder.mPreCheckIn.setEnabled(false);
            viewHolder.mPreCheckOut.setEnabled(false);

            switch (userType) {
                case Constants.GROUP_REPO_MANGER:
                    viewHolder.mCheckIn.setEnabled(true);
                    //viewHolder.mCheckOut.setEnabled(true);
                    // fall through
                case Constants.GROUP_LEADER:
                case Constants.GROUP_WORKER:
                    viewHolder.mPreCheckOut.setEnabled(true);
                    viewHolder.mPreCheckIn.setEnabled(true);
                    break;
            }
        }

        private class PositionTag {
            int mPosition;
        }
    }

    private final class SearchResultWithChongHongButtonsAdapter extends BaseAdapter {

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

        private int getItemPositionOfButton(View v) {
            PositionTag positionTag = (PositionTag) v.getTag();
            if (positionTag != null) {
                Log.i(TAG, "getItemPositionOfButton " + positionTag.mPosition);
                return positionTag.mPosition;
            } else {
                Log.i(TAG, "getItemPositionOfButton is -1!!!");
                return -1;
            }
        }

        private void setItemPositionOfButton(View v, int position) {
            PositionTag positionTag = new PositionTag();
            positionTag.mPosition = position;
            Log.i(TAG, "setItemPositionOfButton " + position);
            v.setTag(positionTag);
        }

        View.OnClickListener mCheckInChongHongListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityChongHong.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    i.putExtra(ActivityChongHong.KEY_CHONGHONG_TYPE, ActivityChongHong.CHECK_IN_CHONGHONG);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        View.OnClickListener mCheckOutChongHongListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
                    Intent i = new Intent(mContext, ActivityChongHong.class);
                    i.putExtra(Constants.KEY_ASSET_INFO, assetInfo);
                    i.putExtra(ActivityChongHong.KEY_CHONGHONG_TYPE, ActivityChongHong.CHECK_OUT_CHONGHONG);
                    startActivityWithOP(mContext, i, mOP);
                }
            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolderAssetInfoWithChongHongButtons viewHolder;

            //check to see if we need to load more data
            if (shouldLoadMoreData(mData, position)) {
                loadMoreData(mCurPageNum + 1);
            }

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_asset_info_with_chonghong_buttons, null);
                viewHolder = new ViewHolderAssetInfoWithChongHongButtons();
                viewHolderAssetInfoFindIDs(viewHolder, v);
                // Continue with buttons
                viewHolder.mCheckInChongHong = (Button) v.findViewById(R.id.list_item_checkin_chonghong);
                viewHolder.mCheckOutChongHong = (Button) v.findViewById(R.id.list_item_checkout_chonghong);

                //setItemPositionOfButton(viewHolder.mCheckInChongHong, position);
                //setItemPositionOfButton(viewHolder.mCheckOutChongHong, position);

                viewHolder.mCheckInChongHong.setOnClickListener(mCheckInChongHongListener);
                viewHolder.mCheckOutChongHong.setOnClickListener(mCheckOutChongHongListener);

                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderAssetInfoWithChongHongButtons) v.getTag();
            }
            CookieGsonRequest.ComponentInfo assetInfo = mData.get(position);
            setItemPositionOfButton(viewHolder.mCheckInChongHong, position);
            setItemPositionOfButton(viewHolder.mCheckOutChongHong, position);
            viewHolderAssetInfoSetInfo(viewHolder, assetInfo);
            return v;
        }

        private class PositionTag {
            int mPosition;
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

    public class ViewHolderAssetInfoWithButtons extends ViewHolderAssetInfo {
        public Button mCheckIn;
        //public Button mCheckOut;
        public Button mPreCheckIn;
        public Button mPreCheckOut;
    }

    public class ViewHolderAssetInfoWithChongHongButtons extends ViewHolderAssetInfo {
        public Button mCheckInChongHong;
        public Button mCheckOutChongHong;
    }
}
