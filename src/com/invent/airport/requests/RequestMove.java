package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestMove extends CookieGsonJsonRequest<CookieGsonRequest.Succeed> {
    public RequestMove(Move input, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_MOVE),
                input, Succeed.class, listener, errorListener);
    }

    public static class Move {
        @SerializedName("barcode")
        public String mBarCode;
        @SerializedName("oldLocationBarcode")
        public String mOldLocationBarcode;
        @SerializedName("newLocationBarcode")
        public String mNewLocationBarcode;
        @SerializedName("pcs")
        public Integer mPCsToMove;
    }
}
