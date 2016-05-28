package com.comvee.tnb.ui.tool.blood;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.comvee.tnb.model.BluetoothModel;

public class BluetoothUtil
{

	private static final String MAC1 = "00:02:5B:00:A5:A5";// 血糖/血压
	private static final String MAC5 = "20:13:11:08:43:28";// 血压
	private static final String MAC2 = "20:13:11:08:38:27";// 心电图
	// private static final String MAC2 = "20:13:11:08:43:28";// 心电图1
	// private static final String MAC2 = "20:13:11:08:42:28";// 心电图
	 private static final String MAC3 = "20:13:11:01:50:79";// 血氧仪
//	private static final String MAC3 = "20:13:11:01:52:74";// 血氧仪1
	private static final String MAC4 = "8C:DE:52:3F:E0:1A";// 体质
	// private static final String MAC4 = "8C:DE:52:3F:96:02";// 体质
	private static final String TAG = "2222";
	private BluetoothAdapter adapter;
	private BluetoothDevice remoteDevice;
	private BluetoothSocket bluetoothSocket;
	private ConnectedThread mConnectedThread;
	private boolean isStartting;// 开始

	private int mType;// 1、血糖血压2、心电图3、血氧5、实时心电图4、体脂称

	private static BluetoothUtil instance = new BluetoothUtil();
	private BluetoothModel mUserInfo;// 只有体质秤用到

	public void setUserInfo(BluetoothModel userInfo)
	{
		this.mUserInfo = userInfo;
	}

	public static BluetoothUtil getInstance()
	{
		return instance;
	}

	/**
	 * 
	 * @param type
	 *            1、血糖血压 2、心电图 3、血氧4、体脂称5、实时心电图
	 */
	public void start(int type)
	{
		if (isStartting)
		{
			return;
		}
		isStartting = true;
		mType = type;
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled())
		{
			adapter.enable();
		}
		// BluetoothDevice device =
		// adapter.getRemoteDevice("20:13:11:01:50:79");
		// BluetoothDevice device =
		// adapter.getRemoteDevice("20:13:11:08:38:27");
		String mac = null;
		switch (mType)
		{
		case 2:
		case 5:
			mac = MAC2;
			break;
		case 3:
			mac = MAC3;
			break;
		case 4:
			mac = MAC4;
			break;
		case 1:
		default:
			mac = MAC1;
			break;
		}
		BluetoothDevice device = adapter.getRemoteDevice(mac);
		if (device.getBondState() != BluetoothDevice.BOND_BONDED)
		{// 判断给定地址下的device是否已经配对
			try
			{
				ClsUtils.autoBond(device.getClass(), device, mType == 4 ? "0000" : "1234");// 设置pin值
				ClsUtils.createBond(device.getClass(), device);
				remoteDevice = device;
				Log.i("111", "配对成功");
			} catch (Exception e)
			{
				Log.i("111", "配对不成功");
			}
		} else
		{
			Log.i("111", "配对成功");
			remoteDevice = device;
		}

