package com.milkit.app.domain.distributedetail.dao;

import com.milkit.app.domain.distributedetail.DistributeDetail;

public interface DistributeDetailCustomDao {

    public DistributeDetail updateReceive(Long distID, String userID) throws Exception;
    
}
