package com.invent.airport.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

import java.util.ArrayList;

public final class RequestLocationQuery extends CookieGsonRequest<RequestLocationQuery.QueryLocationResult> {
    private static final String PAGE = "page";
    private static final String KEYWORD = "keyword";

    public RequestLocationQuery(String locationBarCode, int pageNum, Response.Listener<QueryLocationResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getUrlOfLocationQuery(locationBarCode, pageNum),
                QueryLocationResult.class, listener, errorListener);
    }

    // TODO add page number

    private static String getUrlOfLocationQuery(String barcode, int pageNum) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_LOCATION_QUERY);
        ret = url + Constants.QUESTION
                + KEYWORD + Constants.EQUAL + barcode
                + Constants.AND
                + PAGE + Constants.EQUAL + pageNum;
        L.i(Constants.URL_TAG, "URL of locationQuery is " + ret);
        return ret;
    }

    public static class LocationInfo implements Parcelable {
        @SerializedName("class")
        public String mClass;
        @SerializedName("id")
        public Integer mID;
        @SerializedName("barcode")
        public String mBarcode;
        @SerializedName("name")
        public String mName;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mClass);
            parcel.writeInt(mID);
            parcel.writeString(mBarcode);
            parcel.writeString(mName);
        }

        public LocationInfo(Parcel in) {
            mClass = in.readString();
            mID = in.readInt();
            mBarcode = in.readString();
            mName = in.readString();
        }

        public static final Parcelable.Creator<LocationInfo> CREATOR = new Parcelable.Creator<LocationInfo>() {

            public LocationInfo createFromParcel(Parcel in) {
                return new LocationInfo(in);
            }

            public LocationInfo[] newArray(int size) {
                return new LocationInfo[size];
            }
        };
    }

    public static class QueryLocationResult {
        @SerializedName("page")
        public int mCurPageNum;
        @SerializedName("locations")
        public ArrayList<LocationInfo> mLocations;
    }
}
