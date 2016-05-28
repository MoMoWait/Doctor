package com.comvee.tnb.widget.Voice;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

public class AudioUtil {
	private final static String TAG = "AudioUtil";

	private String mAudioPath;
	private boolean mIsRecording;
	private boolean mIsPlaying;
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;

	// 设置保存路径
	public void setAudioPath(String path) {
		this.mAudioPath = path;
	}

	// 开始录音，并保存到文件中
	public void recordAudio() {
		if (mAudioPath != null && !mAudioPath.equals("")) {
			initRecorder();
			try {
				mRecorder.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mRecorder.start();
		}

		else {
			Log.d(TAG, "AudioUtil 启动录音失败！");
		}

	}

	// 获取音量值，只是针对录音音量
	public int getVolumn() {
		int volumn = 0;

		// 录音
		if (mRecorder != null && mIsRecording) {
			volumn = mRecorder.getMaxAmplitude();
			if (volumn != 0)
				volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 7;
		}

		return volumn;
	}

	// 初始化 录音器
	private void initRecorder() {

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(mAudioPath);
		mIsRecording = true;

	}

	// 停止录音
	public void stopRecord() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
				mIsRecording = false;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mRecorder = null;
				mIsRecording = false;
			}
		}
	}

	// 开始播放
	public void startPlay() {
		if (TextUtils.isEmpty(mAudioPath)) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(mAudioPath);
				mPlayer.prepare();
				mPlayer.start();
				mIsPlaying = true;
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
						mPlayer = null;
						mIsPlaying = false;
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "AudioUtil 播放录音音频失败！");
		}
	}

	public boolean isPlaying() {
		return mIsPlaying;
	}

}
