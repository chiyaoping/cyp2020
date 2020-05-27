package com.cyp.carpark.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyp.carpark.dto.ChargeData;
import com.cyp.carpark.dto.CouponData;
import com.cyp.carpark.dto.DepotcardManagerData;
import com.cyp.carpark.dto.EmailData;
import com.cyp.carpark.dto.IncomeData;
import com.cyp.carpark.dto.ParkinfoallData;
import com.cyp.carpark.entity.DepotInfo;
import com.cyp.carpark.entity.Depotcard;
import com.cyp.carpark.entity.IllegalInfo;
import com.cyp.carpark.entity.IncomeCharts;
import com.cyp.carpark.entity.NowParkspace;
import com.cyp.carpark.entity.ParkSpace;
import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.CouponService;
import com.cyp.carpark.service.DepotInfoService;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.EmailService;
import com.cyp.carpark.service.IllegalInfoService;
import com.cyp.carpark.service.IncomeService;
import com.cyp.carpark.service.ParkinfoallService;
import com.cyp.carpark.service.ParkspaceService;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Constants;
import com.cyp.carpark.utils.Msg;
import com.cyp.carpark.utils.PageUtil;


@Controller
public class IndexController {

	@Autowired
	private UserService userService;
	@Autowired
	private ParkspaceService parkspaceService;
	@Autowired
	private DepotcardService depotcardService;
	@Autowired
	private ParkinfoallService parkinfoallService;
	@Autowired
	private IllegalInfoService illegalInfoService;
	@Autowired
	private IncomeService incomeService;
	@Autowired 
	private CouponService couponService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private DepotInfoService depotInfoService;
	
