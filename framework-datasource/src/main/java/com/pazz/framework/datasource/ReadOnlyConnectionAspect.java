package com.pazz.framework.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 16:46
 * @description: 只读数据源连接切面
 */
@Aspect
@Component
public class ReadOnlyConnectionAspect {

    @Around("@annotation(readOnlyConnection)")
    public Object proceed(ProceedingJoinPoint pjp, ReadOnlyConnection readOnlyConnection) throws Throwable {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE);
            Object result = pjp.proceed();
            return result;
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

}
