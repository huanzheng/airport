package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

public final class RequestPreCheckOut extends CookieGsonJsonRequest<CookieGsonRequest.RecordID> {

    public RequestPreCheckOut(RequestPreCheckIn.PreCheckInOut input, Response.Listener<RecordID> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_PRE_CHECKOUT),
                input, RecordID.class, listener, errorListener);
    }
}
