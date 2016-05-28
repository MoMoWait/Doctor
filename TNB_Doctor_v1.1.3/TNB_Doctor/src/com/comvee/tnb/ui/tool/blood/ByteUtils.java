package com.comvee.tnb.ui.tool.blood;

public class ByteUtils
{

	public static String bytesToHexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static String byteToHexString(byte src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		int v = src & 0xFF;
		String hv = Integer.toHexString(v);
		if (hv.length() < 2)
		{
			stringBuilder.append(0);
		}
		stringBuilder.append(hv);
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static void main(String args)
	{
		byte[] b =
		{ 106, 90 };
	}

	public static int[] byteTo2Int(byte paramByte)
	{
		int[] arrayOfInt =
		{ -1, -1 };
		arrayOfInt[0] = ((paramByte & 0xFF) >> 4);
		arrayOfInt[1] = (paramByte & 0xF);
		return arrayOfInt;
	}

	public static byte[] to2bytes(int paramInt)
	{
		byte[] arrayOfByte = new byte[2];
		arrayOfByte[0] = (byte) (paramInt & 0xFF);
		arrayOfByte[1] = (byte) ((0xFF00 & paramInt) >> 8);
		return arrayOfByte;
	}

	public static byte[] to4bytes(int paramInt)
	{
		byte[] arrayOfByte = new byte[4];
		arrayOfByte[0] = (byte) (paramInt & 0xFF);
		arrayOfByte[1] = (byte) ((0xFF00 & paramInt) >> 8);
		arrayOfByte[2] = (byte) ((0xFF0000 & paramInt) >> 16);
		arrayOfByte[3] = (byte) ((0xFF000000 & paramInt) >> 24);
		return arrayOfByte;
	}

	public static byte[] to8Bytes(long paramLong)
	{
		long l = paramLong;
		byte[] arrayOfByte = new byte[8];
		for (int i = 0;; i++)
		{
			if (i >= arrayOfByte.length)
				return arrayOfByte;
			arrayOfByte[i] = new Long(0xFF & l).byteValue();
			l >>= 8;
		}
	}

	public static int toInt(byte[] paramArrayOfByte)
	{
		int i = 0;
		int j = paramArrayOfByte.length;
		for (int k = 0;; k++)
		{
			if (k >= j)
				return i;
			i += ((0xFF & paramArrayOfByte[k]) << k * 8);
		}
	}

	public static long toLong(byte[] paramArrayOfByte)
	{
		long l1 = 0xFF & paramArrayOfByte[0];
		long l2 = 0xFF & paramArrayOfByte[1];
		long l3 = 0xFF & paramArrayOfByte[2];
		long l4 = 0xFF & paramArrayOfByte[3];
		long l5 = 0xFF & paramArrayOfByte[4];
		long l6 = 0xFF & paramArrayOfByte[5];
		long l7 = 0xFF & paramArrayOfByte[6];
		long l8 = 0xFF & paramArrayOfByte[7];
		long l9 = l2 << 8;
		long l10 = l3 << 16;
		long l11 = l4 << 24;
		long l12 = l5 << 32;
		long l13 = l6 << 40;
		long l14 = l7 << 48;
		return l8 << 56 | (l14 | (l13 | (l12 | (l11 | (l10 | (l1 | l9))))));
	}
}
