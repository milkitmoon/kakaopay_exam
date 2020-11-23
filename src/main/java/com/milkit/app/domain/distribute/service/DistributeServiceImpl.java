package com.milkit.app.domain.distribute.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.dao.DistributeDao;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.dao.DistributeDetailDao;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DistributeServiceImpl {

	@Autowired
	private DistributeDao distributeDao;

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;


	public Distribute insert(Distribute distribute) throws Exception {
		return distributeDao.save(distribute);
	}

	public Distribute getDistribute(String token) throws Exception {
		return distributeDao.findByToken(token);
	}


	@Transactional
	public void insert(Distribute distribute, List<DistributeDetail> distributeDetailList) throws Exception {
		Distribute insertedDistribute = insert(distribute);

		if(insertedDistribute != null && distributeDetailList != null && distributeDetailList.size() > 0) {
			for(DistributeDetail distributeDetail : distributeDetailList) {
				distributeDetail.setDistID(insertedDistribute.getId());
			}
		}
		distributeDetailService.insert(distributeDetailList);
	}
    
}
