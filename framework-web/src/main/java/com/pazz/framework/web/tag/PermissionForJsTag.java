package com.pazz.framework.web.tag;

import com.pazz.framework.web.context.RequestContext;
import com.pazz.framework.web.security.SecurityAccessor;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:51
 * @description: 判断url是否有权限，将相关信息封装到javascript对象中，并返回
 */
public class PermissionForJsTag extends SimpleTagSupport {

    /**
     * url
     */
    private String urls;

    /**
     * SimpleTagSupport标签执行的主方法
     * 调用权限访问控制器，遍历传入的urls的字符串，查找出每一个url对应的权限信息，将键和权限信息封装成一个javascript对象，放入到script 标签中，并返回
     *
     * @throws JspException
     * @throws IOException
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     * doTag
     * @since JDK1.6
     */
    @Override
    public void doTag() throws JspException, IOException {
        if (this.urls == null || "".equals(this.urls)) {
            return;
        }
        String[] urlArray = parseUrls(urls);
        StringBuilder msgObject = new StringBuilder("");
        msgObject.append("<script type='text/javascript'> \n");
        msgObject.append(RequestContext.getCurrentContext().getModuleName());
        msgObject.append(".isPermission = function (url){ \n");
        msgObject.append("permissions = {");
        for (String url : urlArray) {
            boolean message = SecurityAccessor.hasAccessSecurity(url);
            msgObject.append("'" + url + "'" + ":" + message + ",");
        }
        msgObject.deleteCharAt(msgObject.lastIndexOf(","));
        msgObject.append("}; \n");
        msgObject.append("return permissions[url]; \n");
        msgObject.append("}; \n");
        msgObject.append("</script>");
        getJspContext().getOut().write(msgObject.toString());
    }

    /**
     * 设置url字符串
     * setUrls
     *
     * @param urls
     * @return void
     * @since JDK1.6
     */
    public void setUrls(String urls) {
        this.urls = urls;
    }

    /**
     * 解析字符串，获得url组成的数组
     * parseUrls
     *
     * @return String[]
     * @since JDK1.6
     */
    private String[] parseUrls(String urls) {
        String u = urls;
        u = u.replaceAll("\n", "");
        u = u.replaceAll("\r", "");
        String[] urlArray = u.split(",");
        for (int i = 0; i < urlArray.length; i++) {
            urlArray[i] = urlArray[i].trim();
        }
        return urlArray;
    }

}
