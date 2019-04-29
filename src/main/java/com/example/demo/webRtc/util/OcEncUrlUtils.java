//package com.example.demo.webRtc.util;
//
//import java.io.IOException;
//import java.lang.reflect.AccessibleObject;
//import java.lang.reflect.Array;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.TypeVariable;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.KeyDeserializer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.util.Converter;
//
//
///**
// * 处理urlencode数据及multipart/form-data数据
// * 
// * @author clouder
// */
//public abstract class OcEncUrlUtils
//{
//	private static final ObjectMapper OM = new ObjectMapper();
//
//	private static void populate(final String encUrlStr, final Map<String, String> mpUrlKv, final Map<String, List<String>> mpUrlKl)
//	{
//		try
//		{
//			if (encUrlStr == null || encUrlStr.trim().equals(""))
//				return;
//
//			String[] kvStrs = encUrlStr.split("&");
//			for (String e : kvStrs)
//			{
//				String[] kvArr = e.split("=");
//				if (kvArr.length <= 0)
//					continue;
//				if (kvArr[0].trim().equals(""))
//					continue;
//
//				String key = kvArr[0];
//				String value = null;
//
//				if (kvArr.length == 2)
//					value = kvArr[1];
//				else if (kvArr.length > 2)
//					value = e.substring(e.indexOf("=") + 1);
//				else if (e.indexOf("=") >= 0)
//					value = "";
//
//				key = decode(key);
//				value = value == null ? null : decode(value);
//
//				if (mpUrlKv != null)
//				{
//					if (!mpUrlKv.containsKey(value) || value != null)
//						mpUrlKv.put(key, value);
//				}
//				if (mpUrlKl != null)
//				{
//					if (!mpUrlKl.containsKey(key))
//						mpUrlKl.put(key, new LinkedList<String>());
//					if (value != null)
//						mpUrlKl.get(key).add(value);
//				}
//			}
//		}
//		catch (Throwable e)
//		{
//			throw new RuntimeException(e.getMessage(), e);
//		}
//	}
//
//	private static String encodeLiteral(String str)
//	{
//		if (str == null)
//			return str;
//
//		String rs = "";
//		for (int i = 0; i < str.length(); i++)
//		{
//			String sub = str.substring(i, i + 1);
//			try
//			{
//				rs += URLEncoder.encode(sub, "UTF-8");
//			}
//			catch (Throwable e)
//			{
//				rs += sub;
//			}
//		}
//		return rs;
//	}
//
//	private static String decodeLiteral(String str)
//	{
//		String rs = null;
//		if (str == null)
//			return rs;
//		try
//		{
//			Pattern pn = Pattern.compile("((?:%[a-fA-F0-9]{2})+)");
//			Matcher mr = pn.matcher(str);
//			StringBuffer sb = new StringBuffer();
//			while (mr.find())
//			{
//				String s = mr.group(1);
//				try
//				{
//					s = URLDecoder.decode(s, "UTF-8");
//					s = s.replace("^", "\\^");
//					s = s.replace("$", "\\$");
//					s = s.replace("[", "\\[");
//					s = s.replace("]", "\\]");
//					s = s.replace(".", "\\.");
//					s = s.replace("*", "\\*");
//					s = s.replace("(", "\\(");
//					s = s.replace(")", "\\)");
//					s = s.replace("{", "\\{");
//					s = s.replace("}", "\\}");
//					s = s.replace("|", "\\|");
//					s = s.replace("+", "\\+");
//					s = s.replace("?", "\\?");
//					mr.appendReplacement(sb, s);
//				}
//				catch (Throwable e)
//				{
//				}
//			}
//			mr.appendTail(sb);
//			rs = sb.toString();
//		}
//		catch (Throwable e)
//		{
//			rs = str;
//		}
//		return rs;
//	}
//
//	// ---------------
//
//	public static String encode(String str)
//	{
//		String rs = null;
//		if (str == null)
//			return rs;
//		try
//		{
//			rs = URLEncoder.encode(str, "UTF-8");
//		}
//		catch (Throwable e)
//		{
//			rs = encodeLiteral(str);
//		}
//		return rs;
//	}
//
//	public static String decode(String str)
//	{
//		String rs = null;
//		if (str == null)
//			return rs;
//		try
//		{
//			rs = URLDecoder.decode(str, "UTF-8");
//		}
//		catch (Throwable e)
//		{
//			rs = decodeLiteral(str);
//		}
//		return rs;
//	}
//
//	public static Map<String, String[]> mergeParameterMap(final String encUrlStr, Map<String, String[]> mpParameter)
//	{
//		Map<String, String[]> rs = new LinkedHashMap<String, String[]>();
//		if (encUrlStr == null || encUrlStr.trim().equals(""))
//			return mpParameter;
//
//		Map<String, String> mpUrlKv = new LinkedHashMap<String, String>();
//		Map<String, List<String>> mpUrlKl = new LinkedHashMap<String, List<String>>();
//		populate(encUrlStr, mpUrlKv, mpUrlKl);
//
//		for (String key : mpUrlKl.keySet())
//		{
//			List<String> value = mpUrlKl.get(key);
//			rs.put(key, value == null ? null : value.toArray(new String[0]));
//		}
//
//		if (mpParameter == null)
//			return rs;
//		else
//		{
//			for (String key : mpParameter.keySet())
//			{
//				String[] value = mpParameter.get(key);
//				if (value == null)
//					continue;
//				if (!rs.containsKey(key))
//					rs.put(key, value);
//				else
//				{
//					String[] tmps = rs.get(key);
//					if (tmps == null)
//						rs.put(key, value);
//					else
//					{
//						List<String> vs = new LinkedList<String>();
//						vs.addAll(Arrays.asList(tmps));
//						for (String e : value)
//							vs.add(e);
//						rs.put(key, vs.toArray(new String[0]));
//					}
//				}
//			}
//		}
//		return rs;
//	}
//
//	public static Map<String, String> toParameterKvMap(final String encUrlStr)
//	{
//		Map<String, String> mpUrlKv = new LinkedHashMap<String, String>();
//		Map<String, List<String>> mpUrlKl = new LinkedHashMap<String, List<String>>();
//		populate(encUrlStr, mpUrlKv, mpUrlKl);
//		return mpUrlKv;
//	}
//
//	public static Map<String, String[]> toParameterMap(final String encUrlStr)
//	{
//		Map<String, String[]> rs = new LinkedHashMap<String, String[]>();
//		if (encUrlStr == null)
//			return null;
//		if (encUrlStr.trim().equals(""))
//			return rs;
//
//		Map<String, String> mpUrlKv = new LinkedHashMap<String, String>();
//		Map<String, List<String>> mpUrlKl = new LinkedHashMap<String, List<String>>();
//		populate(encUrlStr, mpUrlKv, mpUrlKl);
//
//		for (String key : mpUrlKl.keySet())
//		{
//			List<String> value = mpUrlKl.get(key);
//			rs.put(key, value == null ? null : value.toArray(new String[0]));
//		}
//		return rs;
//	}
//
//	// paraName不对复合对象(这⾥Date不复合对象)起作用，objJt为Map且paraName为空null时以对象方式处理
//	public static <T> T toObject(final String encUrlStr, JavaType objJt, String paraName)
//			throws JsonProcessingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
//	{
//		Map<String, String> mpUrlKv = new LinkedHashMap<String, String>();
//		Map<String, List<String>> mpUrlKl = new LinkedHashMap<String, List<String>>();
//		populate(encUrlStr, mpUrlKv, mpUrlKl);
//		return toObject(objJt, mpUrlKl, paraName);
//	}
//
//	// paraName不对复合对象(这⾥Date不是复合对象)起作用，objJt为Map且paraName为空null时以对象方式处理
//	public static <T> T toObject(Map<String, String[]> mpPara, JavaType objJt, String paraName)
//			throws JsonProcessingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
//	{
//		Map<String, List<String>> mplPara = mpPara == null ? null : new LinkedHashMap<String, List<String>>();
//		if (mpPara != null)
//		{
//			for (String key : mpPara.keySet())
//			{
//				String[] value = mpPara.get(key);
//				List<String> tmps = value == null ? null : new LinkedList<String>();
//				if (value != null)
//					tmps.addAll(Arrays.asList(value));
//				mplPara.put(key, tmps);
//			}
//		}
//		return toObject(objJt, mplPara, paraName);
//	}
//
//	// paraName不对复合对象(这⾥Date不是复合对象)起作用，objJt为Map且paraName为空null时以对象方式处理
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static <T> T toObject(JavaType objJt, Map<String, List<String>> mplPara, String paraName)
//			throws JsonProcessingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
//	{
//		if (objJt == null)
//			throw new NullPointerException();
//		if (objJt.getRawClass().getTypeParameters() != null && objJt.getRawClass().getTypeParameters().length > 0)
//		{
//			if (objJt.getBindings().size() != objJt.getRawClass().getTypeParameters().length)
//				throw new RuntimeException("Parameter: paraJt is generic, but there is no enough binding in paraJt.");
//			TypeVariable<?>[] tvs = objJt.getRawClass().getTypeParameters();
//			for (TypeVariable<?> tv : tvs)
//			{
//				JavaType tmp = objJt.getBindings().findBoundType(tv.getTypeName());
//				if (tmp == null)
//					throw new RuntimeException("Parameter: paraJt is generic, but can not find the binding \"" + tv.getTypeName() + "\" in paraJt.");
//			}
//		}
//		if (mplPara == null)
//			return (T) OcObjStrConvUtils.toNull(objJt.getRawClass());
//
//		List<String> strs = mplPara.get(paraName);
//		List<String> itrStrs = strs == null ? new LinkedList<String>() : strs;
//		String str = strs == null || strs.size() <= 0 ? null : strs.get(strs.size() - 1);
//
//		// 非复合对象
//		if (OcObjStrConvUtils.isSimplyClassWithDate(objJt.getRawClass()))
//		{
//			if (String.class.isAssignableFrom(objJt.getRawClass()))
//				return (T) str;
//			else
//			{
//				try
//				{
//					if (str != null && str.trim().matches("^\\[[\\S\\s]*\\]$") && OcObjStrConvUtils.toJNodeFromJson(str).isArray())
//					{
//						JavaType jt = OcObjStrConvUtils.constructParametricJavaType(List.class, JsonNode.class);
//						List<JsonNode> jns = OcObjStrConvUtils.fromJson(str, jt);
//						str = jns.size() <= 0 ? null : OcObjStrConvUtils.toJson(jns.get(jns.size() - 1));
//					}
//					return OcObjStrConvUtils.fromJson(str, objJt);
//				}
//				catch (Throwable e)
//				{
//					return (T) OcObjStrConvUtils.toNull(objJt.getClass());
//				}
//			}
//		}
//		else if (objJt.getRawClass().isArray())
//		{
//			List<Object> tmps = new LinkedList<Object>();
//			JavaType valueJt = objJt.getContentType();
//			for (String v : itrStrs)
//			{
//				JsonNode jn = OcObjStrConvUtils.toJNodeFromJson(v);
//				boolean isStrClazz = String.class.isAssignableFrom(valueJt.getRawClass());
//				if ((isStrClazz && v != null && v.trim().matches("^\\[[\\S\\s]*\\]$") && jn.isArray()) || (!isStrClazz && jn.isArray()))
//				{
//					Object tmpObj = OcObjStrConvUtils.fromJson(v, objJt);
//					int len = tmpObj == null ? 0 : Array.getLength(tmpObj);
//					for (int i = 0; i < len; i++)
//						tmps.add(Array.get(tmpObj, i));
//				}
//				else
//					tmps.add(OcObjStrConvUtils.fromJson(v, valueJt));
//			}
//			return strs == null ? null : (T) tmps.toArray(OcObjStrConvUtils.fromJson("[]", objJt));
//		}
//		else if (Iterable.class.isAssignableFrom(objJt.getRawClass()))
//		{
//			JavaType valueJt = objJt.getBindings().getBoundType(0);
//			Iterable<?> tmps = OcObjStrConvUtils.fromJson("[]", objJt);
//			Method[] tmpMds = tmps.getClass().getMethods();
//			tmpMds = tmpMds == null ? new Method[0] : tmpMds;
//			Method add = null;
//			for (Method tmpMd : tmpMds)
//			{
//				if (tmpMd.getName().equals("add") && tmpMd.getGenericParameterTypes().length == 1)
//					add = tmpMd;
//			}
//			if (add == null)
//				throw new RuntimeException("There is no \"add\" function.");
//
//			for (String v : itrStrs)
//			{
//				JsonNode jn = OcObjStrConvUtils.toJNodeFromJson(v);
//				boolean isStrClazz = String.class.isAssignableFrom(valueJt.getRawClass());
//				if ((isStrClazz && v != null && v.trim().matches("^\\[[\\S\\s]*\\]$") && jn.isArray()) || (!isStrClazz && jn.isArray()))
//				{
//					Iterable<?> tmpItr = (Iterable<?>) OcObjStrConvUtils.fromJson(v, objJt);
//					Iterator<?> tmpItor = tmpItr == null ? (new LinkedList<Object>()).listIterator() : tmpItr.iterator();
//					while (tmpItor.hasNext())
//						add.invoke(tmps, tmpItor.next());
//				}
//				else
//					add.invoke(tmps, (Object) OcObjStrConvUtils.fromJson(v, valueJt));
//			}
//			return strs == null ? null : (T) tmps;
//		}
//		else if (Map.class.isAssignableFrom(objJt.getRawClass()))
//		{
//			JavaType keyJt = objJt.getBindings().getBoundType(0);
//			JavaType valueJt = objJt.getBindings().getBoundType(1);
//			Map<Object, Object> objMp = OcObjStrConvUtils.instanceMap(objJt);
//			if (paraName == null || paraName.trim().equals(""))
//			{
//				for (String key : mplPara.keySet())
//				{
//					String tmp = mplPara.get(key) != null && mplPara.get(key).size() > 0 ? mplPara.get(key).get(mplPara.get(key).size() - 1) : null;
//					objMp.put(OcObjStrConvUtils.fromJson(key, keyJt), OcObjStrConvUtils.fromJson(tmp, valueJt));
//				}
//				return (T) objMp;
//			}
//			else
//			{
//				Map<String, Object> tmpMp = str == null ? null : new LinkedHashMap<String, Object>();
//				if (str != null)
//				{
//					JavaType tmpJt = OM.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
//					tmpMp = OcObjStrConvUtils.fromJson(str, tmpJt);
//					for (String key : tmpMp.keySet())
//					{
//						String json = OcObjStrConvUtils.toJson(tmpMp.get(key));
//						objMp.put(OcObjStrConvUtils.fromJson(key, keyJt), OcObjStrConvUtils.fromJson(json, valueJt));
//					}
//				}
//				return str == null ? null : (T) objMp;
//			}
//		}
//
//		// 复合对象
//		Object rs = OcObjStrConvUtils.instanceObj(objJt);
//		Map<String, AccessibleObject> mpAccObj = OcObjectUtils.getAllAccObj4JsonDesr(objJt.getRawClass());
//		for (String key : mplPara.keySet())
//		{
//			if (mpAccObj.get(key) == null)
//				continue;
//			strs = mplPara.get(key);
//			itrStrs = strs == null ? new LinkedList<String>() : strs;
//			str = strs == null || strs.size() <= 0 ? null : strs.get(strs.size() - 1);
//
//			Object obj = null;
//			JavaType accJt = null;
//			AccessibleObject ao = mpAccObj.get(key);
//			JsonDeserialize jd = ao.getAnnotation(JsonDeserialize.class);
//			Field fd = (ao instanceof Field) ? (Field) ao : null;
//			Method md = (ao instanceof Field) ? null : (Method) ao;
//			if (ao instanceof Field)
//				accJt = OcObjStrConvUtils.drainJavaType(((Field) ao).getGenericType(), objJt);
//			else
//				accJt = OcObjStrConvUtils.drainJavaType(((Method) ao).getParameters()[0].getParameterizedType(), objJt);
//			boolean isNotUC = jd == null || ((jd.using() == null || jd.using() == JsonDeserializer.None.class) //
//					&& (jd.converter() == null || jd.converter() == Converter.None.class));
//			boolean isNotContentUC = jd == null || ((jd.contentUsing() == null || jd.contentUsing() == JsonDeserializer.None.class)//
//					&& (jd.contentConverter() == null || jd.contentConverter() == Converter.None.class));
//
//			if (accJt.getRawClass().isArray())
//			{
//				if (isNotContentUC)
//					obj = toObject(accJt, mplPara, key);
//				else
//				{
//					JavaType valueJt = accJt.getContentType();
//					List<Object> tmps = new LinkedList<Object>();
//					for (String v : itrStrs)
//					{
//						JsonNode jn = OcObjStrConvUtils.toJNodeFromJson(v);
//						boolean isStrClazz = String.class.isAssignableFrom(valueJt.getRawClass());
//						if ((isStrClazz && v != null && v.trim().matches("^\\[[\\S\\s]*\\]$") && jn.isArray()) || (!isStrClazz && jn.isArray()))
//						{
//							Object tmpObj = OcObjStrConvUtils.readAsObjFromJson(v, accJt, jd.contentUsing(), jd.contentConverter());
//							int len = tmpObj == null ? 0 : Array.getLength(tmpObj);
//							for (int i = 0; i < len; i++)
//								tmps.add(Array.get(tmpObj, i));
//						}
//						else
//							tmps.add(OcObjStrConvUtils.readAsObjFromJson(v, valueJt, jd.contentUsing(), jd.contentConverter()));
//					}
//					obj = strs == null ? null : tmps.toArray(OcObjStrConvUtils.fromJson("[]", accJt));
//				}
//			}
//			else if (Iterable.class.isAssignableFrom(accJt.getRawClass()))
//			{
//				if (isNotContentUC)
//					obj = toObject(accJt, mplPara, key);
//				else
//				{
//					JavaType valueJt = accJt.getBindings().getBoundType(0);
//					Iterable<?> tmps = OcObjStrConvUtils.fromJson("[]", accJt);
//					Method[] tmpMds = tmps.getClass().getMethods();
//					tmpMds = tmpMds == null ? new Method[0] : tmpMds;
//					Method add = null;
//					for (Method tmpMd : tmpMds)
//					{
//						if (tmpMd.getName().equals("add") && tmpMd.getGenericParameterTypes().length == 1)
//							add = tmpMd;
//					}
//					if (add == null)
//						throw new RuntimeException("There is no \"add\" function.");
//
//					for (String v : itrStrs)
//					{
//						JsonNode jn = OcObjStrConvUtils.toJNodeFromJson(v);
//						boolean isStrClazz = String.class.isAssignableFrom(valueJt.getRawClass());
//						if ((isStrClazz && v != null && v.trim().matches("^\\[[\\S\\s]*\\]$") && jn.isArray()) || (!isStrClazz && jn.isArray()))
//						{
//							Iterable<?> tmpItr = (Iterable<?>) OcObjStrConvUtils.readAsObjFromJson(v, accJt, jd.contentUsing(), jd.contentConverter());
//							Iterator<?> tmpItor = tmpItr != null ? tmpItr.iterator() : (new LinkedList<Object>()).listIterator();
//							while (tmpItor.hasNext())
//								add.invoke(tmps, tmpItor.next());
//						}
//						else
//							add.invoke(tmps, (Object) OcObjStrConvUtils.readAsObjFromJson(v, valueJt, jd.contentUsing(), jd.contentConverter()));
//					}
//					obj = strs == null ? null : tmps;
//				}
//			}
//			else if (Map.class.isAssignableFrom(accJt.getRawClass()))
//			{
//				JavaType keyJt = accJt.getBindings().getBoundType(0);
//				JavaType valueJt = accJt.getBindings().getBoundType(1);
//				Class<? extends KeyDeserializer> keyUsing = (jd == null || jd.keyUsing() == null || jd.keyUsing() == KeyDeserializer.None.class) ? null : jd.keyUsing();
//				Class<? extends JsonDeserializer> contentUsing = //
//						(jd == null || jd.contentUsing() == null || jd.contentUsing() == JsonDeserializer.None.class) ? null : jd.contentUsing();
//				Class<? extends Converter> contentConverter = //
//						(jd == null || jd.contentConverter() == null || jd.contentConverter() == Converter.None.class) ? null : jd.contentConverter();
//				contentConverter = contentUsing != null ? null : contentConverter;
//
//				if (str == null)
//					obj = OcObjStrConvUtils.toNull(accJt.getRawClass());
//				else
//				{
//					if (keyUsing == null && (contentUsing == null && contentConverter == null))
//						obj = toObject(accJt, mplPara, key);
//					else
//					{
//						JavaType tmpMpJt = OM.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
//						Map<String, Object> tmpStrMp = str.trim().equals("") ? new LinkedHashMap<String, Object>() : OcObjStrConvUtils.fromJson(str, tmpMpJt);
//						Map<Object, Object> tmpObjMp = OcObjStrConvUtils.instanceMap(accJt);
//						if (keyUsing == null && (contentUsing != null || contentConverter != null))
//						{
//							for (String k : tmpStrMp.keySet())
//								tmpObjMp.put(OcObjStrConvUtils.fromJson(k, keyJt),
//										OcObjStrConvUtils.readAsObjFromJson(OcObjStrConvUtils.toJson(tmpStrMp.get(k)), valueJt, contentUsing, contentConverter));
//						}
//						else if (keyUsing != null && (contentUsing != null || contentConverter != null))
//						{
//							for (String k : tmpStrMp.keySet())
//								tmpObjMp.put(OcObjStrConvUtils.readAsKeyObjFromJson(k, keyJt, keyUsing),
//										OcObjStrConvUtils.readAsObjFromJson(OcObjStrConvUtils.toJson(tmpStrMp.get(k)), valueJt, contentUsing, contentConverter));
//						}
//						else
//						{
//							// 这里只是keyUsing!=null
//							for (String k : tmpStrMp.keySet())
//								tmpObjMp.put(OcObjStrConvUtils.readAsKeyObjFromJson(k, keyJt, keyUsing),
//										OcObjStrConvUtils.fromJson(OcObjStrConvUtils.toJson(tmpStrMp.get(k)), valueJt));
//						}
//						obj = tmpObjMp;
//					}
//				}
//			}
//			else
//			{
//				boolean isSimply = OcObjStrConvUtils.isSimplyClassWithDate(accJt.getRawClass());
//				if (isNotUC)
//					obj = isSimply ? toObject(accJt, mplPara, key) : OcObjStrConvUtils.fromJson(str, accJt);
//				else
//					obj = OcObjStrConvUtils.readAsObjFromJson(str, accJt, jd.using(), jd.converter());
//			}
//
//			if (obj == null)
//				continue;
//			if (!ao.isAccessible())
//				ao.setAccessible(true);
//			if (ao instanceof Field)
//				fd.set(rs, obj);
//			else
//				md.invoke(rs, obj);
//		}
//		return (T) rs;
//	}
//
//}
