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

import android.util.Log;

/**
 * Log utility for geoTF
 *
 * @author Huan Zheng <huan.zheng@intel.com>
 */
public final class L {
    private static final int STACK_LENGTH = 3;

    /**
     * Private constructor
     */
    private L() {
    }

    /**
     * Verbose level
     *
     * @param info Output information
     */
    public static void v(String tag, Object info) {
        if (!D.VERB) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String[] classFullName = e.getClassName().split("\\.");
        String className = classFullName[classFullName.length - 1];
        String str = String.format("[%s.%s]: %s", className, e.getMethodName(),
                info);
        Log.v(tag, str);
    }

    /**
     * Debug level
     *
     * @param info Output information
     */
    public static void d(String tag, Object info) {
        if (!D.DEBUG) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String[] classFullName = e.getClassName().split("\\.");
        String className = classFullName[classFullName.length - 1];
        String str = String.format("[%s.%s]: %s", className, e.getMethodName(),
                info);
        Log.d(tag, str);
    }

    /**
     * Information level
     *
     * @param info Output information
     */
    public static void i(String tag, Object info) {
        if (!D.INFO) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String[] classFullName = e.getClassName().split("\\.");
        String className = classFullName[classFullName.length - 1];
        String str = String.format("[%s.%s]: %s", className, e.getMethodName(),
                info);
        Log.i(tag, str);
    }

    /**
     * Warning level
     *
     * @param info Output information
     */
    public static void w(String tag, Object info) {
        if (!D.WARN) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String str = String.format("[%s]: %s", e.toString(), info);
        Log.w(tag, str);
    }

    /**
     * Error level
     *
     * @param info Output information
     */
    public static void e(String tag, Object info) {
        if (!D.ERROR) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String str = String.format("[%s]: %s", e.toString(), info);
        Log.e(tag, str);
    }

    /**
     * Function to help with debug dead lock
     * Tend to be called when entering critical region
     */
    public static void lock() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String[] classFullName = e.getClassName().split("\\.");
        String className = classFullName[classFullName.length - 1];
        String str = String.format("[%s.%s]: %s", className, e.getMethodName(),
                "LLLLLLocked");

        Log.i("LOCK", str + " in thread " + Thread.currentThread().getId());
    }

    /**
     * Function to help with debug dead lock
     * Tend to be called when leaving critical region
     */
    public static void unLock() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[STACK_LENGTH];
        String[] classFullName = e.getClassName().split("\\.");
        String className = classFullName[classFullName.length - 1];
        String str = String.format("[%s.%s]: %s", className, e.getMethodName(),
                "UUUUUnLocked");

        Log.i("LOCK", str + " in thread " + Thread.currentThread().getId());
    }
}