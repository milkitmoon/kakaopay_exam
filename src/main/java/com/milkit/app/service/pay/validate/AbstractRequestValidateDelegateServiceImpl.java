package com.milkit.app.service.pay.validate;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.milkit.app.api.pay.request.ApprRequest;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;

import org.apache.commons.collections.CollectionUtils;
import org.h2.util.StringUtils;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestValidateDelegateServiceImpl<T extends ApprRequest> implements RequestValidateService<T> {

    @Override
    public T validate(HttpHeaders headers, T request) throws Exception {
        List<String> headers1 = headers.get(AppCommon.DIST_USER_HEADER_STRING);
        List<String> headers2 = headers.get(AppCommon.DIST_ROOM_HEADER_STRING);

        if( CollectionUtils.isEmpty(headers1) || StringUtils.isNullOrEmpty(headers1.get(0)) ) {
            throw new ServiceException(ErrorCodeEnum.NotExistDistUserException.getCode());
        } else {
            request.setUserID(headers1.get(0));
        }
        if( CollectionUtils.isEmpty(headers2) || StringUtils.isNullOrEmpty(headers2.get(0)) ) {
            throw new ServiceException(ErrorCodeEnum.NotExistDistRoomException.getCode());
        } else {
            request.setRoomID(headers2.get(0));
        }

        return addedValidate(request);
    }
    
    abstract protected T addedValidate(T request) throws Exception;

}
