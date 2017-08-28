package jp.co.fake.TryCatchRoman.presentation.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import jp.co.fake.TryCatchRoman.R;
import jp.co.fake.TryCatchRoman.presentation.presenter.ProgressDialogObserver;


/**
 * ProgressDialogFragment
 */

public class ProgressDialogFragment extends DialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    setCancelable(false);
    ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage(getString(R.string.LL0001));
    return progressDialog;
  }

  public static void show(@Nullable Context context) {
    if (getProgressDialogObserver(context) != null) {
      ((ProgressDialogObserver) context.getApplicationContext()).showProgress();

    }
  }

  public static void dismiss(@Nullable Context context) {
    if (getProgressDialogObserver(context) != null) {
      ((ProgressDialogObserver) context.getApplicationContext()).dismissProgress();
    }
  }

  private static @Nullable ProgressDialogObserver getProgressDialogObserver(@Nullable Context context) {
    if (context != null && context.getApplicationContext() != null && context.getApplicationContext() instanceof ProgressDialogObserver) {
      return (ProgressDialogObserver) context.getApplicationContext();
    }
    return null;
  }
}
