package com.pazz.framework.log.filter;

import com.google.common.collect.Sets;
import com.pazz.framework.log.ILogWriter;
import com.pazz.framework.log.entity.LogInfo;
import com.pazz.framework.log.entity.LogType;
import com.pazz.framework.util.DateUtils;
import com.pazz.framework.util.StreamUtils;
import com.pazz.framework.util.string.StringUtil;
import com.pazz.framework.web.context.AppContext;
import com.pazz.framework.web.context.RequestContext;
import com.pazz.framework.web.context.UserContext;
import com.pazz.framework.web.request.ContentCachingRequestWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 17:00
 * @description: 日志过滤器
 */
public class LogFilter extends OncePerRequestFilter {

    private Log log = LogFactory.getLog(getClass());
    /**
     * payload 最大长度
     */
    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 10000;
    /**
     * 是否打印日志
     */
    private boolean logPrint = true;
    /**
     * 是否打印请求request
     */
    private boolean logRequest = true;
    /**
     * 是否打印返回response
     */
    private boolean logResponse = true;
    /**
     * 是否包含header
     */
    private boolean includeHeaders = true;
    /**
     * 是否包含payload
     */
    private boolean includePayload = true;
    /**
     * 最大payload长度
     */
    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;
    /**
     * 过滤排除url
     */
    private Set<String> excludeUrlPatterns = Sets.newLinkedHashSet(Sets.newHashSet("/scripts/**/*", "/styles/**/*", "/WEB-INF/pages/**/*", "/images/**/*", "/**/*.html", "/**/*.jsp", "/**/*.js", "/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.gif"));

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private ILogWriter logWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        if (isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request);
            //记录请求日志
            logRequest(requestToUse);
        }
        HttpServletResponse responseToUse = response;
        if (isFirstRequest && !(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (!isAsyncStarted(request)) {
                logResponse(responseToUse);
                updateResponse(responseToUse);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

    /**
     * 更新response
     *
     * @param response
     * @throws IOException
     * @see ContentCachingResponseWrapper#copyBodyToResponse()
     */
    protected void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (responseWrapper != null) {
            responseWrapper.copyBodyToResponse();
        }
    }

    protected void logRequest(HttpServletRequest request) throws IOException {
        String payload = isIncludePayload() ? getRequestPlayload(request) : StringUtil.EMPTY_STRING;
        if (isLogPrint() && isLogRequest()) {
            log.info(createRequestMessage(request, payload, new Date()));
        }
        if (logWriter != null) {
            String content = StringUtil.defaultIfNull(request.getQueryString()) + payload;
            LogInfo log = getLogInfo(LogInfo.BEGIN_ACTION, content, new Date());
            logWriter.write(log);
        }
    }

    protected String getRequestPlayload(HttpServletRequest request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), Charset.forName(request.getCharacterEncoding()));
    }

    protected String createRequestMessage(HttpServletRequest request, String payload, Date requestDate) {
        StringBuilder msg = new StringBuilder();
        msg.append("Inbound Message\n----------------------------\n");
        msg.append("Address: ").append(request.getRequestURL()).append("\n");
        msg.append("QueryString: ").append(request.getQueryString()).append("\n");
        msg.append("RequestId: ").append(RequestContext.getCurrentContext().getRequestId()).append("\n");
        msg.append("IP: ").append(RequestContext.getCurrentContext().getIp()).append("\n");
        msg.append("RequestDate: ").append(DateUtils.convert(requestDate)).append("\n");
        msg.append("Encoding: ").append(request.getCharacterEncoding()).append("\n");
        msg.append("Content-Type: ").append(request.getContentType()).append("\n");
        if (isIncludeHeaders()) {
            msg.append("Headers: ").append(new ServletServerHttpRequest(request).getHeaders()).append("\n");
        }
        if (isIncludePayload()) {
            int length = Math.min(payload.length(), getMaxPayloadLength());
            msg.append("Payload: ").append(payload.substring(0, length)).append("\n");
        }
        msg.append("----------------------------------------------");
        return msg.toString();
    }

    protected void logResponse(HttpServletResponse response) {
        String payload = isIncludePayload() ? getResponsePayload(response) : StringUtil.EMPTY_STRING;
        if (isLogPrint() && isLogResponse()) {
            log.info(createResponseMessage(response, payload, new Date()));
        }
        if (logWriter != null) {
            LogInfo log = getLogInfo(LogInfo.END_ACTION, payload, new Date());
            logWriter.write(log);
        }
    }

    protected String getResponsePayload(HttpServletResponse response) {
        String payload = StringUtil.EMPTY_STRING;
        if (StringUtil.contains(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            ContentCachingResponseWrapper wrapper =
                    WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                payload = getPayloadFromBuf(buf, wrapper.getCharacterEncoding());
            }
        }
        return payload;
    }

    protected String getPayloadFromBuf(byte[] buf, String characterEncoding) {
        String payload = StringUtil.EMPTY_STRING;
        if (buf.length > 0) {
            int length = Math.min(buf.length, getMaxPayloadLength());
            try {
                payload = new String(buf, 0, length, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return payload;
    }

    protected String createResponseMessage(HttpServletResponse response, String payload, Date responseDate) {
        StringBuilder msg = new StringBuilder();
        msg.append("Outbound Message\n----------------------------\n");
        msg.append("RequestId: ").append(RequestContext.getCurrentContext().getRequestId()).append("\n");
        msg.append("ResponseDate: ").append(DateUtils.convert(responseDate)).append("\n");
        msg.append("Encoding: ").append(response.getCharacterEncoding()).append("\n");
        msg.append("Content-Type: ").append(response.getContentType()).append("\n");
        if (isIncludeHeaders()) {
            msg.append("Headers: ").append(new ServletServerHttpResponse(response).getHeaders()).append("\n");
        }
        if (isIncludePayload()) {
            int length = Math.min(payload.length(), getMaxPayloadLength());
            msg.append("Payload: ").append(payload.substring(0, length)).append("\n");
        }
        msg.append("----------------------------------------------");
        return msg.toString();
    }

    protected LogInfo getLogInfo(String tag, String content, Date date) {
        LogInfo info = new LogInfo();

        info.setRequestId(RequestContext.getCurrentContext().getRequestId());
        final String url = RequestContext.getCurrentContext()
                .getRemoteRequestURL();
        info.setUrl(url);
        info.setIp(RequestContext.getCurrentContext().getIp());
        info.setUserName(UserContext.getCurrentUser() == null ? "" : UserContext.getCurrentUser().getUserName());
        info.setDate(date);
        info.setAction(tag);
        info.setAppName(AppContext.getAppContext().getContextPath());
        info.setModuleName(RequestContext.getCurrentContext().getModuleName());
        info.setType(LogType.WEB);
        info.setArgs(content);
        return info;
    }

    /**
     * 新增过滤排除url
     *
     * @param urlPattern
     */
    public void addExcludeUrlPattern(String... urlPattern) {
        Collections.addAll(this.excludeUrlPatterns, urlPattern);
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludePayload() {
        return includePayload;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }

    public ILogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(ILogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public boolean isLogPrint() {
        return logPrint;
    }

    public void setLogPrint(boolean logPrint) {
        this.logPrint = logPrint;
    }

    public Set<String> getExcludeUrlPatterns() {
        return excludeUrlPatterns;
    }

    public void setExcludeUrlPatterns(Set<String> excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    public AntPathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(AntPathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }
}
