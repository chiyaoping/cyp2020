package com.cyp.carpark.service;

import java.util.List;

import com.cyp.carpark.dto.IncomeData;
import com.cyp.carpark.entity.Income;


public interface IncomeService {

	void save(Income income);

	List<IncomeData> findAllIncome(int page,int size,String content,String startTime,String endTime,int num);

	Income findById(Integer id);

	int findAllIncomeCount(String content,String startTime,String endTime,int num);

	void updateCardnum(String cardnum, String cardnumNew);

	List<IncomeData> findAllIncome(String content, String startTime, String endTime, Integer num);
	
	int findPayByType(int type);
}
