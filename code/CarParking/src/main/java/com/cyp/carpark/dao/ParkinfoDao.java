package com.cyp.carpark.dao;

import com.cyp.carpark.entity.ParkInfo;
import org.apache.ibatis.annotations.Param;


public interface ParkinfoDao extends BaseDao<ParkInfo>{
	//���ͣ��λ��Ϣ
	public void save(ParkInfo parkInfo);
	public ParkInfo findParkinfoByParknum(@Param("parknum")int parknum);
	public void deleteParkinfoByParkNum(@Param("parknum")int parknum);
	public ParkInfo findParkinfoByCardnum(@Param("cardnum")String cardnum);
	public void updateCardnum(@Param("cardnum")String cardnum, @Param("cardnumNew")String cardnumNew);
}
