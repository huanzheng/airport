package com.invent.airport.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.RequestGetOutStandingPreCheckIns;

/**
 * Base class for those contains and displays ComponenetInfo, Fragment is used due to
 * limitation of NetworkImageView
 * Besides, PreCheckInOutInfo is put into here, to easy the implementation in
 * ActivityCheckInWithBarcode, ActivityCheckOut, because they may be used to
 * switch a pre-check-in pre-check-out record to formal check in/out
 */
public class ActivityWithAssetInfo extends AirportBaseActivity {
    private static final String TAG = "FragmentAssetInfo";

    protected FragmentAssetInfo mFragmentAssetInfo;
    protected int mAssetInfoPosition;
    protected FragmentManager mFragmentManager;

    protected CookieGsonRequest.ComponentInfo mAssetInfo;
    protected RequestGetOutStandingPreCheckIns.PreCheckInOutInfo mPreCheckInOutInfo;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mAssetInfo = getIntent().getParcelableExtra(Constants.KEY_ASSET_INFO);
        mPreCheckInOutInfo = getIntent().getParcelableExtra(Constants.KEY_PRECHECK_INOUT_INFO);
        mFragmentManager = getSupportFragmentManager();
    }

    // Layout of every ActivityWithAssetInfo should have the same id
    protected void findFragmentPosition() {
        mAssetInfoPosition = R.id.asset_info;
    }

    void replaceAssetInfo() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentAssetInfo = FragmentAssetInfo.newInstance();
        fragmentTransaction.replace(mAssetInfoPosition, mFragmentAssetInfo);
        fragmentTransaction.commit();
    }

    void addAssetInfo() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentAssetInfo = FragmentAssetInfo.newInstance();
        fragmentTransaction.add(mAssetInfoPosition, mFragmentAssetInfo).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    public static class FragmentAssetInfo extends Fragment {
        protected EditText mAssetBarcode;
        protected EditText mAssetName;
        protected EditText mAssetModel;
        protected EditText mAssetNumber;
        protected EditText mAssetLocationBarcode;
        protected EditText mAssetLocationName;
        protected EditText mAssetType;
        protected EditText mAssetSerial;
        protected EditText mAssetPrice;
        protected EditText mAssetOrderCode;
        protected EditText mAssetPcsIn;
        //protected TextView mAssetPcsAll;
        protected EditText mAssetPcsPerPack;
        protected EditText mAssetLaiYuan;
        protected EditText mAssetPici;
        protected EditText mAssetBeizhu;
        protected NetworkImageView mAssetImage;
        ActivityWithAssetInfo mMainActivity;

        public static FragmentAssetInfo newInstance() {
            FragmentAssetInfo f = new FragmentAssetInfo();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater arg0, ViewGroup arg1,
                                 Bundle arg2) {
            Log.i(TAG, "onCreateView");
            View v = arg0.inflate(R.layout.asset_info, null);
            mAssetImage = (NetworkImageView) v.findViewById(R.id.ast_info_image);
            mAssetBarcode = (EditText) v.findViewById(R.id.ast_info_barcode);
            mAssetName = (EditText) v.findViewById(R.id.ast_info_name);
            mAssetModel = (EditText) v.findViewById(R.id.ast_info_model);
            mAssetNumber = (EditText) v.findViewById(R.id.ast_info_quantity);
            mAssetLocationBarcode = (EditText) v.findViewById(R.id.ast_info_location_barcode);
            mAssetLocationName = (EditText) v.findViewById(R.id.ast_info_location_name);
            mAssetType = (EditText) v.findViewById(R.id.ast_info_control_type);
            mAssetSerial = (EditText) v.findViewById(R.id.ast_info_serial);
            mAssetPrice = (EditText) v.findViewById(R.id.ast_info_price);
            mAssetOrderCode = (EditText) v.findViewById(R.id.ast_info_order_code);
            mAssetPcsIn = (EditText) v.findViewById(R.id.ast_info_in_quantity);
            // mAssetPcsAll = (TextView) v.findViewById(R.id.ast_info_total_quantity);
            mAssetPcsPerPack = (EditText) v.findViewById(R.id.ast_info_pc_per_pack);
            mAssetLaiYuan = (EditText) v.findViewById(R.id.ast_info_laiyuan);
            mAssetPici = (EditText) v.findViewById(R.id.ast_info_pici);
            mAssetBeizhu = (EditText) v.findViewById(R.id.ast_info_beizhu);
            mAssetBarcode.setKeyListener(null);
            mAssetName.setKeyListener(null);
            mAssetModel.setKeyListener(null);
            mAssetNumber.setKeyListener(null);
            mAssetLocationBarcode.setKeyListener(null);
            mAssetLocationName.setKeyListener(null);
            mAssetType.setKeyListener(null);
            mAssetSerial.setKeyListener(null);
            mAssetPrice.setKeyListener(null);
            mAssetOrderCode.setKeyListener(null);
            mAssetPcsIn.setKeyListener(null);
            mAssetPcsPerPack.setKeyListener(null);
            mAssetPici.setKeyListener(null);
            mAssetLaiYuan.setKeyListener(null);
            mAssetBeizhu.setKeyListener(null);
            enforceViewHidden();
            return v;
        }

        private void enforceViewHidden() {
            int userType = AirportApp.getInstance().getUserPrivilege();
            if (userType == Constants.GROUP_LEADER || userType == Constants.GROUP_WORKER) {
                mAssetPrice.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onActivityCreated(Bundle arg0) {
            super.onActivityCreated(arg0);
            mMainActivity = (ActivityWithAssetInfo) getActivity();
            Log.i(TAG, "onActivityCreated");
            if (mMainActivity.mAssetInfo != null)
                setAssetInfo(mMainActivity.mAssetInfo);
        }

        public final void setAssetInfo(CookieGsonRequest.ComponentInfo asset) {
            if (asset.mPhotoURL != null && !asset.mPhotoURL.isEmpty()) {
                mAssetImage.setImageUrl(Constants.getImageURL(asset.mPhotoURL), mMainActivity.mImageLoader);
            }
            mAssetBarcode.setText(asset.mBarcode);
            mAssetName.setText(asset.mName);
            mAssetModel.setText(asset.mModel);
            if (asset.mTotalAvailable != Constants.INVALID_QUANT) {
                mAssetNumber.setText("" + asset.mTotalAvailable);
            } else {
                mAssetNumber.setText("");
            }
            mAssetLocationBarcode.setText(asset.mLocationBarcode);
            mAssetLocationName.setText(asset.mLocation);
            if (asset.mType == Constants.ASSET_TYPE_CONTROLLED) {
                mAssetType.setText(getString(R.string.none_controlled_asset));
            } else {
                mAssetType.setText(getString(R.string.controlled_asset));
            }
            mAssetSerial.setText(asset.mSerial);
            if (asset.mPrice != Constants.INVALID_PRICE) {
                mAssetPrice.setText("" + asset.mPrice);
            } else {
                mAssetPrice.setText("");
            }
            mAssetOrderCode.setText(asset.mOrderCode);
            if (asset.mQuantIn != Constants.INVALID_QUANT) {
                mAssetPcsIn.setText("" + asset.mQuantIn);
            } else {
                mAssetPcsIn.setText("");
            }
            //mAssetPcsAll.setText("" + (asset.mQuantIn + asset.mTotalAvailable));
            mAssetPcsPerPack.setText("" + asset.mQuantPerPack);
            if (asset.mPici != Constants.INVALID_PICI) {
                mAssetPici.setText("" + asset.mPici);
            } else {
                mAssetPici.setText("");
            }
            mAssetBeizhu.setText(""+asset.mBeiZhu);
            mAssetLaiYuan.setText(""+asset.mLaiYuan);
        }
    }
}
