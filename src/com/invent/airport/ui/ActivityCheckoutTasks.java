package com.invent.airport.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestGetCheckOutTaskDetail;
import com.invent.airport.requests.RequestGetCheckOutTaskList;
import com.invent.airport.requests.RequestTakeCheckOutTask;

import java.util.ArrayList;

public final class ActivityCheckoutTasks extends AirportBaseActivity {
    ListView mListView;
    BaseAdapter mFinalAdapter;
    TextView mTitle;

    ArrayList<RequestGetCheckOutTaskDetail.CheckOutTaskDetail> mData = new ArrayList<RequestGetCheckOutTaskDetail.CheckOutTaskDetail>();
    ArrayList<Integer> mCheckoutTaskIds = new ArrayList<Integer>();
    boolean mIsLoading;
    boolean mMoreDataToLoad = true;

    String mTitlePrefix;
    private static final String TAG = "ActivityCheckoutTasks";

    private Response.Listener<RequestGetCheckOutTaskDetail.CheckOutTaskDetail> mQueryResultListener =
            new Response.Listener<RequestGetCheckOutTaskDetail.CheckOutTaskDetail>() {
                @Override
                public void onResponse(RequestGetCheckOutTaskDetail.CheckOutTaskDetail response) {
                    addData(response);
                    if (mData.size() == mCheckoutTaskIds.size()) {
                        mMoreDataToLoad = false;
                    } else {
                        mMoreDataToLoad = true;
                    }
                }
            };

    private void addData(RequestGetCheckOutTaskDetail.CheckOutTaskDetail newData) {
        loadingEnd();
        if (newData != null) {
            // First iterate existing data
            RequestGetCheckOutTaskDetail.CheckOutTaskDetail removeTarget = null;
            for (RequestGetCheckOutTaskDetail.CheckOutTaskDetail checkOutTaskDetail : mData) {
                if (newData.mTaskID.equals(checkOutTaskDetail.mTaskID)) {
                    removeTarget = checkOutTaskDetail;
                    break;
                }
            }
            if (removeTarget != null) {
                mData.remove(removeTarget);
            }
            mData.add(newData);
            Log.i(TAG, "Added data, size is " + mData.size());
            mTitle.setText(mTitlePrefix + "：" + mData.size() + "条");
            mFinalAdapter.notifyDataSetChanged();
        }
    }

    private boolean shouldLoadMoreData(ArrayList<RequestGetCheckOutTaskDetail.CheckOutTaskDetail> data, int position) {
        // If showing the last set of data, request for the next set of data
        Log.i(TAG, "Should load more data ? position is " + position);
        boolean scrollRangeReached = (position >= (data.size() - 1));
        return (scrollRangeReached && !mIsLoading && mMoreDataToLoad);
    }

    private void loadMoreData(int position) {
        Log.i(TAG, "Try to load more data " + position);
        Integer taskId = mCheckoutTaskIds.get(position);
        if (taskId == null) {
            Log.e(TAG, "WTF, position " + position + " is null");
            return;
        }
        Log.i(TAG, "Start to load task detail of ID " + taskId);
        RequestGetCheckOutTaskDetail requestGetOutStandingPreCheckIns = new RequestGetCheckOutTaskDetail(String.valueOf(taskId),
                mQueryResultListener, mErrorListener);
        loadingStart();
        mRequestQueue.add(requestGetOutStandingPreCheckIns);
    }

    private void getCheckOutTaskList() {
        mCheckoutTaskIds.clear();

        RequestGetCheckOutTaskList requestGetCheckOutTaskList = new RequestGetCheckOutTaskList(new Response.Listener<RequestGetCheckOutTaskList.CheckOutTaskList>() {
            @Override
            public void onResponse(RequestGetCheckOutTaskList.CheckOutTaskList response) {
                loadingEnd();
                if (response.mTasks != null) {
                    for (Integer task : response.mTasks) {
                        mCheckoutTaskIds.add(task);
                    }
                }
                Log.i(TAG, "We've got " + mCheckoutTaskIds.size() + " tasks to show");

                if (mCheckoutTaskIds.size() > 0) {
                    // OK, we need to load task detail
                    loadMoreData(0);
                }
            }
        }, mErrorListener);
        loadingStart();
        mRequestQueue.add(requestGetCheckOutTaskList);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_checkout_tasks);

        mTitlePrefix = getString(R.string.checkout_tasks_title);

