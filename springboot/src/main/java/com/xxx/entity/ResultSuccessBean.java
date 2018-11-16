package com.xxx.entity;

public class ResultSuccessBean extends ResultBean{
	
	public ResultSuccessBean(){
		this.code = ResultBean.SUCCESS;
	}
	
	public ResultSuccessBean(String message){
		this.code = ResultBean.SUCCESS;
		this.message = message;
	}
	
	public ResultSuccessBean(String message, Object data){
		this.code = ResultBean.SUCCESS;
		this.message = message;
		this.data = data;
	}

}
