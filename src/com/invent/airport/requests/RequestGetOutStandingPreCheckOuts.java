package com.invent.airport.requests;

import com.android.volley.Response;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestGetOutStandingPreCheckOuts extends CookieGsonRequest<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut> {
    //private static final String TAG = "RequestGetOutStandingPreCheckIns";

    public RequestGetOutStandingPreCheckOuts(int page, Response.Listener<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getURLForPreCheckOuts(page), RequestGetOutStandingPreCheckIns.PendingPreCheckInOut.class, listener, errorListener);
    }

    private static String getURLForPreCheckOuts(int page) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_OUTSTANDING_PRE_CHECKOUTS);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.QUESTION);
        builder.append(Constants.KEY_PAGE).append(Constants.EQUAL).append(page);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "URL of pre checkout page " + page + " is " + ret);
        return ret;
    }
}
