package com.pazz.framework.web.filter;

import com.google.common.collect.Sets;
import com.pazz.framework.util.string.StringUtil;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * @author: 彭坚
 * @create: 2018/11/19 14:53
 * @description: Shutdown 过滤器
 */
public class ShutdownFilter extends OncePerRequestFilter {
    private static final String ALL_ADDRESS = "0.0.0.0";

    private static final String ENDPOINTS_SHUTDOWN_ADDRESS = "endpoints.shutdown.address";

    private Set<String> address = Sets.newHashSet("127.0.0.1", "0:0:0:0:0:0:0:1");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        if (address.contains(ALL_ADDRESS) || address.contains(ip)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Shutdown forbidden from ip:" + ip);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        String shutdownAddress = environment.getProperty(ENDPOINTS_SHUTDOWN_ADDRESS, String.class);
        if (StringUtil.isNotBlank(shutdownAddress)) {
            address.addAll(Arrays.asList(StringUtil.split(shutdownAddress, ",")));
        }
    }
}
