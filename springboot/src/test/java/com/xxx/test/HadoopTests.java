package com.xxx.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试运行方式：右击 --> Run As --> JUnit Test
 * 客户端读写hadoop数据
 * */
@RunWith(SpringRunner.class)
public class HadoopTests {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String hadoop_uri = "hdfs://192.168.3.64:9000";
	
	static{
		System.setProperty("hadoop.home.dir", "/usr/local/src/hadoop-2.6.5");
	}
	
	/**
	 * 测试客户端读取hadoop数据
	 * */
	@Ignore
	public void testReadHadoop() throws Exception{
		String lpath = "E:\\download\\test.txt";
		String rpath  = hadoop_uri+"/input/test.txt";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(lpath));
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(rpath), conf);
		InputStream in = fs.open(new Path(rpath));
		// 将hdfs上的文件缓冲下载到本地
		IOUtils.copyBytes(in, out, 4096, true);
		fs.close();
		IOUtils.closeStream(in);
		IOUtils.closeStream(out);
	}
	
	/**
	 * 测试客户端写入hadoop数据
	 * */
	@Ignore
	public void testWriteHadoop() throws Exception{
		String lpath = "E:\\download\\test.txt";
		String rpath  = hadoop_uri+"/input/test.txt";
		InputStream in = new BufferedInputStream(new FileInputStream(lpath));
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(rpath), conf);
		OutputStream out = fs.create(new Path(rpath), new Progressable(){
			@Override
			public void progress() {
				logger.info("*");
			}
		});
		// 将本地文件缓冲上传到hdfs
		IOUtils.copyBytes(in, out, 4096, true);
		fs.close();
		in.close();
		out.close();
	}
	
	@Test
	public void testAll() throws Exception{
		list("/");
	}
	
	/**
	 * 列出指定目录下所有文件和目录
	 * */
	private void list(String path) throws Exception{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(hadoop_uri), conf);
		FileStatus[] statuses = fs.listStatus(new Path(path));
		for(FileStatus status: statuses){
			logger.info(status.toString());
		}
	}
	
}
