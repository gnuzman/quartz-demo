package com.opencodez.controller;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.Api;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opencodez.configuration.ConfigureQuartz;
import com.opencodez.quartz.jobs.DynamicJob;
import com.opencodez.util.AppUtil;
import com.opencodez.util.PropertiesUtils;

@Api(tags = "Quartz")
@RestController
public class TestController {
	
	@Value("${con.key2}")
	String conKey2;
	
	@Autowired
	private SchedulerFactoryBean schedFactory;
	
	@GetMapping("/getval")
    public String getVal(@RequestParam(value="key", defaultValue="World") String key) {
		Map<String, String> mapOfKeyValue = new HashMap<String, String>();
		mapOfKeyValue.put(key, PropertiesUtils.getProperty(key));
		mapOfKeyValue.put("con.key2", conKey2);
		return AppUtil.getBeanToJsonString(mapOfKeyValue);
    }
	
	@GetMapping("/schedule")
	public String schedule() {
		String scheduled = "Job is Scheduled!!";
		try {
			JobDetailFactoryBean jdfb = ConfigureQuartz.createJobDetail(DynamicJob.class);
			jdfb.setBeanName("dynamicJobBean");
			jdfb.afterPropertiesSet();

			jdfb.getJobDataMap().put("url", "http://baidu.com");
			
			SimpleTriggerFactoryBean stfb = ConfigureQuartz.createTrigger(jdfb.getObject(),5000L);
			stfb.setBeanName("dynamicJobBeanTrigger");
			stfb.afterPropertiesSet();
			
			schedFactory.getScheduler().scheduleJob(jdfb.getObject(), stfb.getObject());

			//////////////////////////////////////////////////////////////////////////////////////////////////

			JobDetailFactoryBean jdfb2 = ConfigureQuartz.createJobDetail(DynamicJob.class);
			jdfb2.setBeanName("dynamicJobBean2");
			jdfb2.afterPropertiesSet();

			jdfb2.getJobDataMap().put("url", "http://sina.com");

			SimpleTriggerFactoryBean stfb2 = ConfigureQuartz.createTrigger(jdfb2.getObject(),5000L);
			stfb2.setBeanName("dynamicJobBeanTrigger2");
			stfb2.afterPropertiesSet();

			schedFactory.getScheduler().scheduleJob(jdfb2.getObject(), stfb2.getObject());
			
		} catch (Exception e) {
			scheduled = "Could not schedule a job. " + e.getMessage();
		}
		return scheduled;
	}
	
	@GetMapping("/unschedule")
	public String unschedule() {
		String scheduled = "Job is Unscheduled!!";
		TriggerKey tkey = new TriggerKey("dynamicJobBeanTrigger");
		JobKey jkey = new JobKey("dynamicJobBean"); 
		try {
			schedFactory.getScheduler().unscheduleJob(tkey);
			schedFactory.getScheduler().deleteJob(jkey);
		} catch (SchedulerException e) {
			scheduled = "Error while unscheduling " + e.getMessage();
		}
		return scheduled;
	}
}