package com.pazz.framework.web.response;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:31
 * @description: 返回结果构造器
 */
public class ResponseBuilder {

    /**
     * 是否成功
     */
    protected boolean success;
    /**
     * 是否业务异常
     */
    protected boolean businessException;
    /**
     * 状态码
     */
    protected String errorCode;
    /**
     * 信息
     */
    protected String message;
    /**
     * 返回结果
     */
    protected Object result;

    protected ResponseBuilder() {
        super();
    }

    public static ResponseBuilder create() {
        return new ResponseBuilder();
    }

    public  ResponseBuilder setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public  ResponseBuilder setBusinessException(boolean businessException) {
        this.businessException = businessException;
        return this;
    }

    public  ResponseBuilder setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public  ResponseBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public  ResponseBuilder setResult(Object result) {
        this.result = result;
        return this;
    }

    public Response build() {
        Response response = new Response();
        return build(response);
    }

    protected Response build(Response response) {
        response.setSuccess(this.success);
        response.setErrorCode(this.errorCode);
        response.setMessage(this.message);
        response.setBusinessException(this.businessException);
        response.setResult(this.result);
        return response;
    }

    public Response buildSuccess(Object result) {
        return buildSuccess(null, result);
    }

    public Response buildSuccess(String message, Object result) {
        return this.setSuccess(true).setBusinessException(false).setMessage(message).setResult(result).build();
    }

    public Response buildBusinessException(String message) {
        return buildBusinessException(null,message);
    }

    public Response buildBusinessException(String errorCode, String message) {
        return buildBusinessException(errorCode,message,null);
    }

    public Response buildBusinessException(String errorCode, String message, Object result) {
        return this.setSuccess(false).setBusinessException(true).setErrorCode(errorCode).setMessage(message).setResult(result).build();
    }

    public Response buildSystemException(String message){
        return buildSystemException(null,message);
    }

    public Response buildSystemException(String errorCode,String message){
        return buildSystemException(errorCode,message,null);
    }

    public Response buildSystemException(String errorCode, String message, Object result){
        return this.setSuccess(false).setBusinessException(false).setErrorCode(errorCode).setMessage(message).setResult(result).build();
    }

}
