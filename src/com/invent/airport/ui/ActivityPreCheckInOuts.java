package com.invent.airport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestGetOutStandingPreCheckIns;
import com.invent.airport.requests.RequestGetOutStandingPreCheckOuts;
import com.invent.airport.requests.RequestPreCheckInHandled;
import com.invent.airport.requests.RequestPreCheckOutHandled;

import java.util.ArrayList;
import java.util.Date;

public final class ActivityPreCheckInOuts extends AirportBaseActivity {
    ListView mListView;
    BaseAdapter mFinalAdapter;
    TextView mTitle;
    public static final String KEY_PRECHECK_IN_OR_OUT = "key_precheckin_out";
    public static final int PRE_CHECK_INS = 1;
    public static final int PRE_CHECK_OUTS = 2;
    private int mPreCheckWhat = PRE_CHECK_INS;

    ArrayList<RequestGetOutStandingPreCheckIns.PreCheckInOutInfo> mData = new ArrayList<RequestGetOutStandingPreCheckIns.PreCheckInOutInfo>();
    boolean mIsLoading;
    boolean mMoreDataToLoad;
    int mCurPageNum;

    String mControlledType;
    String mNoneControlledType;
    String mTitlePrefix;
    private static final String TAG = "ActivityPreCheckInOuts";
    private int mCurrentClickedPosition;

    private Response.Listener<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut> mQueryResultListener =
            new Response.Listener<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut>() {
                @Override
                public void onResponse(RequestGetOutStandingPreCheckIns.PendingPreCheckInOut response) {
                    if (response.mCurPageNum != null && response.mPreCheckInOutInfos != null &&
                            response.mPreCheckInOutInfos.size() == Constants.QUERY_PAGE_SIZE) {
                        mMoreDataToLoad = true;
                    } else {
                        mMoreDataToLoad = false;
                    }
                    mCurPageNum = response.mCurPageNum;
                    addData(response.mPreCheckInOutInfos);
                }
            };


    private void addData(ArrayList<RequestGetOutStandingPreCheckIns.PreCheckInOutInfo> newData) {
        loadingEnd();
        if (newData != null && newData.size() > 0) {
            Log.i(TAG, "Adding data, size is " + newData.size());
            mData.addAll(newData);
            if (mData.size() >= 10) {
                mTitle.setText(mTitlePrefix + "：10条+");
            } else {
                mTitle.setText(mTitlePrefix + "：" + mData.size() + "条");
            }
            mFinalAdapter.notifyDataSetChanged();
        }
    }

    private boolean shouldLoadMoreData(ArrayList<RequestGetOutStandingPreCheckIns.PreCheckInOutInfo> data, int position) {
        // If showing the last set of data, request for the next set of data
        Log.i(TAG, "Should load more data ? position is " + position);
        boolean scrollRangeReached = (position > (data.size() - Constants.QUERY_PAGE_SIZE + 8));
        return (scrollRangeReached && !mIsLoading && mMoreDataToLoad);
    }

    private void loadMoreData(int pageNum) {
        Log.i(TAG, "Start to load page " + pageNum);
        mCurPageNum = pageNum;
        if (isPreCheckIns()) {
            RequestGetOutStandingPreCheckIns requestGetOutStandingPreCheckIns =
                    new RequestGetOutStandingPreCheckIns(mCurPageNum, mQueryResultListener, mErrorListener);
            loadingStart();
            mRequestQueue.add(requestGetOutStandingPreCheckIns);
        } else {
            RequestGetOutStandingPreCheckOuts requestGetOutStandingPreCheckOuts =
                    new RequestGetOutStandingPreCheckOuts(mCurPageNum, mQueryResultListener, mErrorListener);
            loadingStart();
            mRequestQueue.add(requestGetOutStandingPreCheckOuts);
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_precheckins);
        mPreCheckWhat = getIntent().getIntExtra(KEY_PRECHECK_IN_OR_OUT, PRE_CHECK_INS);

        mControlledType = getString(R.string.controlled_asset);
        mNoneControlledType = getString(R.string.none_controlled_asset);

        mListView = (ListView) findViewById(R.id.aty_precheckins_list);
        mTitle = (TextView) findViewById(R.id.aty_precheckins_title);

        if (isPreCheckIns()) {
            mConfirmDialogMsg = "此记录建议转正式入库，确定还要这样处理这个记录吗？";
            mTitlePrefix = getString(R.string.pre_checkin_records);
        } else {
            mConfirmDialogMsg = "此记录建议转正式出库，确定还要这样处理这个记录吗？";
            mTitlePrefix = getString(R.string.pre_checkout_records);
        }
    }

