package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestCheckInWithoutBarCode extends CookieGsonJsonRequest<RequestCheckInWithoutBarCode.BarCode> {
    public RequestCheckInWithoutBarCode(CheckInWithoutBarCode input,
                                        Response.Listener<BarCode> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECK_IN_NEW),
                input, BarCode.class, listener, errorListener);
    }

    public static class BarCode {
        @SerializedName("barcode")
        public String mBarCode;
    }

    public static class CheckInWithoutBarCode {
        @SerializedName("model")
        public String mModel;
        @SerializedName("name")
        public String mName;
        @SerializedName("location")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarCode;
        @SerializedName("serial")
        public String mSerial;
        @SerializedName("price")
        public Float mPrice;
        @SerializedName("pcs")
        public Integer mPieces;
        @SerializedName("pcsPerPackage")
        public Integer mPiecesPerPackage;
        @SerializedName("orderCode")
        public String mOrderCode;
        @SerializedName("type")
        public Integer mType;
        @SerializedName("pici")
        public Integer mPici;
        @SerializedName("laiYuan")
        public String mLaiYuan;
        @SerializedName("beiZhu")
        public String mBeiZhu;
    }
}
