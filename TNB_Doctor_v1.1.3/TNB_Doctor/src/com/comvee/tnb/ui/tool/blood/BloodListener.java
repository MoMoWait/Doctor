package com.comvee.tnb.ui.tool.blood;



import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.comvee.tnb.model.BluetoothModel;

public abstract class BloodListener extends Handler
{
	public abstract void onReceiveData(BluetoothModel value);

	// 获取数据失败
	public void onReceiveDataFail()
	{

	}

	public void onConnectSuccess()
	{
	};

	public void onConnectFail()
	{
	};

	// 血糖专用//请滴血     电子称数据上传中
	public void onToDo()
	{

	}

	public void sendReceiveDataFail()
	{
		sendEmptyMessage(6);
	}

	public void sendTodo()
	{
		sendEmptyMessage(4);
	}

	public void onRealTimeValue(ArrayList<Integer> list, int heartRate)
	{

	}

	// 实时发送数据
	public void sendRealTimeValue(ArrayList<Integer> list, int heartRate)
	{
		sendMessage(obtainMessage(5, heartRate, 5, list));
	}

	public void sendConnectSuccess()
	{
		sendEmptyMessage(2);
	}

	public void sendConnectFail()
	{
		sendEmptyMessage(3);
	}

	public void sendReceiveData(BluetoothModel value)
	{
		sendMessage(obtainMessage(1, 1, 1, value));
	}

	@Override
	public void handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case 1:
			onReceiveData((BluetoothModel) msg.obj);
			break;
		case 2:
			onConnectSuccess();
			break;
		case 3:
			onConnectFail();
			break;
		case 4:
			onToDo();
			break;
		case 5:
			onRealTimeValue((ArrayList<Integer>) msg.obj, msg.arg1);
			break;
		case 6:
			onReceiveDataFail();
			break;
		default:
			break;
		}
		super.handleMessage(msg);
	}
}
