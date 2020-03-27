package com.cyp.carpark.service;

import com.cyp.carpark.dto.ChargeData;
import com.cyp.carpark.entity.DepotInfo;

public interface DepotInfoService {

	void update(ChargeData chargeData);

	DepotInfo findById(int i);

}
