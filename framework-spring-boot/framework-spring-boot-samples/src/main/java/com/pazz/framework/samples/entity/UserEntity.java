package com.pazz.framework.samples.entity;

import com.pazz.framework.entity.IEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * @author: 彭坚
 * @create: 2018/11/16 13:47
 * @description:
 */
@Data
public class UserEntity implements IEntity {

    @Id
    private String id;
    @Column(name = "code")
    @Size(min=5, max=20, message="编号长度在5-20之间")
    private String code;
    @Column(name = "name")
    @Size(min=3, max=20, message="用户名长度只能在2-20之间")
    private String name;

    public UserEntity(){

    }

    public UserEntity(String id,String code,String name){
        this.id = id;
        this.name = name;
        this.code = code;
    }

}
