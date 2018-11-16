package com.xxx.entity;

public enum Status {

	// 可取的枚举值
	Success("0000", "成功"),
	Warning("0001", "警告"),
	Error("9999", "错误");
	
	// 成员变量
	private String code;
	private String msg;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	// 构造方法
	private Status(String code, String msg){
		this.code = code;
		this.msg = msg;
	}
	
	// 取值方法
	public static String getMsgByCode(String code){
		for(Status s: Status.values()){
			if(s.getCode().equalsIgnoreCase(code)){
				return s.getMsg();
			}
		}
		return Error.getCode();
	}
	
	public static String getCodeByMsg(String msg){
		for(Status s: Status.values()){
			if(s.getMsg().equalsIgnoreCase(msg)){
				return s.getCode();
			}
		}
		return Error.getCode();
	}
	
	@Override
	public String toString() {
		return this.code+": "+this.msg;
	}
}
