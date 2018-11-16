package com.pazz.framework.entity;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author: 彭坚
 * @create: 2018/11/16 15:01
 * @description: 基础实体
 */
@Data
public class BaseEntity implements IEntity {

    //id
    @Id
    private String id;
    //创建时间
    private Date createTime;
    //创建人
    private String createUser;
    //修改时间
    private Date modifyTime;
    //修改人
    private String modifyUser;

}
