package com.example.demo.webRtc.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OcObjectUtils
{
	public static void setFieldValue(Object obj, String fieldName, Object value)
	{
		try
		{
			if (obj == null || fieldName == null || fieldName.trim().equals(""))
				return;
			Field field = OcObjectUtils.getFieldWithoutFinal(obj.getClass(), fieldName);
			if (field == null)
				return;
			if (!field.isAccessible())
				field.setAccessible(true);
			field.set(obj, value);
		}
		catch (Throwable e)
		{
		}
	}

	// 不必遍历interface，因interface字段是public static final
	public static Field getFieldWithoutFinal(Class<?> clazz, String fieldName)
	{
		if (clazz == null || fieldName == null || fieldName.trim().equals(""))
			return null;
		fieldName = fieldName.trim();

		Class<?> pt = clazz;
		while (pt != null)
		{
			Field[] fds = pt.getDeclaredFields();
			fds = fds == null ? new Field[0] : fds;
			for (Field fd : fds)
			{
				if (fieldName.equals(fd.getName()))
					return fd;
			}

			pt = pt.getSuperclass();
		}
		return null;
	}

	// 不必遍历interface，因interface字段是public static final
	public static Map<String, Field> getAllFieldsWithoutFinal(Class<?> clazz)
	{
		Map<String, Field> rs = new LinkedHashMap<String, Field>();
		if (clazz == null)
			return rs;

		Class<?> pt = clazz;
		while (pt != null)
		{
			Field[] fds = pt.getDeclaredFields();
			fds = fds == null ? new Field[0] : fds;
			for (Field fd : fds)
			{
				if (!rs.containsKey(fd.getName()) && ((fd.getModifiers() & 16) != 16))
					rs.put(fd.getName(), fd);
			}

			pt = pt.getSuperclass();
		}
		return rs;
	}

	public static Map<String, Field> getAllFields(Class<?> clazz)
	{
		Map<String, Field> rs = new LinkedHashMap<String, Field>();
		if (clazz == null)
			return rs;

		Class<?> pt = clazz;
		while (pt != null)
		{
			// 类
			Field[] fds = pt.getDeclaredFields();
			fds = fds == null ? new Field[0] : fds;
			for (Field fd : fds)
			{
				if (!rs.containsKey(fd.getName()))
					rs.put(fd.getName(), fd);
			}

			// 接口，接口定义的字段全是public static final使用getFields不必往向读取父接口
			Class<?>[] itfs = pt.getInterfaces();
			itfs = itfs == null ? new Class<?>[0] : itfs;
			for (Class<?> itf : itfs)
			{
				Field[] itfFds = itf.getFields();
				itfFds = itfFds == null ? new Field[0] : itfFds;
				for (Field itfFd : itfFds)
				{
					if (!rs.containsKey(itfFd.getName()))
						rs.put(itfFd.getName(), itfFd);
				}
			}

			pt = pt.getSuperclass();
		}
		return rs;
	}

	public static Map<String, AccessibleObject> getAllAccObj4JsonDesr(Class<?> clazz)
	{
		if (clazz == null)
			throw new NullPointerException();

		Map<String, AccessibleObject> rs = new LinkedHashMap<String, AccessibleObject>();

		// 处理字段 -- 为了支持JsonProperty,JsonIgnore
		Map<String, Field> mpField = OcObjectUtils.getAllFieldsWithoutFinal(clazz);
		for (String key : mpField.keySet())
		{
			JsonIgnore ji = mpField.get(key).getAnnotation(JsonIgnore.class);
			if (ji != null)
				continue;

			JsonProperty jp = mpField.get(key).getAnnotation(JsonProperty.class);
			if (jp == null || jp.value() == null || jp.value().trim().equals(""))
			{
				rs.put(key, mpField.get(key));
				continue;
			}
			String jpValue = jp.value().trim();
			if (!rs.containsKey(jpValue))
				rs.put(jpValue, mpField.get(key));
		}

		// 处理方法 -- 为了支持JsonProperty,JsonIgnore
		// 不必遍历接口，因类实现了其方法
		Class<?> pt = clazz;
		while (pt != null)
		{
			Method[] mds = pt.getDeclaredMethods();
			mds = mds == null ? new Method[0] : mds;

			for (Method md : mds)
			{
				// 只处理只有一个参数的方法
				if (md.getParameterCount() != 1)
					continue;

				JsonIgnore ji = md.getAnnotation(JsonIgnore.class);
				if (ji != null)
					continue;

				JsonProperty jp = md.getAnnotation(JsonProperty.class);
				if (jp == null || jp.value() == null || jp.value().trim().equals(""))
					continue;

				boolean has = false;
				String jpValue = jp.value().trim();
				for (String k : rs.keySet())
				{
					if (jpValue.equals(k))
					{
						has = true;
						break;
					}
				}
				if (!has)
					rs.put(jpValue, md);
			}

			pt = pt.getSuperclass();
		}
		return rs;
	}
}
