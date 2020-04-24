package com.cyp.carpark.dao;

import com.cyp.carpark.entity.ParkInfo;
import org.apache.ibatis.annotations.Param;


public interface ParkinfoDao extends BaseDao<ParkInfo>{
	public void save(ParkInfo parkInfo);
	public ParkInfo findParkinfoByParknum(@Param("parknum")int parknum);
	public void deleteParkinfoByParkNum(@Param("parknum")int parknum);
	public ParkInfo findParkinfoByCardnum(@Param("cardnum")String cardnum);
	public ParkInfo findParkinfoByCarnum(@Param("carnum")String carnum);
	public void updateCardnum(@Param("cardnum")String cardnum, @Param("cardnumNew")String cardnumNew);
}
