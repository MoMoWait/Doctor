package com.comvee.tnb.ui.tool.blood;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClsUtils
{

	/**
	 * latform/packages/apps/Settings.git
	 * \Settings\src\com\android\settings\bluetooth\CachedBluetoothDevice.java
	 */
	@SuppressWarnings("unchecked")
	static public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception
	{
		printAllInform(btClass);
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * latform/packages/apps/Settings.git
	 * \Settings\src\com\android\settings\bluetooth\CachedBluetoothDevice.java
	 */
	@SuppressWarnings("unchecked")
	static public boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception
	{
		printAllInform(btClass);
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	@SuppressWarnings("unchecked")
	static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception
	{
		Method setPinMethod = btClass.getMethod("setPin", new Class[]
		{ byte[].class });
		@SuppressWarnings("unused")
		Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice, new Object[]
		{ str.getBytes() });
		return true;
	}

	/**
	 * 
	 * @param clsShow
	 */
	@SuppressWarnings("unchecked")
	static public void printAllInform(Class clsShow)
	{
		try
		{
			// get all the methods
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++)
			{
//				Log.e("method name", hideMethod[i].getName());
			}
			// get all the const
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++)
			{
//				Log.e("Field name", allFields[i].getName());
			}
		} catch (SecurityException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 自动配对设置Pin值
	static public boolean autoBond(Class btClass, BluetoothDevice device, String strPin) throws Exception
	{
		Method autoBondMethod = btClass.getMethod("setPin", new Class[]
		{ byte[].class });
		Boolean result = (Boolean) autoBondMethod.invoke(device, new Object[]
		{ strPin.getBytes() });
		return result;
	}

	static public BluetoothSocket createBluetoothSocket(BluetoothDevice myDevice) throws Exception
	{
		Method m = myDevice.getClass().getMethod("createRfcommSocket", new Class[]
		{ int.class });
		return (BluetoothSocket) m.invoke(myDevice, 1);
	}

	// //开始配对
	// static public boolean createBond(Class btClass,BluetoothDevice device)
	// throws Exception {
	// Method createBondMethod = btClass.getMethod("createBond");
	// Boolean returnValue = (Boolean) createBondMethod.invoke(device);
	// return returnValue.booleanValue();
	// }

}
