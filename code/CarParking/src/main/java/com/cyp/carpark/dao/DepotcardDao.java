package com.cyp.carpark.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cyp.carpark.dto.DepotcardManagerData;
import com.cyp.carpark.entity.Depotcard;


public interface DepotcardDao extends BaseDao<Depotcard>{

	List<DepotcardManagerData> findAllDepotcard(@Param("cardnum")String cardnum,@Param("page")int page,@Param("size")int size);
	void save(Depotcard m);
	Depotcard findByCardnum(@Param("cardnum")String cardnum);
	Depotcard findByCardid(@Param("cardid")int cardid);
	void updateDepotcardBycardnum(Depotcard depotcard);
	void deleteDepotCard(@Param("cardnum")String cardnum);
	void addMoney(@Param("cardnum")String cardnum, @Param("money")double money);
	int findAllDepotcardCount(@Param("cardnum")String cardnum);
	List<DepotcardManagerData> findByCardId(int cardid);
	void updateCardnum(@Param("cardnum")String cardnum, @Param("cardnumNew")String cardnumNew);
	String findCardnumByCarnum(String carnum);
}
