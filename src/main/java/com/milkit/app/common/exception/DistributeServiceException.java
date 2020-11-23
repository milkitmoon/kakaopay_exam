package com.milkit.app.common.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.milkit.app.common.ErrorCodeEnum;


@SuppressWarnings("serial")
public class DistributeServiceException extends ServiceException {


	public DistributeServiceException() {
		this(ErrorCodeEnum.ServiceException.getCode());
	}
	
	public DistributeServiceException(String code) {
		super(code);
	}
	
	public DistributeServiceException(String code, String[] objs) {
		super(code, objs);
	}
	
	
    public DistributeServiceException(String code, String message) {
    	super(code, message);
    }
    
	public DistributeServiceException(String code, String message, String[] objs) {
		super( code, message, objs );
	}


}
