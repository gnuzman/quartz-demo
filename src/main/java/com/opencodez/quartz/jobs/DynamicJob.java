/**
 * 
 */
package com.opencodez.quartz.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.opencodez.util.AppLogger;

/**
 * @author pavan.solapure
 *
 */
@DisallowConcurrentExecution
public class DynamicJob  implements Job {
	
	private final static AppLogger logger = AppLogger.getInstance();
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String url = arg0.getJobDetail().getJobDataMap().get("url").toString();
		logger.info("----- Running Dynamic Job With Simple Trigger ------");
		logger.info("----- " + url);
	}

}
