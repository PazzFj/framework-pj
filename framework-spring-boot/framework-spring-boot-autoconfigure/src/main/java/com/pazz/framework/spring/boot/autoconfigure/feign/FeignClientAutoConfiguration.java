package com.pazz.framework.spring.boot.autoconfigure.feign;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 14:28
 * @description: Feign 自动装配
 */
@Configurable
@AutoConfigureBefore(FeignClientsConfiguration.class)
public class FeignClientAutoConfiguration {



}
