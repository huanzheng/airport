package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestGetCheckOutTaskDetail extends CookieGsonRequest<RequestGetCheckOutTaskDetail.CheckOutTaskDetail> {
    public RequestGetCheckOutTaskDetail(String taskID, Response.Listener<CheckOutTaskDetail> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getCheckoutTaskDetailURL(taskID),
                CheckOutTaskDetail.class, listener, errorListener);
    }

    private static String getCheckoutTaskDetailURL(String taskID) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_CHECKOUT_TASK_INFO);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.SEPERATER).append(taskID);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "URL of checkout task detail is " + ret);
        return ret;
    }

    public static class CheckOutTaskDetail {
        //private static final String TAG = "CheckoutTaskDetail";
        @SerializedName("taskId")
        public String mTaskID;
        @SerializedName("model")
        public String mModel;
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("name")
        public String mName;
        @SerializedName("picUrl")
        public String mPicURL;
        @SerializedName("location")
        public String mLocation;
        @SerializedName("serial")
        public String mSerial;
        @SerializedName("price")
        public Float mPrice;
        @SerializedName("pcs")
        public Integer mPCs;
        @SerializedName("pcsPerPackage")
        public Integer mPCsPerPack;
        @SerializedName("createdBy")
        public String mCreatedBy;
/*
        public void dump() {
            Log.i(TAG, "task Id " + mTaskID);
            Log.i(TAG, "model " + mTaskID);
            Log.i(TAG, "barcode " + mTaskID);
            Log.i(TAG, "name " + mTaskID);
            Log.i(TAG, "task Id " + mTaskID);
            Log.i(TAG, "task Id " + mTaskID);


        }*/
    }
}
