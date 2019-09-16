package com.invent.airport.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

import java.util.ArrayList;

public final class QueryRequest extends CookieGsonRequest<QueryRequest.QueryResult> {
    public static final int QUERY_WITH_BARCODE = 1;
    public static final int QUERY_WITHOUT_BARCODE = 2;
    public static final int QUERY_WITH_BARCODE_PURE_ASSET_INFO = 3;

    //private static final String MODEL = "model";
    private static final String MODEL = "orderCode";
    private static final String NAME = "name";
    private static final String PAGE = "page";

    public QueryRequest(Query query, Response.Listener<QueryResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getQueryUrl(query), QueryResult.class, listener, errorListener);
    }

    // query is valid upon here, no fields should be null
    private static String getQueryUrl(Query query) {
        String ret = null;
        switch (query.mQueryType) {
            case QUERY_WITH_BARCODE: {
                String funUrl = Constants.getFunctionURL(Constants.FUNC_QUERY_WITH_BARCODE);
                ret = funUrl + Constants.SEPERATER + query.mBarcode + Constants.QUESTION
                        + PAGE + Constants.EQUAL + query.mPage;
                break;
            }
            case QUERY_WITHOUT_BARCODE: {
                String funUrl = Constants.getFunctionURL(Constants.FUNC_QUERY);
                StringBuilder sb = new StringBuilder();
                sb.append(Constants.QUESTION);
                if (query.mModel != null && !query.mModel.isEmpty()) {
                    sb.append(MODEL).append(Constants.EQUAL).append(query.mModel);
                }
                if (query.mName != null && !query.mName.isEmpty()) {
                    sb.append(Constants.AND).append(NAME).append(Constants.EQUAL).append(query.mName);
                }
                sb.append(Constants.AND).append(PAGE).append(Constants.EQUAL).append(query.mPage);
                ret = funUrl + sb.toString();
                break;
            }
            case QUERY_WITH_BARCODE_PURE_ASSET_INFO: {
                String funUrl = Constants.getFunctionURL(Constants.FUNC_QUERY_WITH_BARCODE_PURE_ASSERT_INFO);
                ret = funUrl + Constants.QUESTION + "barcode=" + query.mBarcode + Constants.AND
                        + PAGE + Constants.EQUAL + query.mPage;
                break;
            }
        }
        L.i(Constants.URL_TAG, "QueryRequest URL is " + ret);
        return ret;
    }

    public static class Query implements Parcelable {
        public Integer mQueryType;
        public String mBarcode;
        public String mModel;
        public String mName;
        public Integer mPage;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(mQueryType);
            parcel.writeString(mBarcode);
            parcel.writeString(mModel);
            parcel.writeString(mName);
            parcel.writeInt(mPage);
        }

        public Query() {
        }

        public Query(Parcel parcel) {
            mQueryType = parcel.readInt();
            mBarcode = parcel.readString();
            mModel = parcel.readString();
            mName = parcel.readString();
            mPage = parcel.readInt();
        }

        public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() {

            public Query createFromParcel(Parcel in) {
                return new Query(in);
            }

            public Query[] newArray(int size) {
                return new Query[size];
            }
        };
    }

    public static class QueryResult {
        @SerializedName("curPageNum")
        public int mCurPageNum;
        @SerializedName("assets")
        public ArrayList<ComponentInfo> mCurPage;
    }
}
