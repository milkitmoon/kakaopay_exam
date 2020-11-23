package com.milkit.app.service.pay.query;

import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.QueryRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
public class QueryHandlerServiceImpl {

	@Autowired
	private DistributeServiceImpl distributeService;

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;

	public Distribute query(QueryRequest queryRequest) throws Exception {
		Distribute distribute = distributeService.getDistribute(queryRequest.getToken());
		
		List<DistributeDetail> distributeDetail = distributeDetailService.getDistributeDetailReceiveYN(distribute.getId(), "Y");
		distribute.setDetail(distributeDetail);
		distribute.setReceiveAmount(distributeDetail);

		return distribute;
	}
    
}
