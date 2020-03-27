package com.cyp.carpark.service;

import java.util.List;

import com.cyp.carpark.entity.CardType;


public interface CardtypeService {

	List<CardType> findAllCardType();

	CardType findCardTypeByid(int typeid);

}
