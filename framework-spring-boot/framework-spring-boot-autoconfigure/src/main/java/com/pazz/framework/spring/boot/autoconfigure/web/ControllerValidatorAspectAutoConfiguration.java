package com.pazz.framework.spring.boot.autoconfigure.web;

import com.pazz.framework.web.validator.AbstractControllerValidatorAspect;
import com.pazz.framework.web.validator.exception.DataValidatorException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 9:26
 * @description: 数据验证自动装载
 */
@Aspect
@Configuration
@ConditionalOnProperty(prefix = "framework.web.data-validator", name = "enabled", matchIfMissing = true)
@ConditionalOnMissingBean(AbstractControllerValidatorAspect.class)
public class ControllerValidatorAspectAutoConfiguration extends AbstractControllerValidatorAspect {

    @Around("execution(* *..controller..*(..)) && args(..,bindingResult)")
    public Object doAround(ProceedingJoinPoint pjp, BindingResult bindingResult) throws Throwable {
        if (bindingResult.hasErrors()) {
            doErrorHandle(bindingResult);
        }
        return pjp.proceed();
    }

    private void doErrorHandle(BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        StringBuffer sb = new StringBuffer();
        allErrors.forEach(error -> sb.append(error.getDefaultMessage()));
        throw new DataValidatorException(sb.toString());
    }

}
