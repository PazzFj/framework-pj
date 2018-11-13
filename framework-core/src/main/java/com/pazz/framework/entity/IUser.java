package com.pazz.framework.entity;

import java.util.Set;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 14:56
 * @description: 用户接口定义
 */
public interface IUser extends IEntity {

    Set<String> getRoleids();

    Set<String> queryAccessUris();

    void setRoleids(Set<String> paramSet);

    void setUserName(String paramString);

    String getUserName();

    void loadAccessUris();
}
