package com.cyp.carpark.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyp.carpark.dto.CouponData;
import com.cyp.carpark.entity.ParkInfo;
import com.cyp.carpark.dto.DepotcardManagerData;
import com.cyp.carpark.entity.CardType;
import com.cyp.carpark.entity.Depotcard;
import com.cyp.carpark.entity.Income;
import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.CardtypeService;
import com.cyp.carpark.service.CouponService;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.IllegalInfoService;
import com.cyp.carpark.service.IncomeService;
import com.cyp.carpark.service.ParkinfoService;
import com.cyp.carpark.service.ParkinfoallService;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Constants;
import com.cyp.carpark.utils.Msg;

@Controller
public class CardController {
	@Autowired
	private DepotcardService depotcardService;
	@Autowired
	private CardtypeService cardtypeService;
	@Autowired
	private UserService userService;
	@Autowired
	private ParkinfoallService parkinfoallService;
	@Autowired
	private IncomeService incomeService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private IllegalInfoService illegalInfoService;
	@Autowired 
	private ParkinfoService parkinfoService;
	
	@ResponseBody
	@RequestMapping("/index/card/findAllCardType")
	public Msg findAllCardType(){
		List<CardType> cardTypes=cardtypeService.findAllCardType();
		return Msg.success().add("cardTypes", cardTypes);
	}
	/*
	添加停车卡
	 */
	@ResponseBody
	@RequestMapping("/index/card/addDepotCard")
	@Transactional
	public Msg addDepotCard(DepotcardManagerData depotcardManagerData){
		if(Integer.parseInt(depotcardManagerData.getType())!=1){
			depotcardManagerData.setDeductedtime(new Date());
		}
		Depotcard depotcard=depotcardService.save(depotcardManagerData);
		double money=depotcardManagerData.getMoney();
	//	System.out.println(money);
		Income income=new Income();
		if (depotcard==null) {
			return Msg.fail().add("va_msg", "添加停车卡失败");
		}
		int type=Integer.parseInt(depotcardManagerData.getType());
		if(type==2){
			money=depotcard.getMoney();
			money-=Constants.MONTHCARD;
			depotcard.setMoney(money);
			depotcardService.updateDepotcardBycardnum(depotcard);
			income.setMoney(Constants.MONTHCARD);
		}
		if (type==3) {
			money=depotcard.getMoney();
			money-=Constants.YEARCARD;
			depotcard.setMoney(money);
			depotcardService.updateDepotcardBycardnum(depotcard);
			income.setMoney(Constants.YEARCARD);
		}
		income.setCardnum(depotcard.getCardnum());
		income.setTime(new Date());
		income.setMoney(money);
		income.setType(type);
		income.setMethod(depotcardManagerData.getPayid());
		income.setSource(0);
		incomeService.save(income);
		userService.saveByaddDepotCard(depotcardManagerData.getUsername(), depotcardManagerData.getName(), depotcard.getId());
			return Msg.success().add("depotcard", depotcard).add("username", depotcardManagerData.getUsername());	
	}

