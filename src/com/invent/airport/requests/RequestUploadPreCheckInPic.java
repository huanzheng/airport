package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

import java.io.File;

public final class RequestUploadPreCheckInPic extends CookieFileUploadRequest {

    public RequestUploadPreCheckInPic(File file, String recId, Response.Listener<UploadURL> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_ADD_PIC_FOR_PRE_CHECK_IN), file, recId, "recId", listener, errorListener);
    }
}
