package com.xxx.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.util.MapUtils;

import com.xxx.entity.ResultBean;
import com.xxx.entity.ResultFailBean;
import com.xxx.entity.ResultSuccessBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 文件上传、下载控制器
 * 	1、单个/多个文件上传
 * 	2、上传文件大小、类型过滤限制
 * 		2.1、在application.yml中配置
 * 		2.2、在启动类中配置MultipartConfigElement类型的Bean
 * 	3、文件下载
 *  4、使用Swagger2构建Restful APIs文档
 **/
@Api(description = "文件上传、下载控制器")
@Controller
@RequestMapping(value = "/file")
public class FileController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	//通过Value注解直接从application.yml或application-{profiles}.yml配置文件中获取参数
	@Value("${file.upload.dir}")
	private String uploadDir;
	@Value("${file.download.dir}")
	private String downloadDir;
	
	/**
	 * 单个文件上传
	 * */
	@ApiOperation(value="单个文件上传", notes="通过form表单提交post请求，上传单个文件到本地服务器指定目录")
	@ApiImplicitParam(name = "file", value = "上传文件对象", required = true, dataType = "MultipartFile")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Object upload(@RequestParam("file") MultipartFile file){
		ResultBean rb = new ResultSuccessBean("文件上传成功");
		logger.info("上传文件本地存放根目录："+uploadDir);
		try{
			if(!file.isEmpty()){
				String originalFileName = file.getOriginalFilename();
				String extension = FilenameUtils.getExtension(originalFileName);
				String currentFileName = sdf.format(new Date()) + RandomStringUtils.randomAlphanumeric(6).toUpperCase() + "." + extension;
				String path = FilenameUtils.separatorsToSystem(uploadDir);
				FileUtils.forceMkdir(new File(path));
				if(!path.endsWith(File.separator)){
					path = path + File.separator;
				}
				FileUtils.writeByteArrayToFile(new File(FilenameUtils.separatorsToSystem(path+currentFileName)), file.getBytes());
			} else {
				rb = new ResultFailBean("上传文件为空");
			}
		} catch (Exception e){
			e.printStackTrace();
			rb = new ResultFailBean("文件上传失败");
			logger.error("单个文件上传失败");
		}
		return rb;
	}
	
	/**
	 * 多个文件上传
	 * */
	@ApiIgnore
	@RequestMapping(value = "/multi-upload", method = RequestMethod.POST)
	@ResponseBody
	public Object upload(HttpServletRequest request, HttpServletResponse response){
		ResultBean rb = new ResultSuccessBean("文件上传成功");
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
			multipartRequest.setCharacterEncoding("UTF-8");
			Map<String, MultipartFile> files = multipartRequest.getFileMap();
			if(!MapUtils.isEmpty(files)){
				String path = FilenameUtils.separatorsToSystem(uploadDir);
				FileUtils.forceMkdir(new File(path));
				if(!path.endsWith(File.separator)){
					path = path + File.separator;
				}
				for(Map.Entry<String, MultipartFile> entity: files.entrySet()){
					MultipartFile file = entity.getValue();
					String originalFileName = file.getOriginalFilename();
					String extension = FilenameUtils.getExtension(originalFileName);
					String currentFileName = sdf.format(new Date()) + RandomStringUtils.randomAlphanumeric(6).toUpperCase() + "." + extension;
					FileUtils.writeByteArrayToFile(new File(FilenameUtils.separatorsToSystem(path+currentFileName)), file.getBytes());
				}
			} else {
				rb = new ResultFailBean("上传文件为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rb = new ResultFailBean("文件上传失败");
			logger.error("文件上传失败");
		}
		return rb;
	}
	
	/**
	 * 下载本地固定目录下的文件
	 * */
	@ApiOperation(value="下载文件", notes="下载服务器上固定目录下的文件")
	@ApiImplicitParam(name = "filename", value = "需要下载的文件名称", required = true, dataType = "String")
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@ResponseBody
	public Object download(HttpServletRequest request, HttpServletResponse response){
		ResultBean rb = new ResultSuccessBean("文件下载成功");
		logger.info("下载文件本地存放根目录："+downloadDir);
		String filename = request.getParameter("filename");
		if(StringUtils.isNotEmpty(filename)){
			String path = FilenameUtils.separatorsToSystem(downloadDir);
			if(!path.endsWith(File.separator)){
				path = path + File.separator;
			}
			File file = new File(path+filename);
			if(file.exists() && file.isFile()){
				response.setContentType("application/force-download");// 设置强制下载不打开
				response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
				try {
					response.getOutputStream().write(FileUtils.readFileToByteArray(file));
				} catch (Exception e) {
					e.printStackTrace();
					rb = new ResultFailBean("文件下载失败");
					logger.error("文件下载失败");
				}
			} else {
				rb = new ResultFailBean("文件["+filename+"]不存在");
			}
		} else {
			rb = new ResultFailBean("必传参数filename为空");
		}
		return rb;
	}
}
