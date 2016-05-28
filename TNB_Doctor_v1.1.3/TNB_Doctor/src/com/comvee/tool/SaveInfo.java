package com.comvee.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveInfo
{
	private static final String MYLYSETTING_PREFERENCES = "daily.info";


	//保存我的工具上的身高和体重
	public static void setHeight(Context context, int h)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("height", h);
		editor.commit();
	}

	//
	public static int getHeight(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getInt("height", 170);
	}

	//
	public static int getWeight(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getInt("weight", 60);
	}

	//
	public static void setWeight(Context context, int w)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("weight", w);
		editor.commit();
	}
	
	//
	public static int getType(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		return sp.getInt("type", -1);
	}

	//
	public static void setType(Context context, int type)
	{
		SharedPreferences sp = context.getSharedPreferences(
				MYLYSETTING_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("type", type);
		editor.commit();
	}
	
	

	

}
