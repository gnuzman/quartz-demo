package com.opencodez.controller;

import com.opencodez.configuration.ConfigureQuartz;
import com.opencodez.quartz.jobs.DynamicJob;
import com.opencodez.util.AppUtil;
import com.opencodez.util.PropertiesUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Value("${con.key2}")
    String conKey2;

    @Autowired
    private SchedulerFactoryBean schedFactory;

    @GetMapping("/getval")
    public String getVal(@RequestParam(value = "key", defaultValue = "World") String key) {
        Map<String, String> mapOfKeyValue = new HashMap<String, String>();
        mapOfKeyValue.put(key, PropertiesUtils.getProperty(key));
        mapOfKeyValue.put("con.key2", conKey2);
        return AppUtil.getBeanToJsonString(mapOfKeyValue);
    }

    @GetMapping("/schedule")
    public String schedule(@RequestParam(value = "jobName") String jobName,
                           @RequestParam(value = "triggerName") String triggerName,
                           @RequestParam(value = "url") String url) {
        String scheduled = "Job is Scheduled!!";
        try {
            JobDetailFactoryBean jdfb = ConfigureQuartz.createJobDetail(DynamicJob.class);
            jdfb.setBeanName(jobName);
            jdfb.afterPropertiesSet();
            jdfb.getJobDataMap().put("url", url);

            SimpleTriggerFactoryBean stfb = ConfigureQuartz.createTrigger(jdfb.getObject(), 5000L);
            stfb.setBeanName(triggerName);
            stfb.afterPropertiesSet();

            schedFactory.getScheduler().scheduleJob(jdfb.getObject(), stfb.getObject());

        } catch (Exception e) {
            scheduled = "Could not schedule a job. " + e.getMessage();
        }
        return scheduled;
    }

    @GetMapping("/unschedule")
    public String unschedule(@RequestParam(value = "jobName") String jobName,
                             @RequestParam(value = "triggerName") String triggerName) {
        String scheduled = "Job is Unscheduled!!";
        TriggerKey tkey = new TriggerKey(triggerName);
        JobKey jkey = new JobKey(jobName);
        try {
            schedFactory.getScheduler().unscheduleJob(tkey);
            schedFactory.getScheduler().deleteJob(jkey);
        } catch (SchedulerException e) {
            scheduled = "Error while unscheduling " + e.getMessage();
        }
        return scheduled;
    }
}