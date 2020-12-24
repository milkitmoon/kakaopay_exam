package com.milkit.app.service.pay.validate;

import org.springframework.http.HttpHeaders;



public interface RequestValidateService<T> {
    
    public T validate(HttpHeaders headers, T request) throws Exception;

}
