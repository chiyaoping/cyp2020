package com.cyp.carpark.dto;

public class ChargeData {
	
	private Integer hourmoney;
	private Integer daymaxmoney;
	private Integer monthcard;
	private Integer yearcard;
	private Integer illegal;
	public Integer getDaymaxmoney() {
		return daymaxmoney;
	}

	public void setDaymaxmoney(Integer daymaxmoney) {
		this.daymaxmoney = daymaxmoney;
	}

	public Integer getHourmoney() {
		return hourmoney;
	}
	public void setHourmoney(Integer hourmoney) {
		this.hourmoney = hourmoney;
	}
	public Integer getMonthcard() {
		return monthcard;
	}
	public void setMonthcard(Integer monthcard) {
		this.monthcard = monthcard;
	}
	public Integer getYearcard() {
		return yearcard;
	}
	public void setYearcard(Integer yearcard) {
		this.yearcard = yearcard;
	}
	public Integer getIllegal() {
		return illegal;
	}
	public void setIllegal(Integer illegal) {
		this.illegal = illegal;
	}
	
}