    private boolean isPreCheckIns() {
        return (mPreCheckWhat == PRE_CHECK_INS);
    }


    @Override
    protected void dialogConfirmed() {
        if (isPreCheckIns()) {
            preCheckInHandled(mCurrentClickedPosition);
        } else {
            preCheckOutHandled(mCurrentClickedPosition);
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

    void handleStart() {
        if (isPreCheckIns()) {
            showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.pre_checkin_handle_inprogress));
        } else {
            showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.pre_checkout_handle_inprogress));
        }
    }

    void handleFinish() {
        dismissProgressDialog();
    }

    void preCheckInHandled(int position) {
        RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInInfo = mData.get(position);
        if (preCheckInInfo != null) {
            RequestPreCheckInHandled requestPreCheckInHandled = new RequestPreCheckInHandled(new CookieGsonRequest.RecordID(preCheckInInfo.mRecID),
                    new Response.Listener<CookieGsonRequest.Succeed>() {
                        @Override
                        public void onResponse(CookieGsonRequest.Succeed response) {
                            handleFinish();
                            reloadAllInfo();
                        }
                    }, mErrorListener);
            handleStart();
            mRequestQueue.add(requestPreCheckInHandled);
        } else {
            Log.e(TAG, "WTF, position " + position + " is null");
        }
    }

    void preCheckOutHandled(int position) {
        RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInOutInfo = mData.get(position);
        if (preCheckInOutInfo != null) {
            RequestPreCheckOutHandled requestPreCheckOutHandled = new RequestPreCheckOutHandled(new CookieGsonRequest.RecordID(preCheckInOutInfo.mRecID),
                    new Response.Listener<CookieGsonRequest.Succeed>() {
                        @Override
                        public void onResponse(CookieGsonRequest.Succeed response) {
                            handleFinish();
                            reloadAllInfo();
                        }
                    }, mErrorListener);
            handleStart();
            mRequestQueue.add(requestPreCheckOutHandled);
        } else {
            Log.e(TAG, "WTF, position " + position + " is null");
        }
    }

    void switchToFormalCheckIn(int position) {
        RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInInfo = mData.get(position);
        if (preCheckInInfo != null) {
            if (preCheckInInfo.mBarcode != null && !preCheckInInfo.mBarcode.isEmpty()) {
                // With rich info, switch to ActivityCheckInWithBarcode
                CookieGsonRequest.ComponentInfo componentInfo = new CookieGsonRequest.ComponentInfo();
                RequestGetOutStandingPreCheckIns.fillComponentInfo(preCheckInInfo, componentInfo);
                Intent i = new Intent(mContext, ActivityCheckInWithBarcode.class);
                i.putExtra(Constants.KEY_ASSET_INFO, componentInfo);
                i.putExtra(Constants.KEY_PRECHECK_INOUT_INFO, preCheckInInfo);
                startActivity(i);
            } else {
                // With limited info, switch to ActivityCheckInWithoutBarcode
                Intent i = new Intent(mContext, ActivityCheckInWithoutBarcode.class);
                i.putExtra(Constants.KEY_PRECHECK_INOUT_INFO, preCheckInInfo);
                startActivity(i);
            }
        }
    }

    void switchToFormalCheckOut(int position) {
        RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInOutInfo = mData.get(position);
        if (preCheckInOutInfo != null) {
            if (preCheckInOutInfo.mBarcode != null && !preCheckInOutInfo.mBarcode.isEmpty()) {
                // With rich info, switch to ActivityCheckOut
                CookieGsonRequest.ComponentInfo componentInfo = new CookieGsonRequest.ComponentInfo();
                RequestGetOutStandingPreCheckIns.fillComponentInfo(preCheckInOutInfo, componentInfo);
                Intent i = new Intent(mContext, ActivityCheckOut.class);
                i.putExtra(Constants.KEY_ASSET_INFO, componentInfo);
                i.putExtra(Constants.KEY_PRECHECK_INOUT_INFO, preCheckInOutInfo);
                startActivity(i);
            } else {
                // This record can not be formal checkout
                toastInfo("此预出库记录无法转正式出库");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadAllInfo();
    }

    void reloadAllInfo() {
        mFinalAdapter = new PreCheckInsAdapter();
        mData.clear();
        mListView.setAdapter(mFinalAdapter);
        mTitle.setText(mTitlePrefix + "：" + mData.size() + "条");
        loadMoreData(1);
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        loadingEnd();
        handleFinish();
    }


    protected void viewHolderFindIDs(ViewHolderPreCheckIn viewHolder, View v) {
        viewHolder.mNetworkImageView = (NetworkImageView) v.findViewById(R.id.item_precheckin_image);
        viewHolder.mID = (EditText) v.findViewById(R.id.item_precheckin_id);
        viewHolder.mDate = (EditText) v.findViewById(R.id.item_precheckin_time);
        viewHolder.mName = (EditText) v.findViewById(R.id.item_precheckin_name);
        viewHolder.mModel = (EditText) v.findViewById(R.id.item_precheckin_model);
        viewHolder.mQuantity = (EditText) v.findViewById(R.id.item_precheckin_quantity);
        viewHolder.mSerial = (EditText) v.findViewById(R.id.item_precheckin_serial);
        viewHolder.mMarkHandled = (Button) v.findViewById(R.id.item_precheckin_markhandled);
        viewHolder.mFormalCheckInOut = (Button) v.findViewById(R.id.item_precheckin_checkin);
        viewHolder.mButtons = (LinearLayout) v.findViewById(R.id.item_precheckinout_buttons);
        /*if (isPreCheckIns()) {
            viewHolder.mMarkHandled.setVisibility(View.GONE);
            viewHolder.mFormalCheckInOut.setVisibility(View.GONE);
            viewHolder.mButtons.setVisibility(View.GONE);
        }*/
        viewHolder.mControlType = (EditText) v.findViewById(R.id.item_precheckin_control_type);
        viewHolder.mID.setKeyListener(null);
        viewHolder.mDate.setKeyListener(null);
        viewHolder.mName.setKeyListener(null);
        viewHolder.mModel.setKeyListener(null);
        viewHolder.mQuantity.setKeyListener(null);
        viewHolder.mSerial.setKeyListener(null);
    }

    protected void viewHolderSetInfo(ViewHolderPreCheckIn viewHolder, RequestGetOutStandingPreCheckIns.PreCheckInOutInfo info) {
        if (info.mPicURL != null && !info.mPicURL.isEmpty()) {
            viewHolder.mNetworkImageView.setImageUrl(Constants.getImageURL(info.mPicURL), mImageLoader);
        }
        viewHolder.mID.setText(info.mRecID);
        Date date = RequestGetOutStandingPreCheckIns.parseTimeStamp(info.mTimeStamp);
        if (date != null) {
            viewHolder.mDate.setText(RequestGetOutStandingPreCheckIns.getFormattedDateString(date));
        } else {
            viewHolder.mDate.setText(info.mTimeStamp);
        }
        viewHolder.mName.setText(info.mName);
        viewHolder.mModel.setText(info.mModel);
        viewHolder.mQuantity.setText("" + info.mPCs);
        viewHolder.mSerial.setText(info.mSerial);
        if (info.mType != null && info.mType == Constants.ASSET_TYPE_CONTROLLED) {
            viewHolder.mControlType.setText(mControlledType);
        } else {
            viewHolder.mControlType.setText(mNoneControlledType);
        }
        if (isPreCheckIns()) {
            viewHolder.mMarkHandled.setText(R.string.handle_pre_checkin);
            viewHolder.mFormalCheckInOut.setText(R.string.switch_to_formal_checkin);
        } else {
            viewHolder.mMarkHandled.setText(R.string.handle_pre_checkout);
            viewHolder.mFormalCheckInOut.setText(R.string.switch_to_formal_checkout);
        }
    }

    private final class PreCheckInsAdapter extends BaseAdapter {

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
                return positionTag.mPosition;
            } else {
                return -1;
            }
        }

        private void setItemPositionOfButton(View v, int position) {
            Log.i(TAG, "Set position " + position);
            PositionTag positionTag = new PositionTag();
            positionTag.mPosition = position;
            v.setTag(positionTag);
        }

        View.OnClickListener mMarkPreCheckInOutHandledListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInOutInfo = mData.get(position);
                    if (isPreCheckIns()) {
                        // for precheck in, always alert user
                        mCurrentClickedPosition = position;
                        showConfirmDialog();
                        //preCheckInHandled(position);
                    } else {
                        // this precheck out record contains rich information, not good to just mark handled
                        // better to switch to formal check out
                        if (preCheckInOutInfo.mBarcode != null && !preCheckInOutInfo.mBarcode.isEmpty()) {
                            mCurrentClickedPosition = position;
                            showConfirmDialog();
                        } else {
                            preCheckOutHandled(position);
                        }
                    }
                }
            }
        };

        View.OnClickListener mSwitchToFormalCheckInOut = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                Log.i(TAG, "Position is " + position);
                if (position != -1) {
                    if (isPreCheckIns()) {
                        switchToFormalCheckIn(position);
                    } else {
                        switchToFormalCheckOut(position);
                    }
                }
            }
        };

        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolderPreCheckIn viewHolder;

            //check to see if we need to load more data
            if (shouldLoadMoreData(mData, position)) {
                loadMoreData(mCurPageNum + 1);
            }

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_pre_checkin, null);
                viewHolder = new ViewHolderPreCheckIn();
                viewHolderFindIDs(viewHolder, v);
                // Continue with buttons

                setItemPositionOfButton(viewHolder.mMarkHandled, position);
                setItemPositionOfButton(viewHolder.mFormalCheckInOut, position);

                viewHolder.mMarkHandled.setOnClickListener(mMarkPreCheckInOutHandledListener);
                viewHolder.mFormalCheckInOut.setOnClickListener(mSwitchToFormalCheckInOut);

                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderPreCheckIn) v.getTag();
            }
            setItemPositionOfButton(viewHolder.mMarkHandled, position);
            setItemPositionOfButton(viewHolder.mFormalCheckInOut, position);
            RequestGetOutStandingPreCheckIns.PreCheckInOutInfo preCheckInfo = mData.get(position);
            viewHolderSetInfo(viewHolder, preCheckInfo);
            return v;
        }

        private class PositionTag {
            int mPosition;
        }
    }

    public class ViewHolderPreCheckIn {
        public NetworkImageView mNetworkImageView;
        public LinearLayout mButtons;
        public EditText mID;
        public EditText mDate;
        public EditText mName;
        public EditText mModel;
        public EditText mQuantity;
        public EditText mSerial;
        public EditText mControlType;
        public Button mMarkHandled;
        public Button mFormalCheckInOut;
    }
}
