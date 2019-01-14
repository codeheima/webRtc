package com.example.demo.webRtc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpConnectionUtils
{

	// private final static ExecutorService exe = Executors.newFixedThreadPool(800);

	private final static File timefile = new File("time.txt");

	public final static int gson = 10;

	public static <K, V> String request(String path, Map<K, V> map) throws Exception
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

					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
					{
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
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

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception
	{
		FileWriter fw = new FileWriter(timefile, true);
		Map<String, Object> map = new LinkedHashMap<>();
		// map.put("xiaohuifrom", "qwer");
		// map.put("xiaohuiIds", "qwerty");
		// map.put("roomName", "222806b8663549e2b17c4225d3e2b");
		// map.put("chatserver_id", "2");
		// map.put("company_name", "亦云小慧");
		// map.put("im_user_name", "277bd2b6d6784");// map.put("message",
		// "asfdsgfsdgf"); //map.put("meeting_id", "108");
		// map.put("type", "file");
		// map.put("im_user_name", "88d149");
		map.put("token", "4fff3f13120f44b093487a6ee84ca7824f94b3f5");
		map.put("catalog", "1");
		map.put("file_id", "19");
		map.put("record_time", System.currentTimeMillis());
		// map.put("type", "3");
		map.put("comment", "测试");
		map.put("file_content", "这是一个忧伤sdf");
		// map.put("duration_sort", "desc");
		// map.put("record_time_sort", "asc");
		// map.put("token", "a55b9147202947b8bf916a79cca0d50457ba935c");
		// map.put("im_user_name", "aa96d0a8927a4b");
		// map.put("chatserver_id", "1");// 13603bdecce747c98f0f36e29c231df05a362fa9
		// map.put("thirdparty_id", "3");
		// map.put("data",
		// "h/nT8mXYKT5jpiu0QHWLaeVRIjY4zceFRISdbC0x7hq+OXqQLkf1r2zQc3RAOcef");
		// map.put("im_room_names",
		// "78464567ca774ee89807f,51e8da");//,173791b6e14540b9aff0,f3d5845c51054cdaba1eed4,ba60f5e3737042a,0ac27ed8678c4fe38bcc236a38a3bf0,1c77828113e24f6f81aefc,78464567ca774ee89807f,ba8ea6f1f4d7490889c5598435cfea
		// Map<String, String> map1 = new LinkedHashMap<>();
		// map.put("account", "13679487149");
		// map.put("password", "123456");
		// map.put("platform_type", "4");
		// 192.168.105.27:7100;172.28.98.50:7100
		// String path = "http://192.168.1.106:7110/mock/thirdparty/chat/room/do_send";
		// String path = "https://192.168.1.106:7143/business/user/get";
		// String path = "http://192.168.1.106:8110/hottub/user/group/list";
		// String path = "http://appcc.onecloud.cn/hottub/push/send";
		// String path = "http://chatoc.cloudak47.com:7101/business/user/login1";
		String path = "http://192.168.1.113:7110/business/user/space/file/deal";
		System.out.println(uploadFile(path, map, "horse.ogg"));
		// String path = "https://chatoc.onecloud.cn:7100/business/user/login1";
		// String path = "https://chatoc.onecloud.cn:7100/business/user/chat/room/list";
		// String path4 =
		// "http://chat.cloudak47.com:7100/business/user/thirdparty/encrypt/show";
		// System.out.println(request(path, map));
		// String path1 = "http://172.28.98.50:7100/business/user/relate1";
		// while(true) {
		// Future<String> submit = exe.submit(new Callable<String>()
		// {
		//
		// @Override
		// public String call()
		// throws Exception
		// {
		//
		// return request(path, map);
		// }
		// });
		// exe.execute(()->{
		// try
		// {
		// System.out.println(String.format("thread:%dstart\n",
		// Thread.currentThread().getId()));
		// map.put("content", UUID.randomUUID());
		// while(true) {
		// long start = System.currentTimeMillis();

		// String response = request(path, map);
		// String response = uploadFile(path, map ,"horse.ogg");
		// long total = System.currentTimeMillis()-start;
		// JsonObject json = gson.fromJson(response, JsonObject.class);
		// Long openfire_time = json.get("openfireTime").getAsLong();
		// Long chatserver_total = json.get("chatserverTime").getAsLong();
		// Long chatserver_time = chatserver_total-openfire_time;
		// Long clientIoTime = total-chatserver_total;
		// fw.write(openfire_time+"\t"+chatserver_time+"\t"+chatserver_total+"\t"+clientIoTime+"\t"+total+"\n");
		// fw.flush();
		// System.out.println(response);
		// System.out.println(total);
		// long objectFieldOffset =
		// UNSAFE.objectFieldOffset(HttpConnectionUtils.class.getDeclaredField("gson"));
		// Thread.sleep(30000);
		// }
		// System.out.println(objectFieldOffset);
		// java.lang.Thread.sleep(500);
		// } catch (Exception e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// });String
		// }

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
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename + "\"" + end);
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
				throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
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
			strSb.append("--").append("*****").append("\r\n").append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + "\r\n")
					.append("Content-Type: text/plain; charset=utf-8" + "\r\n").append("\r\n")// 参数头设置完以后需要两个换行，然后才是参数内容
					.append(entry.getValue()).append("\r\n");
		}
		return strSb.toString();
	}

	@SuppressWarnings("restriction")
	public static sun.misc.Unsafe getUnsafe() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException
	{
		Class<?> uClass = Class.forName("sun.misc.Unsafe");
		for (Field f : uClass.getDeclaredFields())
		{
			if ("theUnsafe".equals(f.getName()))
			{
				f.setAccessible(true);
				return (sun.misc.Unsafe) f.get(null);

			}
		}
		return null;
	}

	@Test
	public void deleteSpaceFile() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("token", "a57bd794dfa4420297bd7333c5f9001362229dab");
		param.put("file_id", 32);
		System.out.println(request("http://192.168.1.113:7110/business/user/space/file/delete", param));
	}

	@Test
	public void getChatServerList() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		System.out.println(request("http://appcc.pispower.com/hottub/chatserver/get?company_name=%E4%BA%A6%E4%BA%91%E4%BF%A1%E6%81%AF", param));
	}

	@Test
	public void getBranchList() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("token", "d6428a5e24c14cc186c61feac634d2d344a557a4");
		System.out.print(request("https://chatoc.cloudak47.com:17101/business/user/branch/list", param));
	}

	@Test
	public void getSpaceFileList() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("token", "4087b0d9f0814dd1a31cc2a9db6d9c68f00fb18d");
		param.put("catalog", "1");
		System.out.println(request("http://192.168.1.113:7110/business/user/space/file/list", param));
	}

	@Test
	public void getSpaceFileGet() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		// param.put("token", "d6428a5e24c14cc186c61feac634d2d344a557a4");
		param.put("file_id", "17");
		System.out.println(request("http://192.168.1.113:7100/outer/business/user/space/file/get", param));
	}

	@Test
	public void praise() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		// param.put("token", "d6428a5e24c14cc186c61feac634d2d344a557a4");
		param.put("file_id", "17");
		System.out.println(request("http://192.168.1.113:7100/outer/business/user/space/file/appendix/praise", param));
	}

	@Test
	public void testRoomSetting() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("token", "d6428a5e24c14cc186c61feac634d2d344a557a4");
		param.put("im_user_name", "0dbe00e88696433c8a02158e1eeb5");
		param.put("type", "1");
		param.put("value", "false");
		System.out.println(request("https://chatoc.cloudak47.com:17101/business/user/chat/setting/set", param));
	}

	@Test
	public void testLogin() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("account", "13679487149");
		param.put("password", "123456");
		param.put("platform_type", "1");
		System.out.println(request("https://chatoc.cloudak47.com:17101/business/user/login1", param));
	}

	@Test
	public void testRoomList() throws Exception
	{
		// file_id =13 ,aa96d0a8927a4b
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("token", "d6428a5e24c14cc186c61feac634d2d344a557a4");
		System.out.println(request("https://chatoc.cloudak47.com:17101/business/user/chat/room/list", param));
	}

	@Test
	public void test4()
	{
		Instant start = Instant.now();// java8中新时间日期API

		long reduce = LongStream.rangeClosed(0, 150000000000L)//
				.parallel()// 并行流
				.reduce(0, Long::sum);
		System.out.println(reduce);
		Instant end = Instant.now();
		// 2392
		System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());// java8中新时间日期API
	}

	@Test
	public void test5()
	{
		Map<String, Long> map = new ConcurrentHashMap<>();
		while (map.size() < 150000)
		{
			map.put(UUID.randomUUID().toString(), System.currentTimeMillis() + 5 * 1000l);
		}
	}

	@Test
	public void test6() throws Exception {
		LinkedHashMap<Object, Object> param = new LinkedHashMap<>();
		param.put("token", "109d4618321541e3bf7804384721215b22c6eaab");
		System.out.println(request("http://192.168.1.106:7110/business/sys/thirdparty/list", param));
	}

	@Test
	public void test7() throws Exception
	{
		System.out.println(request("http://appcc.pispower.com/hottub/chatserver/list?company_name=default", new LinkedHashMap<>()));
	}

}