	@RequestMapping("/index/toindex")
	public String toIndex(Model model,HttpSession session,@RequestParam(value="tag",required=false) Integer tag,@RequestParam(value="page",required=false) Integer page)
	{
		if(tag==null)
		{
			tag=0;
		}
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page-=1;
		}
		PageUtil<ParkSpace> pageUtil=new PageUtil<ParkSpace>();
		pageUtil.setCurrent(page);
		pageUtil.setTag(tag);
		User user1=(User) session.getAttribute("user");
		List<ParkSpace> list=new ArrayList<ParkSpace>();
		int count=0;
		int countPage=0;
		if(user1!=null)
		{
			if(user1.getRole()==1)
			{
				if(tag==0)
				{
				list=parkspaceService.findAllParkspace(page*10,Constants.PAGESIZE);
				}
				else
				{
				list=parkspaceService.findParkspaceByTag(tag,page*10,Constants.PAGESIZE);
				}
				count=parkspaceService.findAllParkspaceCount(tag);
			}else if(user1.getRole()==2)
			{
				if(tag==0)
				{
				list=parkspaceService.findAllParkspace(page*10,Constants.PAGESIZE);
				}
				else
				{
				list=parkspaceService.findParkspaceByTag(tag,page*10,Constants.PAGESIZE);
				}
				count=parkspaceService.findAllParkspaceCount(tag);
			}else {
				
			}
			countPage=count/10;
			if(count%10!=0)
			{
				countPage+=1;
			}
			pageUtil.setCountPage(countPage);
			pageUtil.setCount(count);
			pageUtil.setPages(list);
			model.addAttribute("parkspaces", pageUtil);
			return "index";
		}else{
			return "login";
		}
	}
	
	@RequestMapping("/index/findAllUser")
	public String findAllUser(Model model, HttpSession session,@RequestParam(value="tag",required=false) Integer tag,@RequestParam(value="page",required=false) Integer page)
	{
		if(tag==null)
		{
			tag=0;
		}
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page-=1;
		}
		List<User> users=null;
		User user1 = (User) session.getAttribute("user");
		PageUtil<User> pageUtil=new PageUtil<User>();
		int count=0;
		int countPage=0;
		if (user1 != null) {
			if (user1.getRole() == 1) {
				users=userService.findUsersByRole(tag.intValue(),page*10,Constants.PAGESIZE);
				count=userService.findAllUserCount(tag);
			} else if (user1.getRole() == 2) {
				users=userService.findUsersByRoleMan(tag.intValue(),page*10,Constants.PAGESIZE);
				count=userService.findAllUserCountMan(tag);
			} else if (user1.getRole() == 3) {
				users=new ArrayList<User>();
				user1=userService.findUserById(user1.getId());
				users.add(user1);
				count=1;
			} else if (user1.getRole() == 4) {

			} else {

			}
		}
		countPage=count/10;
		if(count%10!=0)
		{
			countPage+=1;
		}
		pageUtil.setCountPage(countPage);
		pageUtil.setCount(count);
		pageUtil.setPages(users);
		model.addAttribute("users", pageUtil);
		return "user";
	}
	
	@RequestMapping("/index/findAllDepot")
	public String findAllDepot(Model model, HttpSession session,@RequestParam(value="page",required=false) Integer page,@RequestParam(value="name",required=false) String name)
	{

		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		if(name==null)
		{
			name="";
		}
		List<ParkinfoallData> parkinfoallDatas=null;
		PageUtil<ParkinfoallData> pageUtil=new PageUtil<ParkinfoallData>();
		User user1 = (User) session.getAttribute("user");
		int count=0;
		int countPage=0;
		if (user1 != null) {
			if (user1.getRole() == 1) {
				parkinfoallDatas=parkinfoallService.findAllParkinfoallByLike(page*10,Constants.PAGESIZE,name);
				count=parkinfoallService.findAllParkinfoallCount(name);
			} else if (user1.getRole() == 2) {
				parkinfoallDatas=parkinfoallService.findAllParkinfoallByLike(page*10,Constants.PAGESIZE,name);
				count=parkinfoallService.findAllParkinfoallCount(name);
			} else if (user1.getRole() == 3) {
				Depotcard depotcard=depotcardService.findByCardid(user1.getCardid());
				parkinfoallDatas=parkinfoallService.findByCardNumByPage(page*10,Constants.PAGESIZE,depotcard.getCardnum(),name);
				List<ParkinfoallData> parkinfoallDatas1=parkinfoallService.findByCardNum(depotcard.getCardnum(),name);
				count=parkinfoallDatas1.size();
			} else if (user1.getRole() == 4) {

			} else {

			}
		}
		countPage=count/10;
		if(count%10!=0)
		{
			countPage++;
		}
		pageUtil.setExtra(name);
		pageUtil.setPages(parkinfoallDatas);
		pageUtil.setCount(count);
		pageUtil.setCurrent(page);
		pageUtil.setCountPage(countPage);
		model.addAttribute("parkinfoallDatas", pageUtil);
		return "depot";
	}
	
	@RequestMapping("/index/findAllIllegalinfo")
	public String findAllIllegalinfo(Model model, HttpSession session,@RequestParam(value="page",required=false) Integer page,@RequestParam(value="name",required=false)String name)
	{
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		if(name==null)
		{
			name="";
		}
		List<IllegalInfo> illegalInfo=null;
		PageUtil<IllegalInfo> pageUtil=new PageUtil<IllegalInfo>();
		User user1 = (User) session.getAttribute("user");
		int count=0;
		int countPage=0;
		if (user1 != null) {
			if (user1.getRole() == 1) {
				illegalInfo=illegalInfoService.findAllIllegalInfo(page*10,Constants.PAGESIZE,name);
				count=illegalInfoService.findAllIllegalInfoCount(name);
			} else if (user1.getRole() == 2) {
				illegalInfo=illegalInfoService.findAllIllegalInfo(page*10,Constants.PAGESIZE,name);
				count=illegalInfoService.findAllIllegalInfoCount(name);
			} else if (user1.getRole() == 3) {
				illegalInfo=illegalInfoService.findByUid(user1.getId());
				count=illegalInfo.size();
			} 
		}
		countPage=count/10;
		if(count%10!=0)
		{
			countPage++;
		}
		pageUtil.setExtra(name);
		pageUtil.setPages(illegalInfo);
		pageUtil.setCount(count);
		pageUtil.setCountPage(countPage);
		pageUtil.setCurrent(page);
		model.addAttribute("illegalInfo", pageUtil);
		return "illegalinfo";
	}
	
	@RequestMapping("/index/toDepotcardIndex")
	public String findAllDepotcard(Model model, HttpSession session,@RequestParam(value="cardnum",required=false)String cardnum,@RequestParam(value="page",required=false) Integer page)
	{
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		List<DepotcardManagerData> depotcardManagerDatas = null;
		PageUtil<DepotcardManagerData> pageUtil=new PageUtil<DepotcardManagerData>();
		int count =0;
		int countPage=0;
		User user1 = (User) session.getAttribute("user");
		if(cardnum==null)
		{
			cardnum="";
		}
		if (user1 != null) {
			if (user1.getRole() == 1) {
				depotcardManagerDatas = depotcardService.findAllDepotcard(cardnum,page.intValue()*10,Constants.PAGESIZE);
				count=depotcardService.findAllDepotcardCount(cardnum);
			} else if (user1.getRole() == 2) {
				depotcardManagerDatas = depotcardService.findAllDepotcard(cardnum,page.intValue()*10,Constants.PAGESIZE);
				count=depotcardService.findAllDepotcardCount(cardnum);
			} else if (user1.getRole() == 3) {
				depotcardManagerDatas = depotcardService.findByCardId(user1.getCardid());
				count=depotcardManagerDatas.size();
			} 
		}
		countPage=count/10;
		if(count%10>0)
		{
			countPage++;
		}
		pageUtil.setExtra(cardnum);
		pageUtil.setCurrent(page);
		pageUtil.setCount(count);
		pageUtil.setCountPage(countPage);
		pageUtil.setPages(depotcardManagerDatas);
		model.addAttribute("depotcardManagerDatas", pageUtil);
		return "depotcard";
	}
	
	@RequestMapping("/index/findAllCoupon")
	public String findAllCoupon(Model model, HttpSession session,@RequestParam(value="page",required=false) Integer page,@RequestParam(value="name",required=false) String name)
	{
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		List<CouponData> list = null;
		PageUtil<CouponData> pageUtil=new PageUtil<CouponData>();
		int count =0;
		int countPage=0;
		User user1 = (User) session.getAttribute("user");
		if(name==null)
		{
			name="";
		}
		if (user1 != null) {
			if (user1.getRole() == 1) {
				list = couponService.findAllCoupon(page.intValue()*10,Constants.PAGESIZE,name);
				count=couponService.findAllDepotcardCount(name);
			} else if (user1.getRole() == 2) {
				list = couponService.findAllCoupon(page.intValue()*10,Constants.PAGESIZE,name);
				count=couponService.findAllDepotcardCount(name);
			} else if (user1.getRole() == 3) {
				Depotcard depotcard=depotcardService.findByCardid(user1.getCardid());
				list = couponService.findAllCouponByCardNum(depotcard.getCardnum(),name);
				count=list.size();
			} else if (user1.getRole() == 4) {

			} else {

			}
		}
		countPage=count/10;
		if(count%10>0)
		{
			countPage++;
		}
		pageUtil.setExtra(name);
		pageUtil.setCurrent(page);
		pageUtil.setCount(count);
		pageUtil.setCountPage(countPage);
		pageUtil.setPages(list);
		model.addAttribute("couponDatas", pageUtil);
		return "coupon";
	}
	@RequestMapping("/index/findAllIncome")
	public String findAllIncome(Model model, HttpSession session,@RequestParam(value="page", required=false) Integer page,@RequestParam(value="startTime",required=false)String startTime,@RequestParam(value="endTime",required=false)String endTime,@RequestParam(value="content",required=false)String content,@RequestParam(value="num",required=false)Integer num)
	{
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		if(content==null)
		{
			content="";
		}
		if(startTime==null)
		{
			startTime="";
		}
		if(endTime==null)
		{
			endTime="";
		}
		if(num==null)
		{
			num=9;
		}
		List<IncomeData> incomes=null;
		List<IncomeData> incomes1=null;
		User user1 = (User) session.getAttribute("user");
		PageUtil<IncomeData> pageUtil=new PageUtil<IncomeData>();
		int count =0;
		int countPage=0;
		double countMoney=0;
		if (user1 != null) {
			if (user1.getRole() == 1) {
				incomes = incomeService.findAllIncome(page*10,Constants.PAGESIZE,content,startTime,endTime,num);
				incomes1 = incomeService.findAllIncome(content,startTime,endTime,num);
				if(incomes1.size()>0){
				for(IncomeData incomeData:incomes1)
				{
					countMoney+=incomeData.getMoney();
				}
				}
				count=incomeService.findAllIncomeCount(content,startTime,endTime,num);
				countPage=count/10;
				if(count%10!=0)
				{
					countPage++;
				}
				pageUtil.setCurrent(page);
				pageUtil.setCount(count);
				pageUtil.setCountPage(countPage);
				pageUtil.setPages(incomes);
			} 
		}
		model.addAttribute("incomes", pageUtil);
		model.addAttribute("countMoney", countMoney);
		return "income";
	}
	@RequestMapping("/index/findAllEmail")
	public String findAllEmail(Model model, HttpSession session,@RequestParam(value="page", required=false) Integer page,@RequestParam(value="content", required=false) String content,@RequestParam(value="tag", required=false) Integer tag)
	{
		if(page==null)
		{
			page=0;
		}
		if(page!=0)
		{
			page--;
		}
		if(content==null)
		{
			content="";
		}
		if(tag==null)
		{
			tag=4;
		}
		List<EmailData> emails=null;
		PageUtil<EmailData> pageUtil=new PageUtil<EmailData>();
		int count =0;
		int countPage=0;
		User user1 = (User) session.getAttribute("user");//得到当前登录的用户
		if (user1.getRole()==1||user1.getRole()==2) {
			emails=emailService.findByToId(1);
		}else {
			emails=emailService.findByToId(user1.getId());
		}
		List<EmailData> emailDatas=new ArrayList<EmailData>();
		for(EmailData emailData:emails)
		{
			if(user1.getRole()==3)
			{
				emailData.setIsRead(emailData.getUserisread());
			}
			else{
				emailData.setIsRead(emailData.getManagerisread());
			}
			User sendUser=userService.findUserById(emailData.getSendid());
			if(emailData.getToid()!=0)
			{
			User toUser=userService.findUserById(emailData.getToid());
			emailData.setToUsername(toUser.getUsername());
			}else
			{
				emailData.setToUsername("");
			}
			if(user1.getId()==emailData.getSendid())
			{
				emailData.setIsSend(1);
			}
			emailData.setSendUsername(sendUser.getUsername());
			emailDatas.add(emailData);
		}
		System.out.println(":4");
		count=emailService.findAllEmailCountByUser(user1.getId(),user1.getRole());
		countPage=count/10;
		if(count%10!=0)
			{
				countPage++;
			}
		pageUtil.setCurrent(page);
		pageUtil.setCount(count);
		pageUtil.setCountPage(countPage);
		System.out.println(":5");
		pageUtil.setPages(emailDatas);
		System.out.println(pageUtil.getPages());
		model.addAttribute("emails", pageUtil);
		model.addAttribute("tag", tag);
		return "email";
	}
	
	@RequestMapping("/index/system")
	public String system(Model model, HttpSession session)
	{
		return "system";
	}
	
	@RequestMapping("/index/incomeCharts")
	public String incomeCharts()
	{
		return "incomeCharts";
	}
	
	@RequestMapping("/index/line")
	public String line()
	{
		return "indexcopy";
	}
	
	@ResponseBody
	@RequestMapping("/index/incomeCharts1")
	public IncomeCharts incomeCharts1()
	{	
		int weixin=incomeService.findPayByType(0);
		int zhifubao=incomeService.findPayByType(1);
		int cash=incomeService.findPayByType(2);
		IncomeCharts incomeCharts=new IncomeCharts();
		incomeCharts.setCash(cash);
		incomeCharts.setWeixin(weixin);
		incomeCharts.setZhifubao(zhifubao);
		return incomeCharts;
	}

	@ResponseBody
	@RequestMapping("/index/line1")
	public NowParkspace line1() {
		int ispark=parkspaceService.findNowParkspace(1);
		int nopark=100-ispark;
		NowParkspace nowParkspace=new NowParkspace();
		nowParkspace.setIspark(ispark);
		nowParkspace.setNopark(nopark);
		return nowParkspace;
		
	}

	/**
	 * 用户退出
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/index/exit")
	public String exit(Model model, HttpSession session)
	{
		session.removeAttribute("user");
		return "login";
	}
	@RequestMapping("/index/exportIncome")
	public void exportIncome(@RequestParam(value="datetimepickerStart",required=false) String datetimepickerStart,
			@RequestParam(value="datetimepickerEnd",required=false) String datetimepickerEnd,HttpServletResponse response) {
		if (datetimepickerStart == null) {
			datetimepickerStart = "";
		}
		if (datetimepickerEnd == null) {
			datetimepickerEnd = "";
		}
		List<IncomeData> list = incomeService.findAllIncome("", datetimepickerStart, datetimepickerEnd, 9);

		// HSSFWorkbook
		HSSFWorkbook wb = new HSSFWorkbook();
		//
		HSSFSheet sheet = wb.createSheet("收入详情表");
		//
		HSSFRow row1 = sheet.createRow(0);
		//
		HSSFCell cell = row1.createCell(0);
		//
		cell.setCellValue("111");
		//
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		//
		HSSFRow row2 = sheet.createRow(1);
		//
		row2.createCell(0).setCellValue("���ƺ�");
		row2.createCell(1).setCellValue("ͣ������");
		row2.createCell(2).setCellValue("����");
		row2.createCell(3).setCellValue("���뷽ʽ");
		row2.createCell(4).setCellValue("������Դ");
		row2.createCell(5).setCellValue("����ʱ��");
		row2.createCell(6).setCellValue("ʱ��");
		row2.createCell(7).setCellValue("Υ��");
		//
		int rowsize=2;
		for(IncomeData data:list)
		{
			HSSFRow row3 = sheet.createRow(rowsize);
			row3.createCell(0).setCellValue(data.getCarnum());
			row3.createCell(1).setCellValue(data.getCardnum());
			row3.createCell(2).setCellValue(data.getMoney());
			row3.createCell(3).setCellValue(data.getMethod()==0?"�ֽ�":data.getMethod()==1?"֧����":data.getMethod()==2?"΢��":"�ۿ���");
			row3.createCell(4).setCellValue(data.getSource()==0?"��ֵ":"����");
			row3.createCell(5).setCellValue(data.getTime());
			row3.createCell(6).setCellValue(data.getDuration());
			row3.createCell(7).setCellValue(data.getIsillegal());
			rowsize++;
		}
		OutputStream output;
		try {
			output = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename=incomeDetail.xls");
			response.setContentType("application/msexcel");
			wb.write(output);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/index/setSystem")
	@ResponseBody
	public Msg setSystem(ChargeData chargeData)
	{
		Integer hourmoney=chargeData.getHourmoney();
		Integer daymaxmoney=chargeData.getDaymaxmoney();
		Integer monthcard=chargeData.getMonthcard();
		Integer yearcard=chargeData.getYearcard();
		Integer illegal=chargeData.getIllegal();
		DepotInfo depotInfo=depotInfoService.findById(1);
		if(hourmoney==null||hourmoney==0)
		{
			chargeData.setHourmoney(depotInfo.getHourmoney());
		}
		if(daymaxmoney==null||daymaxmoney==0)
		{
			chargeData.setDaymaxmoney(depotInfo.getDaymaxmoney());
		}
		if(monthcard==null||monthcard==0)
		{
			chargeData.setMonthcard(depotInfo.getMonthcard());
		}
		if(yearcard==null||yearcard==0)
		{
			chargeData.setYearcard(depotInfo.getYearcard());
		}
		if(illegal==null||illegal==0)
		{
			chargeData.setIllegal(depotInfo.getIllegal());
		}
		depotInfoService.update(chargeData);
		return Msg.success();
	}
	
}
