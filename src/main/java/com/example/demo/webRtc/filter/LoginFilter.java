package com.example.demo.webRtc.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

//@Component
//@ServletComponentScan
//@WebFilter(filterName="testFilter",urlPatterns="*")
//public class LoginFilter implements Filter
//{	
//
//	@Override
//	public void destroy()
//	{
//		Filter.super.destroy();
//	}
//
//	@Override
//	public void init(FilterConfig filterConfig)
//			throws ServletException
//	{
//		Filter.super.init(filterConfig);
//	}
//
//	@Override
//	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
//			throws IOException, ServletException
//	{
//
//	}
//
//}
