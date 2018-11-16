package com.xxx.service;

import java.util.List;

import com.xxx.model.Member;

public interface MemberService {
	
	int addMember(Member member);
	
	int delMember(String memid);
	
	int updateMember(Member member);
	
	Member queryMemberByMemid(String memid);
	
    List<Member> findAllMember(int pageNum, int pageSize);
}
