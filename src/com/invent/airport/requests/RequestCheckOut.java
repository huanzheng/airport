package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestCheckOut extends CookieGsonJsonRequest<CookieGsonRequest.RecordID> {
    public RequestCheckOut(CheckOut input, Response.Listener<RecordID> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECK_OUT),
                input, RecordID.class, listener, errorListener);
    }

    public static class CheckOut {
        @SerializedName("barcode")
        public String mBarCode;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("pcs")
        public Integer mQuant;
    }

}
