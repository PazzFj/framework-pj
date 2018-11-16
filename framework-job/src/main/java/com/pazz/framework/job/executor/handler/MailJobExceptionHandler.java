package com.pazz.framework.job.executor.handler;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:42
 * @description: 异常处理器 发送邮件
 */
public class MailJobExceptionHandler implements JobExceptionHandler {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void handleException(final String jobName, final Throwable cause) {
        log.error(String.format("Job '%s' exception occur in job processing", jobName), cause);
    }

}
