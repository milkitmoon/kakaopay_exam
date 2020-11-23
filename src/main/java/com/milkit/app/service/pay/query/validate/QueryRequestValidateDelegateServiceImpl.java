package com.milkit.app.service.pay.query.validate;

import java.util.Date;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.QueryRequest;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.service.pay.validate.AbstractRequestValidateDelegateServiceImpl;
import com.milkit.app.util.DateUtil;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class QueryRequestValidateDelegateServiceImpl extends AbstractRequestValidateDelegateServiceImpl<QueryRequest> {
    
    @Autowired
	private DistributeServiceImpl distributeService;

    @Override
    protected QueryRequest addedValidate(QueryRequest request) throws Exception {
        
        //토큰정보가 있는지 확인
        if( StringUtils.isNullOrEmpty(request.getToken()) ) {
            throw new ServiceException(ErrorCodeEnum.NotExistTokenException.getCode());
        }

        //해당 토큰으로 뿌리기 정보가 없을 경우
		Distribute distribute = distributeService.getDistribute(request.getToken());
        if(distribute == null) {
            throw new ServiceException(ErrorCodeEnum.NotExistDistributeByTokenException.getCode(), new String[]{request.getToken()});
		}

		//사용자가 뿌리기한 정보가 아닙니다.
		if( !request.getUserID().equals(distribute.getUserID()) ) {
            throw new ServiceException(ErrorCodeEnum.DistributeQueryUserNotMatchException.getCode(), new String[]{request.getUserID(), distribute.getUserID()});
		}
		
        //조회하기 유효시간을 초과하였을 경우
        Date currDate = new Date();
        if( DateUtil.compareDate(distribute.getQueryLimitTime(), currDate) > 0) {
            throw new ServiceException(ErrorCodeEnum.QueryExpireTimeException.getCode(), new String[]{distribute.getQueryLimitTime().toString(), currDate.toString()});
        }

        return request;
    }
    
}
