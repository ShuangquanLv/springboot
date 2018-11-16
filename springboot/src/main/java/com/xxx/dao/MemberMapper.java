package com.xxx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xxx.model.Member;

@Mapper
public interface MemberMapper {
    int deleteByPrimaryKey(String memid);

	int insert(Member record);

	int insertSelective(Member record);

	//@Select("select * from p2p_member where memid = #{memid}")
	Member selectByPrimaryKey(String memid);

	int updateByPrimaryKeySelective(Member record);

	int updateByPrimaryKey(Member record);
	
	/**
	 * 查询所有会员
	 * */
	List<Member> selectAllMember();
}