package com.xxx.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务
 * */
@Mapper
public interface QuartzConfigMapper {
	
	/**
	 * 查询当前所有定时任务列表
	 * */
	List<Map<Object, Object>> selectAllQuartz();
}
