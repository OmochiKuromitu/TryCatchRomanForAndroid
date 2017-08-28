package jp.co.fake.TryCatchRoman.presentation.presenter;

/**
 * プログレス表示のオブザーバ
 */

public interface ProgressDialogObserver {
    void showProgress();
    void dismissProgress();
}
