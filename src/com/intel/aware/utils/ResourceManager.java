/*
 *  Copyright (C) 2012 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.aware.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to some paths
 *
 * @author Huan Zheng <huan.zheng@intel.com>
 */
public final class ResourceManager {

    private static final String EXTERNDIR = "aware";
    private static final String TMP = "tmp";
    private static final String LOGFILE = "error.log";
    private static final String TAG = "ResourceManager";

    /**
     * Private constructor
     */
    private ResourceManager() {
    }

    /**
     * Get the directory of external storage
     *
     * @return File that represents the directory, or null if fail
     */
    public static File getExternalStorageDir() {
        if (!Environment.getExternalStorageState().endsWith(
                Environment.MEDIA_MOUNTED)) {
            return null;
        }

        final File directory = new File(
                Environment.getExternalStorageDirectory(), EXTERNDIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory;
    }

    /**
     * Read the content of specific file
     *
     * @param context  Android context
     * @param fileName The file to be read
     * @return byte[]
     */
    public static byte[] readInternalFile(Context context, String fileName) {
        try {
            InputStream is = context.openFileInput(fileName);
            if (is.available() <= 0) {
                return null;
            }

            byte[] buf = new byte[is.available()];
            if (is.read(buf) > 0) {
                return buf;
            } else {
                return null;
            }
        } catch (IOException e) {
            if (D.DEBUG) {
                L.e(TAG, e);
            }
        }
        return null;
    }

    /**
     * Get the temporary file on external storage
     *
     * @return File, or null if fail
     */
    public static File getExternalStorageTmpFile() {
        File externalDir = getExternalStorageDir();
        if (externalDir == null) {
            return null;
        }
        File tmpFile = new File(externalDir, TMP);
        return tmpFile;
    }

    /**
     * Get the error log file
     *
     * @param context Android context
     * @return File
     */
    public static File getExceptionLogFile(Context context) {
        File file = new File(context.getFilesDir(), LOGFILE);
        return file;
    }
}

