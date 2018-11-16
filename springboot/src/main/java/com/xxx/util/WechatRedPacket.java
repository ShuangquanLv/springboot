package com.xxx.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信抢红包算法逻辑实现
 * */
public class WechatRedPacket {

	// 单个红包金额最小值
	private static final float minval = 0.01f;
	// 单个红包金额最大值
	private static final float maxval = 200.00f;
	// 避免单个红包金额过大占用大量资金，设定非最后一个红包的最大值为剩余平均值的N倍
	private static final float times = 2.0f;
	
	/**
	 * 检验当前抽取的红包是否合理
	 * @param money 剩余待抽取总金额
	 * @param count 剩余待抽取总人数
	 * @return -1=剩余金额不足分配，1=剩余金额超出分配，0=剩余金额正常分配
	 * */
	private int isOK(float money, int count) {
		if (money < minval * count) {
			return -1;
		}
		if (money > maxval * count) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * 抢红包核心算法
	 * @param money 剩余待抽取总金额
	 * @param min 此次抽取红包金额下限
	 * @param max 此次抽取红包金额上限
	 * @param count 剩余待抽取总人数
	 * @return 抽到的单个红包金额
	 * */
	private float random(float money, float min, float max, int count){
		// 最后一位，将剩余金额全部发放
		if (count==1){
			return money;
		}
		// 最小值等于最大值，取值固定唯一，直接发放
		if(min == max){
			return min;
		}
		float max_ = max > money ? money : max;
		// 在最大值和最小值间产生一个随机数
		float val = (float) (Math.random() * (max_ - min) + min);
		val = floor(val);
		// 检验下次抽取是否可行
		float balance = money - val;
		int OK = isOK(balance, count - 1);
		// 当前抽取的金额太小，导致后续抽取金额超限
		if (OK > 0) { 
			// 提升下限，重新抽取
			return random(money, val, max, count);
		// 当前抽取的金额太大，导致后续抽取金额不足
		} else if (OK < 0) { 
			// 降低上线，重新抽取
			return random(money, min, val, count);
		// 当前抽取的金额不影响下次正常分配
		} else {
			return val;
		}
	}
	
	/**
	 * 截取小数点后两位
	 * */
	public float floor(float f) {
		return (float) (Math.round(f * 100)) / 100;
	}
	
	/**
	 * 红包拆分
	 * @param money 待拆分总金额
	 * @param count	抢红包总人数
	 * */
	public List<Float> split(float money, int count) {
		List<Float> list = new ArrayList<Float>();
		if (isOK(money, count) != 0) {
			return list;
		}
		for (int i = count; i > 0; i--) {
			// 避免单个红包金额过大占用大量资金，设定非最后一个红包的最大值为剩余平均值的N倍
			float max = money / i * times;
			max = max > money ? money : max;
			max = floor(max);
			float val = random(money, minval, max, i);
			val = floor(val);
			list.add(val);
			money -= val;
		}
		return list;
	}
}
