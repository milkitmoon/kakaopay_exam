package com.milkit.app.domain.distributedetail.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.NamedNativeQuery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distributedetail.DistributeDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DistributeDetailDao extends JpaRepository<DistributeDetail, Long>, JpaSpecificationExecutor<DistributeDetail>  {
	

	public List<DistributeDetail> findByDistID(Long distID);

	public DistributeDetail findByDistIDAndUserID(Long distID, String userID);

	public List<DistributeDetail> findByDistIDAndReceiveYN(Long distID, String receiveYN);

 

}
