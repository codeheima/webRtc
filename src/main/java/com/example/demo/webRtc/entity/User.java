package com.example.demo.webRtc.entity;

public class User
{
	private String name;
	private String nickName;
	private String password;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getNickName()
	{
		return nickName;
	}
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	@Override
	public String toString()
	{
		return "User [name=" + name + ", nickName=" + nickName + ", password=" + password + "]";
	}
	
	

}
