package com.milkit.app.service.pay.receive;

import java.util.Date;
import java.util.List;

import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;

import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.service.pay.DistributeHandlerService;
import com.milkit.app.service.pay.validate.RequestValidateService;
import com.milkit.app.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.util.NestedServletException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReceiveHandlerServiceImpl implements DistributeHandlerService<ReceiveRequest, Long> {

    @Autowired
    private RequestValidateService<ReceiveRequest> validateDelegateService;

    @Autowired
    private DistributeServiceImpl distributeService;

    @Autowired
    private DistributeDetailServiceImpl distributeDetailService;
    

    @Override
    public Long process(HttpHeaders headers, ReceiveRequest request) throws Exception {
        ReceiveRequest receiveRequest = validateDelegateService.validate(headers, request);

        Distribute distribute = distributeService.getDistribute(receiveRequest.getToken());
        DistributeDetail distributeDetail = null;
        try {
            distributeDetail = distributeDetailService.updateReceive(distribute.getId(), receiveRequest.getUserID());
        } catch (NoResultException ex) {
			throw new ServiceException(ErrorCodeEnum.AlreadyReicevedAllException.getCode());
		} catch(TransactionSystemException ex) {
            throw new ServiceException(ErrorCodeEnum.ManyTransactionException.getCode());
        }
        
		return distributeDetail.getAmount();
	}

}
