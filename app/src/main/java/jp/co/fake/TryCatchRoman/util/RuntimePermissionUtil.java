package jp.co.fake.TryCatchRoman.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

import jp.co.fake.TryCatchRoman.R;


/**
 * RuntimePermissionUtil
 */

public class RuntimePermissionUtil {

    /**
     * パーミッションのチェック
     *
     * @param context アプリのContext
     * @param permissions 確認したいpermissions
     * @return 設定済み:true 未設定:false
     */
    public static boolean hasSelfPermissions(@NonNull Context context, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * パーミッションの確認
     *
     * @param grantResults 権限の許可結果
     * @return 許可:true 不許可:false
     */
    public static boolean checkGrantResults(@NonNull int... grantResults) {
        if (grantResults.length == 0) throw new IllegalArgumentException("grantResults is empty");
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 未許可permissionの許可を促すDialogを表示する
     *
     * @param fragmentManager アプリのFragmentManager
     * @param permission 許可したいpermission
     */
    public static void showAlertDialog(FragmentManager fragmentManager, String permission) {
        RuntimePermissionAlertDialogFragment dialog = RuntimePermissionAlertDialogFragment.newInstance(permission);
        dialog.show(fragmentManager, RuntimePermissionAlertDialogFragment.TAG);
    }

    /**
     * 許可ダイアログの再表示判定
     *
     * @param activity アプリのactivity
     * @param permission 許可したいpermission
     * @return 必ず許可:true
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    /**
     * permissionチェックのDialog表示
     */
    // ダイアログ本体
    public static class RuntimePermissionAlertDialogFragment extends DialogFragment {

        public static final String TAG = "RuntimePermissionApplicationSettingsDialogFragment";
        private static final String ARG_PERMISSION_NAME = "permissionName";

        public static RuntimePermissionAlertDialogFragment newInstance(@NonNull String permission) {
            RuntimePermissionAlertDialogFragment fragment = new RuntimePermissionAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PERMISSION_NAME, permission);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String permission = getArguments().getString(ARG_PERMISSION_NAME);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setMessage(permission + getString(R.string.permission_message) + "EA0001")
                    .setPositiveButton(getString(R.string.permission_message_button_ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            // システムのアプリ設定画面
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.permission_message_button_no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            getActivity().finish();
                        }
                    });
            return dialogBuilder.create();
        }
    }

    /**
     * 必要パーミッションのみがmanifestに記載されているかどうか確認する。
     *
     * @param context アプリのコンテキスト
     * @return 記載されていないものがある:true 記載済みのものしかない:false
     */
    public static boolean hasPermissionError(Context context) {
        if (context == null) {
            // 引数が渡されていないケース
            return true;
        }
        String[] requestedPermissions = getPermissionList(context);
        if (requestedPermissions == null) {
            // manifestに一件もpermissionが設定されていないケース
            return true;
        }
        for (String str : requestedPermissions) {
            if (!(str.endsWith(Manifest.permission.CAMERA)) && (!str.endsWith(Manifest.permission.INTERNET)) && (!str.endsWith(Manifest.permission.NFC)) && (!str.endsWith(Manifest.permission.ACCESS_NETWORK_STATE)) && (!str.endsWith(Manifest.permission.WRITE_SECURE_SETTINGS)) && (!str.endsWith(Manifest.permission.SYSTEM_ALERT_WINDOW))) {
                return true;
            }
        }
        return false;
    }

    /**
     * manifestに記載されているパーミッションのリストを返します。
     *
     * @return パーミッションのリスト
     */
    private static String[] getPermissionList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return packageInfo.requestedPermissions;
    }
}
