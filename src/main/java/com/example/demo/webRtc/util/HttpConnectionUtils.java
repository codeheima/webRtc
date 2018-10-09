package com.example.demo.webRtc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
			//if(!StringUtils.isEmpty((String)v)) {
				sb.append(k);
				sb.append("=");
				sb.append(v);
				sb.append("&");
			//}
				
		});
		return sb.toString().endsWith("&")? sb.toString().substring(0,sb.toString().length()-1):sb.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("ocBinaryFile", new File("ll.gif"));
		map.put("type", "image");
		map.put("subType", "gif");
		//map.put("page_size", "20");
		//map.put("token", "8001067e69944636a0e5ae7be50ed25ac9f75ae5");
		//map.put("thirdparty_id", "3");
		//map.put("data", "h/nT8mXYKT5jpiu0QHWLaeVRIjY4zceFRISdbC0x7hq+OXqQLkf1r2zQc3RAOcef");
		//map.put("im_room_name", "7d38f92f4");
//		Map<String, String> map1 = new LinkedHashMap<>();
//		map1.put("account", "13679487149");
//		map1.put("password", "123456");
		//192.168.105.27:7100;172.28.98.50:7100
		String countPath = "http://192.168.1.157:7110/outer/registrar/chat/stat/list";
		String path = "http://192.168.1.157:7110/chat/file/deal";
		String path2 = "http://chat.cloudak47.com:7100/business/user/getuser";
		String path3 = "http://chat.cloudak47.com:7100/business/user/chat/room/list";
		String path4 = "http://chat.cloudak47.com:7100/business/user/thirdparty/encrypt/show";
//		System.out.println(request(path, map));
//		String path1 = "http://172.28.98.50:7100/business/user/relate1";
		System.out.println(uploadFile(path, "ll.gif"));
	}
	
	 /**
     * 多文件上传的方法
     * 
     * @param actionUrl：上传的路径
     * @param uploadFilePaths：需要上传的文件路径，数组
     * @return
     */
    public static String uploadFile(String actionUrl, String... uploadFilePaths) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        DataOutputStream ds = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
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

            // 设置DataOutputStream
            ds = new DataOutputStream(httpURLConnection.getOutputStream());
            for (int i = 0; i < uploadFilePaths.length; i++) {
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
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            ds.flush();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                tempLine = null;
                resultBuffer = new StringBuffer();
                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return resultBuffer.toString();
        }
    }


}
