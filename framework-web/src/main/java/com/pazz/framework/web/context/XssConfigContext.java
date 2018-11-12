package com.pazz.framework.web.context;

import com.pazz.framework.util.string.StringUtil;

import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 18:05
 * @description: xss配置参数
 */
public class XssConfigContext {

    /**
     * 策略：替换成空串
     */
    public static final String REPLACE_EMPTY = "REPLACE_EMPTY";
    /**
     * 策略：替换成转义字符
     */
    public static final String REPLACE_ESCAPE = "REPLACE_ESCAPE";
    /**
     * 策略：重定向
     */
    public static final String REDIRECT = "REDIRECT";
    /**
     * 默认表达式
     */
    public static final String DEFAULT_EXPRESSION = "(?i)(script)+";
    /**
     * 表达式属性名
     */
    public static final String EXPRESSION = "expression";
    /**
     * 策略属性名
     */
    public static final String TACTICS = "tactics";
    /**
     * 路径属性名
     */
    public static final String PATH = "path";
    /**
     * 上下文
     */
    Map<String, Object> context;

    /**
     * 构造时实例化属性
     */
    public XssConfigContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * 获取expression
     */
    public String getExpression() {
        String expression = (String) context.get(EXPRESSION);
        if (StringUtil.isBlank(expression)) {
            return DEFAULT_EXPRESSION;
        }
        return expression;
    }

    /**
     * 获取tactics
     */
    public String getTactics() {
        String tactics = (String) context.get(TACTICS);
        if (StringUtil.isBlank(tactics)) {
            return REPLACE_EMPTY;
        }
        return tactics;
    }

    /**
     * 获取path
     */
    public String getPath() {
        String path = (String) context.get(PATH);
        return path;
    }

}
