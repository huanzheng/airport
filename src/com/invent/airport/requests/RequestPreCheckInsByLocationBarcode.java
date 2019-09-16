package com.invent.airport.requests;

import com.android.volley.Response;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestPreCheckInsByLocationBarcode extends CookieGsonRequest<CookieGsonRequest.QueryPreCheckInBigInfoResult> {
    private static final String PAGE = "page";

    public RequestPreCheckInsByLocationBarcode(String locationBarCode, int pageNum, Response.Listener<QueryPreCheckInBigInfoResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getURLOfPreCheckInsByLocationBarcode(locationBarCode, pageNum),
                QueryPreCheckInBigInfoResult.class, listener, errorListener);
    }

    // TODO add page number

    private static String getURLOfPreCheckInsByLocationBarcode(String barcode, int pageNum) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_PRECHECKIN_BY_LOCATIONBARCODE);
        StringBuilder builder = new StringBuilder();
        ret = url + builder.toString() + Constants.QUESTION
                + "locationBarcode=" + barcode
                + Constants.AND
                + PAGE + Constants.EQUAL + pageNum;
        L.i(Constants.URL_TAG, "URL of getURLOfPreCheckInsByLocationBarcode is " + ret);
        return ret;
    }
}
