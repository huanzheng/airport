package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

public final class RequestPreCheckOutHandled extends CookieGsonJsonRequest<CookieGsonRequest.Succeed> {

    public RequestPreCheckOutHandled(RecordID input, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_PRE_CHECKOUT_HANDLED),
                input, Succeed.class, listener, errorListener);
    }

}
