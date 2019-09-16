package com.invent.airport.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.CookieFileUploadRequest;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestPreCheckIn;
import com.invent.airport.requests.RequestPreCheckOut;
import com.invent.airport.requests.RequestUploadPreCheckOutPic;

public final class ActivityPreCheckOut extends ActivityWithAssetInfo {
    EditText mQuantity;

    Button mPreCheckOut;
    Button mCancel;

    Button mAddPic;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pre_check_out);

        findFragmentPosition();
        addAssetInfo();

        mQuantity = (EditText) findViewById(R.id.ast_pre_checkout_quantity);
        mQuantity.setOnKeyListener(mOnKeyListener);

        mPreCheckOut = (Button) findViewById(R.id.aty_pre_check_out_checkout);
        mCancel = (Button) findViewById(R.id.aty_pre_check_out_cancel);
        mAssetImageView = (ImageView) findViewById(R.id.asset_new_image);
        mAddPic = (Button) findViewById(R.id.ast_pre_checkout_add_pic);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPreCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        mAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CameraUtil.SDCardWritable()) {
                    toastInfo(getString(R.string.err_sdcard_issue));
                    return;
                }
                mAssetImage = CameraUtil.startCameraCapture((Activity) mContext, CameraUtil.CAMERA_REQUEST);
            }
        });
    }

    @Override
    protected void dialogConfirmed() {
        preCheckOut();
    }

    void preCheckOut() {
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_add_pic_precheckout_first));
            return;
        }
        if (checkValidInput()) {
            RequestPreCheckIn.PreCheckInOut preCheckOut = new RequestPreCheckIn.PreCheckInOut();
            int quantity = Integer.valueOf(mQuantity.getText().toString());

            preCheckOut.setComponentInfo(mAssetInfo);
            preCheckOut.mPieces = quantity;

            RequestPreCheckOut requestPreCheckOut = new RequestPreCheckOut(
                    preCheckOut, new Response.Listener<CookieGsonRequest.RecordID>() {
                @Override
                public void onResponse(CookieGsonRequest.RecordID response) {
                    preCheckOutFinish();
                    toastInfo("预出库成功,ID为 " + response.mRecordID);

                    // upload pic
                    RequestUploadPreCheckOutPic requestUploadPreCheckOutPic = new RequestUploadPreCheckOutPic(CameraUtil.fileOfUri(mAssetImage),
                            response.mRecordID, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
                        @Override
                        public void onResponse(CookieFileUploadRequest.UploadURL response) {
                            uploadFinish();
                            toastInfo(getString(R.string.upload_pic_precheckout_succeeded));
                            showFinishDialog();
                        }
                    }, mErrorListener);
                    uploadStart();
                    mRequestQueue.add(requestUploadPreCheckOutPic);
                }
            }, mErrorListener);
            preCheckOutStart();
            mRequestQueue.add(requestPreCheckOut);
        }
    }

    void preCheckOutStart() {
        mPreCheckOut.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pre_checkout_info));
    }

    void preCheckOutFinish() {
        mPreCheckOut.setEnabled(true);
        dismissProgressDialog();
    }

    void uploadStart() {
        mPreCheckOut.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pic_in_progress));
    }

    void uploadFinish() {
        mPreCheckOut.setEnabled(true);
        dismissProgressDialog();
    }


    private boolean checkValidInput() {

        if (mQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.pls_input_precheckout_number));
            return false;
        } else {
            try {
                @SuppressWarnings("unused")
                Integer quant = Integer.valueOf(mQuantity.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.pre_checkout_number_must_be_number));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        preCheckOutFinish();
    }
}