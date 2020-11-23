package com.milkit.app.service.pay.validate;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.milkit.app.api.pay.request.ApprRequest;

import org.springframework.http.HttpHeaders;



public interface RequestValidateService<T> {
    
    public T process(HttpHeaders headers, T request) throws Exception;

}
