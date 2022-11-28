package com.loupe.project;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.loupe.project.camare.AnimSpring;
import com.loupe.project.camare.BitmapUtils;
import com.loupe.project.receiver.BluetoothMonitorReceiver;
import com.loupe.project.camare.CameraPreview;
import com.loupe.project.camare.DataCleanManager;
import com.loupe.project.receiver.NetworkConnectChangedReceiver;
import com.loupe.project.camare.OverCameraView;
import com.loupe.project.receiver.PackageReceiver;
import com.loupe.project.receiver.PhoneReceiver;
import com.loupe.project.camare.TimerUtils;
import com.loupe.project.receiver.BatteryBroadcastReceiver;
import com.loupe.project.receiver.ScreenActionReceiver;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainActivity INSTANCE;

    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 相机预览
     */
    private FrameLayout mPreviewLayout;
    /**
     * 聚焦视图
     */
    private OverCameraView mOverCameraView;
    /**
     * 相机类
     */
    private Camera mCamera;

    private int maxZoom;
    private Camera.Parameters parameters;

    /**
     * 是否正在聚焦
     */
    private boolean isFoucing;

    /**
     * 拍照标记
     */
    private boolean isTakePhoto;

    private Runnable mRunnable;
    private Handler mHandler = new Handler();

    /**
     * 拍照按钮
     */
    private ImageView mPhotoButton;

    /**
     * 确定按钮视图
     */
    private RelativeLayout mConfirmLayout;
    /**
     * 取消保存按钮
     */
    private ImageView mCancleSaveButton;

    /**
     * 图片流暂存
     */
    private byte[] imageData;

    /**
     * 保存按钮
     */
    private ImageView mSaveButton;

    private boolean isFlashing;

    /**
     * 闪光灯
     */
    private ImageView mFlashButton;

    private SeekBar vSeekBar;

    private ImageView ivShow, ivLeft, ivHeightLight, ivScale, ivRoll;

    private DrawerLayout dlPicture;

    private LinearLayout llLeft;

    private RelativeLayout rlMain, rlPicture, rlCache;

    private TextView tvCache, tvTint, tvTintStart, tvTintEnd;

    private boolean isHeightLight;

    private float tintNum = 0;

    private boolean isRoll;

    private int flashClick = 1, lensClick = 1;

    private ScreenActionReceiver screenActionReceiver;

    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    private PhoneReceiver phoneReceiver;

    private BatteryBroadcastReceiver usbReceiver;

    private BluetoothMonitorReceiver bluetoothMonitorReceiver;

    private PackageReceiver packageReceiver;

    private long lastPictureShowTime = 0, lastFlashShowTime = 0, lastLensShowTime = 0, lastUnderShowTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        INSTANCE = this;
        initView();
        initTimer();
        initListener();
        initStatusBar();

        screenActionReceiver = new ScreenActionReceiver();
        screenActionReceiver.register(this);

        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        networkConnectChangedReceiver.register(this);

        phoneReceiver = new PhoneReceiver();
        phoneReceiver.register(this);

        usbReceiver = new BatteryBroadcastReceiver();
        usbReceiver.register(this);

        bluetoothMonitorReceiver = new BluetoothMonitorReceiver();
        bluetoothMonitorReceiver.register(this);

        packageReceiver = new PackageReceiver();
        packageReceiver.register(this);

        try {
            tvCache.setText(DataCleanManager.getTotalCacheSize(INSTANCE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        mPhotoButton.setOnClickListener(this);
        mCancleSaveButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        rlPicture.setOnClickListener(this);
        mFlashButton.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        rlCache.setOnClickListener(this);
        ivHeightLight.setOnClickListener(this);
        ivScale.setOnClickListener(this);
        ivRoll.setOnClickListener(this);

        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress == 0) {
                    ivScale.setImageResource(R.mipmap.btn_scale_normal);
                } else {
                    ivScale.setImageResource(R.mipmap.btn_scale_selected);
                    parameters = mCamera.getParameters();
                    tintNum = ((progress * (1.0f / (maxZoom * 100))) * maxZoom);
                    if (progress >= 1980) {
                        tvTint.setText("20.0");
                    } else if (progress <= 20) {
                        tvTint.setText("0.0");
                    } else {
                        tvTint.setText(String.format("%.1f", tintNum));
                    }
                    parameters.setZoom((int) (tintNum));
                    mCamera.setParameters(parameters);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvTint.setVisibility(View.VISIBLE);
                tvTintStart.setVisibility(View.VISIBLE);
                tvTintEnd.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvTint.setVisibility(View.GONE);
                tvTintStart.setVisibility(View.GONE);
                tvTintEnd.setVisibility(View.GONE);
            }
        });

        rlMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isFoucing) {
                        float x = event.getX();
                        float y = event.getY();
                        isFoucing = true;
                        if (mCamera != null && !isTakePhoto) {
                            mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                        }
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Autofocus Timeout", Toast.LENGTH_SHORT).show();
                                isFoucing = false;
                                mOverCameraView.setFoucuing(false);
                                mOverCameraView.disDrawTouchFocusRect();
                            }
                        };
                        //设置聚焦超时
                        mHandler.postDelayed(mRunnable, 3000);
                    }
                }
                return false;
            }
        });
    }

    private void initView() {
        mPreviewLayout = findViewById(R.id.camera_preview_layout);
        mPhotoButton = findViewById(R.id.take_photo_button);
        mConfirmLayout = findViewById(R.id.ll_confirm_layout);
        mCancleSaveButton = findViewById(R.id.cancle_save_button);
        mSaveButton = findViewById(R.id.save_button);
        rlPicture = findViewById(R.id.rlPicture);
        mFlashButton = findViewById(R.id.flash_button);
        vSeekBar = findViewById(R.id.seekBar_bright);
        ivShow = findViewById(R.id.ivShow);
        dlPicture = findViewById(R.id.dlPicture);
        ivLeft = findViewById(R.id.btn_drawer_left);
        llLeft = findViewById(R.id.llLeft);
        rlMain = findViewById(R.id.rlMain);
        tvCache = findViewById(R.id.tvCache);
        rlCache = findViewById(R.id.rlCache);
        ivHeightLight = findViewById(R.id.ivHeightLight);
        ivScale = findViewById(R.id.ivScale);
        tvTint = findViewById(R.id.tvTint);
        tvTintStart = findViewById(R.id.tvTintStart);
        tvTintEnd = findViewById(R.id.tvTintEnd);
        ivRoll = findViewById(R.id.ivRoll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AndPermission.hasPermissions(INSTANCE, Permission.Group.CAMERA) && AndPermission.hasPermissions(INSTANCE, Permission.Group.STORAGE)) {
            initCamera(false);
        }
    }

    private void initCamera(boolean isRoll) {
        if (isRoll) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        CameraPreview preview = new CameraPreview(this, mCamera);
        mOverCameraView = new OverCameraView(this);

        parameters = mCamera.getParameters();
        maxZoom = parameters.getMaxZoom();
        vSeekBar.setProgress(0);
        vSeekBar.setMax(maxZoom * 20);
        mPreviewLayout.addView(preview);
        mPreviewLayout.addView(mOverCameraView);
    }

    private void initStatusBar() {
        //取消状态栏
        if (CommonLoupeSetUtils.canLightLoupeStatusBar()) {
            //判断是否支持
            UltimateBar.newTransparentBuilder()
                    // 状态栏颜色
                    .statusColor(Color.TRANSPARENT)
                    // 状态栏透明度
                    .statusAlpha(50).build(this).apply();
        }

        //状态内容颜色
        CommonLoupeSetUtils.onlyLoupeLightStatusbarTextDark(this.getWindow(), false);

        checkPermissions();
    }

    //点击按钮，访问如下方法
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(INSTANCE, permissions[0]);
            int m = ContextCompat.checkSelfPermission(INSTANCE, permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || m != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(INSTANCE, permissions, 321);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AndPermission.with(INSTANCE).runtime().setting().start();
                } else {
                    initCamera(false);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.take_photo_button) {
            if (!isTakePhoto) {
                takePhoto();
            }
        } else if (id == R.id.cancle_save_button) {
            cancleSavePhoto();
        } else if (id == R.id.save_button) {
            savePhoto();
        } else if (id == R.id.rlPicture) {
            if (dlPicture.isDrawerOpen(llLeft)) {
                dlPicture.closeDrawer(llLeft);
            }
//            TimerUtils.getInstance().startTimer(1000, 30, 0, 1000);
            openSystemPic(Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera");
        } else if (id == R.id.flash_button) {
            switchFlash();
        } else if (id == R.id.btn_drawer_left) {
            if (dlPicture.isDrawerOpen(llLeft)) { // 左侧菜单列表已打开
                dlPicture.closeDrawer(llLeft); // 关闭左侧抽屉
            } else { // 左侧菜单列表未打开
                dlPicture.openDrawer(llLeft); // 打开左侧抽屉
            }
        } else if (id == R.id.rlCache) {
            if (dlPicture.isDrawerOpen(llLeft)) {
                dlPicture.closeDrawer(llLeft);
            }
            try {
                DataCleanManager.clearAllCache(INSTANCE);
                Toast.makeText(INSTANCE, "Clear Success", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(INSTANCE, "Clear Failed", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.ivHeightLight) {
            if (isHeightLight) {
                isHeightLight = false;
                ivHeightLight.setImageResource(R.mipmap.btn_light_normal);
                releaseWakeLock();
            } else {
                isHeightLight = true;
                ivHeightLight.setImageResource(R.mipmap.btn_light_selected);
                acquireWakeLock(INSTANCE);
            }
        } else if (id == R.id.ivScale) {
            ivScale.setImageResource(R.mipmap.btn_scale_normal);
            vSeekBar.setProgress(0);
            parameters = mCamera.getParameters();
            parameters.setZoom(0);
            mCamera.setParameters(parameters);
        } else if (id == R.id.ivRoll) {
            lensClick++;
            if (lensClick % 2 == 0) {
                long currentLensShowTime = System.currentTimeMillis();
                if ((currentLensShowTime - lastLensShowTime) / 1000 > 30) {
                    lastLensShowTime = currentLensShowTime;
                    //todo 1-4-内部广告位逻辑-点击镜头调整按钮

                }
            }
            AnimSpring.getInstance(ivRoll).startRotateAnim(120, 360);
            //翻转摄像头
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
                mCamera = null;
            }
            isRoll = !isRoll;
            mFlashButton.setVisibility(isRoll ? View.GONE : View.VISIBLE);
            initCamera(isRoll);
        }
    }

    private void switchFlash() {
        isFlashing = !isFlashing;
        mFlashButton.setImageResource(isFlashing ? R.mipmap.btn_flashlight_selected : R.mipmap.btn_flashlight_normal);
        AnimSpring.getInstance(mFlashButton).startRotateAnim(120, 360);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(isFlashing ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Toast.makeText(this, "This device does not support flash", Toast.LENGTH_SHORT).show();
        }

        flashClick++;
        if (flashClick % 2 == 0) {
            long currentFlashShowTime = System.currentTimeMillis();
            if ((currentFlashShowTime - lastFlashShowTime) / 1000 > 30) {
                lastFlashShowTime = currentFlashShowTime;
                //todo 1-3-内部广告位逻辑-点击手电筒按钮

            }

        }
    }

    private void openSystemPic(String path) {
//        File file = new File(path);
//        if (file.exists() && file.listFiles() != null && file.listFiles().length > 0) {
//            new PictureScanner(INSTANCE, path);
//        } else {
//            Toast.makeText(INSTANCE, "please take a photo", Toast.LENGTH_SHORT).show();
//        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long currentPictureShowTime = System.currentTimeMillis();
        if ((currentPictureShowTime - lastPictureShowTime) / 1000 >= 30) {
            lastPictureShowTime = currentPictureShowTime;
            //todo 1-2-内部广告位逻辑-进入相册


        }

    }

    private void savePhoto() {
        FileOutputStream fos = null;
        String cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
        //相册文件夹
        File cameraFolder = new File(cameraPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //保存的图片文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
        File imageFile = new File(imagePath);
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    Bitmap retBitmap = BitmapFactory.decodeFile(imagePath);
                    retBitmap = BitmapUtils.setTakePicktrueOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, retBitmap);
                    if (isRoll) {
                        Bitmap realBitmap = flip(retBitmap);
                        BitmapUtils.saveBitmap(realBitmap, imagePath);
                        ivShow.setImageBitmap(realBitmap);
                    } else {
                        BitmapUtils.saveBitmap(retBitmap, imagePath);
                        ivShow.setImageBitmap(retBitmap);
                    }
                    ivShow.setVisibility(View.VISIBLE);
                    doAnimEnd();
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(imagePath));
                    intent.setData(uri);
                    sendBroadcast(intent);

                    cancleSavePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(INSTANCE, "save failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void takePhoto() {
        isTakePhoto = true;
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                //视图动画
                mPhotoButton.setVisibility(View.GONE);
                mConfirmLayout.setVisibility(View.VISIBLE);
                AnimSpring.getInstance(mConfirmLayout).startRotateAnim(120, 360);
                imageData = bytes;
                //停止预览
                mCamera.stopPreview();
                ivLeft.setVisibility(View.GONE);
                ivHeightLight.setVisibility(View.GONE);
                ivScale.setVisibility(View.GONE);
                mFlashButton.setVisibility(View.GONE);
                vSeekBar.setVisibility(View.GONE);
                ivRoll.setVisibility(View.GONE);
            }
        });
    }

    private void cancleSavePhoto() {
        mPhotoButton.setVisibility(View.VISIBLE);
        mConfirmLayout.setVisibility(View.GONE);
        ivLeft.setVisibility(View.VISIBLE);
        ivHeightLight.setVisibility(View.VISIBLE);
        ivScale.setVisibility(View.VISIBLE);
        mFlashButton.setVisibility(View.VISIBLE);
        vSeekBar.setVisibility(View.VISIBLE);
        ivRoll.setVisibility(View.VISIBLE);
        AnimSpring.getInstance(mPhotoButton).startRotateAnim(120, 360);
        //开始预览
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
    }

    public void doAnimEnd() {

        ScaleAnimation animationOne = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);

        TranslateAnimation animationTwo = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, dp2Px(INSTANCE, 40),

                Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, dp2Px(INSTANCE, 70));

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animationOne);
        animationSet.addAnimation(animationTwo);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(500);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivShow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivShow.setAnimation(animationSet);
    }

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void acquireWakeLock(Context context) {
        try {
            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.screenBrightness = 1.0f;
            this.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseWakeLock() {
        ContentResolver cr = getContentResolver();
        try {
            int mCurrentbrightness = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = mCurrentbrightness;
            getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap flip(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
        Matrix matrixMirrorY = new Matrix();
        matrixMirrorY.setValues(mirrorY);
        matrix.postConcat(matrixMirrorY);
        matrix.postRotate(180);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TimerUtils.getInstance().startTimer(1000, 5, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (StatusBarLoupeColorUtil.isAppOnForeground(this)) {
            long currentUnderShowTime = System.currentTimeMillis();
            if ((currentUnderShowTime - lastUnderShowTime) / 1000 > 300) {
                lastUnderShowTime = currentUnderShowTime;
                //todo 2-1-TW广告位逻辑-在后台倒计时弹出(静默展示)

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screenActionReceiver != null) {
            screenActionReceiver.unRegister(this);
        }
        if (networkConnectChangedReceiver != null) {
            networkConnectChangedReceiver.unRegister(this);
        }
        if (phoneReceiver != null) {
            phoneReceiver.unRegister(this);
        }
        if (usbReceiver != null) {
            usbReceiver.unRegister(this);
        }
        if (bluetoothMonitorReceiver != null) {
            bluetoothMonitorReceiver.unRegister(this);
        }
        if (packageReceiver != null) {
            packageReceiver.unRegister(this);
        }
        TimerUtils.getInstance().cancelTimer();
    }

    private void initTimer() {
        TimerUtils.getInstance().setOnTimerFinishInterface(new TimerUtils.OnTimerFinishInterface() {
            @Override
            public void onHomeTimerBack() {
                //todo 2-3-TW广告位逻辑-Home键按下

            }

            @Override
            public void onPhoneTimeBack() {
                //todo 2-4-TW广告位逻辑-通话接通

            }

            @Override
            public void onWifiTimerBack() {
                //todo 2-5-TW广告位逻辑-WIFI连接/断开

            }

            @Override
            public void onScreenOnTimerBack() {
                //todo 2-2-TW广告位逻辑-屏幕解锁

            }

            @Override
            public void onUsbTimerBack() {
                //todo 2-7-TW广告位逻辑-数据线插入/拔出

            }

            @Override
            public void onLowTimerBack() {
                //todo 2-8-TW广告位逻辑-低电量提示

            }

            @Override
            public void onBlueTimerBack() {
                //todo 2-9-TW广告位逻辑-蓝牙打开/关闭/连接设备

            }

            @Override
            public void onPackageTimerBack() {
                //todo 2-6-TW广告位逻辑-应用安装/卸载

            }
        });
    }
}
