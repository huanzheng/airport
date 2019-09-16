package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

public final class RequestGetUserDetail extends CookieGsonRequest<RequestGetUserDetail.User> {
    //private static final String TAG = "RequestGetUserDetail";

    public RequestGetUserDetail(Response.Listener<RequestGetUserDetail.User> listener, Response.ErrorListener errorListener) {
        super(Method.GET, Constants.getFunctionURL(Constants.FUNC_GET_USER_DETAILS), User.class, listener, errorListener);
    }

    public static class User {
        @SerializedName("groupId")
        public Integer mGroupId;
        @SerializedName("username")
        public String mUsername;
        @SerializedName("realname")
        public String mRealname;
        @SerializedName("avatar")
        public String mAvatar;
        @SerializedName("usergroup")
        public String mUserGroup;
    }
}
