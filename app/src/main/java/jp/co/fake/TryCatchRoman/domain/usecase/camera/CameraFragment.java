package jp.co.fake.TryCatchRoman.domain.usecase.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.fake.TryCatchRoman.R;
import jp.co.fake.TryCatchRoman.presentation.view.base.BaseFragment;


/**
 * CameraQRFragment
 */

public class CameraFragment extends BaseFragment{

  private Camera2StateMachine mCamera2;
  private Context mContext;
  @BindView(R.id.texture) AutoFitTextureView mTextureView;
  @BindView(R.id.imageView2) ImageView mImageView;
  @BindView(R.id.camera_button) ImageButton btnCamera;



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_camera, container, false);
    ButterKnife.bind(this, view);
    mCamera2 = new Camera2StateMachine();
    return inflater.inflate(R.layout.fragment_camera, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();
    mCamera2.open(mTextureView,getActivity());
  }
  @Override
  public void onPause() {
    mCamera2.close();
    super.onPause();
  }

  public void onClickShutter(View view) {
    mCamera2.takePicture(new ImageReader.OnImageAvailableListener() {
      @Override
      public void onImageAvailable(ImageReader reader) {
        // 撮れた画像をImageViewに貼り付けて表示。
        final Image image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.close();

        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
        mTextureView.setVisibility(View.INVISIBLE);
      }
    });
  }

  @OnClick(R.id.camera_button)
  public void submit(View view) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
    alertDialog.setMessage("おされた");
//    alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//
//      @Override
//      public void onClick(DialogInterface dialog, int which) {
//        dialog.dismiss();
//        getActivity().finish();
//        getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
//      }
//    });
    alertDialog.setCancelable(false);
    alertDialog.create();
    alertDialog.show();
  }

}