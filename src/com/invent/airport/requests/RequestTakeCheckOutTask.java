package com.invent.airport.requests;

import com.android.volley.Response;
import com.intel.aware.utils.L;
import com.invent.airport.constants.Constants;

public final class RequestTakeCheckOutTask extends CookieGsonRequest<CookieGsonRequest.Succeed> {
    //private static final String TAG = "RequestTakeCheckOutTask";

    public RequestTakeCheckOutTask(String taskID, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getTakeCheckoutTaskURL(taskID), Succeed.class, listener, errorListener);
    }

    private static String getTakeCheckoutTaskURL(String taskID) {
        String ret = null;
        String url = Constants.getFunctionURL(Constants.FUNC_TAKE_CHECKOUT_TASK);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.SEPERATER);
        builder.append(taskID);
        ret = url + builder.toString();
        L.i(Constants.URL_TAG, "URL of take checkout task is " + ret);
        return ret;
    }
}
