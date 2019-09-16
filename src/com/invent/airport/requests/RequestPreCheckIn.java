package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestPreCheckIn extends CookieGsonJsonRequest<CookieGsonRequest.RecordID> {
    public RequestPreCheckIn(PreCheckInOut input,
                             Response.Listener<CookieGsonRequest.RecordID> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_PRE_CHECK_IN_NEW),
                input, CookieGsonRequest.RecordID.class, listener, errorListener);
    }


    public static class PreCheckInOut {
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("model")
        public String mModel;
        @SerializedName("name")
        public String mName;
        @SerializedName("photoUrl")
        public String mPhotoURL;
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
        @SerializedName("assetType")
        public Integer mAssetType;
        @SerializedName("pici")
        public Integer mPici;
        @SerializedName("laiYuan")
        public String mLaiYuan;
        @SerializedName("beiZhu")
        public String mBeiZhu;

        public void setComponentInfo(ComponentInfo componentInfo) {
            mBarcode = componentInfo.mBarcode;
            mModel = componentInfo.mModel;
            mName = componentInfo.mName;
            mPhotoURL = componentInfo.mPhotoURL;
            mLocation = componentInfo.mLocation;
            mLocationBarCode = componentInfo.mLocationBarcode;
            mSerial = componentInfo.mSerial;
            if (componentInfo.mPrice != Constants.INVALID_PRICE)
                mPrice = componentInfo.mPrice;
            else
                mPrice = (float)0;
            if (componentInfo.mQuantPerPack != Constants.INVALID_QUANT)
                mPiecesPerPackage = componentInfo.mQuantPerPack;
            else
                mPiecesPerPackage = 0;
            mOrderCode = componentInfo.mOrderCode;
            mAssetType = componentInfo.mType;
            if (componentInfo.mPici != Constants.INVALID_PICI)
                mPici = componentInfo.mPici;
            mLaiYuan = componentInfo.mLaiYuan;
            mBeiZhu = componentInfo.mBeiZhu;
        }
    }
}
