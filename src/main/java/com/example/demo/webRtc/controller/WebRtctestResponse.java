package com.example.demo.webRtc.controller;

import com.example.demo.webRtc.common.ApiResponse;
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
public class WebRtctestResponse extends ApiResponse
{
	private String token;
	
	private String ww;

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
