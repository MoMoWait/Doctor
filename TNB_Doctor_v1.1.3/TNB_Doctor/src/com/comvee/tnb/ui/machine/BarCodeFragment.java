package com.comvee.tnb.ui.machine;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.MD5Util;
import com.zbar.lib.IBarCodeable;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;

/**
 * @author friendlove 二维码
 */
public class BarCodeFragment extends BaseFragment implements SurfaceHolder.Callback, IBarCodeable, OnHttpListener, View.OnClickListener {
    private static final float BEEP_VOLUME = 0.50f;
    private static final long VIBRATE_DURATION = 200L;
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    boolean flag = true;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private int FromWhere = 1;// 从那个入口进入 1设备绑定 2医生套餐 3首页左侧栏
    private TitleBarView mBarView;
    private boolean isSliding;

    public static void toFragment(FragmentActivity fragment, boolean isSliding, int fromWhere) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("fromWhere", fromWhere);
        FragmentMrg.toFragment(fragment, BarCodeFragment.class, bundle, true);
    }

    public void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    public void setFromWhere(int fromWhere) {
        FromWhere = fromWhere;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.act_qr_frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onLaunch(bundle);
      /*  if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
            FromWhere = bundle.getInt("fromWhere");
        }
        if (isSliding) {

        }*/
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
        mBarView.setTitle("二维码扫描");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void init() {
        CameraManager.init(getApplicationContext());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
        Button btn = (Button) findViewById(R.id.btn_inpu_sn);
        btn.setOnClickListener(this);
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        TextView text = (TextView) findViewById(R.id.barcode_text);
        if (FromWhere == 2) {
            text.setText("将医生二维码置于矩形框内，购买该私人医生服务!");
            btn.setText("扫描不到？手动输入医生编码");
        }

    }

    @Override
    public void onDestroyView() {
        inactivityTimer.shutdown();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    protected void light() {
        if (flag == true) {
            flag = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(Activity.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        // Toast.makeText(getApplicationContext(), result,
        // Toast.LENGTH_SHORT).show();
        if (FromWhere == 1) {
            requestAddMachine(result);
        }
        if (FromWhere == 2) {
            DoctorServerList.toFragment(getActivity(), result);
        }
        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
        // handler.sendEmptyMessage(R.id.restart_preview);
        // CameraManager.get().closeDriver();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {

        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

        } catch (Exception e) {
            showToast("使用二维码扫描功能需要开启摄像头权限！");
            FragmentMrg.toBack(getActivity());
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            try {
                initCamera(holder);

            } catch (Exception e) {
                showToast("使用二维码扫描功能需要开启摄像头权限！");
                FragmentMrg.toBack(getActivity());
                return;
            }

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    // 绑定设备
    private void requestAddMachine(String code) {
        if (TextUtils.isEmpty(code) || !code.contains("@")) {
            showToast("请扫描正确的设备二维码~");
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 2000);
            return;
        }
        showProgressDialog("正在绑定...");
        String machineID = MD5Util.getMD5String(code.split("@")[0]);
        String machineType = code.split("@")[1];
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MACHINE_ADD);
        http.setPostValueForKey("machineId", machineID);
        http.setPostValueForKey("machineType", machineType);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                showToast("绑定成功");
                UserMrg.DEFAULT_MEMBER.hasMachine = 1;
                MachineListFragment.toFragment(getActivity(), false);
                DrawerMrg.getInstance().updateLefFtagment();
            } else {
                if (null != handler)
                    handler.sendEmptyMessageDelayed(R.id.restart_preview, 2000);
                ComveeHttpErrorControl.parseError(getActivity(), packet);
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        if (null != handler)
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 2000);

        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_inpu_sn:

                toFragment(InputSNFragment.newInstance(FromWhere), false);
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onBackPress() {
        if (FromWhere == 3) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        return false;
    }
}
