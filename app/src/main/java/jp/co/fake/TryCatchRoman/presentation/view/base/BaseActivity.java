package jp.co.fake.TryCatchRoman.presentation.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.co.fake.TryCatchRoman.ApplicationState;
import jp.co.fake.TryCatchRoman.presentation.presenter.ProgressDialogObserver;
import jp.co.fake.TryCatchRoman.presentation.view.dialog.ProgressDialogFragment;
import jp.co.fake.TryCatchRoman.util.Logger;


/**
 * 各Activityでの共通処理が記述されている
 */

public abstract class BaseActivity extends AppCompatActivity implements ProgressDialogObserver {
  private ProgressDialogFragment mProgressDialog;
  private List<ProgressDialogFragment> mProgressDialogs = new ArrayList<>();
  private boolean mIsShowingDialog = false;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ApplicationState.getProgressDialogObservers().add(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    ApplicationState.getProgressDialogObservers().remove(this);
    ApplicationState.getProgressDialogObservers().add(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    ApplicationState.getProgressDialogObservers().remove(this);
  }

  @Override
  public void showProgress() {
    if (mProgressDialog == null || !mProgressDialog.isVisible() || !mIsShowingDialog) {
      mIsShowingDialog = true;
      mProgressDialog = new ProgressDialogFragment();
      try {
        mProgressDialog.show(getFragmentManager(), "Progress");
        mProgressDialogs.add(mProgressDialog);
      } catch (IllegalStateException e) {
        Logger.debug("Progress", e.toString());
      }
    }
  }

  @Override
  public void dismissProgress() {
    if (mProgressDialog != null) {
      mIsShowingDialog = false;
      for (ProgressDialogFragment progressDialogFragment : mProgressDialogs) {
        progressDialogFragment.dismiss();
      }
      mProgressDialogs.clear();
    }
  }
}
