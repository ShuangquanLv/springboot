package com.xxx.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.xxx.dao.MemberMapper;
import com.xxx.dao.UserMapper;
import com.xxx.entity.ResultBean;
import com.xxx.entity.ResultSuccessBean;
import com.xxx.model.Member;
import com.xxx.service.MemberService;

@Service(value = "memberService")
public class MemberServiceImpl implements MemberService{

	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 添加会员
	 * Transactional 使用value具体指定使用哪个事务管理器
	 * */
	@Override
	@Transactional
	public int addMember(Member member) {
		return memberMapper.insert(member);
	}
	
	/**
	 * 删除会员
	 * */
	@Override
	@Transactional
	public int delMember(String memid){
		return memberMapper.deleteByPrimaryKey(memid);
	}

	/**
	 * 分页查询会员列表
	 * */
	@Override
	@Transactional
	public List<Member> findAllMember(int pageNum, int pageSize) {
		// 向service层传入分页参数，利用分页插件pagehelper实现分页功能
		PageHelper.startPage(pageNum, pageSize);
		return memberMapper.selectAllMember();
	}

	@Override
	@Transactional
	public Member queryMemberByMemid(String memid) {
		return memberMapper.selectByPrimaryKey(memid);
	}

	@Override
	@Transactional
	public int updateMember(Member member){
		return memberMapper.updateByPrimaryKeySelective(member);
	}
	
	/**
	 * 按条件查询会员
	 * */
	public ResultBean query(Map<Object, Object> params) {
		ResultBean rb = new ResultSuccessBean("查询会员成功");
		if(params.containsKey("pageNum") && params.containsKey("pageSize")) {
			int pageNum = Integer.parseInt(params.get("pageNum").toString());
			int pageSize = Integer.parseInt(params.get("pageSize").toString());
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Map<Object, Object>> data = userMapper.getUsers(params);
		rb.setData(data);
		return rb;
	}
}
