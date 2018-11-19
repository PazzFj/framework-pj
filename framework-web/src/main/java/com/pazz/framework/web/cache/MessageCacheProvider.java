package com.pazz.framework.web.cache;

import com.pazz.framework.cache.provider.IBatchCacheProvider;
import com.pazz.framework.util.Properties;
import com.pazz.framework.web.context.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:46
 * @description: 国际化缓存数据提供者
 */
@Component("messageCacheProvider")
public class MessageCacheProvider implements IBatchCacheProvider<String, Properties> {

    private Log logger = LogFactory.getLog(MessageCacheProvider.class);
    private Date startTime = new Date();

    /**
     * get
     */
    @Override
    public Map<String, Properties> get() {
        Map<String, Properties> map = new HashMap<String, Properties>();
        final String prefix = AppContext.getAppContext().getPackagePrefix();
        try {
            //加载指定位置的properties文件
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:" + prefix
                    + "**/server/META-INF/messages/message*.properties");
            for (Resource resource : resources) {
                String path = resource.getURL().getPath();
                String classpath = path.substring(path.lastIndexOf(prefix));
                if (logger.isInfoEnabled()) {
                    logger.info("[Framework] add message bundle: " + classpath);
                }
                //取得文件名
//                final String moduleName = classpath.replaceAll("/server/META-INF/.*$", "").replaceAll("^.*/", "");
                final String localeName = classpath.replaceAll(".*\\/message([_a-zA-Z]*)\\.properties$", "$1");
                Properties properties = new Properties();
                InputStream in = resource.getInputStream();
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
//                map.put(moduleName + localeName, properties);

                //一个语言类型，不同模块下的语言文件合并到一个properties中
                String locale = localeName;
                if (StringUtils.isBlank(locale)) {
                    //文件没有语言标识后缀的默认为英文
                    locale = Locale.US.toString();
                }
                if (locale.startsWith("_")) {
                    locale = locale.substring(1);
                }
                Properties p = map.get(locale);
                if (p != null) {
                    p.putAll(properties);
                    map.put(locale, p);
                } else {
                    map.put(locale, properties);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    /**
     * getLastModifyTime
     */
    @Override
    public Date getLastModifyTime() {
        return startTime;
    }


}
