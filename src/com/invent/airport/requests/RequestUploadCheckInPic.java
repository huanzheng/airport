package com.invent.airport.requests;

import com.android.volley.Response;
import com.invent.airport.constants.Constants;

import java.io.File;

public final class RequestUploadCheckInPic extends CookieFileUploadRequest {

    public RequestUploadCheckInPic(File file, String recId, Response.Listener<UploadURL> listener, Response.ErrorListener errorListener) {
        super(Constants.getFunctionURL(Constants.FUNC_ADD_PIC_FOR_CHECK_IN), file, recId, "barcode", listener, errorListener);
    }
}
