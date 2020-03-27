package com.cyp.carpark.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cyp.carpark.dto.EmailData;
import com.cyp.carpark.entity.Email;


public interface EmailDao extends BaseDao<Email>{

	void addEmial(Email email);

	Email findById(int id);
	
	List<EmailData> findByToId(int id);

	void updateManReadById(int id);

	List<EmailData> findByUserId(@Param("page")int page,@Param("size")int size,@Param("uid")int id,@Param("role")int role,@Param("content")String content,@Param("tag")Integer tag);

	int findAllEmailCountByUser(@Param("uid")int uid,@Param("role")int role);

	void updateEmail(Email email);
	
	void deleteEmail(@Param("id")int id);


}
