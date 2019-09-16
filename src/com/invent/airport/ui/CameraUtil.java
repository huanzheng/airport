package com.invent.airport.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class CameraUtil {
    private static final String TAG = "CameraUtil";
    public static final int CAMERA_REQUEST = 8888;
    private static final String FOLDER_NAME = "Airport";

    public static boolean fileExist(Uri uri) {
        if (uri == null) {
            return false;
        }
        File file = fileOfUri(uri);
        return file.exists();
    }

    public static File fileOfUri(Uri uri) {
        File file = new File(filePathOfUri(uri));
        return file;
    }

    public static String filePathOfUri(Uri uri) {
        String path = uri.toString();
        return path.substring(7, path.length());
    }

    private static Uri getOutputMediaFileUri() {
        File file = getOutputMediaFile();
        if (file == null) {
            return null;
        }
        return Uri.fromFile(file);
    }

    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(mContext.getExternalFilesDir(
        //          Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), FOLDER_NAME);
        Log.i(TAG, "media storage dir is " + mediaStorageDir.getAbsolutePath());


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        Log.d(TAG, "final path is " + mediaFile.getAbsolutePath());

        return mediaFile;
    }

    public static Uri startCameraCapture(Activity context, int requestID) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        if (fileUri != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            context.startActivityForResult(cameraIntent, requestID);
        } else {
            Log.e(TAG, "Can not start camera capture, fileUri is null");
        }
        return fileUri;
    }

    public static boolean SDCardWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
