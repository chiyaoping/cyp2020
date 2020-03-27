package com.cyp.carpark.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cyp.carpark.entity.CardType;


public interface CardtypeDao extends BaseDao<CardType>{

	List<CardType> findAllCardType();

	CardType findCardTypeByid(@Param("typeid")int typeid);
	
}
