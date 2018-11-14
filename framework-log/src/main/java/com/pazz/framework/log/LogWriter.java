package com.pazz.framework.log;

import lombok.Data;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 9:18
 * @description: 日志记录
 */
@Data
public class LogWriter implements ILogWriter {

    private ILogBuffer logBuffer;

    @Override
    public void write(Object obj) {
        if (logBuffer != null) {
            logBuffer.write(obj);
        }
    }

}
