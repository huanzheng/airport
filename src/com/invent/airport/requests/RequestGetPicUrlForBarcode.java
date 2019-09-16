package com.invent.airport.requests;

import com.android.volley.Response;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestGetPicUrlForBarcode extends CookieGsonRequest<CookieGsonRequest.PicURL> {
    //private static final String TAG = "RequestGetPicUrlForBarcode";

    public RequestGetPicUrlForBarcode(String barCode, Response.Listener<PicURL> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getURLForGetPicURL(barCode), PicURL.class, listener, errorListener);
    }

    private static String getURLForGetPicURL(String barCode) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_PIC_URL_FOR_BARCODE);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.QUESTION);
        builder.append(Constants.KEY_BARCODE).append(Constants.EQUAL).append(barCode);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "PIC URL of " + barCode + " is " + ret);
        return ret;
    }
}
