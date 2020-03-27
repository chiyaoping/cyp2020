package com.cyp.carpark.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyp.carpark.serviceImpl.PlateRecogniseImpl;
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
	public Result upload2(@RequestParam("file") MultipartFile file) {

	//	System.out.println(parkId);
		if (file.isEmpty() || file==null) {
			List<String> responses = new ArrayList<String>();
			responses.add("�ļ�Ϊ��");
			logger.info("�ļ�Ϊ��");
			return new Result(-1, responses, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		String fileName = file.getOriginalFilename();
		@SuppressWarnings("unused")
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String filePath = "C:\\springUpload\\image\\";
		// fileName = UUID.randomUUID() + suffixName;
		File dest = new File(filePath + fileName);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
			PlateRecognise plateRecognise = new PlateRecogniseImpl();
			String img = filePath + fileName;
			logger.info(img);
			List<String> res = plateRecognise.plateRecognise(filePath + fileName);
			if (res.size() < 1 || res.contains("")) {
				logger.info("ʶ��ʧ�ܣ����绻��ͼƬ���ԣ�");
				List<String> responses = new ArrayList<String>();
				responses.add("ʶ����");
				return new Result(-1, responses, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			Result result = new Result(201, plateRecognise.plateRecognise(filePath + fileName),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			logger.info(result.toString());
			return result;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> responses = new ArrayList<String>();
		responses.add("�ϴ�ʧ��");
	//	return parkInfo;
		return new Result(-1, responses, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	
	@RequestMapping(value = "/fileUpload1")
	public String upload(@RequestParam("file") MultipartFile file,@RequestParam("id")int id,HttpServletResponse response,HttpServletRequest request) {
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
	
			try {
				file.transferTo(dest);
				PlateRecognise plateRecognise = new PlateRecogniseImpl();
				String img = filePath + fileName;
				logger.info(img);
				List<String> res = plateRecognise.plateRecognise(filePath + fileName);
				if (res.size() < 1 || res.contains("")) {
					logger.info("ʶ��ʧ�ܣ����绻��ͼƬ���ԣ�");
					
					//return Msg.fail().add("va_msg", "�������");
					response.setHeader("refresh", "6;url="+request.getContextPath()+"/index/toindex");
					return "error";
					//response.setHeader("refresh", "5;url=/index/toindex");
					//return "redirect:/index/toindex";
				}
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
				parkspaceService.changeStatus(parkId, 1);
				//return "index";
				return "redirect:/index/toindex";
				//return Msg.success();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "redirect:/index/toindex";
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
			parkspaceService.changeStatus(parkId, 1);
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
