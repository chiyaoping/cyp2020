package com.cyp.carpark.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyp.carpark.dao.DepotInfoDao;
import com.cyp.carpark.dto.ChargeData;
import com.cyp.carpark.entity.DepotInfo;
import com.cyp.carpark.service.DepotInfoService;


@Service
public class DepotInfoServiceImpl implements DepotInfoService{

	@Autowired 
	private DepotInfoDao depotInfoDao;
	public void update(ChargeData chargeData) {
		depotInfoDao.update(chargeData);
	}
	public DepotInfo findById(int id) {
		return depotInfoDao.findById(id);
	}

}
