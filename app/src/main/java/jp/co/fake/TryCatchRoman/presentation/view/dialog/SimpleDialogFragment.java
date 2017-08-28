package jp.co.fake.TryCatchRoman.presentation.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * SimpleDialogFragment
 */

public class SimpleDialogFragment extends DialogFragment {
  private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
  private DialogInterface.OnClickListener mOnClickListener;

  public static SimpleDialogFragment newInstance(int titleResId) {
    SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_TITLE_RES_ID, titleResId);
    dialogFragment.setArguments(args);
    return dialogFragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
            .setMessage(getArguments().getInt(ARG_TITLE_RES_ID))
            .setPositiveButton(android.R.string.ok, mOnClickListener)
            .create();
  }

  public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
    mOnClickListener = onClickListener;
  }
}
