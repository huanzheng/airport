package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

public class RequestCheckInChongHong extends RequestChongHong {

    public RequestCheckInChongHong(ChongHong input, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECKIN_CHONGHONG),
                input, listener, errorListener);
    }
}
