package jp.co.fake.TryCatchRoman.util;

import android.util.Log;

import jp.co.fake.TryCatchRoman.BuildConfig;

/**
 * ログ出力のユーティリティクラス
 * <p>
 * Copyright(c) 2016, Techfirm Inc. All Rights Reserved.
 */
@SuppressWarnings("unused")
public class Logger {

  /**
   * show Debug log.
   *
   * @param tag     タグ
   * @param message メッセージ
   */
  public static void debug(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, String.format("[DEBUG](%d) %s",Thread.currentThread().getId(), message));
    }
  }

  /**
   * show Info log.
   *
   * @param tag     タグ
   * @param message メッセージ
   */
  public static void info(String tag, String message) {
    if (BuildConfig.DEBUG || BuildConfig.STAGING) {
      Log.i(tag, String.format("[INFO](%d) %s",Thread.currentThread().getId(), message));
    }
  }

  /**
   * show Error log.
   *
   * @param tag     タグ
   * @param message メッセージ
   */
  public static void error(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, String.format("[ERROR](%d) %s",Thread.currentThread().getId(), message));
    }
  }

  /**
   * show Error log.
   *
   * @param tag     タグ
   * @param message メッセージ
   * @param error   例外
   */
  public static void error(String tag, String message, Throwable error) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, String.format("[ERROR](%d) %s",Thread.currentThread().getId(), message), error);
    }
  }

  /**
   * show Warn log.
   *
   * @param tag     タグ
   * @param message メッセージ
   */
  public static void warn(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.w(tag, String.format("[WARN](%d) %s",Thread.currentThread().getId(), message));
    }
  }
}
