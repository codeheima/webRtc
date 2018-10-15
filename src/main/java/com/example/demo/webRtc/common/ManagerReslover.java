package com.example.demo.webRtc.common;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.demo.webRtc.util.OcEncUrlUtils;
import com.example.demo.webRtc.util.OcObjStrConvUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;



@Configuration
public class ManagerReslover implements HandlerMethodArgumentResolver
{
	private final static ObjectMapper OM = new ObjectMapper();

	@Override
	public Object resolveArgument(MethodParameter arg0, ModelAndViewContainer arg1, NativeWebRequest arg2,
			WebDataBinderFactory arg3)
			throws Exception
	{
		Object rs = null;
		try
		{
			Type type = arg0.getGenericParameterType();
			JavaType containingJt = OM.constructType(arg0.getContainingClass());
			JavaType objJt = OcObjStrConvUtils.drainJavaType(type, containingJt);
			String paraName = arg0.getParameterAnnotation(RequestAttribute.class).value();
			paraName = !StringUtils.isEmpty(paraName) ? paraName : arg0.getParameterName();
			if (Map.class.isAssignableFrom(objJt.getRawClass()))
				paraName = arg0.getParameterAnnotation(RequestAttribute.class).value();
			HttpServletRequest rqst = ((ServletWebRequest) arg2).getRequest();
			Map<String, String[]> multipartParaMp = rqst.getParameterMap();
			rs = OcEncUrlUtils.toObject(multipartParaMp, objJt, paraName);
			rs = rs == null ? objJt.getRawClass().newInstance() : rs;
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		return rs ;
	}

	@Override
	public boolean supportsParameter(MethodParameter arg0)
	{
		
		return arg0.hasParameterAnnotation(RequestAttribute.class);
	}

}
