package com.cyp.carpark.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyp.carpark.dao.ParkinfoDao;
import com.cyp.carpark.dto.FormData;
import com.cyp.carpark.entity.ParkInfo;
import com.cyp.carpark.service.ParkinfoService;


@Service
public class ParkinfoServiceImpl implements ParkinfoService {

	@Autowired
	private ParkinfoDao parkinfoDao;
	public void saveParkinfo(FormData data) {
		Date parkin=new Date();
		ParkInfo parkInfo=new ParkInfo();
		parkInfo.setParknum(data.getParkNum());
		parkInfo.setCarnum(data.getCarNum());
		parkInfo.setCardnum(data.getCardNum());
		parkInfo.setParktem(data.getParkTem());
		parkInfo.setParkin(parkin);
		parkinfoDao.save(parkInfo);
	}
	public ParkInfo findParkinfoByParknum(int parknum) {
		return parkinfoDao.findParkinfoByParknum(parknum);
	}
	public void deleteParkinfoByParkNum(int parkNum) {
		parkinfoDao.deleteParkinfoByParkNum(parkNum);
	}
	public ParkInfo findParkinfoByCardnum(String cardnum) {
		return parkinfoDao.findParkinfoByCardnum(cardnum);
	}
	public ParkInfo findParkinfoByCarnum(String carnum) {
		return parkinfoDao.findParkinfoByCarnum(carnum);
	}
	public void updateCardnum(String cardnum, String cardnumNew) {
		parkinfoDao.updateCardnum(cardnum,cardnumNew);
	}
}
