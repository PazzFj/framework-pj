package com.pazz.framework.web.xss;

/**
 * 验证处理策略
 */
public interface Tactics {
    /**
     * 处理的逻辑方法
     *
     * @param target 目标对象,正则匹配的字符串
     * @param regex  正则表达式
     * @return
     * @see
     */
    String process(String target, String regex) throws ParametersValidatorException;
}
