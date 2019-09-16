package com.invent.airport.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.invent.airport.R;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestMove;

public final class ActivityMove extends ActivityWithAssetInfo {
    EditText mMoveQuantity;
    EditText mNewLocationCode;
    Button mMove;
    Button mCancel;

    @Override
    protected void onEnter() {
        showConfirmDialog();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_move);

        findFragmentPosition();
        addAssetInfo();

        mMove = (Button) findViewById(R.id.aty_move_move);
        mCancel = (Button) findViewById(R.id.aty_move_cancel);
        mMoveQuantity = (EditText) findViewById(R.id.ast_move_quantity);
        mNewLocationCode = (EditText) findViewById(R.id.ast_move_new_location_barcode);
        mMoveQuantity.setOnKeyListener(mOnKeyListener);
        mNewLocationCode.setOnKeyListener(mOnKeyListener);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    @Override
    protected void dialogConfirmed() {
        doMove();
    }

    void doMove() {
        if (checkValidInput()) {
            int quantity = Integer.valueOf(mMoveQuantity.getText().toString());
            RequestMove.Move move = new RequestMove.Move();
            move.mPCsToMove = quantity;
            move.mOldLocationBarcode = mAssetInfo.mLocationBarcode;
            move.mNewLocationBarcode = mNewLocationCode.getText().toString();
            move.mBarCode = mAssetInfo.mBarcode;
            RequestMove requestMove = new RequestMove(move, new Response.Listener<CookieGsonRequest.Succeed>() {
                @Override
                public void onResponse(CookieGsonRequest.Succeed response) {
                    toastInfo("移库成功");
                    moveFinish();
                    //TODO maybe update the UI?
                    showFinishDialog();
                }
            }, mErrorListener);
            moveStart();
            mRequestQueue.add(requestMove);
        }
    }

    @SuppressWarnings("unused")
    private boolean checkValidInput() {

        if (mMoveQuantity.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.aty_move_pls_input_move_number));
            return false;
        } else {
            try {
                Integer quant = Integer.valueOf(mMoveQuantity.getText().toString());
            } catch (NumberFormatException e) {
                toastInfo(getString(R.string.aty_move_must_be_number));
                return false;
            }
        }

        if (mNewLocationCode.getText().toString().isEmpty()) {
            toastInfo(getString(R.string.aty_move_pls_input_new_location_barcode));
            return false;
        }
        return true;
    }

    void moveStart() {
        mMove.setEnabled(false);
        showProgressDialog(getString(R.string.progress_title_op_in_progress), getString(R.string.uploading_move_info));
    }

    void moveFinish() {
        mMove.setEnabled(true);
        dismissProgressDialog();
    }

    @Override
    protected void onErrorPostProcess(VolleyError error) {
        super.onErrorPostProcess(error);
        moveFinish();
    }
}
