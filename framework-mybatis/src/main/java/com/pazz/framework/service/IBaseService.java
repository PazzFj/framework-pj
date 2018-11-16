package com.pazz.framework.service;

import com.pazz.framework.service.delete.IDeleteService;
import com.pazz.framework.service.insert.IInsertService;
import com.pazz.framework.service.select.ISelectService;
import com.pazz.framework.service.update.IUpdateService;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:44
 * @description: 基础service公共接口
 */
public interface IBaseService<T> extends IService, IDeleteService<T>, IInsertService<T>, IUpdateService<T>, ISelectService<T> {
}
