package com.comvee.tool;

import android.content.Context;

public class SpeechUtil
{

	private static SpeechUtil instance;
//	private SpeechSynthesizer mSpeechSynthesizer;

	public static SpeechUtil getInstance(Context cxt)
	{
		return instance = instance != null ? instance : new SpeechUtil(cxt);
	}

	private SpeechUtil(Context cxt)
	{
//		SpeechUser.getUser().login(cxt, null, null, "appid=" + "514821bd", null);
//		// 创建 SpeechSynthesizer 对象，使用前要先登录。详细内容请参考 sample 代码
//		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(cxt);
//		// 设置发音人
//		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "vixq");
//		// 设置语速，范围 0~100
//		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "60");
//		// 设置音量，范围 0~100
//		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");
//		// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
//		// 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//		// 如果不需要保存合成音频，注释该行代码
//		mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()
//				.getAbsoluteFile() + "/iflytek1.pcm");
	}

	public final void speech(String str)
	{
		// 开始合成
//		mSpeechSynthesizer.startSpeaking(str, null);
	}

}
