package com.invent.airport.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieFileUploadRequest;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestCheckInWithoutBarCode;
import com.invent.airport.requests.RequestGetOutStandingPreCheckIns;
import com.invent.airport.requests.RequestPreCheckInHandled;
import com.invent.airport.requests.RequestUploadCheckInPic;

//TODO handle precheckin
public final class ActivityCheckInWithoutBarcode extends AirportBaseActivity {
    EditText mName;
    EditText mModel;
    CheckBox mControlled;
    EditText mSerial;
    EditText mPrice;
    EditText mOrderCode;
    EditText mPcsPerPack;

    EditText mQuantity;
    EditText mLocationBarcode;
    EditText mLocationName;

    EditText mPici;
    EditText mLaiyuan;
    EditText mBeizhu;

    Button mCheckIn;
    Button mCancel;
    Button mAddPic;
    Button mUploadPic;
    String mAssetBarcode;
    String mAssetName;
    private boolean mPendingPrint = false;
    private RequestGetOutStandingPreCheckIns.PreCheckInOutInfo mPreCheckInOutInfo;
    private static final String TAG = "ActivityCheckInWithoutBarcode";

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_check_in_withoutbarcode);
        mPreCheckInOutInfo = getIntent().getParcelableExtra(Constants.KEY_PRECHECK_INOUT_INFO);
        mName = (EditText) findViewById(R.id.asset_check_in_nob_name);
        mModel = (EditText) findViewById(R.id.asset_check_in_nob_model);
        mSerial = (EditText) findViewById(R.id.asset_check_in_nob_serial);
        mPrice = (EditText) findViewById(R.id.asset_check_in_nob_price);
        mOrderCode = (EditText) findViewById(R.id.asset_check_in_nob_order_code);
        mPcsPerPack = (EditText) findViewById(R.id.asset_check_in_nob_pcs_per_pack);
        mControlled = (CheckBox) findViewById(R.id.asset_check_in_controlled);
        mPici = (EditText) findViewById(R.id.asset_check_in_nob_pici);
        mLaiyuan = (EditText) findViewById(R.id.asset_check_in_nob_laiyuan);
        mBeizhu = (EditText) findViewById(R.id.asset_check_in_nob_beizhu);

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
        
        setPreCheckInOutInfoToViews();
        //TODO, remove
        // mLocationBarcode.setText(Constants.LB);

        mCheckIn = (Button) findViewById(R.id.aty_check_in_without_barcode_checkin);
        mCancel = (Button) findViewById(R.id.aty_check_in_without_barcode_cancel);
        mAddPic = (Button) findViewById(R.id.add_pic);
        mUploadPic = (Button) findViewById(R.id.upload_pic);
        mUploadPic.setVisibility(View.GONE); // No need for now
        mAssetImageView = (ImageView) findViewById(R.id.asset_new_image);

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
                uploadPic();
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
        Log.i(TAG, "dialogConfirmed");
        startCheckIn();
    }

    void setPreCheckInOutInfoToViews() {
        if (mPreCheckInOutInfo != null) {
            mName.setText(mPreCheckInOutInfo.mName);
            mModel.setText(mPreCheckInOutInfo.mModel);
            mSerial.setText(mPreCheckInOutInfo.mSerial);
            mPrice.setText("" + mPreCheckInOutInfo.mPrice);
            mOrderCode.setText(mPreCheckInOutInfo.mOrderCode);
            mPcsPerPack.setText("" + mPreCheckInOutInfo.mQuantPerPack);
            if (mPreCheckInOutInfo.mPici != Constants.INVALID_PICI) {
                mPici.setText(""+mPreCheckInOutInfo.mPici);
            }
            mLaiyuan.setText(mPreCheckInOutInfo.mLaiYuan);
            mBeizhu.setText(mPreCheckInOutInfo.mBeiZhu);
            if (mPreCheckInOutInfo.mType == Constants.ASSET_TYPE_CONTROLLED) {
                mControlled.setChecked(true);
            } else {
                mControlled.setChecked(false);
            }
            mLocationBarcode.setText(mPreCheckInOutInfo.mLocationBarcode);
            mLocationName.setText(mPreCheckInOutInfo.mLocation);
            mQuantity.setText("" + mPreCheckInOutInfo.mPCs);
        }
    }

    void uploadPic() {
        if (mAssetBarcode == null) {
            toastInfo(getString(R.string.pls_checkin_before_upload_pic));
            return;
        }
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_add_pic_first));
            return;
        }
        RequestUploadCheckInPic requestUploadCheckInPic = new RequestUploadCheckInPic(CameraUtil.fileOfUri(mAssetImage), mAssetBarcode, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
            @Override
            public void onResponse(CookieFileUploadRequest.UploadURL response) {
                uploadFinish();
                toastInfo(getString(R.string.upload_pic_succeeded));
                if (mPendingPrint) {
                    askIfNeedPrint();
                } else {
                    showFinishDialog();
                }
            }
        }, mErrorListener);
        uploadStart();
        mRequestQueue.add(requestUploadCheckInPic);
    }

    void askIfNeedPrint() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("信息")
                .setMessage("上传成功，需要打印条码吗？")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mPendingPrint = false;
                        startPrint(mAssetName, mAssetBarcode);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogCanceled();
            }
        }).create().show();
    }

    void startCheckIn() {
        if (checkValidInput()) {
            RequestCheckInWithoutBarCode.CheckInWithoutBarCode checkInWithoutBarCode = new RequestCheckInWithoutBarCode.CheckInWithoutBarCode();
            checkInWithoutBarCode.mName = mName.getText().toString();
            mAssetName = checkInWithoutBarCode.mName;
            checkInWithoutBarCode.mModel = mModel.getText().toString();
            checkInWithoutBarCode.mSerial = mSerial.getText().toString();
            checkInWithoutBarCode.mPrice = mPrice.getText().toString().isEmpty() ? null : Float.valueOf(mPrice.getText().toString());
            checkInWithoutBarCode.mOrderCode = mOrderCode.getText().toString();
            checkInWithoutBarCode.mPiecesPerPackage = mPcsPerPack.getText().toString().isEmpty() ? null : Integer.valueOf(mPcsPerPack.getText().toString());
            checkInWithoutBarCode.mPieces = mQuantity.getText().toString().isEmpty() ? null : Integer.valueOf(mQuantity.getText().toString());
            checkInWithoutBarCode.mLocationBarCode = mLocationBarcode.getText().toString();
            checkInWithoutBarCode.mLocation = mLocationName.getText().toString();
            checkInWithoutBarCode.mPici = !mPici.getText().toString().isEmpty() ? Integer.valueOf(mPici.getText().toString()) : null;
            checkInWithoutBarCode.mLaiYuan = !mLaiyuan.getText().toString().isEmpty() ? mLaiyuan.getText().toString() : null;
            checkInWithoutBarCode.mBeiZhu = !mBeizhu.getText().toString().isEmpty() ? mBeizhu.getText().toString() : "";
            if (mControlled.isChecked()) {
                checkInWithoutBarCode.mType = Constants.ASSET_TYPE_CONTROLLED;
            } else {
                checkInWithoutBarCode.mType = Constants.ASSET_TYPE_NONE_CONTROLLED;
            }

            RequestCheckInWithoutBarCode requestCheckInWithoutBarCode = new RequestCheckInWithoutBarCode(checkInWithoutBarCode, new Response.Listener<RequestCheckInWithoutBarCode.BarCode>() {
                @Override
                public void onResponse(RequestCheckInWithoutBarCode.BarCode response) {
                    mCheckIn.setEnabled(true);
                    dismissProgressDialog();
                    toastInfo("入库成功,条码为 " + response.mBarCode);
                    mAssetBarcode = response.mBarCode;
                    if (mPreCheckInOutInfo == null && mAssetImage == null) {
                        // Only if nothing else to do, ask if need print
                        askIfNeedPrint();
                    } else {
                        if (mPreCheckInOutInfo != null) {
                            // handle pre check in first, leave pic upload to the end of pre-checkin handled
                            mPendingPrint = true;
                            handlePreCheckIn();
                        } else {
                            if (mAssetImage != null) {
                                mPendingPrint = true;
                                uploadPic();
                            } else {
                                showFinishDialog();
                            }
                        }
                    }
                }
            }, mErrorListener);
            showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_checkin_info));
            mCheckIn.setEnabled(false);
            mRequestQueue.add(requestCheckInWithoutBarCode);
        }
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
                            if (mAssetImage != null) {
                                // If asset image available, upload it
                                uploadPic();
                            } else {
                                if (mPendingPrint) {
                                    askIfNeedPrint();
                                } else {
                                    showFinishDialog();
                                }
                            }
                        }
                    }, mErrorListener);
            handleStart();
            mRequestQueue.add(requestPreCheckInHandled);
        }
    }

    @SuppressWarnings("unused")
    private boolean checkValidInput() {
        if (mName.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_name));
            return false;
        }
        if (mPrice.getText().toString().isEmpty()) {
            //toastInfo(getString(R.string.info_pls_input_price));
            //return false;
        } else {
            try {
                Float price = Float.valueOf(mPrice.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.info_price_must_be_number));
                return false;
            }
        }
        if (mPcsPerPack.getText().toString().isEmpty()) {
            //toastInfo(getString(R.string.info_pls_input_pcs_per_pack));
            //return false;
        } else {
            try {
                Integer pcs = Integer.valueOf(mPcsPerPack.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.info_pcs_per_pack_must_be_number));
                return false;
            }
        }
        /*
        if (mModel.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_model));
            return false;
        }
        if (mSerial.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_serial));
            return false;
        }

        if (mOrderCode.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_ordercode));
            return false;
        }
       */
        if (mQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_checkin_quantity));
            return false;
        } else {
            try {
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
        if (!mPici.getText().toString().isEmpty()) {
            try {
                Integer quant = Integer.valueOf(mPici.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo("批次需是数字");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        mCheckIn.setEnabled(true);
        uploadFinish();
        handleFinish();
    }

    void startPrint(String name, String barcode) {
        Intent i = new Intent(this, ActivityPrint.class);
        i.putExtra(ActivityPrint.KEY_PRINT_AST_NAME, name);
        i.putExtra(ActivityPrint.KEY_PRINT_AST_BARCODE, barcode);
        startActivity(i);
        finish();
    }
}
