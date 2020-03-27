package com.cyp.carpark.entity;

import java.io.Serializable;
import java.util.Date;


public class Depotcard implements Serializable{
	//ͣ��������Ϣ��
	private int id;
	//����
	private String cardnum;
	//������
	private int type;
	//���
	private double money;
	//����ʱ��
	private Date time;
	//�Ƿ��ʧ
	private int islose;
	//Υ�����
	private int illegalcount;
	//�۷�ʱ��
	private Date deductedtime;
	
	public Date getDeductedtime() {
		return deductedtime;
	}
	public void setDeductedtime(Date deductedtime) {
		this.deductedtime = deductedtime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getIslose() {
		return islose;
	}

	public void setIslose(int islose) {
		this.islose = islose;
	}
	
	public int getIllegalcount() {
		return illegalcount;
	}
	
	public void setIllegalcount(int illegalcount) {
		this.illegalcount = illegalcount;
	}
	
	
}
