package com.comvee.tnb.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ComveePacket extends JSONObject
{

	public static ComveePacket fromJsonString(String str) throws Exception
	{
		return new ComveePacket(str);
	}

	public static ComveePacket fromJsonString(byte[] b) throws Exception
	{
		String str = new String(b, "utf-8");
		return fromJsonString(str);
	}

	public static ComveePacket froXmlString(String str)
	{
		return null;
	}

	private ComveePacket(String json) throws Exception
	{
		super(json);
		if (ComveeHttp.DEBUG)
		{
			Log.v("comvee_http", json);
		}
	}

	public String getResultMsg()
	{
		try
		{
			return this.getJSONObject("res_msg").optString("res_desc");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 成功：0 0000 成功 错误：1 1001 请求报文基本参数异常 1002 SESSION无效 错误：2 2xxx 业务错误
	 * 
	 * @return
	 */
	public int getResultCode()
	{
		try
		{
			return this.getJSONObject("res_msg").optInt("res_code", -1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}
	
	public int getPageTotalCount()
	{
		try
		{
			return this.getJSONObject("body").getJSONObject("pager").optInt("totalRows", -1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public int getPageCurrentIndex()
	{
		try
		{
			return this.getJSONObject("body").getJSONObject("pager").optInt("currentPage", -1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
