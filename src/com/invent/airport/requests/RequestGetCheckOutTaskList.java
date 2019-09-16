package com.invent.airport.requests;

import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;
import com.invent.airport.constants.Constants;

import java.util.ArrayList;

public final class RequestGetCheckOutTaskList extends CookieGsonRequest<RequestGetCheckOutTaskList.CheckOutTaskList> {
    public RequestGetCheckOutTaskList(Response.Listener<CheckOutTaskList> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                Constants.getFunctionURL(Constants.FUNC_GET_CHECKOUT_TASK_LIST),
                CheckOutTaskList.class, listener, errorListener);
    }

    public static class CheckOutTaskList {
        @SerializedName("tasks")
        public ArrayList<Integer> mTasks;
    }
}
