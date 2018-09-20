package com.example.demo.webRtc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static String getFileMd5(File file)
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			FileChannel ch = fis.getChannel();
			MappedByteBuffer map = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			return MD5(map);
		} catch (Exception e)
		{
			return "";
		} finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}

	}

	public static String MD5(ByteBuffer buffer)
	{
		String s = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(buffer);
			byte[] digest = md.digest();
			char[] str = new char[16 * 2];
			int count = 0;
			for (int i = 0; i < 16; i++)
			{
				byte a = digest[i];
				str[count++] = hexDigits[a >>> 4 & 0xf];
				str[count++] = hexDigits[a & 0xf];

			}
			s = new String(str);
			
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return s;

	}

	public static void main(String[] args)
	{
		// e8f83a7b14fab3e8878a3f5614da6a5e
		System.out.println(getFileMd5(new File("mvnw.sdfds")));
	}

	public static String getFileMd5(byte[] b)
	{

		return MD5(ByteBuffer.wrap(b));
	}

}
