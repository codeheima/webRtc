package com.example.demo.webRtc.common.exp;

public class CheckException extends OcException
{
	private static final long serialVersionUID = 1L;

	public CheckException(int code, String message)
	{
		super(message);
		this.code = code;
	}

	public CheckException(int code, String message, Throwable e)
	{
		super(message, e);
		this.code = code;
	}

	// -------------
	public CheckException(String message)
	{
		super(message);
		this.code = 1;
	}

	public CheckException(Throwable e)
	{
		super(e);
		this.code = 1;
	}

	public CheckException(String message, Throwable e)
	{
		super(message, e);
		this.code = 1;
	}
}
