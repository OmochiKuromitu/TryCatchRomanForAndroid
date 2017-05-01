package jp.co.fake.TryCatchRoman.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ネットワーク状況を確認するユーティリティクラス
 * <p>
 * Copyright(c) 2016, Techfirm Inc. All Rights Reserved.
 */

public class Reachability {

  public static boolean isReachable(Context context) {
    NetworkInfo networkInfo = getNetworkInfo(context);
    if(networkInfo != null) {
      return networkInfo.isConnected();
    }
    return false;
  }

  public static boolean isReachableWifi(Context context) {
    NetworkInfo networkInfo = getNetworkInfo(context);
    if (networkInfo != null) {
      return networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
    return false;
  }

  public static boolean isReachableMobile(Context context) {
    NetworkInfo networkInfo = getNetworkInfo(context);
    if (networkInfo != null) {
      return networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
    return false;
  }

  private static NetworkInfo getNetworkInfo(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo();
  }
}