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
import com.invent.airport.requests.RequestCheckInWithBarCode;
import com.invent.airport.requests.RequestPreCheckInHandled;
import com.invent.airport.requests.RequestUploadCheckInPic;

public final class ActivityCheckInWithBarcode extends ActivityWithAssetInfo {
    EditText mQuantity;
    EditText mLocationBarcode;
    EditText mLocationName;

    Button mCheckIn;
    Button mCancel;
    Button mAddPic;
    Button mUploadPic;

    //private static final String TAG = "ActivityCheckInWithBarcode";

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_check_in_withbarcode);

        findFragmentPosition();

        //findViewByIDs();

        // mAssetInfo = getIntent().getParcelableExtra(Constants.KEY_ASSET_INFO);
        //setAssetInfo(mAssetInfo);
        addAssetInfo();

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
        
        mCheckIn = (Button) findViewById(R.id.aty_checkin_with_barcode_checkin);
        mCancel = (Button) findViewById(R.id.aty_checkin_with_barcode_cancel);
        mAddPic = (Button) findViewById(R.id.add_pic);
        mUploadPic = (Button) findViewById(R.id.upload_pic);
        mAssetImageView = (ImageView) findViewById(R.id.asset_new_image);

        mLocationBarcode.setText(mAssetInfo.mLocationBarcode);
        mLocationName.setText(mAssetInfo.mLocation);

        if (mPreCheckInOutInfo != null) {
            mQuantity.setText("" + mPreCheckInOutInfo.mPCs);
        }
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCheckIn.setOnClickListener(new View.OnClickListener() {
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

        mUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAssetImage == null) {
                    toastInfo(getString(R.string.pls_add_pic_first));
                    return;
                }
                RequestUploadCheckInPic requestUploadCheckInPic = new RequestUploadCheckInPic(CameraUtil.fileOfUri(mAssetImage), mAssetInfo.mBarcode, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
                    @Override
                    public void onResponse(CookieFileUploadRequest.UploadURL response) {
                        uploadFinish();
                        //mImageCacheLRU.remove(Constants.getImageURL(response.mPicURL));
                        //mRequestQueue.getCache().remove(Constants.getImageURL(response.mPicURL));

                        // mImageLoader.removeCache(Constants.getImageURL(response.mPicURL));
                        AirportApp.getInstance().removeCache(Constants.getImageURL(response.mPicURL));
                        mAssetInfo.mPhotoURL = response.mPicURL;
                        //mAssetImage.setImageUrl(Constants.getImageURL(response.mPicURL), mImageLoader);
                        replaceAssetInfo();
                    }
                }, mErrorListener);
                uploadStart();
                mRequestQueue.add(requestUploadCheckInPic);
            }
        });
    }

    @Override
    protected void dialogConfirmed() {
        startCheckIn();
    }

    private void startCheckIn() {
        if (checkValidInput()) {
            final int quantity = Integer.valueOf(mQuantity.getText().toString());
            final String locationName = mLocationName.getText().toString();
            final String locationBarcode = mLocationBarcode.getText().toString();
            RequestCheckInWithBarCode.CheckInWithBarCode checkInWithBarcode = new RequestCheckInWithBarCode.CheckInWithBarCode();
            checkInWithBarcode.mBarCode = mAssetInfo.mBarcode;
            checkInWithBarcode.mQuant = quantity;
            if (!locationBarcode.isEmpty()) {
                checkInWithBarcode.mLocationBarcode = locationBarcode;
            }
            if (!locationName.isEmpty()) {
                checkInWithBarcode.mLocation = locationName;
            }
            RequestCheckInWithBarCode requestCheckInWithBarCode = new RequestCheckInWithBarCode(checkInWithBarcode, new Response.Listener<CookieGsonRequest.Succeed>() {
                @Override
                public void onResponse(CookieGsonRequest.Succeed response) {
                    checkInFinish();
                    toastInfo("入库成功");

                    // update UI
                    if (locationBarcode.equals(mAssetInfo.mLocationBarcode)
                            || locationName.equals(mAssetInfo.mLocation)) {
                        if (mAssetInfo.mQuantIn != Constants.INVALID_QUANT) {
                            // No need to calculate for invalid quantity, thus does not affect display
                            mAssetInfo.mQuantIn += quantity;
                        }
                        mFragmentAssetInfo.setAssetInfo(mAssetInfo);
                    }
                    if (mPreCheckInOutInfo != null) {
                        // need to further mark preCheckIn Handled
                        handlePreCheckIn();
                    } else {
                        showFinishDialog();
                    }
                }
            }, mErrorListener);
            checkInStart();
            mRequestQueue.add(requestCheckInWithBarCode);
        }
    }

    void checkInStart() {
        mCheckIn.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_checkin_info));
    }

    void checkInFinish() {
        mCheckIn.setEnabled(true);
        dismissProgressDialog();
    }

    void uploadStart() {
        mUploadPic.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pic_in_progress));
    }

    void uploadFinish() {
        mUploadPic.setEnabled(true);
        dismissProgressDialog();
    }

    void handleStart() {
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.pre_checkin_handle_inprogress));
    }

    void handleFinish() {
        dismissProgressDialog();
    }

    void handlePreCheckIn() {
        if (mPreCheckInOutInfo != null) {
            RequestPreCheckInHandled requestPreCheckInHandled = new RequestPreCheckInHandled(new CookieGsonRequest.RecordID(mPreCheckInOutInfo.mRecID),
                    new Response.Listener<CookieGsonRequest.Succeed>() {
                        @Override
                        public void onResponse(CookieGsonRequest.Succeed response) {
                            mPreCheckInOutInfo = null;
                            handleFinish();
                            showFinishDialog();
                        }
                    }, mErrorListener);
            handleStart();
            mRequestQueue.add(requestPreCheckInHandled);
        }
    }

    private boolean checkValidInput() {

        if (mQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_checkin_quantity));
            return false;
        } else {
            try {
                @SuppressWarnings("unused")
                Integer quant = Integer.valueOf(mQuantity.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.info_checkin_number_must_be_number));
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
        checkInFinish();
        uploadFinish();
        handleFinish();
    }
}
