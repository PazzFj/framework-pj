package com.pazz.framework.samples.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 彭坚
 * @create: 2018/11/16 13:47
 * @description:
 */
@Data
public class OrgEntity implements Serializable {

    private String code;

    private String name;

}
