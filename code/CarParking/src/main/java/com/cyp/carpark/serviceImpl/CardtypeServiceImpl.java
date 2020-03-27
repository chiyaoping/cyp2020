package com.cyp.carpark.serviceImpl;

import java.util.List;

import com.cyp.carpark.dao.CardtypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyp.carpark.entity.CardType;
import com.cyp.carpark.service.CardtypeService;


@Service
public class CardtypeServiceImpl implements CardtypeService {

	@Autowired
	private CardtypeDao cardtypeDao;
	
	public List<CardType> findAllCardType() {
		List<CardType> cardTypes=cardtypeDao.findAllCardType();
		return cardTypes;
	}

	public CardType findCardTypeByid(int typeid) {
		return cardtypeDao.findCardTypeByid(typeid);
	}

}
