package com.xxx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.xxx.model.Member;

@Mapper
public interface MemberMapper {
    int deleteByPrimaryKey(String memid);

	int insert(Member record);

	int insertSelective(Member record);

	Member selectByPrimaryKey(String memid);

	int updateByPrimaryKeySelective(Member record);

	int updateByPrimaryKey(Member record);
	
	/**
	 * 查询所有会员
	 * */
	List<Member> selectAllMember();
}