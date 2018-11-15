package com.pazz.framework.web.response;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:32
 * @description: 分页返回结果builder
 */
public class PageResponseBuilder extends ResponseBuilder {

    /**
     * 总数
     */
    private long totalCount;

    public final PageResponseBuilder setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public static PageResponseBuilder create() {
        return new PageResponseBuilder();
    }

    public PageResponse build() {
        PageResponse response = new PageResponse();
        super.build(response);
        response.setTotalCount(this.totalCount);
        return response;
    }

    public PageResponse buildSuccess(Object result, long totalCount) {
        this.setTotalCount(totalCount);
        return (PageResponse) super.buildSuccess(result);
    }

}
