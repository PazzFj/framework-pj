package com.pazz.framework.web.xss;

/**
 * 非法字符转义的策略逻辑类
 */
public class ReplaceEscape implements Tactics {

    @Override
    public String process(String target, String regex) throws ParametersValidatorException {
        target = target.replaceAll(">", "&gt;");
        target = target.replaceAll("<", "&lt;");
        return target;
    }
}
