package com.invent.airport.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Basically every json POST request should inheritate from this class and implement getBody function
 */
public class CookieFileUploadRequest extends CookieGsonRequest<CookieFileUploadRequest.UploadURL> {
    private static final String TAG = "CookieFileUploadRequest";
    private File mFile;
    private String mRecordID;

    private MultipartEntity entity = new MultipartEntity();
    private static final String FILE_PART_NAME = "image";
    private final String mStringPartKey;

    public CookieFileUploadRequest(String url, File file, String recId, String stringPartKey, Response.Listener<UploadURL> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, UploadURL.class, listener, errorListener);
        Log.i(TAG, "File tend to be uploaded is " + file.getAbsolutePath());
        mFile = file;
        mRecordID = recId;
        mStringPartKey = stringPartKey;
        buildMultipartEntity();
    }

    @SuppressWarnings("deprecation")
    private void buildMultipartEntity() {
        if (mFile == null || !mFile.exists()) {
            Log.e(TAG, "WTF, File does not exist");
            return;
        }
        byte[] data = new byte[(int) mFile.length()];
        try {
            new FileInputStream(mFile).read(data);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to retrieve data from file ");
        }
        String filePath = mFile.getAbsolutePath();
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        String mimeType = "image/";
        if ("jpeg".equals(suffix) || "jpg".equals(suffix)) {
            mimeType += "jpeg";
        } else if ("png".equals(suffix)) {
            mimeType += suffix;
        }
        Log.i(TAG, "mimeType is " + mimeType);
        Log.i(TAG, "File part path is " + mFile.getAbsolutePath());
        entity.addPart(FILE_PART_NAME, new ByteArrayBody(data, mimeType, mFile.getAbsolutePath()));
        try {
            Log.i(TAG, "String part, key is " + mStringPartKey + " value is " + mRecordID);
            entity.addPart(mStringPartKey, new StringBody(mRecordID));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "buildMultipartEntity failed " + e.getMessage());
        }
    }

    @Override
    public String getBodyContentType() {
        String contentType = entity.getContentType().getValue();
        Log.i(TAG, "ContentType is " + contentType);
        return contentType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            Log.e(TAG, "IOException writing to ByteArrayOutputStream");
        }
        byte[] ret = bos.toByteArray();
        if (ret != null) {
            Log.i(TAG, "Body size is " + ret.length);
        } else {
            Log.e(TAG, "No body!");
        }
        return ret;
    }

    public static class UploadURL {
        @SerializedName("picUrl")
        public String mPicURL;
    }
}
