package com.metoo.sqlite.utils.job.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.http.HttpSession;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 14:58
 */
public abstract class CaptchaCleanerJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        HttpSession session = (HttpSession) jobExecutionContext.getMergedJobDataMap().get("session");
        session.removeAttribute("captcha");
        System.out.println("Captcha removed from session.");
    }
}