		// 客户端建立一个BluetoothSocket类去连接远程蓝牙设备
		try
		{
			mConnectedThread = new ConnectedThread();
			mConnectedThread.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private int mRetryCount;

	public final void  sendHeatClose(){
		if(null!=mConnectedThread)
			mConnectedThread.sendVerifyData(ByteUtils.hexStringToBytes("4F020000030001FE"));
	}

	private class ConnectedThread extends Thread
	{
		private DataInputStream mmInStream;
		private DataOutputStream mmOutStream;
		// private int mRetryCount;
		private boolean isContinu = true;

		private void init()
		{
			mRetryCount++;
			try
			{
				if (!adapter.isEnabled())
					adapter.enable();
				UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				bluetoothSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(uuid);
				// bluetoothSocket =
				// ClsUtils.createBluetoothSocket(remoteDevice);
				if (!bluetoothSocket.isConnected())
				{
					bluetoothSocket.connect();// 尝试连接
					mListener.sendConnectSuccess();
				}
			} catch (Exception e)
			{
				if (mRetryCount % 2 == 0)
				{// 没重试两次报一次错误
//					Log.e("blooth", "蓝牙连接错误");
					mListener.sendConnectFail();
				}
				if (isContinu)
				{
					try
					{
						Thread.sleep(3000);
					} catch (Exception e1)
					{
						e1.printStackTrace();
					}
					init();
					return;
				}
			}
			Log.d(TAG, "create ConnectedThread");
			DataInputStream tmpIn = null;
			DataOutputStream tmpOut = null;
			try
			{
				tmpIn = new DataInputStream(bluetoothSocket.getInputStream());
				tmpOut = new DataOutputStream(bluetoothSocket.getOutputStream());
			} catch (Exception e)
			{
//				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		/**
		 * 丰拓 必须要入校验码，此函数自动加入校验码
		 * 
		 * @param buffer
		 */
		private void sendVerifyData1(byte[] buffer)
		{
			byte verify = 0;
			for (int i = 1; i < buffer.length; i++)
			{
				verify ^= buffer[i];
			}
			byte[] bufferTemp = new byte[buffer.length + 1];
			System.arraycopy(buffer, 0, bufferTemp, 0, buffer.length);
			bufferTemp[buffer.length] = verify;
			sendData(bufferTemp);
		}

		/**
		 * 丰拓 必须要入校验码，此函数自动加入校验码
		 * 
		 * @param buffer
		 */
		private void sendVerifyData(byte[] buffer)
		{
			byte verify = 0;
			for (byte b : buffer)
			{
				verify ^= b;
			}
			byte[] bufferTemp = new byte[buffer.length + 1];
			System.arraycopy(buffer, 0, bufferTemp, 0, buffer.length);
			bufferTemp[buffer.length] = verify;
			sendData(bufferTemp);
		}

		private void sendData(byte[] buffer)
		{

			Log.i(TAG, "发送:" + printArray(buffer));
			try
			{
				mmOutStream.write(buffer);
				mmOutStream.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		private void parseDataBMI()
		{

			if (mUserInfo == null)
			{
//				Log.e("chenai", "需要基本信息（身高、性别、年龄），请调用先setUserInfo函数");
				if (null != mListener)
					mListener.sendReceiveDataFail();
				close();
				return;
			}
			if (null != mListener)
				mListener.onToDo();
			StringBuffer sb = new StringBuffer();
			sb.append("fe01").append(mUserInfo.sex == 0 ? "00" : "01").append("00")
					.append(ByteUtils.byteToHexString(ByteUtils.to4bytes(mUserInfo.height)[0]))
					.append(ByteUtils.byteToHexString(ByteUtils.to4bytes(mUserInfo.age)[0])).append("01");
			// byte[] buffer = ByteUtils.hexStringToBytes("");

			byte[] buffer = ByteUtils.hexStringToBytes(sb.toString());
			sendVerifyData1(buffer);
			ArrayList<Integer> list = new ArrayList<Integer>();
			while (isContinu)
			{
				try
				{
					int unByte = mmInStream.readUnsignedByte();
					list.add(Integer.valueOf(unByte));
				} catch (Exception e)
				{
					isContinu = false;
				}

				if (list.size() == 8 && mListener != null && list.get(0) == 253)
				{
					mListener.sendReceiveDataFail();
					isContinu = false;
//					Log.e("chenai", "需要基本信息（身高、性别、年龄），请调用先setUserInfo函数");
					close();
				} else if (list.size() >= 16 && mListener != null)
				{
					// 发送指令让仪器关机
					buffer = ByteUtils.hexStringToBytes("FD35000000000035");
					sendData(buffer);
					isContinu = false;
					BluetoothModel model = new BluetoothModel();
					model.sex = mUserInfo.sex;
					model.age = mUserInfo.age;
					model.height = list.get(3);
					model.weight = (list.get(4) * 256 + list.get(5)) / 10f;
					model.bmi = model.weight / ((model.height / 100f) * (model.height / 100f));
					model.bmi = ((int) (model.bmi * 10)) / 10f;
					model.fat = (list.get(6) * 256 + list.get(7)) / 10f;
					model.bone = list.get(8) / 10f;
					model.muscle = (list.get(9) * 256 + list.get(10)) / 10f;
					model.hasletFat = list.get(11);
					model.water = (list.get(12) * 256 + list.get(13)) / 10f;
					model.hot = list.get(14) * 256 + list.get(15);
					model.metabolism = ((int) (model.hot / model.weight * 10)) / 10f;
					mListener.sendReceiveData(model);
					close();
				}

			}
		}

		// 解析丰拓
		private void parseDataFT()
		{
			if (mType == 2)
			{
				// byte[] buffer =
				// ByteUtils.hexStringToBytes("4102FFFF03FFFFFEBE");
				// sendData(buffer);
				// byte[] buffer =
				// ByteUtils.hexStringToBytes("4F02FFFF03FFFFFE");
				byte[] buffer = ByteUtils.hexStringToBytes("4102FFFF03FFFFFE");
				sendVerifyData(buffer);
			} else
			{
				// byte[] buffer =
				// ByteUtils.hexStringToBytes("4102FFFF03FFFFFEBE");
				// sendData(buffer);
				byte[] buffer = ByteUtils.hexStringToBytes("4102FFFF03FFFFFE");
				sendVerifyData(buffer);
			}

			StringBuffer sb = new StringBuffer();
			ArrayList<Integer> localArrayList = new ArrayList<Integer>();
			ArrayList<Integer> points = new ArrayList<Integer>();

			int bufferSize = 0;
			int cur = 64;
			boolean toNext = false;
			int curIndex = 0;
			int heart = 0;
			int ho2 = 0;

			while (isContinu)
			{
				try
				{
					int unByte = 0;
					unByte = mmInStream.readUnsignedByte();
					localArrayList.add(Integer.valueOf(unByte));
					int m = localArrayList.size() - curIndex;
					System.out.println("数据==>" + Integer.valueOf(unByte));
					if ((m > 10) && (localArrayList.get(curIndex + 7) == 2 || localArrayList.get(curIndex + 7) == 1)
							&& curIndex > 9 && (m < 6 + (localArrayList.get(curIndex + 4))))
					{
						heart = localArrayList.get(curIndex + 9);
						ho2 = localArrayList.get(curIndex + 10);
						if (mType == 5)
						{
							points.add(Integer.valueOf(unByte));
						}
					} else if (Integer.valueOf(unByte) == cur)
					{
						curIndex = localArrayList.size() - 1;
						toNext = false;
						System.out.println("数据头===============================" + Integer.valueOf(unByte));
					} else
					{
						if (!toNext)
						{
							if (cur == 78)
							{
								cur = 64;
							} else
							{
								cur++;
							}
						}
						toNext = true;
					}
					if (bufferSize == 255)
					{
						if (mType == 5 && mListener != null)
						{
							mListener.onRealTimeValue(points, heart);
							bufferSize = 0;
							points.clear();
						}

					} else
					{
						bufferSize++;
					}

				} catch (Exception e)
				{
					// e.printStackTrace();

					isContinu = false;
					if (mType == 2 || mType == 3)
					{
						if (!localArrayList.isEmpty())
						{
							BluetoothModel model = new BluetoothModel();
							if (mType == 2)
							{
								model.heartRate = heart;
							} else
							{
								model.HO2 = heart;
								model.heartRate = ho2;
							}
							if (null != mListener)
								mListener.sendReceiveData(model);
						}

					}

				}
			}
		}

		// 解析艾欧乐
		private void parseDataBioland()
		{
			while (isContinu)
			{
				byte[] buffer = new byte[1];
//				Log.i(TAG, "zgq_data==================等待接收");
				StringBuffer sb = new StringBuffer();
				try
				{
					int nreadlen = mmInStream.read(buffer);
					sb.append(new String(buffer));
					while (sb.length() != 43)
					{
						mmInStream.read(buffer);
						sb.append(new String(buffer));
					}
//					Log.i(TAG, "zgq_data==================2接收:" + sb.toString());
					String rec = sb.toString();
					String strClientCode = rec.substring(4, 7);
					String strErrCode = rec.substring(7, 9);
					String machineSortCode = rec.substring(9, 11);
					String machineCode = rec.substring(11, 18);
					String value = rec.substring(20, 29);
					String time = rec.substring(29, 39);
					String validCode = rec.substring(39, 43);
					if (!value.equals("000000000") && machineSortCode.equals("02"))
					{ // 当为000000000的时候为握手包
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMddHHmm");
						java.util.Date date;
						date = sdf.parse(time);
						sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
//						Log.i(TAG, "zgq_data=客户码:" + strClientCode + " 错误码" + strErrCode + " 机种码:" + machineSortCode
//								+ " 机器码:" + machineCode + " 血糖值:" + Integer.valueOf(value) + " 时间:" + sdf.format(date)
//								+ " 验证码:" + validCode);

						int nn = Integer.valueOf(value);
						double d = (Math.round(nn * 10 * 1.0 / 18) * 1.0) / 10;
						BluetoothModel model = new BluetoothModel();
						model.blood = d;
						model.time = sdf.format(date);
						if (null != mListener)
							mListener.sendReceiveData(model);
						close();
						// mListener.onChangeValue(String.format("%0.1f",
						// Integer.valueOf(value) / 18f));
					} else if (!value.equals("000000000") && machineSortCode.equals("01"))
					{// 血压
						String hi = value.substring(0, 3);
						String lo = value.substring(3, 6);
						String heart = value.substring(6, 9);
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMddHHmm");
						java.util.Date date = sdf.parse(time);
						sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
//						Log.i(TAG, "zgq_data=客户码:" + strClientCode + " 错误码" + strErrCode + " 机种码:" + machineSortCode
//								+ " 机器码:" + machineCode + " 高压:" + Integer.parseInt(hi) + " 低压:" + Integer.parseInt(lo)
//								+ " 心率:" + Integer.parseInt(heart) + " 时间:" + sdf.format(date) + " 验证码:" + validCode);
						if (null != mListener)
						{
							BluetoothModel model = new BluetoothModel();
							model.bloodpressurediastolic = Integer.parseInt(hi);
							model.bloodpressuresystolic = Integer.parseInt(lo);
							model.heartRate = Integer.parseInt(heart);
							model.time = sdf.format(date);
							if (null != mListener)
								mListener.sendReceiveData(model);
							close();
						}
					} else
					{
//						Log.i(TAG, "zgq_data=握手包  机种码为:" + machineSortCode);
						if (null != mListener)
							mListener.sendTodo();
					}
					mmOutStream.write((strClientCode + strErrCode + validCode).getBytes());
//					Log.i(TAG, "zgq_data==================3发送:" + strClientCode + strErrCode + validCode);
				} catch (Exception e)
				{
//					Log.i(TAG, "蓝牙断开");
					isContinu = false;
				}
				buffer = null;
			}

		}

		@Override
		public void run()
		{
			isContinu = true;
			init();
			// while (isContinu)
			// {
			switch (mType)
			{
			case 3:
			case 2:
			case 5:
				parseDataFT();
				break;
			case 4:
				parseDataBMI();
				break;
			case 1:
			default:
				parseDataBioland();
				break;
			}
			// }
		}

		public void cancel()
		{
			try
			{

				isContinu = false;
				if (null != bluetoothSocket)
					bluetoothSocket.close();
				if (null != mmInStream)
					mmInStream.close();
				if (null != mmOutStream)
					mmOutStream.close();
			} catch (IOException e)
			{
//				Log.e("111", "close() of connect socket failed", e);
			}
		}
	}

	private String printArray(byte[] array)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < array.length; i++)
		{
			sb.append(ByteUtils.byteToHexString(array[i]));
			if (i < array.length - 1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	private BloodListener mListener;

	public void setListener(BloodListener listener)
	{
		mListener = listener;
	}

	public void close()
	{
		isStartting = false;
		if (mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		try
		{
			if (remoteDevice != null)
			{
				ClsUtils.removeBond(remoteDevice.getClass(), remoteDevice);
			}
			if (adapter != null && adapter.isEnabled())
			{
				adapter.cancelDiscovery();
				// adapter.disable();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
