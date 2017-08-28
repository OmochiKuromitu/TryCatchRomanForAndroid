package jp.co.fake.TryCatchRoman.presentation.view.base;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import jp.co.fake.TryCatchRoman.util.RuntimePermissionUtil;

import jp.co.fake.TryCatchRoman.R;
/**
 * 各Fragmentでの共通処理が記述されている
 */

public abstract class BaseFragment extends Fragment {
  private static final int PERMISSION_REQUEST_CODE = 1001;
  protected static final int OPERATION_TYPE_NFC = 0;
  protected static final int OPERATION_TYPE_NETWORK = 1;
  protected static final int TRANSITION_TYPE_PUSH_IN = 0;
  protected static final int TRANSITION_TYPE_PUSH_OUT = 1;
  protected static final int TRANSITION_TYPE_MODAL_IN = 2;
  protected static final int TRANSITION_TYPE_MODAL_OUT = 3;

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // アラート表示中に画面回転すると length ０でコールバックされるのでガードする
    if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
      // 失敗した場合
      if (!RuntimePermissionUtil.checkGrantResults(grantResults)) {
        if (RuntimePermissionUtil.hasSelfPermissions(getActivity(), Manifest.permission.CAMERA)) {
          String message;
          int closeType;
          message = getString(R.string.operation_or_permission_error_message_is_contract);
          closeType = TRANSITION_TYPE_MODAL_OUT;
          Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
          getActivity().finish();
          setTransitionAnim(closeType);
        } else {
          new Handler().post(new Runnable() {

            @Override
            public void run() {
              RuntimePermissionUtil.showAlertDialog(getFragmentManager(), getString(R.string.operation_camera));
            }
          });
        }
      } else {
        // カメラの権限が取れただけなので、その他の権限の確認を行う。
        onPermissionCheck();
      }
    }
  }

  /**
   * 画面遷移時のアニメーション設定
   *
   * @param animType アニメーションの種類
   */
  protected void setTransitionAnim(int animType) {
    switch (animType) {
      case TRANSITION_TYPE_PUSH_IN:
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        break;
      case TRANSITION_TYPE_PUSH_OUT:
        getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
        break;
      case TRANSITION_TYPE_MODAL_IN:
        getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottom);
        break;
      case TRANSITION_TYPE_MODAL_OUT:
        getActivity().overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_out_top);
        break;
      default:
        break;
    }
  }

  /**
   * 必要なPermissionが揃っている時の処理。各Fragmentで対応
   */
  protected void onPermissionSuccess() {}

  /**
   * パーミッションの状態を確認
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  protected void onPermissionCheck() {
    if (RuntimePermissionUtil.hasSelfPermissions(getActivity(), Manifest.permission.CAMERA)) {
      // 権限がある場合は、そのまま通常処理を行う
      onPermissionSuccess();
    } else {
      // 権限がない場合は、パーミッション確認アラートを表示する
      if (!RuntimePermissionUtil.hasSelfPermissions(getActivity(), Manifest.permission.CAMERA)) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
      }
    }
  }

  /**
   * 必要権限が許可されていない場合の確認ダイアログ表示
   *
   * @param type 許可されていない権限
   */
  private void showOperationDialog(final int type) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
    alertDialog.setMessage(getString(R.string.operation_or_permission_error_message_is_contract));
    alertDialog.setNegativeButton("終了", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        String message;
        int closeType;
        message = getString(R.string.operation_or_permission_error_message_is_contract);
        closeType = TRANSITION_TYPE_MODAL_OUT;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        getActivity().finish();
        setTransitionAnim(closeType);
      }
    });
    alertDialog.setPositiveButton("設定", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        onOperationDialogPositiveClick(type);
      }
    });
    alertDialog.setCancelable(false);
    alertDialog.create();
    alertDialog.show();
  }

  /**
   * 確認ダイアログのokが押された時の処理。各Fragmentで対応
   *
   * @param type 選択した権限
   */
  protected void onOperationDialogPositiveClick(int type) {}
}