        mListView = (ListView) findViewById(R.id.aty_checkout_tasks_list);
        mTitle = (TextView) findViewById(R.id.aty_checkout_tasks_title);
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
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.handle_checkout_task_inprogress));
    }

    void handleFinish() {
        dismissProgressDialog();
    }

    void handleCheckOutTask(int position) {
        RequestGetCheckOutTaskDetail.CheckOutTaskDetail checkOutTaskDetail = mData.get(position);
        if (checkOutTaskDetail != null) {
            RequestTakeCheckOutTask requestTakeCheckOutTask = new RequestTakeCheckOutTask(checkOutTaskDetail.mTaskID, new Response.Listener<CookieGsonRequest.Succeed>() {
                @Override
                public void onResponse(CookieGsonRequest.Succeed response) {
                    handleFinish();
                    reloadAllInfo();
                }
            }, mErrorListener);

            handleStart();
            mRequestQueue.add(requestTakeCheckOutTask);
        } else {
            Log.e(TAG, "WTF, position " + position + " is null");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadAllInfo();
    }

    void reloadAllInfo() {
        mFinalAdapter = new CheckOutTaskAdapter();
        mCheckoutTaskIds.clear();
        mData.clear();
        mTitle.setText(mTitlePrefix + "：" + mData.size() + "条");
        mListView.setAdapter(mFinalAdapter);
        getCheckOutTaskList();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        loadingEnd();
        handleFinish();
    }


    protected void viewHolderFindIDs(ViewHolderCheckOutTask viewHolder, View v) {
        viewHolder.mNetworkImageView = (NetworkImageView) v.findViewById(R.id.item_ckout_task_image);
        viewHolder.mID = (EditText) v.findViewById(R.id.item_ckout_task_id);
        viewHolder.mBarcode = (EditText) v.findViewById(R.id.item_ckout_task_barcode);
        viewHolder.mName = (EditText) v.findViewById(R.id.item_ckout_task_name);
        viewHolder.mModel = (EditText) v.findViewById(R.id.item_ckout_task_model);
        viewHolder.mQuantity = (EditText) v.findViewById(R.id.item_ckout_task_quantity);
        viewHolder.mLocationBarcode = (EditText) v.findViewById(R.id.item_ckout_task_location_barcode);
        viewHolder.mCreatedBy = (EditText) v.findViewById(R.id.item_ckout_task_createdby);
        viewHolder.mCheckIn = (Button) v.findViewById(R.id.item_ckout_task_handle);
        viewHolder.mCheckOut = (Button) v.findViewById(R.id.item_ckout_task_cancel);
        viewHolder.mID.setKeyListener(null);
        viewHolder.mBarcode.setKeyListener(null);
        viewHolder.mName.setKeyListener(null);
        viewHolder.mModel.setKeyListener(null);
        viewHolder.mQuantity.setKeyListener(null);
        viewHolder.mLocationBarcode.setKeyListener(null);
        viewHolder.mCreatedBy.setKeyListener(null);
    }

    protected void viewHolderSetInfo(ViewHolderCheckOutTask viewHolder, RequestGetCheckOutTaskDetail.CheckOutTaskDetail info) {
        if (info.mPicURL != null && !info.mPicURL.isEmpty()) {
            viewHolder.mNetworkImageView.setImageUrl(Constants.getImageURL(info.mPicURL), mImageLoader);
        }
        viewHolder.mID.setText(info.mTaskID);
        viewHolder.mName.setText(info.mName);
        viewHolder.mModel.setText(info.mModel);
        viewHolder.mQuantity.setText("" + info.mPCs);
        viewHolder.mBarcode.setText(info.mBarcode);
        viewHolder.mLocationBarcode.setText(info.mLocation);
        viewHolder.mCreatedBy.setText(info.mCreatedBy);
    }

    private final class CheckOutTaskAdapter extends BaseAdapter {

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

        View.OnClickListener mCheckInListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getItemPositionOfButton(view);
                if (position != -1) {
                    handleCheckOutTask(position);
                }
            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolderCheckOutTask viewHolder;

            //check to see if we need to load more data
            if (shouldLoadMoreData(mData, position)) {
                loadMoreData(position + 1);
            }

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_checkout_task, null);
                viewHolder = new ViewHolderCheckOutTask();
                viewHolderFindIDs(viewHolder, v);
                // Continue with buttons

                setItemPositionOfButton(viewHolder.mCheckIn, position);
                setItemPositionOfButton(viewHolder.mCheckOut, position);

                viewHolder.mCheckIn.setOnClickListener(mCheckInListener);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderCheckOutTask) v.getTag();
            }
            setItemPositionOfButton(viewHolder.mCheckIn, position);
            setItemPositionOfButton(viewHolder.mCheckOut, position);
            RequestGetCheckOutTaskDetail.CheckOutTaskDetail checkOutTaskDetail = mData.get(position);
            viewHolderSetInfo(viewHolder, checkOutTaskDetail);
            return v;
        }

        private class PositionTag {
            int mPosition;
        }
    }

    public class ViewHolderCheckOutTask {
        public NetworkImageView mNetworkImageView;
        public EditText mID;
        public EditText mName;
        public EditText mModel;
        public EditText mQuantity;
        public EditText mBarcode;
        public EditText mLocationBarcode;
        public EditText mCreatedBy;
        public Button mCheckIn;
        public Button mCheckOut;
    }
}
