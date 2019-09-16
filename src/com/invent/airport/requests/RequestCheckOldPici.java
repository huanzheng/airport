package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestCheckOldPici extends CookieGsonJsonRequest<RequestCheckOldPici.Result> {
    public RequestCheckOldPici(Barcode input,
                               Response.Listener<Result> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECK_PICI),
                input, Result.class, listener, errorListener);
    }

    public static class Barcode {
        @SerializedName("barcode")
        public String mBarcode;

        public Barcode(String code) {
            mBarcode = code;
        }
    }
    public static class Result {
        @SerializedName("result")
        public boolean mResult;
    }
}
