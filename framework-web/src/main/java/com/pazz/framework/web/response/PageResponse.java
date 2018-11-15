package com.pazz.framework.web.response;

import lombok.Data;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:30
 * @description: Web 分页返回结果
 */
@Data
public class PageResponse<T> extends Response<T> {

    /**
     * 总数
     */
    private long totalCount;

}
