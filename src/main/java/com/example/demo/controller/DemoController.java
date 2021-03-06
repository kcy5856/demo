package com.example.demo.controller;

import com.example.demo.annotation.Limit;
import com.example.demo.annotation.MyAspect;
import com.example.demo.common.enums.ErrorEnum;
import com.example.demo.exception.BizException;
import com.example.demo.model.Student;
import com.example.demo.model.common.Result;
import com.example.demo.rabbitmq.MsgProducer;
import com.example.demo.service.DemoService;
import com.example.demo.utils.RedisUtils;
import com.example.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

@RestController
public class DemoController {
	Logger logger = LoggerFactory.getLogger(DemoController.class);
	
	@Autowired
	private DemoService demoService;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	RedisUtils redisUtils;

	//注入RabbitMQ的模板
	@Autowired
	private MsgProducer msgProducer;

	@RequestMapping(value="/change", method = RequestMethod.GET)
	public String changeParam(@RequestParam(value="isPublic", required = false) Boolean isPublic) {
		logger.debug("isPublic: {}", isPublic);
		logger.info(redisUtils.get("gao").toString());
		return "ok";
	}

    @RequestMapping(value="/get", method = RequestMethod.GET)
    public Integer getInt() {
        return 1;
    }

	@MyAspect
	@RequestMapping(value="/hello", method = RequestMethod.GET)
	public Result getHello(@RequestParam(value="name") String name) {
		redisUtils.set("gao", "jin");
		Object bean = applicationContext.getBean("demoServiceImpl");
		if (!Objects.isNull(bean)) {
			logger.info(bean.getClass().toString());
		}
		String val = demoService.getName(name);
		logger.debug(val);
		demoService.printData();
		return ResultUtil.error(ErrorEnum.DATA_NOT_EXIST);
	}

	/**
	 * 测试
	 */
	@Limit(name="testLimit", key = "hello", prefix = "limit", period = 30, count = 5)
	@RequestMapping(value="/sendmsg", method = RequestMethod.GET)
	public Student sendMsg(@RequestParam String msg, @RequestParam String key){
	    String nkey = "item.aaa";
	    String mmsg = "test";
		msgProducer.sendMessage(nkey, mmsg);
		//返回消息
        Student student = new Student();
        student.setBirthday(new Date());
        student.setName("奥力给");
		return student;
	}
	
}
