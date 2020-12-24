package com.milkit.app.service.pay.distribute.validate;

import com.milkit.app.api.pay.request.ApprRequest;
import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.service.pay.validate.AbstractRequestValidateDelegateServiceImpl;

import org.springframework.stereotype.Component;

@Component
public class DistributeRequestValidateDelegateServiceImpl extends AbstractRequestValidateDelegateServiceImpl<DistributeRequest> {

    @Override
    protected DistributeRequest addedValidate(DistributeRequest request) throws Exception {
        
        //뿌리기 금액이 0이하인지 확인
        if(request.getAmount() < 1) {
            throw new ServiceException(ErrorCodeEnum.AmountNotOverZeroException.getCode(), new String[]{String.valueOf(request.getAmount())});
        }
        //뿌리기 인원이 0이하인지 확인
        if(request.getDistCount() < 1) {
            throw new ServiceException(ErrorCodeEnum.DistCountNotOverZeroException.getCode(), new String[]{String.valueOf(request.getDistCount())});
        }
        //뿌리기 금액이 뿌리기 인원보다 작은지 확인
        if(request.getAmount() < request.getDistCount()) {
            throw new ServiceException(ErrorCodeEnum.NotEnoughAmountException.getCode(), new String[]{ String.valueOf(request.getAmount()), String.valueOf(request.getDistCount()) });
        }

        return request;
    }
    
}
