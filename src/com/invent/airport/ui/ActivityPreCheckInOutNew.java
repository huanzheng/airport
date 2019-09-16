package com.invent.airport.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieFileUploadRequest;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestCheckOldPici;
import com.invent.airport.requests.RequestPreCheckIn;
import com.invent.airport.requests.RequestPreCheckOut;
import com.invent.airport.requests.RequestUploadPreCheckInPic;
import com.invent.airport.requests.RequestUploadPreCheckOutPic;

public final class ActivityPreCheckInOutNew extends AirportBaseActivity {
    EditText mName;
    EditText mModel;
    CheckBox mControlled;
    EditText mSerial;
    EditText mPrice;
    EditText mOrderCode;
    EditText mPcsPerPack;
    TextView mTitle;

    EditText mQuantity;
    EditText mLocationBarcode;
    EditText mLocationName;
    EditText mPici;
    EditText mLaiyuan;
    EditText mBeizhu;

    Button mPreCheckInOut;
    Button mCancel;
    Button mAddPic;
    Button mUploadPic;
    String mAssetBarcode;

    public static final int PRECHECKIN_NEW = 1;
    public static final int PRECHECKOUT_NEW = 2;
    public static final String KEY_PRECHECK_OP = "key_precheck_op";
    private int mPreCheckOP = PRECHECKIN_NEW;
    private CookieGsonRequest.ComponentInfo mComponentInfo;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mPreCheckOP = getIntent().getIntExtra(KEY_PRECHECK_OP, PRECHECKIN_NEW);
        mComponentInfo = getIntent().getParcelableExtra(Constants.KEY_ASSET_INFO);
        setContentView(R.layout.activity_pre_check_in_new);

        TextView labelQuantity = (TextView) findViewById(R.id.label_quantitiy);

        mName = (EditText) findViewById(R.id.asset_check_in_nob_name);
        mName.setHint(getString(R.string.no_blank));
        mModel = (EditText) findViewById(R.id.asset_check_in_nob_model);
        mSerial = (EditText) findViewById(R.id.asset_check_in_nob_serial);
        mPrice = (EditText) findViewById(R.id.asset_check_in_nob_price);
        mOrderCode = (EditText) findViewById(R.id.asset_check_in_nob_order_code);
        mPcsPerPack = (EditText) findViewById(R.id.asset_check_in_nob_pcs_per_pack);
        mTitle = (TextView) findViewById(R.id.aty_pre_check_in_out_new_title);
        mPici = (EditText) findViewById(R.id.asset_check_in_nob_pici);
        mLaiyuan = (EditText) findViewById(R.id.asset_check_in_nob_laiyuan);
        mBeizhu = (EditText) findViewById(R.id.asset_check_in_nob_beizhu);
        mControlled = (CheckBox) findViewById(R.id.asset_check_in_controlled);

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
        //TODO, remove
        //mLocationBarcode.setText(Constants.LB);

        mPreCheckInOut = (Button) findViewById(R.id.aty_pre_check_in_new_checkin);
        mCancel = (Button) findViewById(R.id.aty_pre_check_in_new_cancel);
        mAddPic = (Button) findViewById(R.id.add_pic);
        mUploadPic = (Button) findViewById(R.id.upload_pic);
        mAssetImageView = (ImageView) findViewById(R.id.asset_pre_checkin_image);

        enforceViewHidden();

