package com.milkit.app.common.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import com.milkit.app.common.ErrorCodeEnum;

import org.springframework.validation.BindingResult;

import lombok.Data;
import lombok.Getter;


@SuppressWarnings("serial")
public class ValidationServiceException extends ServiceException {

	@Getter
    private List<ErrorDetail> errorDetail;

	public ValidationServiceException() {
		super(ErrorCodeEnum.ValidateException.getCode());
	}
	
	public ValidationServiceException(BindingResult result) {
		this();
		this.errorDetail = result
						.getAllErrors().stream()
						.map(error -> new ErrorDetail(error.getObjectName(), error.getDefaultMessage())).collect(Collectors.toList());
	}


	public String getMessage() {
    	String errMessage = null;

		String argMessage = ErrorCodeEnum.getMessage(getCode());
   		if(argMessage != null && !argMessage.equals("")) {
			if(errorDetail != null && errorDetail.size() > 0) {
				errMessage = getMessage(argMessage, errorDetail);
			} else {
				errMessage = argMessage;
			}
   		} else {
   			errMessage = super.getMessage();
   		}

    	return errMessage;
	}

	private String getMessage(String argMessage, List<ErrorDetail> errorsList) {
		StringBuilder sb = new StringBuilder();

		sb.append(argMessage).append("\n");
		for(ErrorDetail errorInfo : errorsList) {
			sb.append("[").append(errorInfo.field).append("]:").append(errorInfo.message).append("\n");
		}

		return sb.toString();
	}

	@Data
    public class ErrorDetail {

        private String field;

        private String message;

        ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

	



}
