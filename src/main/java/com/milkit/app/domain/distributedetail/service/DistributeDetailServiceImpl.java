package com.milkit.app.domain.distributedetail.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.LockTimeoutException;
import javax.persistence.PessimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.dao.DistributeDao;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.dao.DistributeDetailDao;
import com.milkit.app.domain.distributedetail.dao.DistributeDetailCustomDao;
import com.milkit.app.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DistributeDetailServiceImpl {
	
	
    @Autowired
    private DistributeDetailDao distributeDetailDao;
    
    @Autowired
    private DistributeDetailCustomDao distributeDetailCustomDao;

    
    public DistributeDetail insert(DistributeDetail distributeDetail) throws Exception {
        return distributeDetailDao.save(distributeDetail);
	}

	public List<DistributeDetail> insert(List<DistributeDetail> distributeDetailList) throws Exception {
		return distributeDetailDao.saveAll(distributeDetailList);
	}
	
	public List<DistributeDetail> getDistributeDetail(Long distID) throws Exception {
		return distributeDetailDao.findByDistID(distID);
	}

	public DistributeDetail getDistributeDetail(Long distID, String userID) throws Exception {
		return distributeDetailDao.findByDistIDAndUserID(distID, userID);
	}

	public List<DistributeDetail> getDistributeDetailReceiveYN(Long distID, String receiveYN) throws Exception {
		return distributeDetailDao.findByDistIDAndReceiveYN(distID, receiveYN);
	}

	public DistributeDetail updateReceive(Long distID, String userID) throws Exception {
		return distributeDetailCustomDao.updateReceive(distID, userID);
	}

	public Long getReceiveAmount(Long distID) throws Exception {
		List<DistributeDetail> list = getDistributeDetailReceiveYN(distID, "Y");
		
		if(list != null) {
			return list.stream().mapToLong(x -> x.getAmount()).sum();
		} else {
			return 0l;
		}
	}


}
