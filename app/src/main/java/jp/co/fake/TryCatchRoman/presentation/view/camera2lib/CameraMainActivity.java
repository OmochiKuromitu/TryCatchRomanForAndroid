package jp.co.fake.TryCatchRoman.presentation.view.camera2lib;

import java.nio.ByteBuffer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.fake.TryCatchRoman.R;
import jp.co.fake.TryCatchRoman.util.CustomImageView;

public class CameraMainActivity extends Activity {
	private Camera2StateMachine mCamera2;
	private MediaProjectionManager mediaProjectionManager;
	private MediaProjection mediaProjection;
	private static final int REQUEST_MEDIA_PROJECTION = 1001;

	private int displayWidth, displayHeight;
	private ImageReader imageReader;
	private VirtualDisplay virtualDisplay;
	private int screenDensity;
	private ImageView imageView;

	@BindView(R.id.TextureView)
	AutoFitTextureView mTextureView ;

	@BindView(R.id.ImageView)
	ImageView mImageView ;

	@BindView(R.id.chara1_image)
	CustomImageView chara1_image ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		ButterKnife.bind(this);

		mCamera2 = new Camera2StateMachine();

		mediaProjectionManager = (MediaProjectionManager)
				getSystemService(MEDIA_PROJECTION_SERVICE);

		chara1_image.setImageResource(R.drawable.shime1);
		chara1_image.setVisibility(View.VISIBLE);

		// 画面の縦横サイズとdpを取得
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenDensity = displayMetrics.densityDpi;
		displayWidth = displayMetrics.widthPixels;
		displayHeight = displayMetrics.heightPixels;

		// permissionを確認するintentを投げ、ユーザーの許可・不許可を受け取る
		if (loadCature().equals("NO") || loadCature().equals("")){
			startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
					REQUEST_MEDIA_PROJECTION);
		}

	}

	// ユーザーの許可を受け取る
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_MEDIA_PROJECTION == requestCode) {
			if (resultCode != RESULT_OK) {
				saveCature("NO");
				// 拒否された
				Toast.makeText(this, "User cancelled", Toast.LENGTH_LONG).show();
				return;
			}else{
				saveCature("YES");
			}
			// 許可された結果を受け取る
			setUpMediaProjection(resultCode, data);
		}
	}

	private void setUpMediaProjection(int code, Intent intent) {
		mediaProjection = mediaProjectionManager.getMediaProjection(code, intent);
		setUpVirtualDisplay();
	}

	private void setUpVirtualDisplay() {

		imageReader = ImageReader.newInstance(displayWidth, displayHeight, PixelFormat.RGBA_8888, 2);
		virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
				displayWidth, displayHeight, screenDensity,
				DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
				imageReader.getSurface(), null, null);
	}

	@OnClick(R.id.imageButton1)
	public void getScreenshot() {
		// ImageReaderから画面を取り出す
		Log.d("debug", "getScreenshot");

		Image image = imageReader.acquireLatestImage();
		Image.Plane[] planes = image.getPlanes();
		ByteBuffer buffer = planes[0].getBuffer();

		int pixelStride = planes[0].getPixelStride();
		int rowStride = planes[0].getRowStride();
		int rowPadding = rowStride - pixelStride * displayWidth;

		// バッファからBitmapを生成
		Bitmap bitmap = Bitmap.createBitmap(
				displayWidth + rowPadding / pixelStride, displayHeight,
				Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(buffer);
		image.close();

		mImageView.setImageBitmap(bitmap);

		mImageView.setVisibility(View.VISIBLE);
		mTextureView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
			//許可されていない時の処理
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
				//拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
			} else {
				//まだ許可を求める前の時、許可を求めるダイアログを表示します。
				ActivityCompat.requestPermissions(this, new String[]
						{android.Manifest.permission.CAMERA,
						android.Manifest.permission.WRITE_EXTERNAL_STORAGE
						}, 0);

			}
		}else{
			mCamera2.open(this, mTextureView);
		}

	}
	@Override
	protected void onPause() {
//		mCamera2.close();
		super.onPause();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mImageView.getVisibility() == View.VISIBLE) {
			mTextureView.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.INVISIBLE);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

//	@OnClick(R.id.imageButton1)
	public void onClickShutter() {
		Log.e("Camera", "おされた");
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

				// 画像の横、縦サイズを取得
				int imageWidth = bitmap.getWidth();
				int imageHeight = bitmap.getHeight();

				// 画像中心を基点に90度回転
				// Matrix インスタンス生成
				Matrix matrix = new Matrix();
				matrix.setRotate(90, imageWidth/2, imageHeight/2);

				// 90度回転したBitmap画像を生成
				Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);

				mImageView.setImageBitmap(bitmap2);
				mImageView.setVisibility(View.VISIBLE);
				mTextureView.setVisibility(View.INVISIBLE);
			}
		});
	}

	/** 入力された「名前」を「年齢」をプリファレンスに保存する */
	public void saveCature(String results){
		// プリファレンスの準備 //
		SharedPreferences pref =
				this.getSharedPreferences( "capture", this.MODE_PRIVATE );

		// プリファレンスに書き込むためのEditorオブジェクト取得 //
		SharedPreferences.Editor editor = pref.edit();

		// "user_name" というキーで名前を登録
		editor.putString( "capture", results );

		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	public String loadCature(){
		// プリファレンスの準備 //
		SharedPreferences pref =
				this.getSharedPreferences( "capture", this.MODE_PRIVATE );

		// "user_name" というキーで保存されている値を読み出す
		return pref.getString( "capture", "" );
	}
}
