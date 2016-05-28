package com.comvee.tnb.widget.Voice;

import java.io.Serializable;

public class VoiceInfo implements Serializable
{
	/**
	 * 
	 */
	
	private String attUrl;
	private String isPrivate;
	private String voiceLength;
	private boolean isPlaying;// 是否正在播放。用于控制PlayImageView状态

	public String getAttUrl()
	{
		return attUrl;
	}

	public void setAttUrl(String attUrl)
	{
		this.attUrl = attUrl;
	}

	public String getIsPrivate()
	{
		return isPrivate;
	}

	public void setIsPrivate(String isPrivate)
	{
		this.isPrivate = isPrivate;
	}

	public String getVoiceLength()
	{
		return voiceLength;
	}

	public void setVoiceLength(String voiceLength)
	{
		this.voiceLength = voiceLength;
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}

}
