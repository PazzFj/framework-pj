package com.pazz.framework.log;

import com.pazz.framework.exception.BusinessException;
import com.pazz.framework.log.entity.LogInfo;
import com.pazz.framework.log.entity.LogType;
import com.pazz.framework.web.context.AppContext;
import com.pazz.framework.web.context.RequestContext;
import com.pazz.framework.web.context.UserContext;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 9:25
 * @description: 记录异常日志
 */
public class LogExceptionWriter implements ILogExceptionWriter {

    private ILogSender logSender;
    private boolean writeBusinessException = false;
    private boolean writeException = true;

    @Override
    public void write(Object obj) {
        if (logSender == null || !writeException || !(obj instanceof Exception)) {
            //不写异常
            return;
        }
        if (!writeBusinessException && obj instanceof BusinessException) {
            //不写业务异常
            return;
        }
        Exception exception = (Exception) obj;
        logSender.send(getExceptionLogInfo(exception));
    }

    private LogInfo getExceptionLogInfo(Exception exception) {
        LogInfo info = new LogInfo();
        info.setRequestId(RequestContext.getCurrentContext().getRequestId());
        final String url = RequestContext.getCurrentContext().getRemoteRequestURL();
        info.setUrl(url);
        info.setIp(RequestContext.getCurrentContext().getIp());
        info.setUserName(UserContext.getCurrentUser() == null ? "" : UserContext.getCurrentUser().getUserName());
        info.setDate(info.newDate());
        info.setAction(LogType.EXCEPTION);
        info.setAppName(AppContext.getAppContext().getContextPath());
        info.setModuleName(RequestContext.getCurrentContext().getModuleName());
        info.setType(LogType.WEB);
        info.setMessage(ExceptionUtils.getStackTrace(exception));
        return info;
    }

    public ILogSender getLogSender() {
        return logSender;
    }

    public void setLogSender(ILogSender logSender) {
        this.logSender = logSender;
    }

    public boolean isWriteBusinessException() {
        return writeBusinessException;
    }

    public void setWriteBusinessException(boolean writeBusinessException) {
        this.writeBusinessException = writeBusinessException;
    }

    public boolean isWriteException() {
        return writeException;
    }

    public void setWriteException(boolean writeException) {
        this.writeException = writeException;
    }
}
