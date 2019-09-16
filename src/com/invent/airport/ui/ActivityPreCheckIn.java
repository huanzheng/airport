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
import com.invent.airport.requests.RequestUploadPreCheckInPic;

public final class ActivityPreCheckIn extends ActivityWithAssetInfo {
    EditText mQuantity;
    EditText mLocationBarcode;
    EditText mLocationName;

    Button mPreCheckIn;
    Button mCancel;

    Button mAddPic;
    Button mUploadPic;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pre_check_in);

        findFragmentPosition();
        addAssetInfo();

        mQuantity = (EditText) findViewById(R.id.ast_pre_checkin_quantity);
        mPreCheckIn = (Button) findViewById(R.id.aty_pre_check_in_checkin);
        mCancel = (Button) findViewById(R.id.aty_pre_check_in_cancel);
        mAssetImageView = (ImageView) findViewById(R.id.asset_new_image);
        mAddPic = (Button) findViewById(R.id.add_pic);

        mQuantity = (EditText) findViewById(R.id.ast_check_in_input_quantity);
        mLocationBarcode = (EditText) findViewById(R.id.ast_check_in_input_location_barcode);
        mLocationName = (EditText) findViewById(R.id.ast_check_in_input_location_name);
        mQuantity.setOnKeyListener(mOnKeyListener);
        mLocationBarcode.setOnKeyListener(mOnKeyListener);
        mLocationName.setOnKeyListener(mOnKeyListener);
        View.OnFocusChangeListener mOnFocusedChangedListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mLocationBarcode.setText("");
                    mLocationName.setText("");
                }
            }
        };
        mLocationBarcode.setOnFocusChangeListener(mOnFocusedChangedListener);
        mLocationName.setOnFocusChangeListener(mOnFocusedChangedListener);
        mLocationBarcode.setText(mAssetInfo.mLocationBarcode);
        mLocationName.setText(mAssetInfo.mLocation);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPreCheckIn.setOnClickListener(new View.OnClickListener() {
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
        mUploadPic = (Button) findViewById(R.id.upload_pic);
        mUploadPic.setVisibility(View.GONE);
    }

    @Override
    protected void dialogConfirmed() {
        preCheckIn();
    }

    void preCheckIn() {
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_upload_pic_precheckin_first));
            return;
        }
        if (checkValidInput()) {
            RequestPreCheckIn.PreCheckInOut preCheckIn = new RequestPreCheckIn.PreCheckInOut();
            int quantity = Integer.valueOf(mQuantity.getText().toString());

            preCheckIn.setComponentInfo(mAssetInfo);
            preCheckIn.mPieces = quantity;
            preCheckIn.mLocationBarCode = !mLocationBarcode.getText().toString().isEmpty() ? mLocationBarcode.getText().toString() : "";
            preCheckIn.mLocation = !mLocationName.getText().toString().isEmpty() ? mLocationName.getText().toString() : "";

            RequestPreCheckIn requestPreCheckIn = new RequestPreCheckIn(preCheckIn, new Response.Listener<CookieGsonRequest.RecordID>() {
                @Override
                public void onResponse(CookieGsonRequest.RecordID response) {
                    preCheckInFinish();
                    toastInfo("预入库成功,ID为 " + response.mRecordID);

                    // upload pic for pre check in
                    RequestUploadPreCheckInPic requestUploadPreCheckInPic = new RequestUploadPreCheckInPic(CameraUtil.fileOfUri(mAssetImage),
                            response.mRecordID, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
                        @Override
                        public void onResponse(CookieFileUploadRequest.UploadURL response) {
                            uploadFinish();
                            toastInfo(getString(R.string.upload_pic_precheckin_succeeded));
                            showFinishDialog();
                        }
                    }, mErrorListener);
                    uploadStart();
                    mRequestQueue.add(requestUploadPreCheckInPic);
                }
            }, mErrorListener);
            preCheckInStart();
            mRequestQueue.add(requestPreCheckIn);
        }
    }

    void preCheckInStart() {
        mPreCheckIn.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pre_checkin_info));
    }

    void preCheckInFinish() {
        mPreCheckIn.setEnabled(true);
        dismissProgressDialog();
    }

    void uploadStart() {
        mPreCheckIn.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pic_in_progress));
    }

    void uploadFinish() {
        mPreCheckIn.setEnabled(true);
        dismissProgressDialog();
    }


    private boolean checkValidInput() {

        if (mQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.pls_input_pre_checkin_num));
            return false;
        } else {
            try {
                @SuppressWarnings("unused")
                Integer quant = Integer.valueOf(mQuantity.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.pre_check_in_number_must_be_number));
                return false;
            }
        }
        if (mLocationBarcode.getText().toString().isEmpty()
                && mLocationName.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_l_barcode_or_name));
            return false;
        }
        return true;
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        preCheckInFinish();
    }
}
