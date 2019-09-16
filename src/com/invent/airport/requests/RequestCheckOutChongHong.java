package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

public class RequestCheckOutChongHong extends RequestChongHong {

    public RequestCheckOutChongHong(ChongHong input, Response.Listener<Succeed> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_CHECKOUT_CHONGHONG),
                input, listener, errorListener);
    }
}
