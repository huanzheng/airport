package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestCheckInWithBarCode extends CookieGsonJsonRequest<CookieGsonRequest.Succeed> {
    public RequestCheckInWithBarCode(CheckInWithBarCode checkIn, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECK_IN_UPDATE),
                checkIn, Succeed.class, listener, errorListener);
    }

    public static class CheckInWithBarCode {
        @SerializedName("barcode")
        public String mBarCode;
        @SerializedName("pcs")
        public Integer mQuant;
        @SerializedName("location")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
    }
}
