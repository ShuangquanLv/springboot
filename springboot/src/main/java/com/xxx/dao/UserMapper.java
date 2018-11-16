package com.xxx.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

	public List<Map<Object, Object>> getUsers(Map<Object, Object> params);
}
