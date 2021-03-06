package com.xxx.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xxx.config.SpringContextUtil;
import com.xxx.entity.ResultBean;
import com.xxx.entity.ResultFailBean;
import com.xxx.entity.ResultSuccessBean;
import com.xxx.model.Member;
import com.xxx.service.MemberService;
import com.xxx.service.SimpleConsumerService;
import com.xxx.util.StringUtil;

/**
 * RESTful API公共控制器 <br/>
 * RestController：Spring4之后加入的注解，原来在@Controller中返回json需要@ResponseBody来配合，
 * 使用@RestController相当于@Controller+@ResponseBody组合，返回json格式数据，方法上就不需要@ResponseBody注解了。
 * 但@RestController就不能返回jsp、html等页面了，使用@Controller才能返回页面。参见TemplateController控制器。
 **/
@RestController
@RequestMapping(value = "/common")
@EnableAutoConfiguration
public class CommonController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private MemberService memberService;
	@Autowired
	private SimpleConsumerService simpleConsumerService;
	@Autowired
	private SpringContextUtil springContextUtil;
	
	/**
	 * produces作用：指定返回值类型和字符编码
	 * 请求URL样例：
	 * */
	@ResponseBody
    @RequestMapping(value = "/add", produces = {"application/json;charset=UTF-8"})
    public int addUser(Member member){
        return memberService.addMember(member);
    }
	
	/**
	 * 请求URL样例：http://localhost:8080/common/query/000000000010
	 * */
	@ResponseBody
    @RequestMapping(value = "/query/{memid}", produces = {"application/json;charset=UTF-8"})
    public Object query(@PathVariable("memid") String memid){
		ResultBean result = new ResultSuccessBean();
		result.setMessage("查询单个会员成功");
        Member member = memberService.queryMemberByMemid(memid);
        result.setData(member);
        logger.info("查询会员："+memid+"信息成功！");
        return result;
    }
	
	/**
	 * 请求URL样例：http://localhost:8080/common/delete?memid=000000000010A
	 * RequestParam获取请求参数
	 * */
	@ResponseBody
    @RequestMapping(value = "/delete", produces = {"application/json;charset=UTF-8"})
	public Object delete(@RequestParam("memid") String memid){
		ResultBean result = null;
		//String memid = "";
        int rows = memberService.delMember(memid);
        if(rows>0){
        	result = new ResultSuccessBean();
    		result.setMessage("删除单个会员成功");
        } else {
        	result = new ResultFailBean();
        	result.setMessage("删除单个会员失败");
        }
        return result;
	}
	
	/**
	 * 请求URL样例：http://localhost:8080/common/update?memid=000000000010&logincount=10
	 * RequestBody获取所有请求参数(只适应post请求，不支持get请求, 适用于ajax前端将所有请求参数拼成json传)
	 * 通过request/response获取所有请求参数
	 * 对于Session获取和添加
	 * */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Object updateMember(HttpServletRequest request, HttpServletResponse response){
		ResultBean result = new ResultSuccessBean();
    	result.setMessage("更新会员信息成功");
    	String memid = request.getParameter("memid");
    	Long logincount = Long.parseLong(request.getParameter("logincount"));
    	Member member = new Member();
    	member.setMemid(memid);
    	member.setLogincount(logincount);
        int rows = memberService.updateMember(member);
        logger.info("更新会员信息执行结果："+rows);
        return result;
	}

	/**
	 * PathVariable绑定到函数的参数中
	 * 请求URL样例：http://localhost:8080/common/all/1/10
	 * */
    @ResponseBody
    @RequestMapping(value = "/all/{pageNum}/{pageSize}", produces = {"application/json;charset=UTF-8"})
    public Object findAllUser(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
    	ResultBean result = new ResultSuccessBean();
    	result.setMessage("分页查询会员列表成功");
        List<Member> members = memberService.findAllMember(pageNum, pageSize);
        result.setData(members);
        return result;
    }

    /**
	 * 调用远端zookeeper服务
	 * 请求URL样例：http://localhost:8080/common/user?name=赵刚
	 * 			http://localhost:8080/common/user?telephone=13736160267
	 * */
    @ResponseBody
    @RequestMapping(value = "/user", produces = {"application/json;charset=UTF-8"})
    public Object getUser(HttpServletRequest request, HttpServletResponse response){
    	ResultBean result = new ResultSuccessBean();
    	Map<Object, Object> params = new HashMap<Object, Object>();
    	String name = request.getParameter("name");
    	String telephone = request.getParameter("telephone");
    	if(StringUtils.isNotEmpty(name)){
    		params.put("name", name);
    	}
    	if(StringUtils.isNotEmpty(telephone)){
    		params.put("telephone", telephone);
    	}
    	try {
			List<Map<Object, Object>> users = simpleConsumerService.getUsers(params);
			result.setMessage("获取会员信息成功"); 
			result.setData(users);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResultFailBean();
        	result.setMessage("获取会员信息失败"); 
		}
    	
    	return result;
    }
    
    /**
	 * 通过反射机制动态调用服务
	 * 请求URL样例：http://localhost:8080/common/memberService/query
	 * 利用postman赋请求参数telephone=13428655932
	 * */
    @ResponseBody
    @RequestMapping(value = "/{target}/{action}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Object call(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable("target") String target, @PathVariable("action") String action){
		ResultBean result = new ResultSuccessBean();
    	result.setMessage("消息处理成功");
    	Map<Object, Object> params = new HashMap<Object, Object>();
    	for(Map.Entry<String, String[]> entry: request.getParameterMap().entrySet()) {
    		String key = entry.getKey();
    		String[] values = entry.getValue();
    		//将数组转为字符串，防止出现错误：Parameters: [Ljava.lang.String
    		//[Ljava.lang.String;@f0c85e；[表示一维数组；[[表示二维数组 ；L表示一个对象； java.lang.String表示对象的类型； @后面表示该对象的HashCode
    		String value = StringUtil.toString(values);
    		params.put(key, value);
    	}
    	try {
    		result = invoke(target, action, params);
	        logger.info("消息处理执行结果："+JSON.toJSONString(result));
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = new ResultFailBean("消息处理失败");
    	}
        return result;
	}
    private ResultBean invoke(String target, String action, Map<Object, Object> params) throws Exception{
		Object obj = springContextUtil.getBean(target);
		Method m = obj.getClass().getMethod(action, Map.class);
		Object object = m.invoke(obj, params);
		return (ResultBean)object;
	}
}
