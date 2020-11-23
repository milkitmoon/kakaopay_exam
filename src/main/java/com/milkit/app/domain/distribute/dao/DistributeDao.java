package com.milkit.app.domain.distribute.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.milkit.app.domain.distribute.Distribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DistributeDao extends JpaRepository<Distribute, Long> {

	public Distribute findByToken(String token);

/*
    @Query(value = "SELECT DISTRIBUTE_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    public Long getSeqNextVal();
*/

}
