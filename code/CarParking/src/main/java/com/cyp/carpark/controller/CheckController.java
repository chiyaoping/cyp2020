package com.cyp.carpark.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyp.carpark.dto.CouponData;
import com.cyp.carpark.dto.FormData;
import com.cyp.carpark.entity.Depotcard;
import com.cyp.carpark.entity.IllegalInfo;
import com.cyp.carpark.entity.Income;
import com.cyp.carpark.entity.ParkInfo;
import com.cyp.carpark.entity.Parkinfoall;
import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.CouponService;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.IllegalInfoService;
import com.cyp.carpark.service.IncomeService;
import com.cyp.carpark.service.ParkinfoService;
import com.cyp.carpark.service.ParkinfoallService;
import com.cyp.carpark.service.ParkspaceService;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Constants;
import com.cyp.carpark.utils.Msg;


@Controller
public class CheckController {

	@Autowired
	private ParkinfoService parkinfoservice;
	@Autowired
	private ParkspaceService parkspaceService;
	@Autowired
	private DepotcardService depotcardService;
	@Autowired 
	private UserService userService;
	@Autowired
	private IllegalInfoService illegalInfoService;
	@Autowired
	private ParkinfoallService parkinfoallService;
	@Autowired
	private IncomeService incomeService;
	@Autowired
	private CouponService couponService;
	
	static int i=0;
	@RequestMapping("/index/check/checkIn")
	@ResponseBody
	@Transactional
	public Msg checkIn(Model model, FormData data) {
		Depotcard depotcard=depotcardService.findByCardnum(data.getCardNum());
		if(data.getParkTem()!=1)
		{
		if(depotcard!=null)
		{
			if(depotcard.getIslose()==1)
			{
				return Msg.fail().add("va_msg", "该停车卡已挂失");
			}
		}else{
			return Msg.fail().add("va_msg", "停车卡不存在");
		}
		}
		parkinfoservice.saveParkinfo(data);
		parkspaceService.changeStatus(data.getId(), 1);
		return Msg.success();
	}

	/**
	 * 汽车出库
	 * @param model
	 * @param data
	 * @return
	 */
	@RequestMapping("/index/check/checkOut")
	@ResponseBody
	@Transactional
	public Msg checkOut(Model model, FormData data) {
		int pay_money=data.getPay_money();
		Date parkout=new Date();
		Parkinfoall parkinfoall=new Parkinfoall();
		ParkInfo parkInfo=parkinfoservice.findParkinfoByParknum(data.getParkNum());
		if(data.getPay_type()==9)
		{
			Depotcard depotcard=depotcardService.findByCardnum(data.getCardNum());
			IllegalInfo illegalInfo=illegalInfoService.findByCardnumParkin(data.getCardNum(),parkInfo.getParkin());
			Income income=new Income();
			List<CouponData> coupons=couponService.findAllCouponByCardNum(data.getCardNum(), "");
			if(coupons!=null&&coupons.size()>0)
			{
				couponService.deleteCoupon(coupons.get(0).getId());
			}
			depotcardService.addMoney(data.getCardNum(), 0);
			income.setMoney(pay_money);
			income.setMethod(data.getPayid());
			income.setCardnum(data.getCardNum());
			income.setCarnum(data.getCarNum());
			if(depotcard!=null)
			{
			income.setType(depotcard.getType());
			}
			if(illegalInfo!=null)
			{
				income.setIsillegal(1);
			}
			income.setSource(1);
			income.setTime(parkout);
			Date parkin=parkInfo.getParkin();
			long day=parkout.getTime()-parkin.getTime();
			long time=day/(1000*60);
			if(day%(1000*60)>0){
			time+=1;
			}
			income.setDuration(time);
			incomeService.save(income);
		}else{
			if(data.getPay_type()==9)
			{
				return Msg.fail().add("va_msg", "支付类型出错");
			}else if(data.getPay_type()==0)
			{
				Depotcard depotcard=depotcardService.findByCardnum(data.getCardNum());
				IllegalInfo illegalInfo=illegalInfoService.findByCardnumParkin(data.getCardNum(),parkInfo.getParkin());
				double money=depotcard.getMoney();
				List<CouponData> coupons=couponService.findAllCouponByCardNum(data.getCardNum(), "");
				if(coupons!=null&&coupons.size()>0)
				{
					money-=coupons.get(0).getMoney();
					couponService.deleteCoupon(coupons.get(0).getId());
				}
				money-=pay_money;
				depotcardService.addMoney(depotcard.getCardnum(),money);
				/*Income income=new Income();
				income.setMoney(pay_money);
				income.setMethod(data.getPayid());
				income.setCardnum(data.getCardNum());
				income.setCarnum(data.getCarNum());
				income.setType(depotcard.getType());
				if(illegalInfo!=null)
				{
					income.setIsillegal(1);
				}
				income.setSource(1);
				income.setTime(parkout);*/
				/*Date parkin=parkInfo.getParkin();
				long day=parkout.getTime()-parkin.getTime();
				long time=day/(1000*60);
				if(day%(1000*60)>0){
				time+=1;
				}
				income.setDuration(time);
				income.setTrueincome(1);
				incomeService.save(income);*/
			}else{

			}
		}
		parkinfoall.setCardnum(parkInfo.getCardnum());
		parkinfoall.setCarnum(parkInfo.getCarnum());
		parkinfoall.setParkin(parkInfo.getParkin());
		parkinfoall.setParknum(data.getParkNum());
		parkinfoall.setParkout(parkout);
		parkinfoall.setParktemp(parkInfo.getParktem());
		parkinfoallService.save(parkinfoall);
		parkspaceService.changeStatusByParkNum(data.getParkNum(),0);
		parkinfoservice.deleteParkinfoByParkNum(data.getParkNum());
		return Msg.success();
	}

