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
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieFileUploadRequest;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestCheckOut;
import com.invent.airport.requests.RequestPreCheckOutHandled;
import com.invent.airport.requests.RequestUploadCheckOutPic;

public final class ActivityCheckOut extends ActivityWithAssetInfo {

    EditText mQuantity;
    Button mAddPic;
    Button mCheckout;
    Button mCancel;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_check_out);

        findFragmentPosition();
        addAssetInfo();

        mQuantity = (EditText) findViewById(R.id.ast_checkout_quantity);
        mAddPic = (Button) findViewById(R.id.ast_check_out_add_pic);
        mCheckout = (Button) findViewById(R.id.aty_checkout_checkout);
        mCancel = (Button) findViewById(R.id.aty_checkout_cancel);
        mAssetImageView = (ImageView) findViewById(R.id.asset_new_image);
        mQuantity.setOnKeyListener(mOnKeyListener);
        if (mPreCheckInOutInfo != null) {
            mQuantity.setText("" + mPreCheckInOutInfo.mPCs);
        }
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
        checkOut();
    }

    void checkOut() {
        final int quantity;
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
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_add_checkout_pic));
            return;
        }
        RequestCheckOut.CheckOut checkOut = new RequestCheckOut.CheckOut();
        checkOut.mBarCode = mAssetInfo.mBarcode;
        checkOut.mQuant = quantity;
        checkOut.mLocationBarcode = mAssetInfo.mLocationBarcode;
        RequestCheckOut requestCheckOut = new RequestCheckOut(checkOut, new Response.Listener<CookieGsonRequest.RecordID>() {
            @Override
            public void onResponse(CookieGsonRequest.RecordID response) {
                checkOutFinish();
                toastInfo("出库成功,ID为 " + response.mRecordID);
                // Update UI
                if (mAssetInfo.mQuantIn != Constants.INVALID_QUANT) {
                    mAssetInfo.mQuantIn -= quantity;
                }
                mFragmentAssetInfo.setAssetInfo(mAssetInfo);
                // Start to upload pic
                RequestUploadCheckOutPic requestUploadCheckOutPic = new RequestUploadCheckOutPic(
                        CameraUtil.fileOfUri(mAssetImage), response.mRecordID, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
                    @Override
                    public void onResponse(CookieFileUploadRequest.UploadURL response) {
                        uploadFinish();
                        toastInfo(getString(R.string.upload_checkout_pic_succeeded));

                        // finally, we handle precheckout
                        if (mPreCheckInOutInfo != null) {
                            handlePreCheckOut();
                        } else {
                            showFinishDialog();
                        }
                    }
                }, mErrorListener);
                uploadStart();
                mRequestQueue.add(requestUploadCheckOutPic);
            }
        }, mErrorListener);
        checkOutStart();
        mRequestQueue.add(requestCheckOut);
    }

    void handlePreCheckOut() {
        if (mPreCheckInOutInfo != null) {
            RequestPreCheckOutHandled requestPreCheckOutHandled = new RequestPreCheckOutHandled(new CookieGsonRequest.RecordID(mPreCheckInOutInfo.mRecID),
                    new Response.Listener<CookieGsonRequest.Succeed>() {
                        @Override
                        public void onResponse(CookieGsonRequest.Succeed response) {
                            toastInfo("预出库记录处理成功");
                            handleFinish();
                            mPreCheckInOutInfo = null;
                            showFinishDialog();
                        }
                    }, mErrorListener);
            handleStart();
            mRequestQueue.add(requestPreCheckOutHandled);
        }
    }

    void handleStart() {
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.pre_checkout_handle_inprogress));
    }

    void handleFinish() {
        dismissProgressDialog();
    }

    void checkOutStart() {
        mCheckout.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_checkout_info));
    }

    void checkOutFinish() {
        mCheckout.setEnabled(true);
        dismissProgressDialog();
    }

    void uploadStart() {
        mCheckout.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pic_in_progress));
    }

    void uploadFinish() {
        mCheckout.setEnabled(true);
        dismissProgressDialog();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        checkOutFinish();
        uploadFinish();
        handleFinish();
    }
}
