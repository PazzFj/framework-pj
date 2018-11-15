package com.pazz.framework.entity;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:14
 * @description: IFunction
 */
public interface IFunction extends IEntity {

    /**
     * 用户功能菜单的href
     */
    String getUri();

    /**
     * 功能菜单的id
     */
    String getKey();

    /**
     * 功能菜单的的代码号：code
     */
    String getFunctionCode();

    /**
     * 功能是否被启用
     */
    Boolean getValidFlag();

    /**
     * 拿名称
     */
    String getName();
}
