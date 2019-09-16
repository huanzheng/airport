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
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

/**
 * Specialized crash logger when unexpected exception happens, it will dump backtrace to a file
 *
 * @author Huan Zheng <huan.zheng@intel.com>
 */
public final class CrashLogger implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private static final String TAG = "CrashLogger";

    /**
     * Constructor
     *
     * @param context The android Context
     */
    public CrashLogger(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;
    }

    /**
     * Print out current time
     *
     * @param pw Target PrintWriter
     */
    private void printTime(PrintWriter pw) {
        pw.println(Calendar.getInstance().getTime().toLocaleString());
    }

    /**
     * Dump device information into PrintWriter
     *
     * @param pw Target PrintWriter
     */
    private void dumpDeviceInfo(PrintWriter pw) {
        String str = String.format(
                "Brand:%s, Manufacturer:%s, Product:%s,Version:%s, build:%s, ",
                Build.BRAND, Build.MANUFACTURER, Build.MODEL,
                Build.VERSION.RELEASE, Build.DISPLAY);
        pw.println(str);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            File file = ResourceManager.getExceptionLogFile(mContext);
            if (file == null) {
                return;
            }
            L.i(TAG, "Log uncaughtException into " + file.getAbsolutePath());

            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            pw.println("------------------------------------------------------");
            printTime(pw);
            dumpDeviceInfo(pw);
            ex.printStackTrace(pw);
            pw.println();
            pw.close();
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // ignore this, because it will not happened.
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (D.DEBUG) {
                // if debug mode. we rethrow the exception to let the QA know
                // it.when release. we consume it and do not let the user know.
                mDefaultHandler.uncaughtException(thread, ex);
            }
        }
    }
}
