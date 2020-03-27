package com.cyp.carpark.serviceImpl;

import java.util.List;

import com.cyp.carpark.dao.CouponDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyp.carpark.dto.CouponData;
import com.cyp.carpark.entity.Coupon;
import com.cyp.carpark.service.CouponService;


@Service
public class CouponServiceImpl implements CouponService {
	@Autowired
	private CouponDao couponDao;
	public List<CouponData> findAllCoupon(int page, int size, String name) {
		return couponDao.findAllCoupon(page,size,name);
	}
	public int findAllDepotcardCount(String name) {
		return couponDao.findAllDepotcardCount(name);
	}
	public Coupon findCouponById(int id) {
		return couponDao.findCouponById(id);
	}
	public void deleteCoupon(Integer id) {
		couponDao.deleteCoupon(id);
	}
	public List<CouponData> findAllCouponByCardNum(String cardnum,String name) {
		return couponDao.findAllCouponByCardNum(cardnum,name);
	}
	public void updateCardnum(String cardnum, String cardnumNew) {
		couponDao.updateCardnum(cardnum,cardnumNew);
	}
	public void addCoupon(Coupon coupon) {
		couponDao.addCoupon(coupon);
	}

}
