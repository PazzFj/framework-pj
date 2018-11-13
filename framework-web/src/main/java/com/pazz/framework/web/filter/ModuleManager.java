package com.pazz.framework.web.filter;

import com.pazz.framework.util.file.IOUtils;
import com.pazz.framework.web.context.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:28
 * @description: struts模块管理器
 */
public final class ModuleManager {

    private static final Log LOGGER = LogFactory.getLog(ModuleManager.class);

    private static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private static final String PACKAGE_PREFIX = "net/yto/";

    private static final String PAGES_FROM = "/server/META-INF/pages/";

    private static final String PAGES_TO = "/WEB-INF/pages/";

    private static final String PAGES_SUFFIX = "";

    private static final String SCRIPTS_FROM = "/server/META-INF/scripts/";

    private static final String SCRIPTS_TO = "/scripts/";

    private static final String SCRIPTS_SUFFIX = "";

    private static final String STYLES_FROM = "/server/META-INF/styles/";

    private static final String STYLES_TO = "/styles/";

    private static final String STYLES_SUFFIX = "";

    private static final String IMAGES_FROM = "/server/META-INF/images/";

    private static final String IMAGES_TO = "/images/";

    private static final String IMAGES_SUFFIX = "";

    private ModuleManager() {
    }

    /**
     * 导出所有资源
     */
    public static void export(ServletContext servletContext) {
        //导出页面
        export(servletContext, PAGES_FROM, PAGES_TO, PAGES_SUFFIX);
        //导出js脚本
        export(servletContext, SCRIPTS_FROM, SCRIPTS_TO, SCRIPTS_SUFFIX);
        //导出css
        export(servletContext, STYLES_FROM, STYLES_TO, STYLES_SUFFIX);
        //导出图片
        export(servletContext, IMAGES_FROM, IMAGES_TO, IMAGES_SUFFIX);
    }

    /**
     * 导出资源
     */
    private static void export(ServletContext servletContext, String from, String to, String suffix) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("[Framework] servlet root dir: " + servletContext.getRealPath("/"));
            }
            String packagePrefix = StringUtils.isEmpty(AppContext.getAppContext().getPackagePrefix()) ? PACKAGE_PREFIX : AppContext.getAppContext().getPackagePrefix();
            Resource[] resources = resolver.getResources("classpath*:" + packagePrefix + "**" + from + "**" + suffix);
            if (resources != null && resources.length > 0) {
                for (Resource resource : resources) {
                    try {
                        String path = resource.getURL().getPath();

                        int j = path.lastIndexOf(from);

                        String pathHeader = path.substring(0, j);

                        int i = pathHeader.lastIndexOf('/');
                        String module = path.substring(i + 1, j);
                        String page = path.substring(j + from.length());
                        String dist = to + module + "/" + page;

                        File file = new File(servletContext.getRealPath(dist));
                        if (!file.getName().matches("^.*[.].*$")) {
                            File dir = new File(file.getPath() + "\\" + File.separator);
                            //先创建目录
                            if (!dir.exists()) {
                                boolean b = file.mkdirs();
                                if (!b) {
                                    LOGGER.error("create dir failure!");
                                }
                                if (LOGGER.isInfoEnabled()) {
                                    LOGGER.info("[Framework] create dir: " + dir);
                                }
                            }
                        }
                        File dir = file.getParentFile();
                        if (!dir.exists()) {
                            boolean b = dir.mkdirs();
                            if (!b) {
                                LOGGER.error("creates dir failure!");
                            }
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("[Framework] create dir: " + dir);
                            }
                        }
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("[Framework] release resource: " + dist);
                        }
                        OutputStream out = new FileOutputStream(file);
                        InputStream in = resource.getInputStream();
                        IOUtils.copy(in, out, true);

                        //设置释放文件的最后修改时间
                        file.setLastModified(resource.lastModified());

                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
