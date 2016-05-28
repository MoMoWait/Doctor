/**
 * 文件名  VolumeViewer
 * 包含类   VolumeViewer
 * 文件描述   音量指示器
 * 创建日期  2013-5-13
 * 版本信息 V1.0  
 * 作者    王秀芝
 * 版权声明 Copyright (c) 2007—2013 Wootion.Co.Ltd. All rights reserved.
 */
package com.comvee.tnb.widget.Voice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
//import android.util.Log;

/**
 * 
 * 类名 VolumeViewer
 * 描述 音量指示器
 * 作者  toney
 * 创建日期 2013-10-13
 * 修改记录：
 *  日期          版本      修改者      描述
 *
 */
public class VolumeViewer extends View
{

//	private final static String TAG = "VolumeViewer";
	private Paint mPaint;
	private int mVolumeValue = 0;
	private boolean mIsFresh = true;

	public VolumeViewer(Context context) 
	{
		super(context);
		init(context);
	}
	
	public VolumeViewer(Context context, AttributeSet attrs) 
	{
		super(context,attrs);
		init(context);
	}

	//初始化
	private void init(Context context) 
	{             
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);     
        mVolumeValue = 0;        
    }
	
	/**
	 * 
	 * 设置音量大小，并更新UI
	 * <p>设置音量大小，并更新UI</p>
	 * @param volume 音量大小
	 * @throws
	 */
	public void setVolumeValue(int volume)
	{
//		Log.d("VolumeViewer", "volume is "+volume);
		this.mVolumeValue = volume;
		if(!mIsFresh)  mIsFresh = true;
		
    }
	/**
	 * 
	 * 停止更新图画
	 * <p>停止更新图画</p>
	 * @throws
	 */
	public void stopFresh()
	{
		mIsFresh = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	
		//根据应显示的格数画指示器
		final int height = getHeight();
//		Log.d(TAG, "height:"+height);
		for (int i = 1; i <= mVolumeValue; i++)
		{
			int top = height - i * 20;//矩形间的距离 20-12
			canvas.drawRect(0, top, 10+i*5, top + 12, mPaint);//矩形宽等差增加
		}
		
		if(mIsFresh) 
		{
			postInvalidateDelayed(10);
		}
		
	}	
}
