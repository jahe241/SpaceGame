package com.spacegame.utils;

import android.util.Log;

/**
 * The DebugLogger class is a utility class that provides a simple logging mechanism for debugging
 * purposes.
 */
public class DebugLogger {
  static final boolean DEBUG = true;

  public static void log(String tag, String message) {
    if (DEBUG) {
      Log.d(tag, message);
    }
  }
}
