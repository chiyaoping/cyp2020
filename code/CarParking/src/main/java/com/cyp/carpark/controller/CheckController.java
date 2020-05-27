package com.cyp.carpark.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.cyp.carpark.utils.Base64ImageUtils;
import com.cyp.carpark.utils.HttpClientUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.springframework.web.multipart.MultipartFile;


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

	/**
	 * 手动入库
	 * @param model
	 * @param data
	 * @return
	 */
	@RequestMapping("/index/check/checkIn")
	@ResponseBody
	@Transactional
	public Msg checkIn(Model model, FormData data) {

		if(data.getParkTem()!=1)
		{
			Depotcard depotcard=depotcardService.findByCardnum(data.getCardNum());
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
		System.out.println(model);
		parkinfoservice.saveParkinfo(data);
		parkspaceService.changeStatus(data.getId(), 1,data.getCarNum());
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
			IllegalInfo illegalInfo=illegalInfoService.findByCarnum(data.getCarNum(),parkInfo.getParkin());
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
				IllegalInfo illegalInfo=illegalInfoService.findByCarnum(data.getCarNum(),parkInfo.getParkin());
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

	/**
	 * 通过车牌号查找停车信息
	 * @param carnum
	 * @return
	 */
	@RequestMapping("/index/check/findParkinfoByCarnum")
	@ResponseBody
	public Msg findParkinfoByCadnum(@RequestParam("carnum") String carnum) {
		System.out.println("this is bycarnum");
		ParkInfo parkInfo = parkinfoservice.findParkinfoByCarnum(carnum);
		//System.out.println("ello"+parkInfo.getId());
		if(parkInfo!=null)
		{
			return Msg.success().add("parkInfo", parkInfo);
		}
		return Msg.fail();
	}
	/**
	 * 通过停车卡查找停车信息
	 * @param cardnum
	 * @return
	 */
	@RequestMapping("/index/check/findParkinfoByCardnum")
	@ResponseBody
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
	 * @return
	 */
	@RequestMapping("/index/check/illegalSubmit")
	@ResponseBody
	public Msg illegalSubmit(FormData data)
	{
		System.out.println("this is ill sub!");
		ParkInfo parkInfo=parkinfoservice.findParkinfoByCarnum(data.getCarNum());
		IllegalInfo info=new IllegalInfo();
		IllegalInfo illegalInfo=illegalInfoService.findByCarnum(data.getCarNum(),parkInfo.getParkin());
		if(illegalInfo!=null)
		{
			return Msg.fail().add("va_msg", "已经添加过违规");
		}
		info.setCardnum(data.getCardNum());
		info.setCarnum(data.getCarNum());
		info.setIllegalInfo(data.getIllegalInfo());
		info.setUid(1);
		Date date=new Date();
		info.setTime(date);
		info.setParkin(parkInfo.getParkin());
		info.setDelete("N");
		try {
			
		illegalInfoService.save(info);
		
			}
		catch (Exception e) {
			e.printStackTrace();
			return Msg.fail().add("va_msg", "添加违规失败");
		}
		return Msg.success().add("va_msg", "添加违规成功");
	}

	/**
	 * 自动添加违规信息
	 * @param file
	 * @return
	 */
	@RequestMapping("/index/check/autoillegalSubmit")
	public String autoillegalSubmit(@RequestParam("file") MultipartFile file,@RequestParam("illegalInfo") String illegalInfo)
	{
		System.out.println(illegalInfo);
		String fileName = file.getOriginalFilename();
		@SuppressWarnings("unused")
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String filePath = "C:\\springUpload\\image\\";
		// fileName = UUID.randomUUID() + suffixName;
		File dest = new File(filePath + fileName);
		System.out.println("file-path:"+dest.toString());
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String token = "24.2fdf50fc57f6f37ebab5974f8193bf22.2592000.1592396491.282335-19309228";
		String Filepath = dest.toString();
		String image = Base64ImageUtils.GetImageStrFromPath(Filepath);
		String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate?access_token="+token;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		Map<String, String> bodys = new HashMap<String, String>();
		bodys.put("image", image);
		try {
			CloseableHttpResponse response2 =  HttpClientUtils.doHttpsPost(url,headers,bodys);
			String Sreq = HttpClientUtils.toString(response2);
			System.out.println(HttpClientUtils.toString(response2));
			String realCarnum = Sreq.substring(Sreq.indexOf("number"),Sreq.indexOf("probability")).substring(9,16);

			ParkInfo parkInfo=parkinfoservice.findParkinfoByCarnum(realCarnum);
			IllegalInfo info=new IllegalInfo();
			IllegalInfo illegalInfoall=illegalInfoService.findByCarnum(realCarnum,parkInfo.getParkin());
			if(illegalInfoall!=null)
			{
//				return Msg.fail().add("va_msg", "已经添加过违规");
			}
//			info.setCardnum(data.getCardNum());
			info.setCarnum(realCarnum);
			info.setIllegalInfo(illegalInfo);
			info.setUid(1);
			Date date=new Date();
			info.setTime(date);
			info.setParkin(parkInfo.getParkin());
			info.setDelete("N");
			try {

				illegalInfoService.save(info);
//				return Msg.success().add("va_msg", "添加违规成功");
				return "illegalinfo";
			}
			catch (Exception e) {
				e.printStackTrace();
//				return Msg.fail().add("va_msg", "添加违规失败");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return Msg.success().add("va_msg", "添加违规成功");
		return "illegalinfo";
	}
	/**
	 * 计算停车费，判断停车位是否支付成功
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
			System.out.println("day："+day);
			time=day/(1000*60*60);
			System.out.println("time："+time);
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
			System.out.println("day："+day);
			time=day/(1000*60*60);
			System.out.println("time："+time);
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
