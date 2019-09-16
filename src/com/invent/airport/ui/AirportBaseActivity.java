package com.invent.airport.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.invent.airport.R;
import com.invent.airport.constants.Constants;
import com.invent.airport.requests.AirportError;
import com.invent.airport.requests.RequestGetUserDetail;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Base class wraps common functions
 * 1, start Activity with operation code
 * 2, get operation code when onCreate
 * 3, show/dismiss progress dialog
 * 4, show confirmation dialog
 * 5, retrieve volley related instances
 * 6, provide volley error listener
 * 7, provide OnKeyListener
 * 8, provide toast information helper
 */
public class AirportBaseActivity extends FragmentActivity {
    private static final String TAG_VE = "VolleyError";
    protected RequestQueue mRequestQueue;
    protected ImageLoader mImageLoader;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected ImageLoader.ImageCache mImageCache;
    protected LruCache<String, Bitmap> mImageCacheLRU;

    protected String mOPInProgress;
    protected int mOP = Constants.OP_NONE;
    protected boolean mToastAirportError = true;
    private static final String TAG = "AirportBaseActivity";

    protected String mConfirmDialogTitle = "";
    protected String mConfirmDialogMsg = "";
    protected Uri mAssetImage;
    protected ImageView mAssetImageView;

    protected View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (AirportApp.hardwareEnterPressed(keyEvent)) {
                onEnter();
                return true;
            }
            return false;
        }
    };

    protected void onEnter() {
    }

    protected Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onError(error);
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent i = getIntent();
        if (i != null) {
            mOP = i.getIntExtra(Constants.KEY_OP, Constants.OP_NONE);
            Log.i(TAG, "Get op " + mOP);
        }
        mConfirmDialogTitle = getString(R.string.warning);
        mConfirmDialogMsg = getString(R.string.dialog_message);
        mRequestQueue = AirportApp.getInstance().getRequestQueue();
        mImageLoader = AirportApp.getInstance().getImageLoader();
        mImageCache = AirportApp.getInstance().getImageCache();
        mImageCacheLRU = AirportApp.getInstance().getImageCacheLRU();
        mContext = this;
        mOPInProgress = getResources().getString(R.string.progress_title_op_in_progress);
        //InternetControl.EnableInternet(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    protected void showProgressDialog(String title, String content) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mContext, title, content);
            mProgressDialog.setCancelable(false);
        }
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected RequestGetUserDetail.User getUser() {
        return AirportApp.getInstance().getUser();
    }

    protected void startActivityWithOP(Context context, Intent i, int op) {
        Log.i(TAG, "Put op " + op);
        i.putExtra(Constants.KEY_OP, op);
        context.startActivity(i);
    }

    protected void toastInfo(String info) {
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
    }

    protected void onError(VolleyError error) {
        OnErrorPreProcess(error);

        NetworkResponse networkResponse = error.networkResponse;
        Throwable exception = error.getCause();
        if (networkResponse != null) {
            Log.e(TAG_VE, "Error with networkResponse " + networkResponse.statusCode);
        }
        if (exception != null) {
            Log.e(TAG_VE, "Error with Throwable " + error.getMessage());
        }
        if (error instanceof AirportError) {
            if (mToastAirportError) {
                Toast.makeText(mContext, getString(R.string.err_error) + ((AirportError) error).mErrorMsg, Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG_VE, "Airport Error " + ((AirportError) error).mErrorMsg);
        } else {
            if (networkResponse != null) {
                if (networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    Toast.makeText(mContext, getString(R.string.err_no_access_right), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Status code is SC_UNAUTHRORIZED");
                    checkAndRedirect();
                } else if (networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    Toast.makeText(mContext, getString(R.string.err_connection_problem), Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(mContext, getString(R.string.network_response_code) + networkResponse.statusCode, Toast.LENGTH_SHORT).show();
            } else if (exception != null) {
                Log.e(TAG_VE, error.getMessage());
                if (exception.getMessage().contains("authentication")) {
                    Toast.makeText(mContext, getString(R.string.err_no_access_right), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Exception of authentication");
                    checkAndRedirect();
                } else {
                    //Toast.makeText(mContext, getString(R.string.err_connection_problem), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext, getString(R.string.err_connection_problem), Toast.LENGTH_SHORT).show();
                }
            }
        }
        onErrorPostProcess(error);
    }

    protected void OnErrorPreProcess(VolleyError error) {
        dismissProgressDialog();
    }

    protected void onErrorPostProcess(VolleyError error) {
    }

    private void checkAndRedirect() {
        if (!mContext.getClass().getCanonicalName().equals(ActivityLogin.class.getCanonicalName())) {
            Intent i = new Intent(mContext, ActivityLogin.class);
            mContext.startActivity(i);
            ((Activity) mContext).finish();
        }
    }

    protected void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(mConfirmDialogTitle)
                .setMessage(mConfirmDialogMsg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        dialogConfirmed();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogCanceled();
            }
        }).create().show();
    }

    protected void showFinishDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(mConfirmDialogTitle)
                .setMessage("操作完成，是否返回主界面")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(mContext, ActivityEntry.class);
                        startActivity(i);
                        finish();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }


    protected void dialogConfirmed() {
    }

    protected void dialogCanceled() {
    }

    private Bitmap decodeJPGTOBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize  = 4;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private String scaledownJPG(String filePath){
        Bitmap bitmap = decodeJPGTOBitmap(filePath);
        if (bitmap == null) {
            return null;
        }
        FileOutputStream out;
        String ret = filePath + ".cmp.jpg";
        try {
            out = new FileOutputStream(ret);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        try {
            out.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        File toRemove = new File(filePath);
        toRemove.delete();
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraUtil.CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (!CameraUtil.fileExist(mAssetImage)) {
                Log.e(TAG, "Captured photo does not exist");
                mAssetImage = null;
                return;
            }
            String scaledJpg = scaledownJPG(CameraUtil.filePathOfUri(mAssetImage));
            if (scaledJpg == null) {
                mAssetImage = null;
                return;
            } else {
                mAssetImage = Uri.fromFile(new File(scaledJpg));
            }
            if (mAssetImageView != null && mAssetImage != null) {
                Log.i(TAG, "Set captured image to " + mAssetImage.toString());
                mAssetImageView.setImageURI(mAssetImage);
            }
        } else {
            Log.e(TAG, "Capture photo failed");
            mAssetImage = null;
        }
    }
}
