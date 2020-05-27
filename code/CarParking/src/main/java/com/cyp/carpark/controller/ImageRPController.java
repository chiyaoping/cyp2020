package com.cyp.carpark.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyp.carpark.serviceImpl.PlateRecogniseImpl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sun.istack.logging.Logger;
import com.cyp.carpark.dto.FormData;
import com.cyp.carpark.entity.ParkInfo;
import com.cyp.carpark.entity.Result;
import com.cyp.carpark.service.CouponService;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.IllegalInfoService;
import com.cyp.carpark.service.IncomeService;
import com.cyp.carpark.service.ParkinfoService;
import com.cyp.carpark.service.ParkinfoallService;
import com.cyp.carpark.service.ParkspaceService;
import com.cyp.carpark.service.PlateRecognise;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Base64ImageUtils;
import com.cyp.carpark.utils.HttpClientUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ImageRPController {
	
	private static final Logger logger = Logger.getLogger(ImageRPController.class);

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
	
	@RequestMapping(value = "/fileUpload")
	public String fileLpload() {

		return "upload";
	}
	@ResponseBody
	@RequestMapping(value = "/fileUpload2")
	public String upload2(@RequestParam("file") MultipartFile file,@RequestParam("id")int id,HttpServletResponse response,HttpServletRequest request) {
		int parkId=id;
		ParkInfo parkInfo=new ParkInfo();
		FormData formData=new FormData();
		System.out.println(parkId);

		String fileName = file.getOriginalFilename();
		@SuppressWarnings("unused")
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String filePath = "C:\\springUpload\\image\\";
		// fileName = UUID.randomUUID() + suffixName;
		File dest = new File(filePath + fileName);
		System.out.println("file-path:"+dest);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/index/toindex";

	}

	@RequestMapping(value = "/getCarnum")
	public String getCarnum(@RequestParam("file") MultipartFile file){
		RedirectAttributes attr = null;
		attr.addFlashAttribute("","");
		return "redirect:/index/toindex";
	}
	@RequestMapping(value = "/comeinBycarnum")
	public String comeinBycarnum(@RequestParam("carnum") String realCarnum,@RequestParam("parkid")int parkid,HttpServletResponse response,HttpServletRequest request) {
		FormData formData=new FormData();
		try {
			parkspaceService.changeStatus(parkid, 1,realCarnum);//标记停车位为已停车状态
			if (depotcardService.findCardnumByCarnum(realCarnum)!=null) {
				formData.setCardNum(depotcardService.findCardnumByCarnum(realCarnum));
				formData.setCarNum(realCarnum);
				formData.setParkNum(parkid);
				formData.setParkTem(0);
			}else {
				formData.setCardNum("");
				formData.setCarNum(realCarnum);
				formData.setParkNum(parkid);
				formData.setParkTem(1);
			}
			parkinfoservice.saveParkinfo(formData);//将停车信息保存到历史停车
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/index/toindex";
	}
	@RequestMapping(value = "/fileUpload1")
	public String upload(@RequestParam("file") MultipartFile file,@RequestParam("id")int id,HttpServletResponse response,HttpServletRequest request) {
		int parkId=id;
		ParkInfo parkInfo=new ParkInfo();
		FormData formData=new FormData();
		System.out.println(file);
	
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
			System.out.println("jsonObject:".substring(0,5));
			String realCarnum = Sreq.substring(Sreq.indexOf("number"),Sreq.indexOf("probability")).substring(9,16);
			parkspaceService.changeStatus(parkId, 1,realCarnum);
			if (depotcardService.findCardnumByCarnum(realCarnum)!=null) {
					formData.setCardNum(depotcardService.findCardnumByCarnum(realCarnum));
					formData.setCarNum(realCarnum);
					formData.setParkNum(parkId);
					formData.setParkTem(0);
				}else {
					formData.setCardNum("");
					formData.setCarNum(realCarnum);
					formData.setParkNum(parkId);
					formData.setParkTem(1);
				}
			parkinfoservice.saveParkinfo(formData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/index/toindex";
	
//			try {
//				file.transferTo(dest);
//				PlateRecognise plateRecognise = new PlateRecogniseImpl();
//				String img = filePath + fileName;
//				logger.info(img);
//				List<String> res = plateRecognise.plateRecognise(filePath + fileName);
//				if (res.size() < 1 || res.contains("")) {
//					logger.info("出错了");
//
//					//return Msg.fail().add("va_msg", "�������");
//					response.setHeader("refresh", "6;url="+request.getContextPath()+"/index/toindex");
//					return "error";
//					//response.setHeader("refresh", "5;url=/index/toindex");
//					//return "redirect:/index/toindex";
//				}
//				String carNum=res.get(0);
//				Result result = new Result(201, plateRecognise.plateRecognise(filePath + fileName),
//						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				logger.info(result.toString());
//				if (depotcardService.findCardnumByCarnum(carNum)!=null) {
//					formData.setCardNum(depotcardService.findCardnumByCarnum(carNum));
//					formData.setCarNum(carNum);
//					formData.setParkNum(parkId);
//					formData.setParkTem(0);
//				}else {
//					formData.setCardNum("");
//					formData.setCarNum(carNum);
//					formData.setParkNum(parkId);
//					formData.setParkTem(1);
//				}
//
//				parkinfoservice.saveParkinfo(formData);
//				parkspaceService.changeStatus(parkId, 1);
//				//return "index";
//				return "redirect:/index/toindex";
//				//return Msg.success();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			

	}
	
	
	@RequestMapping(value = "/fileUpload3")
	public String upload3(@RequestParam("file") MultipartFile file,@RequestParam("id")int id) throws Exception, IOException {
		int parkId=id;
		ParkInfo parkInfo=new ParkInfo();
		FormData formData=new FormData();
		System.out.println(parkId);
	
		String fileName = file.getOriginalFilename();
		@SuppressWarnings("unused")
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String filePath = "C:\\springUpload\\image\\";
		// fileName = UUID.randomUUID() + suffixName;
		File dest = new File(filePath + fileName);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
	
			file.transferTo(dest);
			PlateRecognise plateRecognise = new PlateRecogniseImpl();
			String img = filePath + fileName;
			logger.info(img);
			List<String> res = plateRecognise.plateRecognise(filePath + fileName);
			String carNum=res.get(0);
			Result result = new Result(201, plateRecognise.plateRecognise(filePath + fileName),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			logger.info(result.toString());
			if (depotcardService.findCardnumByCarnum(carNum)!=null) {
				formData.setCardNum(depotcardService.findCardnumByCarnum(carNum));
				formData.setCarNum(carNum);
				formData.setParkNum(parkId);
				formData.setParkTem(0);
			}else {
				formData.setCardNum("");
				formData.setCarNum(carNum);
				formData.setParkNum(parkId);
				formData.setParkTem(1);
			}
			
			parkinfoservice.saveParkinfo(formData);
			parkspaceService.changeStatus(parkId, 1,"null");
			//return "index";
			return "redirect:/index/toindex";
			//return Msg.success();
	
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/plateRecognise")
	public List<String> plateRecognise(@RequestParam("imgPath") String imgPath) {
		PlateRecognise plateRecognise = new PlateRecogniseImpl();
		return plateRecognise.plateRecognise(imgPath);
	}
}
