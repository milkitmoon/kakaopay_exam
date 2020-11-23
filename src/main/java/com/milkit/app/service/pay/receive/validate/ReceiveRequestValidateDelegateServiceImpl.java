package com.milkit.app.service.pay.receive.validate;

import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.service.pay.validate.AbstractRequestValidateDelegateServiceImpl;
import com.milkit.app.util.DateUtil;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ReceiveRequestValidateDelegateServiceImpl extends AbstractRequestValidateDelegateServiceImpl<ReceiveRequest> {

    @Autowired
    private DistributeServiceImpl distributeService;
    
    @Autowired
    private DistributeDetailServiceImpl distributeDetailService;
    

    @Override
    protected ReceiveRequest addedValidate(ReceiveRequest request) throws Exception {
        
        //토큰정보가 있는지 확인
        if( StringUtils.isNullOrEmpty(request.getToken()) ) {
            throw new ServiceException(ErrorCodeEnum.NotExistTokenException.getCode());
        }

        //해당 토큰으로 뿌리기 정보가 없을 경우
        Distribute distribute = distributeService.getDistribute(request.getToken());
        if(distribute == null) {
            throw new ServiceException(ErrorCodeEnum.NotExistDistributeByTokenException.getCode(), new String[]{request.getToken()});
        }

        //받기 요청 사용자가 뿌리기를 수행한 사용자일 경우
        String distributeUserID = distribute.getUserID();
        if( request.getUserID().equals(distributeUserID) ) {
            throw new ServiceException(ErrorCodeEnum.NotReiveDistributeUserException.getCode(), new String[]{distributeUserID});
        }

        //받기 요청 사용자의 대화방ID가 뿌리기 대화방ID와 다를 경우
        String distributeRoomID = distribute.getRoomID();
        if( !request.getRoomID().equals(distributeRoomID) ) {
            throw new ServiceException(ErrorCodeEnum.NotEqualsRoomIDException.getCode(), new String[]{distributeRoomID, request.getRoomID()});
        }

        //받기 요청 사용자가 이미 뿌리기 금액을 받았을 경우
        DistributeDetail existDistributeDetail = distributeDetailService.getDistributeDetail(distribute.getId(), request.getUserID());
        if( existDistributeDetail != null ) {
            throw new ServiceException(ErrorCodeEnum.AlreadyReicevedUserException.getCode(), new String[]{String.valueOf(distribute.getId()), request.getUserID()});
        }

        //받기 유효시간을 초과하였을 경우
        Date currDate = new Date();
        if( DateUtil.compareDate(distribute.getReceiveLimitTime(), currDate) > 0) {
            throw new ServiceException(ErrorCodeEnum.ReceiveExpireTimeException.getCode(), new String[]{distribute.getReceiveLimitTime().toString(), currDate.toString()});
        }

        return request;
    }
    
}
