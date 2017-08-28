package jp.co.fake.TryCatchRoman.presentation.view.myroom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.fake.TryCatchRoman.R;
import jp.co.fake.TryCatchRoman.domain.usecase.camera.AutoFitTextureView;
import jp.co.fake.TryCatchRoman.domain.usecase.camera.Camera2StateMachine;
import jp.co.fake.TryCatchRoman.util.CustomImageView;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
  private final static int RESULT_CAMERA = 1001;
  private final static int REQUEST_PERMISSION = 1002;

  @BindView(R.id.image_view) AutoFitTextureView mySurfaceView ;
  @BindView(R.id.previewImage) ImageView previewImage ;
  @BindView(R.id.chara1_image) CustomImageView chara1_image ;
  @BindView(R.id.chara2_image) CustomImageView chara2_image ;
  @BindView(R.id.camera_button) Button cameraButton ;
  @BindView(R.id.camera_button2) Button cameraButton2 ;
  @BindView(R.id.camera_button3) Button cameraButton3 ;
  @BindView(R.id.stopbutton) Button stopbutton ;

  private Uri cameraUri;
  private File cameraFile;
  private String filePath;
  private Camera2StateMachine mCamera2;
  private int preDx, preDy, newDx, newDy;
  private AnimationDrawable frameAnimation1 ;
  private AnimationDrawable frameAnimation2 ;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    //SurfaceView
//    mySurfaceView.setVisibility(View.VISIBLE);
    chara1_image.setVisibility(View.VISIBLE);
    chara2_image.setVisibility(View.VISIBLE);
    chara1_image.setImageResource(R.drawable.guda_animation);
    chara2_image.setImageResource(R.drawable.roma_animation);
//    previewImage.setVisibility(View.GONE);
    mCamera2 = new Camera2StateMachine();

//    chara1_image.setOnTouchListener(this);
//    chara2_image.setOnTouchListener(this);

    // AnimationDrawableを取得
    frameAnimation1 = (AnimationDrawable) chara1_image.getDrawable();

    // アニメーションの開始
    frameAnimation1.start();

    // AnimationDrawableを取得
    frameAnimation2 = (AnimationDrawable) chara2_image.getDrawable();

    // アニメーションの開始
    frameAnimation2.start();

  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    int id = view.getId();
    if (id == R.id.chara1_image) {
      moveImage(chara1_image,event);
    }else{
      moveImage(chara2_image,event);
    }
    // x,y 位置取得
    newDx = (int)event.getRawX();
    newDy = (int)event.getRawY();



    // タッチした位置を古い位置とする
    preDx = newDx;
    preDy = newDy;

    return true;
  }

  public void moveImage(CustomImageView imageView,MotionEvent event){
    switch (event.getAction()) {
      // タッチダウンでdragされた
      case MotionEvent.ACTION_MOVE:
        // ACTION_MOVEでの位置
        int dx = imageView.getLeft() + (newDx - preDx);
        int dy = imageView.getTop() + (newDy - preDy);

        // 画像の位置を設定する
        imageView.layout(dx, dy, dx + imageView.getWidth(), dy + imageView.getHeight());

        Log.d("onTouch","ACTION_MOVE: dx="+dx+", dy="+dy);
        break;
    }
  }

  @OnClick(R.id.camera_button)
  public void submit() {
    chara1_image.setVisibility(View.VISIBLE);
    chara2_image.setVisibility(View.GONE);
  }

  @OnClick(R.id.camera_button2)
  public void submit2() {
    chara1_image.setVisibility(View.GONE);
    chara2_image.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.camera_button3)
  public void submit3() {
    chara1_image.setVisibility(View.VISIBLE);
    chara2_image.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.stopbutton)
  public void submit4() {
    if (frameAnimation2.isRunning()){
      frameAnimation1.stop();
      frameAnimation1.stop();
      frameAnimation2.stop();
      frameAnimation2.stop();
    }else{
      frameAnimation1.start();
      frameAnimation1.start();
      frameAnimation2.start();
      frameAnimation2.start();
    }

  }


  @Override
  protected void onResume() {
    super.onResume();
//    mCamera2.open(mySurfaceView, getApplicationContext());
  }
  @Override
  protected void onPause() {
    mCamera2.close();
    super.onPause();
  }

  public void onClickShutter() {
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

        previewImage.setImageBitmap(bitmap);
        previewImage.setVisibility(View.VISIBLE);
        mySurfaceView.setVisibility(View.GONE);
      }
    });
  }

  protected void onSaveInstanceState(Bundle outState){
    outState.putParcelable("CaptureUri", cameraUri);
  }

  private void cameraIntent(){
    // 保存先のフォルダーを作成
    File cameraFolder = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG"
    );
    cameraFolder.mkdirs();

    // 保存ファイル名
    String fileName = new SimpleDateFormat("ddHHmmss").format(new Date());
    filePath = cameraFolder.getPath() +"/" + fileName + ".jpg";
    Log.d("debug","filePath:"+filePath);

    // capture画像のファイルパス
    cameraFile = new File(filePath);
    cameraUri = Uri.fromFile(cameraFile);

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
    startActivityForResult(intent, RESULT_CAMERA);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CAMERA) {
      if(cameraUri != null){
        previewImage.setImageURI(cameraUri);
      }
      else{
        Log.d("debug","cameraUri == null");
      }
    }

  }

  // Runtime Permission check
  private void checkPermission(){
    // 既に許可している
    cameraIntent();
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//      cameraIntent();
//    }
//    // 拒否していた場合
//    else{
//      requestLocationPermission();
//    }
  }

  // 許可を求める
  private void requestLocationPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

    } else {
      Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
      toast.show();

      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);

    }
  }

  // 結果の受け取り
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION) {
      // 使用が許可された
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        cameraIntent();
        return;

      } else {
        // それでも拒否された時の対応
        Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
        toast.show();
      }
    }
  }
}