	/**
	 * 通过停车位号查找停车位信息
	 * @param parknum
	 * @return
	 */
	@RequestMapping("/index/check/findParkinfoByParknum")
	@ResponseBody
	public Msg findParkinfoByParknum(@RequestParam("parkNum") int parknum) {
		ParkInfo parkInfo = parkinfoservice.findParkinfoByParknum(parknum);
		return Msg.success().add("parkInfo", parkInfo);
	}
	
	@RequestMapping("/index/check/findParkinfoByCardnum")
	@ResponseBody
	// ����ͣ��λ��/���ƺŲ���ͣ��λ��Ϣ
	public Msg findParkinfoByCardnum(@RequestParam("cardnum") String cardnum) {
		ParkInfo parkInfo = parkinfoservice.findParkinfoByCardnum(cardnum);
		//System.out.println("ello"+parkInfo.getId());
		if(parkInfo!=null)
		{ 
			return Msg.success().add("parkInfo", parkInfo);
		}
		return Msg.fail();
	}
	
	@RequestMapping("/index/check/findParkinfoDetailByParknum")
	@ResponseBody
	//����ͣ��λ�Ų���ͣ����ϸ��Ϣ
	public Msg findParkinfoDetailByParknum(@RequestParam("parkNum") int parknum)
	{
		ParkInfo parkInfo = parkinfoservice.findParkinfoByParknum(parknum);
		if(parkInfo==null)
		{
			return Msg.fail();
		}
		Date date=parkInfo.getParkin();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String parkin=formatter.format(date);
		System.out.println(parkInfo.toString());
		String cardnum=parkInfo.getCardnum();
		Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		int cardid=0;
		User user =null;
		if(depotcard!=null)
		{
		cardid=depotcard.getId();
		user =userService.findUserByCardid(cardid);
		}
		return Msg.success().add("parkInfo", parkInfo).add("user", user).add("parkin", parkin);
	}

	/**
	 * 违规信息提交
	 * @param data
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/index/check/illegalSubmit")
	@ResponseBody
	public Msg illegalSubmit(FormData data,HttpSession httpSession)
	{
		User currentUser=(User) httpSession.getAttribute("user");
		//System.out.println(data.getCardNum());
		ParkInfo parkInfo=parkinfoservice.findParkinfoByCardnum(data.getCardNum());
		//System.out.println(parkInfo.getParkin()+"hell");
		IllegalInfo info=new IllegalInfo();
		IllegalInfo illegalInfo=illegalInfoService.findByCardnumParkin(data.getCardNum(),parkInfo.getParkin());
		if(illegalInfo!=null)
		{
			return Msg.fail().add("va_msg", "提交失败");
		}
		info.setCardnum(data.getCardNum());
		info.setCarnum(data.getCarNum());
		String cardnum=data.getCarNum();
		//String carnum=data.getCarNum();
		//Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		//System.out.println(depotcard.getCardnum());
	//	int cardid=0;
		//User user =null;
		/*if(depotcard!=null)
		{
		int cardid=depotcard.getId();

		User user =userService.findUserByCardid(cardid);
		System.out.println(user.getId()+"1");
		info.setUid(user.getId());
		}else {
			info.setUid(i+1);
		}*/
		//info.setUsername(user.getUsername());
		info.setIllegalInfo(data.getIllegalInfo());
		
