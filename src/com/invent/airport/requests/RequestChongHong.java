package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;

public class RequestChongHong extends CookieGsonJsonRequest<CookieGsonRequest.Succeed> {

    public RequestChongHong(String url, ChongHong input, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(url,
                input, Succeed.class, listener, errorListener);
    }

    public static class ChongHong {
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("location")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("pcs")
        public Integer mPCs;
    }
}
