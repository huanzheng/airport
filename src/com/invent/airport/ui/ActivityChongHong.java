package com.invent.airport.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestCheckInChongHong;
import com.invent.airport.requests.RequestCheckOutChongHong;
import com.invent.airport.requests.RequestChongHong;

public final class ActivityChongHong extends ActivityWithAssetInfo {
    public static final String KEY_CHONGHONG_TYPE = "key_chonghong_type";
    public static final int CHECK_IN_CHONGHONG = 1;
    public static final int CHECK_OUT_CHONGHONG = 2;
    private int mChongHongType = CHECK_IN_CHONGHONG;
    EditText mQuantity;
    EditText mLocationBarcode;
    EditText mLocationName;

    Button mChongHong;
    Button mCancel;

    //private static final String TAG = "ActivityCheckInWithBarcode";

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_chonghong);

        findFragmentPosition();
        mChongHongType = getIntent().getIntExtra(KEY_CHONGHONG_TYPE, CHECK_IN_CHONGHONG);

        //findViewByIDs();

        // mAssetInfo = getIntent().getParcelableExtra(Constants.KEY_ASSET_INFO);
        //setAssetInfo(mAssetInfo);
        addAssetInfo();

        mQuantity = (EditText) findViewById(R.id.ast_chonghong_quantity);
        mLocationBarcode = (EditText) findViewById(R.id.ast_chonghong_location_barcode);
        mLocationName = (EditText) findViewById(R.id.ast_chonghong_location_name);
        mQuantity.setOnKeyListener(mOnKeyListener);
        mLocationBarcode.setOnKeyListener(mOnKeyListener);
        mLocationName.setOnKeyListener(mOnKeyListener);

        mChongHong = (Button) findViewById(R.id.aty_chonghong_chonghong);
        mCancel = (Button) findViewById(R.id.aty_chonghong_cancel);

        mLocationBarcode.setText(mAssetInfo.mLocationBarcode);
        mLocationName.setText(mAssetInfo.mLocation);

        if (isCheckInChongHong()) {
            mChongHong.setText(R.string.chonghong_checkin);
        } else {
            mChongHong.setText(R.string.chonghong_checkout);
        }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mChongHong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    private boolean isCheckInChongHong() {
        return (mChongHongType == CHECK_IN_CHONGHONG);
    }

    @Override
    protected void dialogConfirmed() {
        startChongHong();
    }

    private void startChongHong() {
        if (checkValidInput()) {
            final int quantity = Integer.valueOf(mQuantity.getText().toString());
            final String locationName = mLocationName.getText().toString();
            final String locationBarcode = mLocationBarcode.getText().toString();
            RequestChongHong.ChongHong chongHong = new RequestChongHong.ChongHong();
            chongHong.mBarcode = mAssetInfo.mBarcode;
            chongHong.mPCs = quantity;
            if (!locationBarcode.isEmpty()) {
                chongHong.mLocationBarcode = locationBarcode;
            }
            if (!locationName.isEmpty()) {
                chongHong.mLocation = locationName;
            }

            if (isCheckInChongHong()) {
                RequestCheckInChongHong requestCheckInChongHong = new RequestCheckInChongHong(chongHong, new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        chongHongFinished();
                        toastInfo("入库冲红成功");
                        showFinishDialog();
                    }
                }, mErrorListener);
                chongHongStart();
                mRequestQueue.add(requestCheckInChongHong);
            } else {
                RequestCheckOutChongHong requestCheckOutChongHong = new RequestCheckOutChongHong(chongHong, new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        chongHongFinished();
                        toastInfo("出库冲红成功");
                        showFinishDialog();
                    }
                }, mErrorListener);
                chongHongStart();
                mRequestQueue.add(requestCheckOutChongHong);
            }
        }
    }

    void chongHongStart() {
        mChongHong.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_chonghong_info));
    }

    void chongHongFinished() {
        mChongHong.setEnabled(true);
        dismissProgressDialog();
    }

    private boolean checkValidInput() {

        if (mQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.pls_input_chonghong_quantity));
            return false;
        } else {
            try {
                Integer quant = Integer.valueOf(mQuantity.getText().toString());
                if (quant >= 0) {
                    toastInfo(getString(R.string.chonghong_must_be_negetive));
                    return false;
                }
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
        chongHongFinished();
    }
}
