package com.cyp.carpark.dao;

import com.cyp.carpark.dto.ChargeData;
import com.cyp.carpark.entity.DepotInfo;


public interface DepotInfoDao extends BaseDao<DepotInfo>{
	public void update(ChargeData chargeData);
	public DepotInfo findById(int id);
}
