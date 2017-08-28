package jp.co.fake.TryCatchRoman;

import android.app.Application;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jp.co.fake.TryCatchRoman.presentation.presenter.ProgressDialogObserver;
import jp.co.fake.TryCatchRoman.util.Logger;

/**
 * Applicationクラスの拡張クラスです
 */

public class ApplicationState extends Application implements ProgressDialogObserver {

  public static List<ProgressDialogObserver> getProgressDialogObservers() {
    return mProgressDialogObservers;
  }
  private static List<ProgressDialogObserver> mProgressDialogObservers = new CopyOnWriteArrayList<>();

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void showProgress() {
    Logger.debug("PDF", "SHOW");
    for (ProgressDialogObserver progressDialogObserver : mProgressDialogObservers) {
      progressDialogObserver.showProgress();
      Logger.debug("PDF", "SHOW0");
    }
  }

  @Override
  public void dismissProgress() {
    Logger.debug("PDF", "DISMISS");
    for (ProgressDialogObserver progressDialogObserver : mProgressDialogObservers) {
      progressDialogObserver.dismissProgress();
      Logger.debug("PDF", "DISMISS0");
    }
  }
}
