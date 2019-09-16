package com.invent.airport.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class RequestGetOutStandingPreCheckIns extends CookieGsonRequest<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut> {
    //private static final String TAG = "RequestGetOutStandingPreCheckIns";

    public RequestGetOutStandingPreCheckIns(int page, Response.Listener<PendingPreCheckInOut> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getURLForPreCheckIns(page), PendingPreCheckInOut.class, listener, errorListener);
    }

    private static String getURLForPreCheckIns(int page) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_OUTSTANDING_PRE_CHECKINS);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.QUESTION);
        builder.append(Constants.KEY_PAGE).append(Constants.EQUAL).append(page);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "URL of pre check page " + page + " is " + ret);
        return ret;
    }

    public static class PendingPreCheckInOut {
        @SerializedName("curPageNum")
        public Integer mCurPageNum;
        @SerializedName("records")
        public ArrayList<PreCheckInOutInfo> mPreCheckInOutInfos;
    }

    public static void fillComponentInfo(PreCheckInOutInfo preCheckInOutInfo, ComponentInfo componentInfo) {
        componentInfo.mBarcode = preCheckInOutInfo.mBarcode;
        componentInfo.mPhotoURL = preCheckInOutInfo.mAssetPhotoUrl;
        componentInfo.mQuantIn = Constants.INVALID_QUANT;
        componentInfo.mLocationBarcode = preCheckInOutInfo.mLocationBarcode;
        componentInfo.mModel = preCheckInOutInfo.mModel;
        componentInfo.mLocation = preCheckInOutInfo.mLocation;
        componentInfo.mName = preCheckInOutInfo.mName;
        componentInfo.mOrderCode = preCheckInOutInfo.mOrderCode;
        componentInfo.mPrice = preCheckInOutInfo.mPrice;
        componentInfo.mQuantPerPack = preCheckInOutInfo.mQuantPerPack;
        componentInfo.mTotalAvailable = Constants.INVALID_QUANT;
        componentInfo.mType = preCheckInOutInfo.mType;
        componentInfo.mSerial = preCheckInOutInfo.mSerial;
        componentInfo.mPici = preCheckInOutInfo.mPici;
        componentInfo.mLaiYuan = preCheckInOutInfo.mLaiYuan;
        componentInfo.mBeiZhu = preCheckInOutInfo.mBeiZhu;
    }

    public static class PreCheckInOutInfo implements Parcelable {
        @SerializedName("recId")
        public String mRecID;
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("timestamp")
        public String mTimeStamp;
        @SerializedName("model")
        public String mModel;
        @SerializedName("name")
        public String mName;
        @SerializedName("type")
        public Integer mType;
        @SerializedName("picUrl")
        public String mPicURL;
        @SerializedName("pcs")
        public Integer mPCs;
        @SerializedName("serial")
        public String mSerial;
        @SerializedName("locationName")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("assetPhotoUrl")
        public String mAssetPhotoUrl;
        @SerializedName("pcsPerPackage")
        public Integer mQuantPerPack;
        @SerializedName("orderCode")
        public String mOrderCode;
        @SerializedName("price")
        public Float mPrice;
        @SerializedName("pici")
        public Integer mPici;
        @SerializedName("laiYuan")
        public String mLaiYuan;
        @SerializedName("beiZhu")
        public String mBeiZhu;
        public transient Date mDate;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mRecID);
            if (mBarcode != null) {
                parcel.writeString(mBarcode);
            } else {
                parcel.writeString("");
            }
            if (mTimeStamp != null) {
                parcel.writeString(mTimeStamp);
            } else {
                parcel.writeString("");
            }
            if (mModel != null) {
                parcel.writeString(mModel);
            } else {
                parcel.writeString("");
            }
            if (mName != null) {
                parcel.writeString(mName);
            } else {
                parcel.writeString("");
            }
            if (mType != null) {
                parcel.writeInt(mType);
            } else {
                parcel.writeInt(Constants.INVALID_QUANT);
            }
            if (mPicURL != null) {
                parcel.writeString(mPicURL);
            } else {
                parcel.writeString("");
            }
            if (mPCs != null) {
                parcel.writeInt(mPCs);
            } else {
                parcel.writeInt(Constants.INVALID_QUANT);
            }
            if (mSerial != null) {
                parcel.writeString(mSerial);
            } else {
                parcel.writeString("");
            }
            if (mLocation != null) {
                parcel.writeString(mLocation);
            } else {
                parcel.writeString("");
            }
            if (mLocationBarcode != null) {
                parcel.writeString(mLocationBarcode);
            } else {
                parcel.writeString("");
            }
            if (mAssetPhotoUrl != null) {
                parcel.writeString(mAssetPhotoUrl);
            } else {
                parcel.writeString("");
            }
            if (mQuantPerPack != null) {
                parcel.writeInt(mQuantPerPack);
            } else {
                parcel.writeInt(Constants.INVALID_QUANT);
            }
            if (mOrderCode != null) {
                parcel.writeString(mOrderCode);
            } else {
                parcel.writeString("");
            }
            if (mPrice != null) {
                parcel.writeFloat(mPrice);
            } else {
                parcel.writeFloat(Constants.INVALID_PRICE);
            }
            if (mPici != null) {
                parcel.writeInt(mPici);
            } else {
                parcel.writeInt(Constants.INVALID_PICI);
            }
            if (mLaiYuan != null) {
                parcel.writeString(mLaiYuan);
            } else {
                parcel.writeString("");
            }
            if (mBeiZhu != null) {
                parcel.writeString(mBeiZhu);
            } else {
                parcel.writeString("");
            }
        }

        public PreCheckInOutInfo(Parcel in) {
            mRecID = in.readString();
            mBarcode = in.readString();
            mTimeStamp = in.readString();
            mModel = in.readString();
            mName = in.readString();
            mType = in.readInt();
            mPicURL = in.readString();
            mPCs = in.readInt();
            mSerial = in.readString();
            mLocation = in.readString();
            mLocationBarcode = in.readString();
            mAssetPhotoUrl = in.readString();
            mQuantPerPack = in.readInt();
            mOrderCode = in.readString();
            mPrice = in.readFloat();
            mPici = in.readInt();
            mLaiYuan = in.readString();
            mBeiZhu = in.readString();
        }

        public static final Parcelable.Creator<PreCheckInOutInfo> CREATOR = new Parcelable.Creator<PreCheckInOutInfo>() {

            public PreCheckInOutInfo createFromParcel(Parcel in) {
                return new PreCheckInOutInfo(in);
            }

            public PreCheckInOutInfo[] newArray(int size) {
                return new PreCheckInOutInfo[size];
            }
        };
    }

    public static Date parseTimeStamp(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(timestamp);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getFormattedDateString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return format.format(date);
    }
}
