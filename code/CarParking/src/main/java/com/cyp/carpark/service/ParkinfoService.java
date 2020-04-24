package com.cyp.carpark.service;

import com.cyp.carpark.dto.FormData;
import com.cyp.carpark.entity.ParkInfo;


public interface ParkinfoService {
	public void saveParkinfo(FormData data);
	public ParkInfo findParkinfoByParknum(int parknum);
	public void deleteParkinfoByParkNum(int parkNum);
	public ParkInfo findParkinfoByCardnum(String cardnum);
	public ParkInfo findParkinfoByCarnum(String carnum);
	public void updateCardnum(String cardnum, String cardnumNew);
}
