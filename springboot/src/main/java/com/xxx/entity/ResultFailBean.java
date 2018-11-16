package com.xxx.entity;

public class ResultFailBean extends ResultBean{

	public ResultFailBean(){
		this.code = ResultBean.ERROR;
	}
	
	public ResultFailBean(String message){
		this.code = ResultBean.ERROR;
		this.message = message;
	}
	
	public ResultFailBean(String message, Object data){
		this.code = ResultBean.ERROR;
		this.message = message;
		this.data = data;
	}
}
