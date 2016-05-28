package com.comvee.tnb.widget.Voice;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;

public class MediaPlayerUtils {

	private MediaPlayer mediaPlayer = null;
	private static Context context;
	private static MediaPlayerUtils instance = null;
	private String tempVoiceUrl = "";// 标记语音播放url。默认空
	private VoiceInfo mCurVoiceInfo = null;// 当前播放的语音对象
	private boolean playState = false; // 播放状态

	private IOperationImg iOperationImg;

	private MediaPlayerUtils() {
	}

	public static MediaPlayerUtils getInstance(Context context) {
		if (instance == null) {
			MediaPlayerUtils.context = context;
			instance = new MediaPlayerUtils();
			// TelephonyManager telephonyManager = (TelephonyManager) context
			// .getSystemService(Context.TELEPHONY_SERVICE);
			// telephonyManager.listen(new MyPhoneListener(),
			// PhoneStateListener.LISTEN_CALL_STATE);
		}
		return instance;
	}

	public void playSDCardVoice(String voicePath, VoiceInfo voiceInfo, final Handler mHandler, IOperationImg iOperationImg) {
		this.iOperationImg = iOperationImg;
		playSDCardVoice(voicePath, voiceInfo, mHandler);
	}

	/**
	 * 播放sdcard音频
	 * 
	 * @param voicePath
	 * @param voiceInfo
	 */
	public void playSDCardVoice(String voicePath, VoiceInfo voiceInfo, final Handler mHandler) {
		boolean isSame = false;
		if (mCurVoiceInfo != voiceInfo) {
			if (mCurVoiceInfo != null) {
				mCurVoiceInfo.setPlaying(false);
			}
			mCurVoiceInfo = voiceInfo;
			isSame = false;
		} else {
			isSame = true;
		}
		if (!playState || !isSame) {
			try {
				if (mediaPlayer != null) {
					stop(mCurVoiceInfo, mHandler);
				}

				mediaPlayer = new MediaPlayer();
				// mediaPlayer.reset();
				// 模拟器里播放传url，真机播放传getAmrPath()
				mediaPlayer.setDataSource(voicePath);
				// 循环播放
				mediaPlayer.setLooping(false);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mediaPlayer.start();
					}
				});
				playState = true;
				mCurVoiceInfo.setPlaying(true);// 动起来
				mHandler.sendEmptyMessage(ParamsConfig.VOICE);
				OperationView(mCurVoiceInfo);
				// 设置播放结束时监听
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						playState = false;
						mediaPlayer.release();
						mediaPlayer = null;
						mCurVoiceInfo.setPlaying(false);
						OperationView(mCurVoiceInfo);
						mHandler.sendEmptyMessage(ParamsConfig.VOICE);
					}
				});
				mediaPlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						// TODO Auto-generated method stub
						stop(mCurVoiceInfo, mHandler);
						return true;
					}
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			stop(voiceInfo, mHandler);
		}
	}

	public void playNetVoice(String voiceUrl, VoiceInfo voiceInfo, final Handler mHandler, IOperationImg iOperationImg) {
		// 播放前先暂停之前的音频 只能播放一个
		stop(mCurVoiceInfo, new Handler());
		this.iOperationImg = iOperationImg;
		playNetVoice(voiceUrl, voiceInfo, mHandler);
	}

	/**
	 * 播放网络音频
	 * 
	 * @param voiceUrl
	 * @param voiceInfo
	 */
	public void playNetVoice(String voiceUrl, VoiceInfo voiceInfo, final Handler mHandler) {
		boolean isSame = false;
		if (mCurVoiceInfo != voiceInfo) {
			if (mCurVoiceInfo != null) {
				mCurVoiceInfo.setPlaying(false);
			}
			mCurVoiceInfo = voiceInfo;
			isSame = false;
		} else {
			isSame = true;
		}
		if (!tempVoiceUrl.equals(voiceUrl) || !isSame) {
			try {
				if (mediaPlayer != null) {
					stop(mCurVoiceInfo, mHandler);
				}

				Uri uri = Uri.parse(voiceUrl);
				mediaPlayer = new MediaPlayer();
				// mediaPlayer.reset();
				mediaPlayer.setDataSource(context, uri);
				// 循环播放
				mediaPlayer.setLooping(false);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mediaPlayer.start();
					}
				});
				// 先刷新界面
				tempVoiceUrl = voiceUrl;
				mCurVoiceInfo.setPlaying(true);// 动起来
				OperationView(mCurVoiceInfo);
				mHandler.sendEmptyMessage(ParamsConfig.VOICE);
				// 设置播放结束时监听
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mediaPlayer.release();
						mediaPlayer = null;
						mCurVoiceInfo.setPlaying(false);
						tempVoiceUrl = "";
						OperationView(mCurVoiceInfo);
						mHandler.sendEmptyMessage(ParamsConfig.VOICE);
					}
				});
				mediaPlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						// TODO Auto-generated method stub
						stop(mCurVoiceInfo, mHandler);
						return true;
					}
				});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			stop(mCurVoiceInfo, mHandler);
		}
	}

	public void stopPlay() {

	}

	private void stop(VoiceInfo voiceInfo, Handler mHandler) {
		try {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				if (voiceInfo != null) {
					voiceInfo.setPlaying(false);
				}
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} else {
				if (voiceInfo != null) {
					voiceInfo.setPlaying(false);
				}
				mediaPlayer.release();
				mediaPlayer = null;
			}
			tempVoiceUrl = "";
			playState = false;
			OperationView(voiceInfo);
			mHandler.sendEmptyMessage(ParamsConfig.VOICE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void releasePlayer() {
		tempVoiceUrl = "";
		playState = false;
		if (mCurVoiceInfo != null) {
			mCurVoiceInfo.setPlaying(false);
			mCurVoiceInfo = null;
		}
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
		context = null;
		instance = null;
	}

	public void pause() {
		try {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void release() {
		try {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.release();
				mediaPlayer = null;
			} else {
				mediaPlayer = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 只有电话来了之后才暂停音乐的播放
	 */
	static class MyPhoneListener extends android.telephony.PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话来了
				if (instance != null) {
					instance.callIsComing();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 通话结束
				if (instance != null) {
					instance.callIsDown();
				}
				break;
			}
		}
	}

	/**
	 * 来电话了
	 */
	public void callIsComing() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 通话结束
	 */
	public void callIsDown() {

	}

	private void OperationView(VoiceInfo info) {
		System.out.println("OperationView");
		if (iOperationImg != null) {
			if (info.isPlaying()) {
				System.out.println("play");
				iOperationImg.play();
			} else {
				System.out.println("stop");
				iOperationImg.stop();
			}
		}
	}

}
