package com.invent.airport.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;
import com.invent.airport.ui.AirportApp;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CookieGsonRequest<T> extends Request<T> {
    private static final String TAG = "CookieGsonRequest";
    /**
     * Gson parser
     */
    protected final Gson mGson;

    /**
     * Class type for the response
     */
    private final Class<T> mClass;
    //MultipartEntity mE;
    /**
     * Callback for response delivery
     */
    private final Listener<T> mListener;

    public CookieGsonRequest(int method, String url, Class<T> objectClass,
                             Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mClass = objectClass;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        AirportApp.getInstance().addSessionCookie(headers);
        return headers;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            L.i(TAG, "Response is " + json);
            if (response.statusCode != HttpStatus.SC_OK) {
                AirportError airportError = new AirportError(500, "Invalid HTTP Status " + response.statusCode);
                return Response.error(airportError);
            }
            // empty payload, together with status ok, indicates succeed
            if (response.statusCode == HttpStatus.SC_OK && (json == null || json.isEmpty())) {
                Succeed ret = new Succeed();
                // assert T must be Succeed.class when this happens
                assert (mClass.isInstance(ret));
                return (Response<T>) Response.success(ret,
                        getCacheEntry());
            }

            JSONObject object;
            try {
                object = new JSONObject(json);
            } catch (JSONException e) {
                L.e(TAG, "Invalid JSON String from server " + json + " " + e.getMessage());
                return Response.error(new ParseError(e));
            }

            try {
                // Let's first check if there's errorcode
                int errorCode = object.getInt(Constants.KEY_ERRORCODE);
                String errorMsg = object.getString(Constants.KEY_DESCRIPTION);
                L.e(TAG, "Operation error " + errorCode + " " + errorMsg);
                return Response.error(new AirportError(errorCode, errorMsg));
            } catch (JSONException e) {
                // OK, since we can not get errorCode and errorMsg, we are good
                return Response.success(mGson.fromJson(json, mClass),
                        getCacheEntry());
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    /**
     * Some common classes defined below
     */


    public static class Succeed {

    }

    public static class PicURL {
        @SerializedName("picUrl")
        public String mURL;
    }

    public static class RecordID {
        @SerializedName("recId")
        public String mRecordID;

        public RecordID(String recID) {
            mRecordID = recID;
        }

        public RecordID() {
        }
    }

    public static class TaskID {
        @SerializedName("taskId")
        public String mTaskID;
    }

    public static class ComponentInfo implements Parcelable {
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("name")
        public String mName;
        @SerializedName("model")
        public String mModel;
        @SerializedName("photoUrl")
        public String mPhotoURL;
        @SerializedName("location")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("serial")
        public String mSerial;
        @SerializedName("price")
        public Float mPrice;
        @SerializedName("orderCode")
        public String mOrderCode;
        @SerializedName("pcsIn")
        public Integer mQuantIn;
        @SerializedName("totAvailable")
        public Integer mTotalAvailable;
        @SerializedName("type")
        public Integer mType;
        @SerializedName("pcsPerPackage")
        public Integer mQuantPerPack;
        @SerializedName("pici")
        public Integer mPici;
        @SerializedName("laiYuan")
        public String mLaiYuan;
        @SerializedName("beiZhu")
        public String mBeiZhu;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mBarcode);
            parcel.writeString(mName);
            parcel.writeString(mModel);
            parcel.writeString(mPhotoURL);
            parcel.writeString(mLocation);
            parcel.writeString(mLocationBarcode);
            parcel.writeString(mSerial);
            parcel.writeFloat(mPrice);
            parcel.writeString(mOrderCode);
            parcel.writeInt(mQuantIn);
            parcel.writeInt(mTotalAvailable);
            parcel.writeInt(mType);
            parcel.writeInt(mQuantPerPack);
            if (mPici != null)
                parcel.writeInt(mPici);
            else
                parcel.writeInt(Constants.INVALID_PICI);
            if (mLaiYuan != null)
                parcel.writeString(mLaiYuan);
            else
                parcel.writeString("");
            if (mBeiZhu != null)
                parcel.writeString(mBeiZhu);
            else
                parcel.writeString("");
        }

        public ComponentInfo(Parcel in) {
            mBarcode = in.readString();
            mName = in.readString();
            mModel = in.readString();
            mPhotoURL = in.readString();
            mLocation = in.readString();
            mLocationBarcode = in.readString();
            mSerial = in.readString();
            mPrice = in.readFloat();
            mOrderCode = in.readString();
            mQuantIn = in.readInt();
            mTotalAvailable = in.readInt();
            mType = in.readInt();
            mQuantPerPack = in.readInt();
            mPici = in.readInt();
            mLaiYuan = in.readString();
            mBeiZhu = in.readString();
        }

        public ComponentInfo() {
        }

        public static final Parcelable.Creator<ComponentInfo> CREATOR = new Parcelable.Creator<ComponentInfo>() {

            public ComponentInfo createFromParcel(Parcel in) {
                return new ComponentInfo(in);
            }

            public ComponentInfo[] newArray(int size) {
                return new ComponentInfo[size];
            }
        };
    }
    public static class QueryPreCheckInBigInfoResult {
        @SerializedName("curPageNum")
        public int mCurPageNum;
        @SerializedName("preCheckIns")
        public ArrayList<PreCheckInBigInfo> mCurPage;
    }

    public static class PreCheckInBigInfo implements Parcelable {
        @SerializedName("assetBarcode")
        public String mBarcode;
        @SerializedName("assetName")
        public String mName;
        @SerializedName("model")
        public String mModel;
        @SerializedName("assetPhotoUrl")
        public String mPhotoURL;
        @SerializedName("locationName")
        public String mLocation;
        @SerializedName("locationBarcode")
        public String mLocationBarcode;
        @SerializedName("serial")
        public String mSerial;
        @SerializedName("price")
        public Float mPrice;
        @SerializedName("orderCode")
        public String mOrderCode;
        //@SerializedName("pcsIn")
        //public Integer mQuantIn;
        //@SerializedName("totAvailable")
       // public Integer mTotalAvailable;
        @SerializedName("assetType")
        public Integer mType;
        @SerializedName("pcsPerPackage")
        public Integer mQuantPerPack;
        @SerializedName("pici")
        public Integer mPici;
        @SerializedName("laiYuan")
        public String mLaiYuan;
        @SerializedName("beiZhu")
        public String mBeiZhu;
        @SerializedName("pcs")
        public Integer mPcs;
        @SerializedName("photoId")
        public String mPhotoId;

        @Override
        public int describeContents() {
            return 0;
        }

        private void fillParcelString(Parcel parcel, String s) {
            if (s != null)
                parcel.writeString(s);
            else
                parcel.writeString("");
        }
        private void fillParcelFloat(Parcel parcel, Float f) {
            if (f != null)
                parcel.writeFloat(f);
            else
                parcel.writeFloat(Constants.INVALID_PRICE);
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            fillParcelString(parcel, mBarcode);
            fillParcelString(parcel, mName);
            fillParcelString(parcel, mModel);
            fillParcelString(parcel, mPhotoURL);
            fillParcelString(parcel, mLocation);
            fillParcelString(parcel, mLocationBarcode);
            fillParcelString(parcel, mSerial);
            fillParcelFloat(parcel, mPrice);
            fillParcelString(parcel, mOrderCode);

            //parcel.writeInt(mQuantIn);
            //parcel.writeInt(mTotalAvailable);
            parcel.writeInt(mType);
            parcel.writeInt(mQuantPerPack);
            if (mPici != null)
                parcel.writeInt(mPici);
            else
                parcel.writeInt(Constants.INVALID_PICI);
            fillParcelString(parcel, mLaiYuan);
            fillParcelString(parcel, mBeiZhu);
            if (mPcs != null)
                parcel.writeInt(mPcs);
            else
                parcel.writeInt(Constants.INVALID_QUANT);
            fillParcelString(parcel, mPhotoId);
        }

        public PreCheckInBigInfo(Parcel in) {
            mBarcode = in.readString();
            mName = in.readString();
            mModel = in.readString();
            mPhotoURL = in.readString();
            mLocation = in.readString();
            mLocationBarcode = in.readString();
            mSerial = in.readString();
            mPrice = in.readFloat();
            mOrderCode = in.readString();
            //mQuantIn = in.readInt();
            //mTotalAvailable = in.readInt();
            mType = in.readInt();
            mQuantPerPack = in.readInt();
            mPici = in.readInt();
            mLaiYuan = in.readString();
            mBeiZhu = in.readString();
            mPcs = in.readInt();
            mPhotoId = in.readString();
        }

        public void fillComponentInfo(ComponentInfo componentInfo) {
         /*   Parcel parcel = Parcel.obtain();
            writeToParcel(parcel, 0);
            PreCheckInBigInfo preCheckInBigInfo = new PreCheckInBigInfo(parcel);*/
            if (mBarcode != null)
                componentInfo.mBarcode = mBarcode;
            else
                componentInfo.mBarcode = Constants.INVALID_BARCODE;
            if (mName != null)
                componentInfo.mName = mName;
            else
                componentInfo.mName = "";
            if (mSerial != null)
                componentInfo.mSerial = mSerial;
            else
                componentInfo.mSerial = "";
            if (mModel != null) {
                componentInfo.mModel = mModel;
            } else
                componentInfo.mModel = "";
            componentInfo.mLocation = mLocation;
            componentInfo.mLocationBarcode = mLocationBarcode;
            componentInfo.mPici = mPici;
            componentInfo.mLaiYuan = mLaiYuan;
            componentInfo.mBeiZhu = mBeiZhu;
            componentInfo.mSerial = mSerial;
            componentInfo.mPrice = mPrice;
            componentInfo.mQuantPerPack = mQuantPerPack;
            componentInfo.mType = mType;
            componentInfo.mPhotoURL = mPhotoURL;
            componentInfo.mQuantIn = Constants.INVALID_QUANT;
            componentInfo.mTotalAvailable = Constants.INVALID_QUANT;
            //parcel.recycle();
        }

        public PreCheckInBigInfo() {
        }

        public static final Parcelable.Creator<PreCheckInBigInfo> CREATOR = new Parcelable.Creator<PreCheckInBigInfo>() {

            public PreCheckInBigInfo createFromParcel(Parcel in) {
                return new PreCheckInBigInfo(in);
            }

            public PreCheckInBigInfo[] newArray(int size) {
                return new PreCheckInBigInfo[size];
            }
        };
    }
}
