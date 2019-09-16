package com.invent.airport.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.AirportError;
import com.invent.airport.requests.CookieFileUploadRequest;
import com.invent.airport.requests.CookieGsonRequest;
import com.invent.airport.requests.QueryRequest;
import com.invent.airport.requests.RequestAssetsByLocationBarcode;
import com.invent.airport.requests.RequestCheckInWithBarCode;
import com.invent.airport.requests.RequestCheckInWithoutBarCode;
import com.invent.airport.requests.RequestCheckOut;
import com.invent.airport.requests.RequestCreateCheckOutTask;
import com.invent.airport.requests.RequestGetCheckOutTaskDetail;
import com.invent.airport.requests.RequestGetCheckOutTaskList;
import com.invent.airport.requests.RequestGetOutStandingPreCheckIns;
import com.invent.airport.requests.RequestGetOutStandingPreCheckOuts;
import com.invent.airport.requests.RequestGetPicUrlForBarcode;
import com.invent.airport.requests.RequestMove;
import com.invent.airport.requests.RequestTakeCheckOutTask;
import com.invent.airport.requests.RequestUploadCheckInPic;
import com.invent.airport.requests.RequestUploadPreCheckInPic;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@SuppressLint("SdCardPath")
@SuppressWarnings("unused")

public final class ActivityEntry extends AirportBaseActivity {
    int mUserType = Constants.GROUP_WORKER;
    String mUserName;

    final int BADGE_PRE_CHECKIN = 1;
    final int BADGE_PRE_CHECKOUT = 2;
    final int BADGE_CHECKOUT_TASK = 3;
    TextView mUserNameTextView;
    TextView mUserTypeTextView;
    TextView mBack;

    Button mCheckIn;
    Button mCheckOut;
    Button mPreCheckIn;
    Button mPreCheckOut;
    Button mRecordsPreCheckIn;
    Button mRecordsPreCheckOut;
    Button mGenerateCheckOutTask;
    Button mTakeCheckOutTask;
    Button mMove;
    Button mCorrect;
    Button mSearchComponent;
    Button mSearchLocation;
    Context mContext;
    NetworkImageView mAvatar;

    BadgeView mBadgeCheckOutTasks;
    BadgeView mBadgeViewPreCheckIns;
    BadgeView mBadgeViewPreCheckOuts;
    private String mTestBarcode;

    RequestGetOutStandingPreCheckIns mRequestGetOutStandingPreCheckIns;
    RequestGetOutStandingPreCheckOuts mRequestGetOutStandingPreCheckOuts;
    RequestGetCheckOutTaskList mRequestGetCheckOutTaskList;

    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor
            = new ScheduledThreadPoolExecutor(1);

