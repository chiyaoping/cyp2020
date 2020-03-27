package com.cyp.carpark.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyp.carpark.entity.Depotcard;
import com.cyp.carpark.entity.ParkInfo;
import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.DepotcardService;
import com.cyp.carpark.service.ParkinfoService;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Msg;


@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private DepotcardService depotcardService;
	@Autowired
	private ParkinfoService parkinfoService;

		@ResponseBody
		@RequestMapping("/index/user/checkUsername")
		public Msg checkUsername(@RequestParam("username")String username){
			System.out.println("username:"+username);
			User user=userService.findUserByUsername(username);
			if(user==null)
			{
				return Msg.fail().add("va_msg", "用户名不存在");
			}
			return Msg.success();
		}
		
		//添加用户
		@ResponseBody
		@RequestMapping("/index/user/addUser")
		public Msg addUser(User user){
			user.setSex("男");
			user.setName(user.getUsername());
			userService.save(user);
			user=userService.findUserByUsername(user.getUsername());
			if(user==null)
			{
				return Msg.fail().add("va_msg", "添加用户失败");
			}
				return Msg.success().add("va_msg", "添加用户成功");
		}
		
		//查找user
		@ResponseBody
		@RequestMapping("/index/user/findUserById")
		public Msg findUserById(@RequestParam("uid")Integer uid,HttpSession httpSession)
		{
			User user=userService.findUserById(uid.intValue());
			if(user==null)
			{
				return Msg.fail().add("va_msg", "用户名不存在");
			}else
			{
				User currentUser=(User) httpSession.getAttribute("user");
				return Msg.success().add("user",user).add("role", currentUser.getRole());
			}
			
		}
		
		//修改user
		@ResponseBody
		@RequestMapping("/index/user/editUser")
		public Msg editUser(User user){
			int uid=user.getId();
			User temUser=userService.findUserById(uid);
			if(user.getRole()==0)
			{
				user.setRole(temUser.getRole());
			}
			user.setPassword(temUser.getPassword());
			user.setCardid(temUser.getCardid());
			try {
						userService.update(user);
			} catch (Exception e) {
				return Msg.fail().add("va_msg", "修改失败");
			}
				return Msg.success().add("va_msg", "修改成功");
		}
		
		//删除user
		@ResponseBody
		@RequestMapping("/index/user/deleteUser")
		@Transactional
		public Msg deleteUser(@RequestParam("uid") Integer uid)
		{
			User user=userService.findUserById(uid);
			if(user!=null)
			{
				int cardid=user.getCardid();
				if(cardid!=0)
				{
					Depotcard depotcard=depotcardService.findByCardid(cardid);
					String cardnum=depotcard.getCardnum();
					ParkInfo parkInfo=parkinfoService.findParkinfoByCardnum(cardnum);
					if(parkInfo!=null)
					{
						return Msg.fail().add("va_msg", "停车信息不为空。");
					}else{
						depotcardService.deleteDepotCard(cardnum);
					}
				}
				userService.delUserById(uid.intValue());
				return Msg.success().add("va_msg", "删除成功");
			}else{
				return Msg.fail().add("va_msg", "删除失败");
			}
		}
				
}
