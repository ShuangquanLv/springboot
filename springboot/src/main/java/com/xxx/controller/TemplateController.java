package com.xxx.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xxx.model.Member;
import com.xxx.service.MemberService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 模板引擎控制器
 **/
@Controller
@RequestMapping(value = "/template")
//@EnableAutoConfiguration
public class TemplateController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MemberService memberService;

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
}