	//	info.setUsername(data.get);
	//	info.setUid(currentUser.getId());
		//info.setUid(user.getId());
		info.setUid(1);
		Date date=new Date();
		info.setTime(date);
		
		info.setParkin(parkInfo.getParkin());
		
		info.setDelete("N");
		
		try {
			
		illegalInfoService.save(info);
		
		} catch (Exception e) {
			e.printStackTrace();
			return Msg.fail().add("va_msg", "添加违规失败");
		}
		return Msg.success().add("va_msg", "添加违规成功");
	}

	/**
	 * 判断停车位是否支付成功
	 * @param parknum
	 * @return
	 */
	@RequestMapping("/index/check/ispay")
	@ResponseBody
	public Msg ispay(@RequestParam("parknum") Integer parknum)
	{
		ParkInfo parkInfo=parkinfoservice.findParkinfoByParknum(parknum.intValue());
		Date date=new Date();
		Date parkin;
		long time=0;
		long day=0;
		int illegalmoney=0;
		if(parkInfo==null)
		{
			return Msg.fail().add("type", 9);
		}
		IllegalInfo illegalInfo=illegalInfoService.findByCarnum(parkInfo.getCarnum(),parkInfo.getParkin());
		if(illegalInfo!=null)
		{
			illegalmoney=Constants.ILLEGAL;
		}
		if(StringUtils.isEmpty(parkInfo.getCardnum()))
		{
			parkin=parkInfo.getParkin();
			day=date.getTime()-parkin.getTime();
			time=day/(1000*60*60);
			if(day%(1000*60*60)>0){
			time+=1;
			}
			return Msg.success().add("money_pay", time*Constants.TEMPMONEY+illegalmoney).add("va_msg", "出库成功"+(illegalmoney>0? ",停车产生的违规是："+illegalInfo.getIllegalInfo():""));
		}
		String cardnum=parkInfo.getCardnum();
		Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		if(depotcard!=null&&depotcard.getType()==1)
		{
			double balance=depotcard.getMoney();
			int money=0;
			List<CouponData> coupons=couponService.findAllCouponByCardNum(cardnum, "");
			if(coupons!=null&&coupons.size()>0)
			{
				money=coupons.get(0).getMoney();
			}
			parkin=parkInfo.getParkin();
			day=date.getTime()-parkin.getTime();
			time=day/(1000*60*60);
			if(day%(1000*60*60)>0){
			time+=1;
			}
			if(balance+money-illegalmoney<time*Constants.HOURMONEY)
			{
			return Msg.success().add("money_pay", time*Constants.HOURMONEY+illegalmoney-money-balance).add("va_msg", "出库成功"+(illegalmoney>0? ",停车产生的违规是："+illegalInfo.getIllegalInfo():""));
			}else{
			return Msg.fail().add("type", 0).add("money_pay", time*Constants.HOURMONEY+illegalmoney-money);
			}
		}
		Date deductedtime=depotcard.getDeductedtime();
		if(depotcard.getType()>1)
		{
		day=date.getTime()-deductedtime.getTime();
		}
		if(depotcard.getType()==3){
			time=day/(1000*60*60*24*30);
		}
		if(depotcard.getType()==4){
			time=day/(1000*60*60*24*365);
		}
		if(time<1)
		{
			return Msg.fail().add("type", 1);
		}else{
			double balance=depotcard.getMoney();
			int money=0;
			List<CouponData> coupons=couponService.findAllCouponByCardNum(cardnum, "");
			if(coupons!=null&&coupons.size()>0)
			{
				money=coupons.get(0).getMoney();
			}
			parkin=parkInfo.getParkin();
			day=date.getTime()-parkin.getTime();
			time=day/(1000*60*60);
			if(day%(1000*60*60)>0){
			time+=1;
			}
			if(balance+money-illegalmoney<time*Constants.HOURMONEY)
			{
			return Msg.success().add("money_pay", time*Constants.HOURMONEY+illegalmoney-money-balance).add("va_msg", "出库成功"+(illegalmoney>0? ",停车产生的违规是："+illegalInfo.getIllegalInfo():""));
			}else{
			return Msg.fail().add("type", 0);
			}
		}
	}

}
