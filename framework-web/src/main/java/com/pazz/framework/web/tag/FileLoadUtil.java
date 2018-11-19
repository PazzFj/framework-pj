package com.pazz.framework.web.tag;

import com.pazz.framework.holder.WebApplicationContextHolder;
import com.pazz.framework.web.context.AppContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:34
 * @description: 提供文件操作的的工具类
 */
public final class FileLoadUtil {

    /**
     * <p>通过模块名称和文件名称查找文件</p>
     */
    public static Resource[] getResourcesForClasspath(String moduleName, String fileName) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + AppContext.getAppContext().getPackagePrefix() + "**/" + moduleName
                + "/server/META-INF/" + fileName);
        return resources;
    }

    /**
     * <p>通过模块名称和文件名称查找文件</p>
     */
    public static InputStream getInputStreamForClasspath(String moduleName, String fileName) throws FileNotFoundException, IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + AppContext.getAppContext().getPackagePrefix() + "**/" + moduleName
                + "/server/META-INF/" + fileName);
        if (resources == null || resources.length < 1) {
            throw new FileNotFoundException("file '" + fileName + "' not found in this module");
        }
        InputStream in = resources[0].getInputStream();
        return in;
    }

    public static InputStream getInputStreamForClasspath(String fileName) throws FileNotFoundException, IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver
                .getResources("classpath*:" + fileName);
        if (resources == null || resources.length < 1) {
            throw new FileNotFoundException("file '" + fileName + "' not found in this root path!");
        }
        InputStream in = resources[0].getInputStream();
        return in;
    }

    public static Resource[] getResourcesForServletpath(String path) throws IOException {
        Resource[] resources = WebApplicationContextHolder.getWebApplicationContext().getResources(path);
        return resources;
    }

    public static InputStream getInputStreamForServletpath(String filePath) throws FileNotFoundException, IOException {
        Resource[] resources = WebApplicationContextHolder.getWebApplicationContext().getResources(filePath);
        if (resources == null || resources.length < 1) {
            throw new FileNotFoundException("file '" + filePath + "' not found in this module");
        }
        InputStream in = resources[0].getInputStream();
        return in;
    }

}
