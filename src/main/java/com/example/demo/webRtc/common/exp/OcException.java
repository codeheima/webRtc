package com.example.demo.webRtc.common.exp;

public class OcException extends Exception
{
	private static final long serialVersionUID = 1L;
	protected int code;

	public int getCode()
	{
		return this.code;
	}

	public OcException(int code, String message)
	{
		super(message);
		this.code = code;
	}

	public OcException(int code, String message, Throwable e)
	{
		super(message, e);
		this.code = code;
	}

	// -------------
	public OcException(String message)
	{
		super(message);
		this.code = 1;
	}

	public OcException(Throwable e)
	{
		super(e);
		this.code = 1;
	}

	public OcException(String message, Throwable e)
	{
		super(message, e);
		this.code = 1;
	}
}
