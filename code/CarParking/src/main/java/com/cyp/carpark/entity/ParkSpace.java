package com.cyp.carpark.entity;

import java.io.Serializable;


public class ParkSpace implements Serializable{
	private Integer id;
	private int parkid;
	private int status;
	private int tag;
	private String carnum;

	public String getCarnum() {
		return carnum;
	}

	public void setCarnum(String carnum) {
		this.carnum = carnum;
	}


	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getParkid() {
		return parkid;
	}
	public void setParkid(int parkid) {
		this.parkid = parkid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
