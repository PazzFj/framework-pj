package com.pazz.framework.web.controller;

import com.pazz.framework.exception.BusinessException;
import com.pazz.framework.log.ILogExceptionWriter;
import com.pazz.framework.web.response.PageResponse;
import com.pazz.framework.web.response.PageResponseBuilder;
import com.pazz.framework.web.response.Response;
import com.pazz.framework.web.response.ResponseBuilder;
import com.pazz.framework.web.security.exception.AccessNotAllowException;
import com.pazz.framework.web.security.exception.UserNotLoginException;
import com.pazz.framework.web.validator.exception.DataValidatorException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:27
 * @description: Controlle 基础抽象类
 */
public abstract class AbstractController {

    protected final Log log = LogFactory.getLog(getClass());
    /**
     * 数据验证错误
     */
    public final static String ERROR_CODE_VALIDATOR = "10000";
    /**
     * 业务异常错误
     */
    public final static String ERROR_CODE_BUSINESS = "20000";
    /**
     * 系统异常
     */
    public final static String ERROR_CODE_SYSTEM = "90000";

    @Autowired(required = false)
    private ILogExceptionWriter logExceptionWriter;

    public Response returnSuccess() {
        return returnSuccess(null);
    }

    /**
     * 返回结果
     */
    public Response returnSuccess(Object result) {
        return ResponseBuilder.create().buildSuccess(result);
    }

    /**
     * 返回分页结果
     */
    public PageResponse returnPageSuccess(Object result, long totalCount) {
        return PageResponseBuilder.create().buildSuccess(result, totalCount);
    }

    /**
     * 拦截UserNotLoginException异常
     */
    @ExceptionHandler(UserNotLoginException.class)
    public String handleUserNotLoginException() {
        //用户未登录重定向到根目录
        return "redirect:/";
    }

    /**
     * 异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(Exception exception) {
        //打印异常
        log.error(exception.getMessage(), exception);
        if (logExceptionWriter != null) {
            logExceptionWriter.write(exception);
        }
        if (exception instanceof IllegalArgumentException) {
            //参数校验异常
            return ResponseBuilder.create().buildBusinessException(AbstractController.ERROR_CODE_VALIDATOR, exception.getMessage());
        }
        if (exception instanceof AccessNotAllowException) {
            return ResponseBuilder.create().buildBusinessException(AbstractController.ERROR_CODE_VALIDATOR, "权限不足!");
        }
        if (exception instanceof DataValidatorException) {
            //数据验证异常
            return ResponseBuilder.create().buildBusinessException(AbstractController.ERROR_CODE_VALIDATOR, ((BusinessException) exception).getErrorCode());
        }
        if (exception instanceof BusinessException) {
            //业务异常
            return ResponseBuilder.create().buildBusinessException(AbstractController.ERROR_CODE_BUSINESS, ((BusinessException) exception).getErrorCode());
        } else {
            //系统异常
            return ResponseBuilder.create().buildSystemException(AbstractController.ERROR_CODE_SYSTEM, ExceptionUtils.getStackTrace(exception));
        }
    }

}
