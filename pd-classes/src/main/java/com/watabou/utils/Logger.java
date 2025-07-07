package com.watabou.utils;

import android.util.Log;

public class Logger {
	private static final String TAG = "PD";

	public static void e(String message, Throwable tr) {
	Log.e(TAG, message, tr);
	}
}
