package com.example.demo.webRtc.controller;

import com.example.demo.webRtc.common.ApiRequest;
import com.example.demo.webRtc.common.exp.CheckException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
@JacksonXmlRootElement(localName = "xml")
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(creatorVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class WebRtctestRequest extends ApiRequest<WebRtctestResponse>
{
	private String token;
	
	private String ww;

	@Override
	public Class<WebRtctestResponse> getApiResponseClass()
	{
		return WebRtctestResponse.class;
	}

	@Override
	public void check()
			throws CheckException
	{
		// TODO Auto-generated method stub
		
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getWw()
	{
		return ww;
	}

	public void setWw(String ww)
	{
		this.ww = ww;
	}
	
	

}
