package com.xxx.controller;

import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxx.model.Member;
import com.xxx.service.MemberService;
import com.xxx.service.impl.EchartServiceImpl;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 模板引擎控制器
 * 使用@Controller返回jsp、html等页面，如果需要返回json数据，需要在方法上加上@ResponseBody注解，参见getChartsData()方法。
 * 使用@RestController相当于@Controller+@ResponseBody组合，只返回json格式数据，方法上就不需要@ResponseBody注解了。参见CommonController控制器。
 **/
@Controller
@RequestMapping(value = "/template")
//@EnableAutoConfiguration
public class TemplateController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MemberService memberService;
	@Autowired 
	private EchartServiceImpl echartService;

	/**
	 * PathVariable绑定到函数的参数中
	 * ResponseBody，默认返回json格式
	 * 请求URL样例：http://localhost:8080/template/all/1/10
	 * */
	@ApiOperation(value="分页查询会员", notes="指定页数指定一页大小，分页查询会员")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "pageSize", value = "一页大小", required = true, dataType = "Integer")
	})
    @RequestMapping(value = "/all/{pageNum}/{pageSize}", method={RequestMethod.GET, RequestMethod.POST})
    public String findAllUser(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize, Model model){
        List<Member> members = memberService.findAllMember(pageNum, pageSize);
        // 向模板的数值模型添加属性
        model.addAttribute("members", members);
        // 返回模板文件的名称，对应src/main/resources/templates/index.html
        return "index";
    }
	
	/**
	 * 在thymeleaf模板上生产Echart图表
	 * 请求URL样例：http://localhost:8080/template/charts
	 * */
	@RequestMapping(value = "/charts", method={RequestMethod.GET, RequestMethod.POST})
    public String getCharts(){
        // 返回模板文件的名称，对应src/main/resources/templates/charts.html
        return "charts";
    }
	
	/**
	 * 在thymeleaf模板上生产Echart图表
	 * 请求URL样例：http://localhost:8080/template/charts/data
	 * */
	@ResponseBody
	@RequestMapping(value = "/charts/data", method={RequestMethod.POST}, produces = {"application/json;charset=UTF-8"})
    public Map<Object, Object> getChartsData(HttpServletRequest request, HttpServletResponse response){
		Map<Object, Object> result = new HashMap<Object, Object>();
		int step = Integer.parseInt(request.getParameter("step"));
		List<Integer> data = new ArrayList<Integer>();
		Integer[] vals = new Integer[] {5, 20, 36, 10, 10, 20};
		for(Integer i: vals){
			data.add(i+step);
		}
		result.put("bar", data);
		try {
			Map<Object, Object> lineOption = echartService.queryLineData(new HashMap<Object, Object>());
			Map<Object, Object> pieOption = echartService.queryPieData(new HashMap<Object, Object>());
			result.put("line", lineOption);
			result.put("pie", pieOption);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }
}
