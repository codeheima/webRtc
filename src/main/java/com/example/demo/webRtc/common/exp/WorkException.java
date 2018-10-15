package com.example.demo.webRtc.common.exp;

public class WorkException extends OcException
{
	private static final long serialVersionUID = 1L;

	public WorkException(int code, String message)
	{
		super(message);
		this.code = code;
	}

	public WorkException(int code, String message, Throwable e)
	{
		super(message, e);
		this.code = code;
	}

	// -------------
	public WorkException(String message)
	{
		super(message);
		this.code = 1;
	}

	public WorkException(Throwable e)
	{
		super(e);
		this.code = 1;
	}

	public WorkException(String message, Throwable e)
	{
		super(message, e);
		this.code = 1;
	}
}
