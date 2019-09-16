package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestCreateCheckOutTask extends CookieGsonJsonRequest<CookieGsonRequest.TaskID> {


    public RequestCreateCheckOutTask(CheckOutTask input, Response.Listener<TaskID> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_NEW_CHECKOUT_TASK),
                input, TaskID.class, listener, errorListener);
    }

    public static class CheckOutTask {
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("pcs")
        public Integer mPCs;
    }
}
