package com.cyp.carpark.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyp.carpark.dto.CouponData;
import com.cyp.carpark.entity.Coupon;
import com.cyp.carpark.entity.Depotcard;
import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.CouponService;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Msg;


@Controller
public class CouponController {
	
	@Autowired
	private CouponService couponService;
	@Autowired
	private UserService userService;
	@Autowired
	private DepotcardService depotcardService;
	
	@ResponseBody
	@RequestMapping("/index/coupon/findCouponById")
	public Msg findCouponById(@RequestParam("id") Integer id)
	{
		Coupon coupon=couponService.findCouponById(id.intValue());
		if(coupon==null)
		{
			return Msg.fail().add("va_msg", "该优惠券不存在");
		}
		return Msg.success().add("coupon", coupon);
	}
	@ResponseBody
	@RequestMapping("/index/coupon/deleteCoupon")
	public Msg deleteCoupon(@RequestParam("id") Integer id)
	{
		Coupon coupon=couponService.findCouponById(id.intValue());
		if(coupon==null)
		{
			return Msg.fail().add("va_msg", "删除失败");
		}else{
			couponService.deleteCoupon(id);
			return Msg.success().add("va_msg", "删除成功");
		}
	}
	
	//设置优惠券
	@ResponseBody
	@RequestMapping("/index/coupon/setCoupon")
	public Msg setCoupon(CouponData couponData)
	{
		int money=couponData.getMoney();
		String cardnum=couponData.getCardnum();
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		Coupon coupon=new Coupon();
		coupon.setCouponNum(uuid);
		coupon.setCardnum(cardnum);
		coupon.setMoney(money);
		coupon.setTime(new Date());
		couponService.addCoupon(coupon);
		return Msg.success().add("va_msg", "设置优惠券成功");
//		if(userService.findAllUserCount(3)<count)
//		{
//			return Msg.fail().add("va_msg", "您没有权限设置优惠券。");
//		}

//		List<User> list=userService.finAllUserByRole(3);
//		Set<User> userSet=new HashSet<User>();
//		for(User user:list)
//		{
//			userSet.add(user);
//		}
//		Iterator<User> it = userSet.iterator();
//		int c=0;
//		try {
//			while (it.hasNext()) {
//				if(c>=count)
//				{
//					break;
//				}
//				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//				User user = it.next();
//				Depotcard depotcard=depotcardService.findByCardid(user.getCardid());
//				Coupon coupon=new Coupon();
//				coupon.setCouponNum(uuid);
//				coupon.setCardnum(depotcard.getCardnum());
//				coupon.setMoney(money);
//				coupon.setTime(new Date());
//				couponService.addCoupon(coupon);
//				c++;
//			}
//		} catch (Exception e) {
//			return Msg.fail().add("va_msg", "设置优惠券失败");
//		}

	}
}
