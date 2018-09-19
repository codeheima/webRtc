package com.example.demo.webRtc.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;


public class HttpConnectionUtils
{
	public static <K,V> String request(String path, Map<K, V> map)
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
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
				conn = null;
			}
		}
	}

	private static <K,V> String deal(Map<K, V> map)
	{
		StringBuilder sb = new StringBuilder();
		
		map.forEach((K k,V v)->{
			if(!StringUtils.isEmpty((String)v)) {
				sb.append(k);
				sb.append("=");
				sb.append(v);
				sb.append("&");
			}
				
		});
		return sb.toString().endsWith("&")? sb.toString().substring(0,sb.toString().length()-1):sb.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		Map<String, String> map = new LinkedHashMap<>();
		map.put("mobile", "13679487149");
		map.put("code", "123456");
		Map<String, String> map1 = new LinkedHashMap<>();
		map1.put("account", "qwerty");
		map1.put("password", "123456");
		String path = "http://192.168.1.157:7110/business/user/relate";
		//System.out.println(request(path, map));
		String path1 = "http://192.168.1.157:7110/business/user/relate1";
		System.out.println(request(path1, map1));
	}

}
