package com.invent.airport.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.invent.airport.R;
import com.invent.airport.constants.Constants;

public final class ActivitySearchNoResult extends ActivitySearchResultBase {

    Button mResearch;
    Button mCheckInNew;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_no_result);
        getSearchTypeAndSetTitle();

        mResearch = (Button) findViewById(R.id.aty_search_noresult_research);
        mCheckInNew = (Button) findViewById(R.id.aty_aty_search_noresult_checkin_new);

        Log.i("FUCK", "op is " + mOP);

        if (mOP != Constants.OP_CHECKIN && mOP != Constants.OP_PRECHECKIN ) { //&& mOP != Constants.OP_PRECHECKOUT) {
            Log.i("FUCK", "set to gone");
            mCheckInNew.setVisibility(View.GONE);
        } else {
            Log.i("FUCK", "set to visible");
            mCheckInNew.setVisibility(View.VISIBLE);
            if (mOP == Constants.OP_CHECKIN) {
                mCheckInNew.setText(getString(R.string.aty_srch_norelt_checkin_new));
            } else if (mOP == Constants.OP_PRECHECKIN) {
                mCheckInNew.setText(getString(R.string.aty_srch_norelt_precheckin));
            } else if (mOP == Constants.OP_PRECHECKOUT) {
                mCheckInNew.setText(getString(R.string.aty_srch_norelt_precheckout));
            }
        }
        mResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assume search result always invoked by Search
                finish();
            }
        });

        mCheckInNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOP == Constants.OP_CHECKIN) {
                    Intent i = new Intent(mContext, ActivityCheckInWithoutBarcode.class);
                    mContext.startActivity(i);
                    finish();
                } else if (mOP == Constants.OP_PRECHECKIN) {
                    Intent i = new Intent(mContext, ActivityPreCheckInOutNew.class);
                    i.putExtra(ActivityPreCheckInOutNew.KEY_PRECHECK_OP, ActivityPreCheckInOuts.PRE_CHECK_INS);
                    mContext.startActivity(i);
                    finish();
                } else if (mOP == Constants.OP_PRECHECKOUT) {
                    Intent i = new Intent(mContext, ActivityPreCheckInOutNew.class);
                    i.putExtra(ActivityPreCheckInOutNew.KEY_PRECHECK_OP, ActivityPreCheckInOuts.PRE_CHECK_OUTS);
                    mContext.startActivity(i);
                    finish();
                }
            }
        });
    }
}
