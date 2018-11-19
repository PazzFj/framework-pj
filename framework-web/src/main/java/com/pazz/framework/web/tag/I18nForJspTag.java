package com.pazz.framework.web.tag;

import com.pazz.framework.web.message.MessageBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:35
 * @description: 提供前台jsp页面使用的国际化标签
 */
public class I18nForJspTag extends SimpleTagSupport {

    //国际化信息key
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void doTag() throws JspException, IOException {
        MessageBundle messageBundle = new MessageBundle();
        getJspContext().getOut().write(messageBundle.getMessage(key));
    }

}