	/**
	 * 通过停车卡ID查找停车卡
	 * @param cardnum
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/card/findDepotCardByCardnum")
	public Msg findDepotCardnum(@RequestParam("cardnum")String cardnum,HttpSession session){
		User currentUser=(User) session.getAttribute("user");
		Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		if (depotcard==null) {
			return Msg.fail();
		}
		int typeid=depotcard.getType();
		int cardid=depotcard.getId();
		User user=userService.findUserByCardid(cardid);
		CardType cardType=cardtypeService.findCardTypeByid(typeid);
		List<CardType> cardTypes=cardtypeService.findAllCardType();
		return Msg.success().add("depotcard", depotcard).add("cardType", cardType)
				.add("cardTypes", cardTypes).add("user", user).add("user_role", currentUser.getRole());
	}

	/**
	 * 显示停车卡信息
	 * @param depotcardManagerData
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/card/alertDepotCard")
	public Msg alertDepotCard(DepotcardManagerData depotcardManagerData){
		Depotcard depotcard=depotcardService.findByCardnum(depotcardManagerData.getCardnum());
		if (depotcardManagerData.getType()==null) {
			depotcardManagerData.setType(Integer.toString(depotcard.getType()));
		}
		if (depotcardManagerData.getIslose()!=depotcard.getIslose()
				||Integer.parseInt(depotcardManagerData.getType())!=depotcard.getType()) {
			depotcard.setIslose(depotcardManagerData.getIslose());
			depotcard.setType(Integer.parseInt(depotcardManagerData.getType()));
			depotcardService.updateDepotcardBycardnum(depotcard);
		}else{
			return Msg.fail();
		}
		return Msg.success();
	}

	/**
	 * 删除停车卡
	 * @param cardnum
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/card/deleteDepotCard")
	@Transactional
	public Msg deleteDepotCard(@RequestParam("cardnum")String cardnum)
	{
		Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		int cardid=depotcard.getId();
		ParkInfo parkInfo=parkinfoService.findParkinfoByCardnum(cardnum);
		//����ͣ������ɾ
		if(parkInfo!=null)
		{
			return Msg.fail().add("va_msg", "�г�����ͣ��������ɾ����");
		}
		userService.deleteUserByCardid(cardid);
		depotcardService.deleteDepotCard(cardnum);
		return Msg.success();
	}

	/**
	 * 查找优惠券
	 * @param cardnum
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/card/findCoupon")
	public Msg findCoupon(@RequestParam("cardnum")String cardnum)
	{
		List<CouponData> list=couponService.findAllCouponByCardNum(cardnum, "");
		if(list!=null&&list.size()>0)
		{
			return Msg.success().add("val", list.get(0).getMoney());
		}
		return Msg.fail();
	}


	/**
	 *
	 * @param depotcardManagerData
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/card/rechargeDepotCardSubmit")
	public Msg rechargeDepotCardSubmit(DepotcardManagerData depotcardManagerData){
		Depotcard depotcard=depotcardService.findByCardnum(depotcardManagerData.getCardnum());
		Income income=new Income();
		if(depotcard==null)
		{
			return Msg.fail().add("va_msg", "停车卡不存在");
		}
		double moneymore=depotcard.getMoney()+depotcardManagerData.getMoney();
		double money=depotcardManagerData.getMoney();
		List<CouponData> list=couponService.findAllCouponByCardNum(depotcardManagerData.getCardnum(), "");
		if(list!=null&&list.size()>0)
		{
			couponService.deleteCoupon(list.get(0).getId());
		}
		try {
			depotcardService.addMoney(depotcardManagerData.getCardnum(),moneymore);
		} catch (Exception e) {
			return Msg.fail().add("va_msg", "出错了");
		}
		income.setCardnum(depotcardManagerData.getCardnum());
		income.setType(depotcard.getType());
		income.setSource(0);
		income.setMethod(depotcardManagerData.getPayid());
		income.setMoney(money);
		income.setTime(new Date());
		incomeService.save(income);
		return Msg.success();
	}
	
	@ResponseBody
	@RequestMapping("/index/card/changeLoseCard")
	@Transactional
	public Msg changeLoseCard(DepotcardManagerData depotcardManagerData)
	{
		String cardnum=depotcardManagerData.getCardnum();
		Depotcard depotcard=depotcardService.findByCardnum(cardnum);
		User user=userService.findUserByCardid(depotcard.getId());
		if(StringUtils.isEmpty(cardnum))
		{
			return Msg.fail();
		}
		Date date=new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String trans=formatter.format(date);
		String dateStr=trans.replaceAll(" ", "").replaceAll("-", "");
		String cardnumNew=user.getUsername()+dateStr;
		Depotcard depotcardNew=depotcardService.findByCardnum(cardnumNew);
		if(depotcardNew!=null)
		{
			return Msg.fail();
		}
		depotcardService.updateCardnum(cardnum,cardnumNew);
		couponService.updateCardnum(cardnum,cardnumNew);
		illegalInfoService.updateCardnum(cardnum,cardnumNew);
		incomeService.updateCardnum(cardnum,cardnumNew);
		parkinfoService.updateCardnum(cardnum,cardnumNew);
		parkinfoallService.updateCardnum(cardnum,cardnumNew);
		depotcardNew=depotcardService.findByCardnum(cardnumNew);
		depotcardNew.setIslose(0);
		depotcardService.updateDepotcardBycardnum(depotcardNew);
		return Msg.success();
	}
	@ResponseBody
	@RequestMapping("/index/card/isAlertType")
	public Msg isAlertType(DepotcardManagerData depotcardManagerData)
	{
		Depotcard depotcard=depotcardService.findByCardnum(depotcardManagerData.getCardnum());
		if(depotcardManagerData.getType()==null)
		{
			depotcardManagerData.setType(Integer.toString(depotcard.getType()));
		}
		if(depotcard.getType()!=Integer.parseInt(depotcardManagerData.getType()))
		{
			if(Integer.parseInt(depotcardManagerData.getType())>1)
			{
				double money=depotcard.getMoney();
				List<CouponData> listCou=couponService.findAllCouponByCardNum(depotcard.getCardnum(), "");
				if(listCou!=null&&listCou.size()>0)
				{
					money+=listCou.get(0).getMoney();
				}
				if(Integer.parseInt(depotcardManagerData.getType())==2)
				{
					
					if(money<Constants.MONTHCARD)
					{
						return Msg.fail().add("money_pay", Constants.MONTHCARD-money);
					}else{
						return Msg.success().add("money_pay", Constants.MONTHCARD);
					}
				}else{
					if(money<Constants.YEARCARD)
					{
						return Msg.fail().add("money_pay", Constants.YEARCARD-money);
					}else{
						return Msg.success().add("money_pay", Constants.MONTHCARD);
					}
				}
			}
		}
		return Msg.success().add("money_pay", 0);
	}
	
	
	
	
}
