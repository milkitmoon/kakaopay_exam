package com.milkit.app.service.pay.validate;

import javax.servlet.http.HttpServletRequest;

import com.milkit.app.api.pay.request.ApprRequest;
import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.QueryRequest;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.service.pay.distribute.validate.DistributeRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.query.validate.QueryRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.receive.validate.ReceiveRequestValidateDelegateServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class RequestValidateHandlerServiceImpl {


	public <T extends ApprRequest> T process(HttpHeaders headers, T request) throws Exception {
		RequestValidateService<T> requestValidateService = null;
		
		if(request instanceof DistributeRequest) {
			requestValidateService = distributeRequestValidateDelegateService();
		} else if(request instanceof ReceiveRequest) {
			requestValidateService = receiveRequestValidateDelegateService();
		} else if(request instanceof QueryRequest) {
			requestValidateService = queryRequestValidateDelegateService();
		} else {
			throw new ServiceException(ErrorCodeEnum.NotExistApprRequestException.getCode());
        }
        
		return requestValidateService.process(headers, request);
	}

    @Bean
    public RequestValidateService distributeRequestValidateDelegateService() throws Exception {
        return new DistributeRequestValidateDelegateServiceImpl();
    }

    @Bean
    public RequestValidateService receiveRequestValidateDelegateService() throws Exception {
        return new ReceiveRequestValidateDelegateServiceImpl();
    }

    @Bean
    public RequestValidateService queryRequestValidateDelegateService() throws Exception {
        return new QueryRequestValidateDelegateServiceImpl();
    }
    
}
