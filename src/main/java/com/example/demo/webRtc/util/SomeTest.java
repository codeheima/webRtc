package com.example.demo.webRtc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.example.demo.webRtc.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sun.misc.Unsafe;

public class SomeTest
{

	// private final static ExecutorService exe = Executors.newFixedThreadPool(800);

	private final static File timefile = new File("time.txt");

	private final static Gson gson = new GsonBuilder().create();

	public static <K, V> String request(String path, Map<K, V> map)
			throws Exception
	{
		HttpURLConnection conn = null;
		try
		{
			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection)
			{
				HttpsURLConnection httpsConn = (HttpsURLConnection) conn;

				// 信用所有
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
				{
					public X509Certificate[] getAcceptedIssuers()
					{
						return new X509Certificate[] {};
					}

					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException
					{
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException
					{
					}
				} };
				SSLContext sc = SSLContext.getInstance("SSL");// TLS
				sc.init(null, trustAllCerts, new SecureRandom());
				SSLSocketFactory newFactory = sc.getSocketFactory();
				httpsConn.setSSLSocketFactory(newFactory);
				// 不验证主机
				httpsConn.setHostnameVerifier(new HostnameVerifier()
				{
					public boolean verify(String hostname, SSLSession session)
					{
						return true;
					}
				});
			}
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setRequestProperty("Oc_Chatserver_MinVersion", "3.0.0");
			conn.setRequestProperty("Oc_Platform_AppVersion", "2.8.1");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.getOutputStream().write(deal(map).getBytes("UTF-8"));
			String rspStr = IOUtils.toString(conn.getInputStream(), "UTF-8");
			return rspStr;
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (conn != null)
			{
				conn.disconnect();
				conn = null;
			}
		}
	}

	private static <K, V> String deal(Map<K, V> map)
	{
		StringBuilder sb = new StringBuilder();

		map.forEach((K k, V v) ->
		{
			// if(!StringUtils.isEmpty((String)v)) {
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append("&");
			// }

		});
		return sb.toString().endsWith("&") ? sb.toString().substring(0, sb.toString().length() - 1) : sb.toString();
	}
	/**
	 * 多文件上传的方法
	 * 
	 * @param actionUrl：上传的路径
	 * @param uploadFilePaths：需要上传的文件路径，数组
	 * @return
	 */
	@SuppressWarnings("finally") // 1534815632000
	public static String uploadFile(String actionUrl, Map<String, Object> map, String... uploadFilePaths)
	{
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		DataOutputStream ds = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		try
		{
			// 统一资源
			URL url = new URL(actionUrl);
			// 连接类的父类，抽象类
			URLConnection urlConnection = url.openConnection();
			// http的连接类
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpURLConnection.setDoInput(true);
			// 设置是否向httpUrlConnection输出
			httpURLConnection.setDoOutput(true);
			// Post 请求不能使用缓存
			httpURLConnection.setUseCaches(false);
			// 设定请求的方法，默认是GET
			httpURLConnection.setRequestMethod("POST");
			// 设置字符编码连接参数
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			// 设置字符编码
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			// 设置请求内容类型
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			httpURLConnection.setRequestProperty("Oc_Chatserver_MinVersion", "3.0.0");
			httpURLConnection.setRequestProperty("Oc_Platform_AppVersion", "2.8.1");
			// 设置DataOutputStream
			ds = new DataOutputStream(httpURLConnection.getOutputStream());
			ds.write(getStrParams(map).getBytes("UTF-8"));
			for (int i = 0; i < uploadFilePaths.length; i++)
			{
				String uploadFile = uploadFilePaths[i];
				String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename
						+ "\"" + end);
				ds.writeBytes(end);
				FileInputStream fStream = new FileInputStream(uploadFile);
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1)
				{
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				/* close streams */
				fStream.close();
			}
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			ds.flush();
			if (httpURLConnection.getResponseCode() >= 300)
			{
				throw new Exception(
						"HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
			}

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				inputStream = httpURLConnection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);
				tempLine = null;
				resultBuffer = new StringBuffer();
				while ((tempLine = reader.readLine()) != null)
				{
					resultBuffer.append(tempLine);
					resultBuffer.append("\n");
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (ds != null)
			{
				try
				{
					ds.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null)
			{
				try
				{
					inputStreamReader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			return resultBuffer.toString();
		}
	}

	private static String getStrParams(Map<String, Object> strParams)
	{
		StringBuilder strSb = new StringBuilder();
		for (Map.Entry<String, Object> entry : strParams.entrySet())
		{
			strSb.append("--").append("*****").append("\r\n")
					.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + "\r\n")
					.append("Content-Type: text/plain; charset=utf-8" + "\r\n").append("\r\n")// 参数头设置完以后需要两个换行，然后才是参数内容
					.append(entry.getValue()).append("\r\n");
		}
		return strSb.toString();
	}

	@SuppressWarnings("restriction")
	public static sun.misc.Unsafe getUnsafe()
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException
	{
		Class<?> uClass = Class.forName("sun.misc.Unsafe");
		for (Field f : uClass.getDeclaredFields())
		{
			if ("theUnsafe".equals(f.getName()))
			{
				f.setAccessible(true);
				return (sun.misc.Unsafe) f.get(sun.misc.Unsafe.class);

			}
		}
		return null;
	}
	@Test
	public void UnSafeTest() throws Throwable {
		Unsafe unsafe = getUnsafe();
		Field name = User.class.getDeclaredField("name");
		Field nickName = User.class.getDeclaredField("nickName");
		Field password = User.class.getDeclaredField("password");
		User user = (User) unsafe.allocateInstance(User.class);
		unsafe.putObject(user, unsafe.objectFieldOffset(name), "sdflkds");
		unsafe.putObject(user, unsafe.objectFieldOffset(nickName), "大傻子");
		unsafe.putObject(user, unsafe.objectFieldOffset(password), "qwertyu");
		System.out.println(user);
	}

}
