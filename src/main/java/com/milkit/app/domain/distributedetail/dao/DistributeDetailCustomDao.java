package com.milkit.app.domain.distributedetail.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distributedetail.DistributeDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DistributeDetailCustomDao  {
 
	@PersistenceContext
	EntityManager entityManager;
	

	@Transactional
	public DistributeDetail updateReceive(Long distID, String userID) throws Exception {
		TypedQuery<DistributeDetail> query = entityManager.createQuery("SELECT d FROM DistributeDetail d WHERE DIST_ID = :distID AND RECEIVE_YN = 'N'", DistributeDetail.class);
		query.setParameter("distID", distID);
		query.setMaxResults(1);
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);		//배타적 잠금
		query.setHint("javax.persistence.lock.timeout", "3000");

		DistributeDetail distributeDetail = query.getSingleResult();

		Date currDate = new Date();
		distributeDetail.setUserID(userID);
		distributeDetail.setReceiveYN("Y");
		distributeDetail.setReceiveTime(currDate);

		return distributeDetail;
	}
	


}
