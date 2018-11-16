package com.xxx.entity;

public class ResultBean {
	
	public static final Integer SUCCESS = 0;
    public static final Integer ERROR = 99;
    
    public Integer code;
    public String message;
    public Object data;
    
    public ResultBean(){
    	
    }

    public ResultBean(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
    
	public ResultBean(Integer code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    
}
