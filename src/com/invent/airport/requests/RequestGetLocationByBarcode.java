package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestGetLocationByBarcode extends CookieGsonRequest<RequestGetLocationByBarcode.LocationInfo> {


    public RequestGetLocationByBarcode(String barCode, Response.Listener<LocationInfo> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getURLOfGetLocationByBarcode(barCode),
                LocationInfo.class, listener, errorListener);
    }

    private static String getURLOfGetLocationByBarcode(String barcode) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_LOCATION_BY_BARCODE);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.SEPERATER).append(barcode);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "URL of getLocationByBarcode is " + ret);
        return ret;
    }

    public static class LocationInfo {
        @SerializedName("locationBarcode")
        String mLocationBarcode;
        @SerializedName("location")
        String mLocation;
    }
}
