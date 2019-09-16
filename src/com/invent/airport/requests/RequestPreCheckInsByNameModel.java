package com.invent.airport.requests;

import com.android.volley.Response;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestPreCheckInsByNameModel extends CookieGsonRequest<CookieGsonRequest.QueryPreCheckInBigInfoResult> {
    private static final String PAGE = "page";

    public RequestPreCheckInsByNameModel(String name, String model, int pageNum, Response.Listener<QueryPreCheckInBigInfoResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                getURLOfPreCheckInsByNameModel(name, model, pageNum),
                QueryPreCheckInBigInfoResult.class, listener, errorListener);
    }

    // TODO add page number

    private static String getURLOfPreCheckInsByNameModel(String name, String model, int pageNum) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_GET_PRECHECKIN_BY_NAME_MODEL);
        StringBuilder builder = new StringBuilder();
        ret = url + builder.toString() + Constants.QUESTION
                + "name=" + name
                + Constants.AND
                + "orderCode=" + model
                + Constants.AND
                + PAGE + Constants.EQUAL + pageNum;
        L.i(Constants.URL_TAG, "URL of getURLOfPreCheckInsByNameModel is " + ret);
        return ret;
    }


}
