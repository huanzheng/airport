package com.invent.airport.requests;

import com.android.volley.VolleyError;

@SuppressWarnings("serial")
public final class AirportError extends VolleyError {
    public final int mErrorCode;
    public final String mErrorMsg;

    public AirportError(int errorCode, String errorMsg) {
        super(errorMsg);
        mErrorCode = errorCode;
        mErrorMsg = errorMsg;
    }
}
