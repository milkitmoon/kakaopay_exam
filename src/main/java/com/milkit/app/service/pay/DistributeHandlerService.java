package com.milkit.app.service.pay;

import com.milkit.app.api.pay.request.ApprRequest;

import org.springframework.http.HttpHeaders;

public interface DistributeHandlerService<T extends ApprRequest, K> {
    
    public K process(HttpHeaders headers, T request) throws Exception;

}