        if (isPreCheckIn()) {
            mTitle.setText(getString(R.string.pre_check_in));
            labelQuantity.setText(R.string.check_in_quantity);
            mPreCheckInOut.setText(R.string.pre_check_in);
        } else {
            mTitle.setText(getString(R.string.pre_check_out));
            labelQuantity.setText(R.string.check_out_quantity);
            mPreCheckInOut.setText(R.string.pre_check_out);
        }
        if (mComponentInfo != null)
            setComponentInfoToViews(mComponentInfo);
        mPreCheckInOut.setOnClickListener(new View.OnClickListener() {
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

        mUploadPic.setVisibility(View.GONE);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       /* mUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    }
                }, mErrorListener);
                uploadStart();
                mRequestQueue.add(requestUploadCheckInPic);
            }
        });*/
    }

    private void enforceViewHidden() {
        int userType = AirportApp.getInstance().getUserPrivilege();
        if (userType == Constants.GROUP_LEADER || userType == Constants.GROUP_WORKER) {
            mPrice.setVisibility(View.INVISIBLE);
        }
    }

    private void setComponentInfoToViews(CookieGsonRequest.ComponentInfo componentInfo) {
        mTitle.setText(mTitle.getText().toString() + " " + componentInfo.mBarcode);
        mName.setText(componentInfo.mName);

        mModel.setText(componentInfo.mModel);
        mSerial.setText(componentInfo.mSerial);
        mPrice.setText("" + componentInfo.mPrice);
        mOrderCode.setText(componentInfo.mOrderCode);
        mPcsPerPack.setText("" + componentInfo.mQuantPerPack);
        mPici.setText("" + componentInfo.mPici);
        mLaiyuan.setText(componentInfo.mLaiYuan);
        mBeizhu.setText(componentInfo.mBeiZhu);
        if (componentInfo.mType == Constants.ASSET_TYPE_CONTROLLED)
            mControlled.setChecked(true);

        //mQuantity.setText("");
        mLocationBarcode.setText(componentInfo.mLocationBarcode);
        mLocationName.setText(componentInfo.mLocation);
    }

    @Override
    protected void dialogConfirmed() {
        if (!isPreCheckIn() && mComponentInfo != null && !mComponentInfo.mBarcode.isEmpty()) {
            // only if pre checkout with asset barcode, will check pici first
            checkPici();
        } else {
            doPreCheckInOutNew();
        }
    }

    private boolean isPreCheckIn() {
        return (mPreCheckOP == PRECHECKIN_NEW);
    }

    private void doPreCheckInOutNew() {
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_add_pic_first));
            return;
        }
        if (checkValidInput()) {
            if (isPreCheckIn()) {
                RequestPreCheckIn.PreCheckInOut preCheckIn = new RequestPreCheckIn.PreCheckInOut();
                preCheckInOutSetValues(preCheckIn);

                RequestPreCheckIn requestPreCheckIn = new RequestPreCheckIn(preCheckIn, new Response.Listener<CookieGsonRequest.RecordID>() {
                    @Override
                    public void onResponse(CookieGsonRequest.RecordID response) {
                        preCheckInOutFinish();
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
                preCheckInOutStart();
                mRequestQueue.add(requestPreCheckIn);
            } else {
                RequestPreCheckIn.PreCheckInOut preCheckOut = new RequestPreCheckIn.PreCheckInOut();
                preCheckInOutSetValues(preCheckOut);
                if (mComponentInfo != null) {
                    preCheckOut.mBarcode = mComponentInfo.mBarcode;
                }

                RequestPreCheckOut requestPreCheckOut = new RequestPreCheckOut(
                        preCheckOut, new Response.Listener<CookieGsonRequest.RecordID>() {
                    @Override
                    public void onResponse(CookieGsonRequest.RecordID response) {
                        preCheckInOutFinish();
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
                preCheckInOutStart();
                mRequestQueue.add(requestPreCheckOut);
            }
        }
    }

    void preCheckInOutSetValues(RequestPreCheckIn.PreCheckInOut preCheckInOut) {
        preCheckInOut.mName = mName.getText().toString();
        preCheckInOut.mModel = !mModel.getText().toString().isEmpty() ? mModel.getText().toString() : "";
        preCheckInOut.mSerial = !mSerial.getText().toString().isEmpty() ? mSerial.getText().toString() : "";
        preCheckInOut.mPrice = !mPrice.getText().toString().isEmpty() ? Float.valueOf(mPrice.getText().toString()) : null;
        preCheckInOut.mOrderCode = !mOrderCode.getText().toString().isEmpty() ? mOrderCode.getText().toString() : "";
        preCheckInOut.mPiecesPerPackage = !mPcsPerPack.getText().toString().isEmpty() ? Integer.valueOf(mPcsPerPack.getText().toString()) : null;
        preCheckInOut.mPieces = Integer.valueOf(mQuantity.getText().toString());
        preCheckInOut.mLocationBarCode = !mLocationBarcode.getText().toString().isEmpty() ? mLocationBarcode.getText().toString() : "";
        preCheckInOut.mLocation = !mLocationName.getText().toString().isEmpty() ? mLocationName.getText().toString() : "";
        preCheckInOut.mPici = !mPici.getText().toString().isEmpty() ? Integer.valueOf(mPici.getText().toString()) : null;
        preCheckInOut.mLaiYuan = !mLaiyuan.getText().toString().isEmpty() ? mLaiyuan.getText().toString() : null;
        preCheckInOut.mBeiZhu = !mBeizhu.getText().toString().isEmpty() ? mBeizhu.getText().toString() : "";
    }

    void preCheckInOutStart() {
        mPreCheckInOut.setEnabled(false);
        if (isPreCheckIn())
            showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pre_checkin_info));
        else
            showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pre_checkout_info));
    }

    void preCheckInOutFinish() {
        mPreCheckInOut.setEnabled(true);
        dismissProgressDialog();
    }

    void uploadStart() {
        mPreCheckInOut.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_pic_in_progress));
    }

    void uploadFinish() {
        mPreCheckInOut.setEnabled(true);
        dismissProgressDialog();
    }

    @SuppressWarnings("unused")
    private boolean checkValidInput() {
        if (mName.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.info_pls_input_name));
            return false;
        }

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

    private void checkPici() {
        if (mAssetImage == null) {
            toastInfo(getString(R.string.pls_add_pic_first));
            return;
        }
        if (checkValidInput()) {
            RequestCheckOldPici requestCheckOldPici = new RequestCheckOldPici(new RequestCheckOldPici.Barcode(mComponentInfo.mBarcode),
                    new Response.Listener<RequestCheckOldPici.Result>() {
                        @Override
                        public void onResponse(RequestCheckOldPici.Result response) {
                            checkPiciFinish();
                            if (response.mResult == true) {
                                // Have old pici, need to notify user about this
                                new AlertDialog.Builder(mContext)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(mConfirmDialogTitle)
                                        .setMessage("此本备件有老的批次，确定继续出库吗")
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                doPreCheckInOutNew();
                                            }
                                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).create().show();
                            } else {
                                // No old pici, just go ahead and checkout
                                doPreCheckInOutNew();
                            }
                        }
                    }, mErrorListener);
            checkPiciStart();
            mRequestQueue.add(requestCheckOldPici);
        }
    }

    void checkPiciStart() {
        mPreCheckInOut.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), "检查旧批次中");
    }

    void checkPiciFinish() {
        mPreCheckInOut.setEnabled(true);
        dismissProgressDialog();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        uploadFinish();
        preCheckInOutFinish();
        checkPiciFinish();
    }
}
