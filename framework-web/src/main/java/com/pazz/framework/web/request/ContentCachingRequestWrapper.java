package com.pazz.framework.web.request;

import com.pazz.framework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:25
 * @description: 缓存请求内容
 */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RequestCachingInputStream(body);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return  new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    private  class RequestCachingInputStream extends ServletInputStream{

        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }
        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener) {
        }
    }

}
