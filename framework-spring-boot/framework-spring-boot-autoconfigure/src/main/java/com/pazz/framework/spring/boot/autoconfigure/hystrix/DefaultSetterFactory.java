package com.pazz.framework.spring.boot.autoconfigure.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import feign.Feign;
import feign.Target;
import feign.hystrix.SetterFactory;

import java.lang.reflect.Method;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:04
 * @description: 调节器
 */
public class DefaultSetterFactory implements SetterFactory {

    @Override
    public HystrixCommand.Setter create(Target<?> target, Method method) {
        String groupKey = target.name();
        String commandKey;
        com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand hystrixCommand = method.getAnnotation(com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand.class);
        if (hystrixCommand != null && hystrixCommand.commandKey().length() > 0) {
            commandKey = hystrixCommand.commandKey();
        } else {
            commandKey = Feign.configKey(target.type(), method);
        }
        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey)).andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
    }

}