    private static final String TAG = "ActivityEntry";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_entry);
        mContext = this;

        mCheckIn = (Button) findViewById(R.id.aty_entry_check_in);
        mCheckIn.setVisibility(View.GONE);
        mCheckOut = (Button) findViewById(R.id.aty_entry_check_out);
        mPreCheckIn = (Button) findViewById(R.id.aty_entry_pre_check_in);
        mPreCheckOut = (Button) findViewById(R.id.aty_entry_pre_check_out);
        mRecordsPreCheckIn = (Button) findViewById(R.id.aty_entry_records_pre_checkin);
        mRecordsPreCheckOut = (Button) findViewById(R.id.aty_entry_records_pre_checkout);
        mGenerateCheckOutTask = (Button) findViewById(R.id.aty_entry_task_generate);
        mTakeCheckOutTask = (Button) findViewById(R.id.aty_entry_task_take);
        mMove = (Button) findViewById(R.id.aty_entry_move);
        mCorrect = (Button) findViewById(R.id.aty_entry_correct);
        mSearchComponent = (Button) findViewById(R.id.aty_entry_search);
        mSearchLocation = (Button) findViewById(R.id.aty_entry_search_location);
        mAvatar = (NetworkImageView) findViewById(R.id.aty_entry_user_icon);
        mUserNameTextView = (TextView) findViewById(R.id.aty_entry_user_name);
        mUserTypeTextView = (TextView) findViewById(R.id.aty_entry_user_type);
        mAvatar.setDefaultImageResId(R.drawable.ic_avatar_user);
        mBack = (TextView) findViewById(R.id.back);

        // disable some buttons
        mCheckIn.setEnabled(false);
        mCheckOut.setEnabled(false);
        mMove.setEnabled(false);
        mCorrect.setEnabled(false);
        mGenerateCheckOutTask.setEnabled(false);
        mPreCheckIn.setEnabled(false);
        mPreCheckOut.setEnabled(false);
        mRecordsPreCheckOut.setEnabled(false);
        mRecordsPreCheckIn.setEnabled(false);
        mTakeCheckOutTask.setEnabled(false);

        if (getUser() != null) {
            mUserNameTextView.setText(getUser().mRealname);
            mUserTypeTextView.setText(getUser().mUserGroup);
            mAvatar.setImageUrl(Constants.getAvatarURL(getUser().mAvatar), mImageLoader);
        }
        mUserType = AirportApp.getInstance().getUserPrivilege();
        enforceButtonStatus(mUserType);

        mBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
        mCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_CHECKIN);
            }
        });
        mCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_CHECKOUT);
            }
        });
        mCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //queryTest();
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_CHONGHONG);
            }
        }
        );
        mMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_MOVE);
            }
        });
        mPreCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_PRECHECKIN);
            }
        });
        mPreCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_PRECHECKOUT);
            }
        });
        mGenerateCheckOutTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                startActivityWithOP(mContext, i, Constants.OP_CREATE_CHECKOUT_TASK);
            }
        });
        mSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearchLocation.class);
                mContext.startActivity(i);
            }
        });
        mSearchComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivitySearch.class);
                mContext.startActivity(i);
            }
        });
        mRecordsPreCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivityPreCheckInOuts.class);
                i.putExtra(ActivityPreCheckInOuts.KEY_PRECHECK_IN_OR_OUT, ActivityPreCheckInOuts.PRE_CHECK_INS);
                mContext.startActivity(i);
            }
        });
        mRecordsPreCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivityPreCheckInOuts.class);
                i.putExtra(ActivityPreCheckInOuts.KEY_PRECHECK_IN_OR_OUT, ActivityPreCheckInOuts.PRE_CHECK_OUTS);
                mContext.startActivity(i);
            }
        });
        mTakeCheckOutTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivityCheckoutTasks.class);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Adding two requests");
        checkPendingPreCheckIns();
        checkPendingCheckoutTasks();
        checkPendingPreCheckOuts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Canceling two requests");
        if (mRequestGetOutStandingPreCheckIns != null)
            mRequestGetOutStandingPreCheckIns.cancel();
        if (mRequestGetOutStandingPreCheckOuts != null)
            mRequestGetOutStandingPreCheckOuts.cancel();
        if (mRequestGetCheckOutTaskList != null)
            mRequestGetCheckOutTaskList.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(mContext, ActivityConfig.class);
                mContext.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enforceButtonStatus(int userType) {
        switch (userType) {
            case Constants.GROUP_REPO_MANGER:
                mCheckIn.setEnabled(true);
                mCheckOut.setEnabled(true);
                mMove.setEnabled(true);
                mCorrect.setEnabled(true);
                mRecordsPreCheckOut.setEnabled(true);
                mRecordsPreCheckIn.setEnabled(true);
                // fall through
            case Constants.GROUP_LEADER:
            case Constants.GROUP_WORKER:
                mPreCheckOut.setEnabled(true);
                mPreCheckIn.setEnabled(true);
                mTakeCheckOutTask.setEnabled(true);
                mGenerateCheckOutTask.setEnabled(true);
                break;
        }
    }

    private void showBadge(int num, int whichBadge, Button button, boolean show) {
        BadgeView badgeView = null;
        switch (whichBadge) {
            case BADGE_PRE_CHECKIN:
                if (mBadgeViewPreCheckIns == null) {
                    mBadgeViewPreCheckIns = new BadgeView(this, button);
                }
                badgeView = mBadgeViewPreCheckIns;
                break;
            case BADGE_PRE_CHECKOUT:
                if (mBadgeViewPreCheckOuts == null) {
                    mBadgeViewPreCheckOuts = new BadgeView(this, button);
                }
                badgeView = mBadgeViewPreCheckOuts;
                break;
            case BADGE_CHECKOUT_TASK:
                if (mBadgeCheckOutTasks == null) {
                    mBadgeCheckOutTasks = new BadgeView(this, button);
                }
                badgeView = mBadgeCheckOutTasks;
                break;
        }
        if (badgeView == null) {
            badgeView = new BadgeView(this, button);
        }
        if (!show) {
            badgeView.hide();
            return;
        }
        if (num == Constants.QUERY_PAGE_SIZE)
            badgeView.setText(String.valueOf(num) + "+");
        else
            badgeView.setText(String.valueOf(num));
        badgeView.setTextColor(Color.BLUE);
        badgeView.setBackgroundResource(R.drawable.bg_badger);
        badgeView.setGravity(Gravity.CENTER);
        badgeView.setTextSize(12);
        badgeView.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showPendingTasks(int num) {
        showBadge(num, BADGE_CHECKOUT_TASK, mTakeCheckOutTask, true);
    }

    private void showPendingPreCheckIns(int num) {
        showBadge(num, BADGE_PRE_CHECKIN, mRecordsPreCheckIn, true);
    }

    private void showPendingPreCheckOuts(int num) {
        showBadge(num, BADGE_PRE_CHECKOUT, mRecordsPreCheckOut, true);
    }

    private void hidePendingPreCheckIns() {
        showBadge(0, BADGE_PRE_CHECKIN, mRecordsPreCheckIn, false);
    }

    private void hidePendingPreCheckOuts() {
        showBadge(0, BADGE_PRE_CHECKOUT, mRecordsPreCheckOut, false);
    }

    private void hidePendingCheckOutTasks() {
        showBadge(0, BADGE_CHECKOUT_TASK, mTakeCheckOutTask, false);
    }

    private void checkPendingPreCheckIns() {
        if (mUserType == Constants.GROUP_ADMIN) {
            return;
        }
        hidePendingPreCheckIns();
        mRequestGetOutStandingPreCheckIns = new RequestGetOutStandingPreCheckIns(1, new Response.Listener<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut>() {
            @Override
            public void onResponse(RequestGetOutStandingPreCheckIns.PendingPreCheckInOut response) {
                if (response.mPreCheckInOutInfos != null) {
                    showPendingPreCheckIns(response.mPreCheckInOutInfos.size());
                }
            }
        }, mErrorListener);
        mRequestQueue.add(mRequestGetOutStandingPreCheckIns);
    }

    private void checkPendingPreCheckOuts() {
        if (mUserType == Constants.GROUP_ADMIN) {
            return;
        }
        hidePendingPreCheckOuts();
        mRequestGetOutStandingPreCheckOuts = new RequestGetOutStandingPreCheckOuts(1, new Response.Listener<RequestGetOutStandingPreCheckIns.PendingPreCheckInOut>() {
            @Override
            public void onResponse(RequestGetOutStandingPreCheckIns.PendingPreCheckInOut response) {
                if (response.mPreCheckInOutInfos != null) {
                    showPendingPreCheckOuts(response.mPreCheckInOutInfos.size());
                }
            }
        }, mErrorListener);
        mRequestQueue.add(mRequestGetOutStandingPreCheckOuts);
    }

    private void checkPendingCheckoutTasks() {
        if (mUserType == Constants.GROUP_ADMIN) {
            return;
        }
        hidePendingCheckOutTasks();
        mRequestGetCheckOutTaskList = new RequestGetCheckOutTaskList(new Response.Listener<RequestGetCheckOutTaskList.CheckOutTaskList>() {
            @Override
            public void onResponse(RequestGetCheckOutTaskList.CheckOutTaskList response) {
                if (response.mTasks != null) {
                    showPendingTasks(response.mTasks.size());
                }
            }
        }, mErrorListener);
        mRequestQueue.add(mRequestGetCheckOutTaskList);
    }

    private void checkInTest() {
        RequestCheckInWithoutBarCode.CheckInWithoutBarCode checkInWithoutBarCode = new RequestCheckInWithoutBarCode.CheckInWithoutBarCode();
        checkInWithoutBarCode.mLocation = "B区12排3列";
        checkInWithoutBarCode.mLocationBarCode = "b123356";
        checkInWithoutBarCode.mModel = "model-test";
        checkInWithoutBarCode.mName = "model-name1";
        checkInWithoutBarCode.mPieces = 100;
        checkInWithoutBarCode.mPiecesPerPackage = 20;
        checkInWithoutBarCode.mPrice = (float) 1.5;
        checkInWithoutBarCode.mSerial = "serial-test";

        RequestCheckInWithoutBarCode requestCheckInWithoutBarCode = new RequestCheckInWithoutBarCode(checkInWithoutBarCode, new Response.Listener<RequestCheckInWithoutBarCode.BarCode>() {
            @Override
            public void onResponse(RequestCheckInWithoutBarCode.BarCode response) {
                Log.i(TAG, "Check in succeed " + response.mBarCode);
                Toast.makeText(mContext, "入库成功，条码为 " + response.mBarCode, Toast.LENGTH_SHORT).show();
                mTestBarcode = response.mBarCode;

                final String responseBarcode = response.mBarCode;
                QueryRequest.Query query = new QueryRequest.Query();
                query.mQueryType = QueryRequest.QUERY_WITH_BARCODE;
                query.mPage = 1;
                query.mBarcode = response.mBarCode;
                QueryRequest queryRequest = new QueryRequest(query, new Response.Listener<QueryRequest.QueryResult>() {
                    @Override
                    public void onResponse(QueryRequest.QueryResult response) {
                        Log.i(TAG, "query result " + response.mCurPageNum);
                        Toast.makeText(mContext, "第 " + response.mCurPageNum + " 页 " + " 得到结果数 " + response.mCurPage.size(), Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                //AirportApp.getInstance().getRequestQueue().add(queryRequest);

                RequestCheckInWithBarCode.CheckInWithBarCode checkInWithBarCode = new RequestCheckInWithBarCode.CheckInWithBarCode();
                checkInWithBarCode.mBarCode = response.mBarCode;
                checkInWithBarCode.mQuant = 20;
                checkInWithBarCode.mLocationBarcode = "b123356";
                RequestCheckInWithBarCode requestCheckInWithBarcode = new RequestCheckInWithBarCode(checkInWithBarCode, new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        Toast.makeText(mContext, "CheckInWithBarCode Succeed", Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                //AirportApp.getInstance().getRequestQueue().add(requestCheckInWithBarcode);

                RequestCheckOut.CheckOut checkOut = new RequestCheckOut.CheckOut();
                checkOut.mBarCode = response.mBarCode;
                checkOut.mLocationBarcode = "b123356";
                checkOut.mQuant = 10;
                RequestCheckOut requestCheckOut = new RequestCheckOut(checkOut, new Response.Listener<CookieGsonRequest.RecordID>() {
                    @Override
                    public void onResponse(CookieGsonRequest.RecordID response) {
                        Toast.makeText(mContext, "Checkout succeeded " + response.mRecordID, Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                //AirportApp.getInstance().getRequestQueue().add(requestCheckOut);

                RequestMove.Move move = new RequestMove.Move();
                move.mBarCode = response.mBarCode;
                move.mOldLocationBarcode = "b123356";
                move.mNewLocationBarcode = "b1884456";
                move.mPCsToMove = 10;
                RequestMove requestMove = new RequestMove(move, new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        Toast.makeText(mContext, "Move succeeded ", Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                //AirportApp.getInstance().getRequestQueue().add(requestMove);

                RequestUploadCheckInPic requestUploadCheckInPic = new RequestUploadCheckInPic(new File("/mnt/sdcard/IMG.jpg"), response.mBarCode, new Response.Listener<CookieFileUploadRequest.UploadURL>() {
                    @Override
                    public void onResponse(CookieFileUploadRequest.UploadURL response) {
                        Toast.makeText(mContext, "File uploaded " + response.mPicURL, Toast.LENGTH_SHORT).show();
                        RequestGetPicUrlForBarcode requestGetPicUrlForBarcode = new RequestGetPicUrlForBarcode(responseBarcode, new Response.Listener<CookieGsonRequest.PicURL>() {
                            @Override
                            public void onResponse(CookieGsonRequest.PicURL response) {
                                Toast.makeText(mContext, "URL is " + response.mURL, Toast.LENGTH_SHORT).show();
                            }
                        }, mErrorListener);
                        //AirportApp.getInstance().getRequestQueue().add(requestGetPicUrlForBarcode);
                    }
                }, mErrorListener);
                //AirportApp.getInstance().getRequestQueue().add(requestUploadCheckInPic);

                RequestAssetsByLocationBarcode requestAssetsByLocationBarcode = new RequestAssetsByLocationBarcode("b123356", 0, new Response.Listener<RequestAssetsByLocationBarcode.QueryLocationResult>() {
                    @Override
                    public void onResponse(RequestAssetsByLocationBarcode.QueryLocationResult response) {
                        Log.i(TAG, "query result " + response.mCurPageNum);
                        Toast.makeText(mContext, "第 " + response.mCurPageNum + " 页 " + " 得到结果数 " + response.mCurPage.size(), Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                AirportApp.getInstance().getRequestQueue().add(requestAssetsByLocationBarcode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AirportError) {
                    Toast.makeText(mContext, ((AirportError) error).mErrorMsg, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Check in Error " + error.getMessage());
            }
        }
        );

        AirportApp.getInstance().getRequestQueue().add(requestCheckInWithoutBarCode);
    }

    private void queryTest() {
        QueryRequest.Query query = new QueryRequest.Query();
        query.mQueryType = QueryRequest.QUERY_WITHOUT_BARCODE;
        query.mName = "model-name";

        QueryRequest queryRequest = new QueryRequest(query, new Response.Listener<QueryRequest.QueryResult>() {
            @Override
            public void onResponse(QueryRequest.QueryResult response) {
                Log.i(TAG, "query result " + response.mCurPageNum);
                Toast.makeText(mContext, "第 " + response.mCurPageNum + " 页 " + " 得到结果数 " + response.mCurPage.size(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AirportError) {
                    Toast.makeText(mContext, ((AirportError) error).mErrorMsg, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Query Error " + error.getMessage());
            }
        }
        );
        AirportApp.getInstance().getRequestQueue().add(queryRequest);
    }

    private void testFileUpload() {
        File file = new File("/mnt/sdcard/IMG.jpg");
        if (!file.exists()) {
            Log.e(TAG, "File does not exist!");
            Toast.makeText(mContext, "File does not exist!", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUploadPreCheckInPic upload = new RequestUploadPreCheckInPic(file, "3", new Response.Listener<CookieFileUploadRequest.UploadURL>() {
            @Override
            public void onResponse(CookieFileUploadRequest.UploadURL response) {
                Log.i(TAG, "File uploaded " + response.mPicURL);
                Toast.makeText(mContext, "File uploaded " + response.mPicURL, Toast.LENGTH_SHORT).show();
            }
        }, mErrorListener);
        AirportApp.getInstance().getRequestQueue().add(upload);
    }


    void testCheckOutTask() {
        RequestCreateCheckOutTask.CheckOutTask checkOutTask = new RequestCreateCheckOutTask.CheckOutTask();
        checkOutTask.mBarcode = mTestBarcode;
        checkOutTask.mLocationBarcode = "b123356";
        checkOutTask.mPCs = 10;
        RequestCreateCheckOutTask requestCreateCheckOutTask = new RequestCreateCheckOutTask(checkOutTask, new Response.Listener<CookieGsonRequest.TaskID>() {
            @Override
            public void onResponse(CookieGsonRequest.TaskID response) {
                Toast.makeText(mContext, "CheckOutTask Created " + response.mTaskID, Toast.LENGTH_SHORT).show();

                RequestGetCheckOutTaskList requestGetCheckOutTaskList = new RequestGetCheckOutTaskList(new Response.Listener<RequestGetCheckOutTaskList.CheckOutTaskList>() {
                    @Override
                    public void onResponse(RequestGetCheckOutTaskList.CheckOutTaskList response) {
                        Toast.makeText(mContext, "CheckOutTask size " + response.mTasks.size(), Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                AirportApp.getInstance().getRequestQueue().add(requestGetCheckOutTaskList);

                RequestGetCheckOutTaskDetail requestGetCheckOutTaskDetail = new RequestGetCheckOutTaskDetail(response.mTaskID, new Response.Listener<RequestGetCheckOutTaskDetail.CheckOutTaskDetail>() {
                    @Override
                    public void onResponse(RequestGetCheckOutTaskDetail.CheckOutTaskDetail response) {
                        Toast.makeText(mContext, "CheckOutTaskDetail " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                AirportApp.getInstance().getRequestQueue().add(requestGetCheckOutTaskDetail);

                RequestTakeCheckOutTask requestTakeCheckOutTask = new RequestTakeCheckOutTask(response.mTaskID, new Response.Listener<CookieGsonRequest.Succeed>() {
                    @Override
                    public void onResponse(CookieGsonRequest.Succeed response) {
                        Toast.makeText(mContext, "CheckOut Task Taken", Toast.LENGTH_SHORT).show();
                    }
                }, mErrorListener);
                AirportApp.getInstance().getRequestQueue().add(requestTakeCheckOutTask);
            }
        }, mErrorListener);
        AirportApp.getInstance().getRequestQueue().add(requestCreateCheckOutTask);
    }
}
