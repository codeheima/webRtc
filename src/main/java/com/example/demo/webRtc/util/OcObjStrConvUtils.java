package com.example.demo.webRtc.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 对象与xml,json序列化与反序列化处理类
 * 
 * @author clouder
 */
public abstract class OcObjStrConvUtils
{
	private static XmlMapper XM;
	private static final ObjectMapper OM = new ObjectMapper();
	private static final List<String> PRIMITIVE_TYPE = Arrays.asList("boolean", "byte", "char", "int", "long", "float", "double");
	static
	{
		XMLInputFactory inputFactory = new WstxInputFactory();
		inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
		XM = new XmlMapper(new XmlFactory(inputFactory, new WstxOutputFactory()));
		XM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		XM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		XM.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		XM.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		XM.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OM.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		OM.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		OM.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ObjectMapper createObjectMapper(Class<? extends JsonDeserializer> desrClass, Class<? extends Converter> convClass)
	{
		desrClass = desrClass == null ? null : (desrClass == JsonDeserializer.None.class ? null : desrClass);
		convClass = convClass == null ? null : (convClass == Converter.None.class ? null : convClass);
		if (desrClass == null && convClass == null)
			throw new NullPointerException();

		JavaType jt = null;
		JsonDeserializer desr = null;
		Converter conv = null;
		if (desrClass != null)
		{
			Type type = JsonDeserializer.class.getTypeParameters()[0];
			jt = drainJavaType(type, OM.constructType(desrClass));
			try
			{
				desr = desrClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		else
		{
			jt = OM.constructType(convClass);
			Method[] tmpMds = Converter.class.getMethods();
			tmpMds = tmpMds == null ? new Method[0] : tmpMds;
			Method convert = null;
			for (Method tmpMd : tmpMds)
			{
				if (tmpMd.getName().equals("convert") && tmpMd.getGenericParameterTypes().length == 1)
					convert = tmpMd;
			}
			if (convert == null)
				throw new RuntimeException("There is no \"convert\" function.");
			jt = drainJavaType(convert.getGenericParameterTypes()[0], jt);

			try
			{
				conv = convClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		final JavaType jtFinal = jt;
		final JsonDeserializer desrFinal = desr;
		final Converter convFinal = conv;
		ObjectMapper om = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		StdDeserializer sd = new StdDeserializer(jt)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Object deserialize(JsonParser arg0, DeserializationContext arg1)
					throws IOException, JsonProcessingException
			{
				if (desrFinal != null)
					return desrFinal.deserialize(arg0, arg1);
				else
					return convFinal.convert(fromJson(arg0.getText(), jtFinal));

			}
		};
		module.addDeserializer(jt.getRawClass(), sd);
		om.registerModules(module);
		return om;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ObjectMapper createKeyObjectMapper(Class<? extends KeyDeserializer> keyDesrClass)
	{
		keyDesrClass = keyDesrClass == null ? null : (keyDesrClass == KeyDeserializer.None.class ? null : keyDesrClass);
		if (keyDesrClass == null)
			throw new NullPointerException();

		KeyDeserializer keyDesr = null;
		try
		{
			keyDesr = keyDesrClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}

		JavaType jt = OM.constructType(keyDesrClass);
		Method[] tmpMds = KeyDeserializer.class.getMethods();
		tmpMds = tmpMds == null ? new Method[0] : tmpMds;
		Method deserializeKey = null;
		for (Method tmpMd : tmpMds)
		{
			if (tmpMd.getName().equals("deserializeKey") && tmpMd.getGenericParameterTypes().length == 2)
				deserializeKey = tmpMd;
		}
		if (deserializeKey == null)
			throw new RuntimeException("There is no \"deserializeKey\" function.");
		jt = drainJavaType(deserializeKey.getGenericParameterTypes()[0], jt);

		final JavaType jtFinal = jt;
		final KeyDeserializer keyDesrFinal = keyDesr;
		ObjectMapper om = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		StdDeserializer sd = new StdDeserializer(jt)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Object deserialize(JsonParser arg0, DeserializationContext arg1)
					throws IOException, JsonProcessingException
			{
				return keyDesrFinal.deserializeKey(fromJson(arg0.getText(), jtFinal), arg1);
			}
		};
		module.addDeserializer(jt.getRawClass(), sd);
		om.registerModules(module);
		return om;
	}

	// --------------

	public static <T> T toNull(Class<T> clazz)
	{
		if (clazz == null)
			throw new RuntimeException("Parameter: clazz is null.");

		if (PRIMITIVE_TYPE.contains(clazz.getName()))
		{
			if (clazz.getName().equals("boolean"))
			{
				Boolean b = new Boolean(false);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("byte"))
			{
				Byte b = new Byte((byte) 0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("char"))
			{
				Character b = new Character((char) 0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("int"))
			{
				Integer b = new Integer(0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("long"))
			{
				Long b = new Long(0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("float"))
			{
				Float b = new Float(0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else if (clazz.getName().equals("double"))
			{
				Double b = new Double(0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
			else
			{
				Byte b = new Byte((byte) 0);
				@SuppressWarnings("unchecked")
				T obj = (T) b;
				return obj;
			}
		}
		else
			return null;
	}

	public static Date toDate(String input)
			throws ParseException
	{
		if (input == null)
			throw new NullPointerException();
		String str = input.trim();
		str = str.startsWith("\"") && str.endsWith("\"") ? str.substring(1, str.length() - 1).trim() : str;

		try
		{
			String p1 = "[^0-9]*([0-9]{2,4})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]*";
			String p2 = "[^0-9]*([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]*";
			String p3 = "[^0-9]*([0-9]{2,4})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]+([0-9]{1,2})[^0-9]*";
			String p4 = "[^0-9]*([0-9]{14})[^0-9]*";

			if (str.trim().equals("null"))
				return null;
			else if (str.matches("^[0-9]+$"))
				return new Date(Long.parseLong(str));
			else if (str.matches(p1))
			{
				SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss");
				String s1 = str.replaceAll(p1, "$1");
				String s2 = str.replaceAll(p1, "$2");
				String s3 = str.replaceAll(p1, "$3");
				if (str.matches(p2))
					return sdfHms.parse(s1 + ":" + s2 + ":" + s3);
				else
					return sdfYmd.parse(s1 + "-" + s2 + "-" + s3);
			}
			else if (str.matches(p2))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				String hour = str.replaceAll(p2, "$1");
				String minute = str.replaceAll(p2, "$2");
				String second = str.replaceAll(p2, "$3");
				return sdf.parse(hour + ":" + minute + ":" + second);
			}
			else if (str.matches(p3))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String year = str.replaceAll(p3, "$1");
				String month = str.replaceAll(p3, "$2");
				String day = str.replaceAll(p3, "$3");
				String hour = str.replaceAll(p3, "$4");
				String minute = str.replaceAll(p3, "$5");
				String second = str.replaceAll(p3, "$6");
				return sdf.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
			}
			else if (str.matches(p4))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				return sdf.parse(str.replaceAll(p4, "$1"));
			}
			else
			{
				Date tmp = null;
				try
				{
					tmp = OM.readValue("\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"", Date.class);
				}
				catch (Throwable e)
				{
				}
				if (tmp != null)
					return tmp;
				else if (input.trim().startsWith("\"") && input.trim().endsWith("\""))
					return OM.readValue(input.trim(), Date.class);
				else
					return OM.readValue("\"" + input.replace("\\", "\\\\").replace("\"", "\\\"") + "\"", Date.class);
			}
		}
		catch (ParseException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw new ParseException(e.getMessage(), 0);
		}
	}

	public static boolean isSimplyClassWithDate(Class<?> clazz)
	{
		if (clazz == null)
			throw new NullPointerException();

		if (PRIMITIVE_TYPE.contains(clazz.getName()) || Number.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)//
				|| Character.class.isAssignableFrom(clazz) || Enum.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz))
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> T instanceObj(JavaType objJt)
	{
		Object rs = null;
		try
		{
			if (isSimplyClassWithDate(objJt.getRawClass()))
			{
				if (PRIMITIVE_TYPE.contains(objJt.getRawClass().getName()))
					rs = toNull(objJt.getRawClass());
				else if (Number.class.isAssignableFrom(objJt.getRawClass()))
					rs = fromJson("0", objJt);
				else if (Boolean.class.isAssignableFrom(objJt.getRawClass()))
					rs = new Boolean(false);
				else if (Character.class.isAssignableFrom(objJt.getRawClass()))
					rs = new Character('\0');
				else if (Enum.class.isAssignableFrom(objJt.getRawClass()))
				{
					Method md = objJt.getRawClass().getMethod("values");
					Object arrObj = md.invoke(null);
					if (arrObj != null && Array.getLength(arrObj) > 0)
						rs = Array.get(arrObj, 0);

				}
				else if (String.class.isAssignableFrom(objJt.getRawClass()))
					rs = new String();
				else if (Date.class.isAssignableFrom(objJt.getRawClass()))
					rs = new Date();
			}
			else if (objJt.getRawClass().isArray() || Iterable.class.isAssignableFrom(objJt.getRawClass()))
				rs = fromJson("[]", objJt);
			else if (Map.class.isAssignableFrom(objJt.getRawClass()))
				rs = instanceMap(objJt);
			else
			{

				try
				{
					rs = OcObjStrConvUtils.fromJson("{}", objJt);
				}
				catch (Throwable e)
				{
					rs = objJt.getRawClass().newInstance();
				}
			}
		}
		catch (Throwable e)
		{
		}

		if (rs == null)
			throw new RuntimeException("Can not instance the Object \"" + objJt.getRawClass().getName() + "\".");
		return (T) rs;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> instanceMap(JavaType mpJt)
	{
		Map<K, V> rs = null;
		if (mpJt == null)
			throw new NullPointerException();
		if (!Map.class.isAssignableFrom(mpJt.getRawClass()))
			throw new RuntimeException("Parameter: jtMp is not Map class.");

		JavaType keyJt = mpJt.getBindings().getBoundType(0);
		boolean isSimply = isSimplyClassWithDate(keyJt.getRawClass());

		try
		{
			if (isSimply)
				rs = fromJson("{}", mpJt);
		}
		catch (Throwable e)
		{
		}
		if (rs != null)
			return rs;

		if (mpJt.getRawClass().isAssignableFrom(Map.class))
			rs = new LinkedHashMap<>();
		else
		{
			try
			{
				rs = (Map<K, V>) mpJt.getRawClass().newInstance();
			}
			catch (Throwable e)
			{
				// complete the rest if you can
			}
		}
		if (rs == null)
			throw new RuntimeException("Can not instance the Map.");
		return rs;
	}

	public static JavaType drainJavaType(Type type, final JavaType containingJt)
	{
		if (type == null)
			throw new NullPointerException();

		if (type instanceof Class)
			return OM.constructType(type);
		else if (type instanceof GenericArrayType)
		{
			GenericArrayType gat = (GenericArrayType) type;
			if (gat.getGenericComponentType() instanceof Class)
				return OM.getTypeFactory().constructArrayType((Class<?>) gat.getGenericComponentType());
			else if (gat.getGenericComponentType() instanceof TypeVariable)
			{
				JavaType jt = getTypeVariableBoundType((TypeVariable<?>) gat.getGenericComponentType(), containingJt);
				return OM.getTypeFactory().constructArrayType(jt);
			}
			else
			{
				JavaType jt = drainJavaType(gat.getGenericComponentType(), containingJt);
				return OM.getTypeFactory().constructArrayType(jt);
			}
		}
		else if (type instanceof TypeVariable)
			return getTypeVariableBoundType((TypeVariable<?>) type, containingJt);
		else if (type instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType) type;
			Class<?> rawClass = OM.constructType(type).getRawClass();
			List<JavaType> jts = new LinkedList<JavaType>();
			for (Type t : pt.getActualTypeArguments())
				jts.add(drainJavaType(t, containingJt));
			return OM.getTypeFactory().constructParametricType(rawClass, jts.toArray(new JavaType[0]));
		}
		else
			return OM.constructType(Object.class);
	}

	public static JavaType getTypeVariableBoundType(final TypeVariable<?> tv, final JavaType containingJt)
	{
		JavaType rs = OM.constructType(Object.class);
		if (tv == null || containingJt == null)
			throw new NullPointerException();

		Class<?> ptClass = containingJt.getRawClass();
		JavaType ptJt = containingJt;

		while (ptClass != null)
		{
			Type[] types = ptClass.getTypeParameters();
			types = types == null ? new Type[0] : types;

			for (Type e : types)
			{
				if (tv == e)
				{
					rs = ptJt == null ? null : ptJt.getBindings().findBoundType(tv.getTypeName());
					if (rs != null)
						return rs;
				}
			}

			Class<?>[] clazzs = ptClass.getInterfaces();
			clazzs = clazzs == null ? new Class<?>[0] : clazzs;
			List<JavaType> jts = ptJt.getInterfaces();
			jts = jts == null ? new LinkedList<JavaType>() : jts;

			for (int i = 0; i < clazzs.length; i++)
			{
				Type[] tTypes = clazzs[i].getTypeParameters();
				tTypes = tTypes == null ? new Type[0] : tTypes;
				if (tTypes.length <= 0)
					continue;

				JavaType jt = jts.get(i);
				if (clazzs[i] != jt.getRawClass())
				{
					for (JavaType e : jts)
					{
						if (clazzs[i] == e.getRawClass())
						{
							jt = e;
							break;
						}
					}
				}

				for (Type e : tTypes)
				{
					if (tv == e)
					{
						rs = jt == null ? null : jt.getBindings().findBoundType(tv.getTypeName());
						if (rs != null)
							return rs;
					}
				}
			}

			ptClass = ptClass.getSuperclass();
			ptJt = ptJt.getSuperClass();
		}
		return rs;
	}

	public static Class<?> getTypeVariableBoundClass(final TypeVariable<?> tv, final JavaType containingJt)
	{
		JavaType jt = getTypeVariableBoundType(tv, containingJt);
		return jt.getRawClass();
	}

	public static JavaType constructJavaType(Class<?> clazz)
	{
		if (clazz != null && clazz.getTypeParameters() != null && clazz.getTypeParameters().length > 0)
			throw new RuntimeException("It is a generic class, please use constructParametricJavaType.");
		return OM.constructType(clazz);
	}

	public static JavaType constructArrayType(Class<?> elementType)
	{
		if (elementType != null && elementType.getTypeParameters() != null && elementType.getTypeParameters().length > 0)
			throw new RuntimeException("It is a generic class, please use constructArrayType(elementType).");
		return OM.getTypeFactory().constructArrayType(elementType);
	}

	public static JavaType constructArrayType(JavaType elementType)
	{
		return OM.getTypeFactory().constructArrayType(elementType);
	}

	public static JavaType constructParametricJavaType(Class<?> clazz1, Class<?>... clazz2)
	{
		return OM.getTypeFactory().constructParametricType(clazz1, clazz2);
	}

	public static JavaType constructParametricJavaType(Class<?> raw, JavaType... paraJts)
	{
		return OM.getTypeFactory().constructParametricType(raw, paraJts);
	}

	// ------xml--------

	public static String toXml(Object obj)
			throws JsonProcessingException
	{
		return XM.writeValueAsString(obj);
	}

	public static String toXml(Object obj, String rootName)
			throws JsonProcessingException
	{
		if (rootName == null)
			throw new NullPointerException();
		return XM.writer().withRootName(rootName).writeValueAsString(obj);
	}

	public static String toXml(Object obj, boolean indent)
			throws JsonProcessingException
	{
		if (indent)
			XM.enable(SerializationFeature.INDENT_OUTPUT);
		String rs = null;
		try
		{
			rs = XM.writeValueAsString(obj);
		}
		finally
		{
			if (indent)
				XM.disable(SerializationFeature.INDENT_OUTPUT);
		}
		return rs;
	}

	public static String toXml(Object obj, String rootName, boolean indent)
			throws JsonProcessingException
	{
		if (rootName == null)
			throw new NullPointerException();

		if (indent)
			XM.enable(SerializationFeature.INDENT_OUTPUT);
		String rs = null;
		try
		{
			rs = XM.writer().withRootName(rootName).writeValueAsString(obj);
		}
		finally
		{
			if (indent)
				XM.disable(SerializationFeature.INDENT_OUTPUT);
		}
		return rs;
	}

	public static JsonNode toJNodeFromXml(String xml)
			throws JsonProcessingException, IOException
	{
		return XM.readTree(xml);
	}

	public static <T> T fromXml(String xml, Class<T> clazz)
			throws JsonProcessingException, IOException
	{
		if (clazz != null && clazz.getTypeParameters() != null && clazz.getTypeParameters().length > 0)
			throw new IOException("It is a generic class, please use JavaType.");
		return fromXml(xml, XM.constructType(clazz));
	}

	public static <T> T fromXml(File xml, Class<T> clazz)
			throws JsonProcessingException, IOException
	{
		if (clazz != null && clazz.getTypeParameters() != null && clazz.getTypeParameters().length > 0)
			throw new IOException("It is a generic class, please use JavaType.");
		return fromXml(xml, XM.constructType(clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, JavaType jt)
			throws JsonProcessingException, IOException
	{
		if (jt == null)
			throw new NullPointerException();

		if (xml == null || xml.trim().equals(""))
			return (T) toNull(jt.getRawClass());
		else
			return XM.readValue(xml, jt);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(File xml, JavaType jt)
			throws JsonProcessingException, IOException
	{
		if (jt == null)
			throw new NullPointerException();

		if (xml == null || !xml.isFile())
			return (T) toNull(jt.getRawClass());
		else
			return XM.readValue(xml, jt);
	}

	// ------json--------

	public static String toJson(Object obj)
			throws JsonProcessingException
	{
		return OM.writeValueAsString(obj);
	}

	public static String toJson(Object obj, boolean indent)
			throws JsonProcessingException
	{
		if (indent)
			OM.enable(SerializationFeature.INDENT_OUTPUT);
		String rs = null;
		try
		{
			rs = OM.writeValueAsString(obj);
		}
		finally
		{
			if (indent)
				OM.disable(SerializationFeature.INDENT_OUTPUT);
		}
		return rs;
	}

	public static JsonNode toJNodeFromJson(String json)
			throws JsonProcessingException, IOException
	{
		if (json == null || json.trim().equals(""))
			return OM.readTree("null");
		else
		{
			try
			{
				return OM.readTree(json);
			}
			catch (Throwable e)
			{
				return OM.readTree("\"" + json.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
			}
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz)
			throws JsonProcessingException, IOException
	{
		if (clazz != null && clazz.getTypeParameters() != null && clazz.getTypeParameters().length > 0)
			throw new IOException("It is a generic object, please use JavaType.");
		return fromJson(json, OM.constructType(clazz));
	}

	public static <T> T fromJson(File json, Class<T> clazz)
			throws JsonProcessingException, IOException
	{
		if (clazz != null && clazz.getTypeParameters() != null && clazz.getTypeParameters().length > 0)
			throw new IOException("It is a generic object, please use JavaType.");
		return fromJson(json, OM.constructType(clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, JavaType jt)
			throws JsonProcessingException, IOException
	{
		if (jt == null)
			throw new NullPointerException();

		if (json == null)
			return (T) toNull(jt.getRawClass());
		else if (json.trim().equals("null"))
			return (T) toNull(jt.getRawClass());
		else if (json.trim().equals(""))
		{
			if (String.class.isAssignableFrom(jt.getRawClass()))
				return (T) json;
			else
				return (T) toNull(jt.getRawClass());
		}

		if (!isSimplyClassWithDate(jt.getRawClass()))
			return OM.readValue(json, jt);

		boolean isBool = "boolean".equals(jt.getRawClass().getName()) || Boolean.class.isAssignableFrom(jt.getRawClass());
		if (isBool)
		{
			json = json.trim();
			if (json.startsWith("\"") && json.endsWith("\""))
				return OM.readValue(json.substring(1, json.length() - 1).trim().replace("\\\\", "\\").replace("\\\"", "\""), jt);
			else
				return OM.readValue(json, jt);
		}
		else if (Date.class.isAssignableFrom(jt.getRawClass()))
		{
			try
			{
				return (T) toDate(json);
			}
			catch (ParseException e)
			{
				throw new IOException(e.getMessage(), e);
			}
		}
		else
		{
			if (json.trim().startsWith("\"") && json.trim().endsWith("\""))
				return OM.readValue(json.trim(), jt);
			else
				return OM.readValue("\"" + json.replace("\\", "\\\\").replace("\"", "\\\"") + "\"", jt);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(File json, JavaType jt)
			throws JsonProcessingException, IOException
	{
		if (jt == null)
			throw new NullPointerException();
		if (json == null || !json.isFile())
			return (T) toNull(jt.getRawClass());
		else
			return OM.readValue(json, jt);
	}

	/**
	 * @param json
	 * @param jt
	 *            若是个复合对象(这⾥Date也不是复合对象)，参数desrClass/convClass不起任何作用
	 * @param desrClass
	 * @param convClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Object readAsObjFromJson(String json, JavaType jt, Class<? extends JsonDeserializer> desrClass, Class<? extends Converter> convClass)
			throws JsonProcessingException, IOException
	{
		ObjectMapper om = null;
		if (!OcObjStrConvUtils.isSimplyClassWithDate(jt.getRawClass()))
			om = new ObjectMapper();
		else
			om = createObjectMapper(desrClass, convClass);
		if (om == null)
			throw new IOException("Can not create ObjectMapper.");

		if (json == null)
			return OcObjStrConvUtils.toNull(jt.getRawClass());
		if (!OcObjStrConvUtils.isSimplyClassWithDate(jt.getRawClass()))
			return om.readValue(json, jt);

		boolean isBool = "boolean".equals(jt.getRawClass().getName()) || Boolean.class.isAssignableFrom(jt.getRawClass());
		if (isBool)
		{
			json = json.trim();
			if (json.startsWith("\"") && json.endsWith("\""))
				return om.readValue(json.substring(1, json.length() - 1).trim().replace("\\\\", "\\").replace("\\\"", "\""), jt);
			else
				return om.readValue(json, jt);
		}
		else
		{
			if (json.trim().startsWith("\"") && json.trim().endsWith("\""))
				return om.readValue(json.trim(), jt);
			else
				return om.readValue("\"" + json.replace("\\", "\\\\").replace("\"", "\\\"") + "\"", jt);
		}
	}

	/**
	 * @param json
	 * @param jt
	 *            若是个复合对象(这⾥Date也不是复合对象)，参数keyDesrClass不起任何作用
	 * @param keyDesrClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Object readAsKeyObjFromJson(String json, JavaType jt, Class<? extends KeyDeserializer> keyDesrClass)
			throws JsonProcessingException, IOException
	{
		ObjectMapper om = null;
		if (!OcObjStrConvUtils.isSimplyClassWithDate(jt.getRawClass()))
			om = new ObjectMapper();
		else
			om = createKeyObjectMapper(keyDesrClass);
		if (om == null)
			throw new IOException("Can not create ObjectMapper.");

		if (json == null)
			return OcObjStrConvUtils.toNull(jt.getRawClass());
		if (!OcObjStrConvUtils.isSimplyClassWithDate(jt.getRawClass()))
			return om.readValue(json, jt);

		boolean isBool = "boolean".equals(jt.getRawClass().getName()) || Boolean.class.isAssignableFrom(jt.getRawClass());
		if (isBool)
		{
			json = json.trim();
			if (json.startsWith("\"") && json.endsWith("\""))
				return om.readValue(json.substring(1, json.length() - 1).trim().replace("\\\\", "\\").replace("\\\"", "\""), jt);
			else
				return om.readValue(json, jt);
		}
		else
		{
			if (json.trim().startsWith("\"") && json.trim().endsWith("\""))
				return om.readValue(json.trim(), jt);
			else
				return om.readValue("\"" + json.replace("\\", "\\\\").replace("\"", "\\\"") + "\"", jt);
		}
	}
}
