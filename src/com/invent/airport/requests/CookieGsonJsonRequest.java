package com.invent.airport.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.intel.aware.utils.L;

import java.io.UnsupportedEncodingException;

/**
 * Basically every json POST request should inheritate from this class and implement getBody function
 */
public abstract class CookieGsonJsonRequest<T> extends CookieGsonRequest<T> {
    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private static final String TAG = "CookieGsonJsonRequest";
    private Object mInput;

    public CookieGsonJsonRequest(String url, Object input, Class<T> objectClass, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, objectClass, listener, errorListener);
        mInput = input;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            String body = mInput == null ? null : mGson.toJson(mInput);
            L.i(TAG, "getBody of " + mInput.getClass().getCanonicalName() + " is " + body);
            return body == null ? null : body.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            L.e(TAG, "Unsupported Encoding while trying to get the bytes");
            return null;
        }
    }
}
