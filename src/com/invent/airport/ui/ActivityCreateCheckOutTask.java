package com.invent.airport.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestCreateCheckOutTask;

public final class ActivityCreateCheckOutTask extends ActivityWithAssetInfo {

    EditText mQuantity;
    Button mCheckout;
    Button mCancel;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_create_check_out_task);

        findFragmentPosition();
        addAssetInfo();

        mQuantity = (EditText) findViewById(R.id.ast_checkouttask_quantity);
        mCheckout = (Button) findViewById(R.id.aty_create_checkout_task_checkout);
        mCancel = (Button) findViewById(R.id.aty_create_checkout_task_cancel);
        mQuantity.setOnKeyListener(mOnKeyListener);

        mCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void dialogConfirmed() {
        checkOut();
    }

    void checkOut() {
        int quantity;
        try {
            quantity = Integer.valueOf(mQuantity.getText().toString());
        } catch (NumberFormatException e) {
            toastInfo(getString(R.string.aty_checkout_quantity_invalid));
            return;
        }
        if (quantity < 0) {
            toastInfo(getString(R.string.number_cannot_be_negative));
            return;
        }
        RequestCreateCheckOutTask.CheckOutTask checkOutTask = new RequestCreateCheckOutTask.CheckOutTask();
        checkOutTask.mPCs = quantity;
        checkOutTask.mLocationBarcode = mAssetInfo.mLocationBarcode;
        checkOutTask.mBarcode = mAssetInfo.mBarcode;

        RequestCreateCheckOutTask requestCreateCheckOutTask = new RequestCreateCheckOutTask(
                checkOutTask, new Response.Listener<CookieGsonRequest.TaskID>() {
            @Override
            public void onResponse(CookieGsonRequest.TaskID response) {
                checkOutFinish();
                toastInfo("生成出库任务成功,ID为" + response.mTaskID);
                showFinishDialog();
            }
        }, mErrorListener);
        checkOutStart();
        mRequestQueue.add(requestCreateCheckOutTask);
    }

    void checkOutStart() {
        mCheckout.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.creating_checkout_task));
    }

    void checkOutFinish() {
        mCheckout.setEnabled(true);
        dismissProgressDialog();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        checkOutFinish();
    }
}
