package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

import java.util.ArrayList;

public final class RequestAssetsByLocationBarcode extends CookieGsonRequest<RequestAssetsByLocationBarcode.QueryLocationResult> {
    private static final String PAGE = "page";

    public RequestAssetsByLocationBarcode(String locationBarCode, int pageNum, Response.Listener<QueryLocationResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getURLOfAssetsByLocationBarcode(locationBarCode, pageNum),
                QueryLocationResult.class, listener, errorListener);
    }

    // TODO add page number

    private static String getURLOfAssetsByLocationBarcode(String barcode, int pageNum) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_ASSETS_BY_LOCATION);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.SEPERATER).append(barcode);
        ret = url + builder.toString() + Constants.QUESTION
                + PAGE + Constants.EQUAL + pageNum;
        L.i(Constants.URL_TAG, "URL of getLocationByBarcode is " + ret);
        return ret;
    }

    public static class QueryLocationResult {
        @SerializedName("curPageNum")
        public int mCurPageNum;
        @SerializedName("assets")
        public ArrayList<ComponentInfo> mCurPage;
    }


}
