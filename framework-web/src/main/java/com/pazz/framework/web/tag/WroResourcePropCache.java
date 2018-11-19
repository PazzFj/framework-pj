package com.pazz.framework.web.tag;

import com.pazz.framework.util.PropertiesHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:53
 * @description: 页面静态文件版本信息缓存
 */
public class WroResourcePropCache {

    private static final Log logger = LogFactory
            .getLog(WroResourcePropCache.class);

    private static final WroResourcePropCache INSTANCE = new WroResourcePropCache();

    private boolean devConfig = false;

    /**
     * 保存所有缓存实例
     */
    private final Map<String, Properties> caches = new ConcurrentHashMap<String, Properties>();

    /**
     * 禁止从外部拿到实例
     * 创建一个新的实例 WroResourcePropCache.
     */
    private WroResourcePropCache() {
        Properties frameworkConfig = new Properties();
        try {
            InputStream in = FileLoadUtil.getInputStreamForClasspath("framework.properties");
            PropertiesHelper propHelper = new PropertiesHelper(frameworkConfig);
            propHelper.load(in);
            devConfig = propHelper.getBoolean("dev", false);
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    public static boolean getDevConfig() {
        return WroResourcePropCache.getInstance().devConfig;
    }

    public static WroResourcePropCache getInstance() {
        return INSTANCE;
    }

    /**
     * 根据wroResPropId获取缓存实例
     */
    public Properties getWroResourceInfo(String moduleName) {
        String wroResPropId = "wro_" + moduleName;
        Properties wroResPropInfo = caches.get(wroResPropId);
        if (WroResourcePropCache.getDevConfig() || wroResPropInfo == null) {
            wroResPropInfo = new Properties();
            try {
                InputStream in = FileLoadUtil.getInputStreamForClasspath(moduleName, "wromapping.properties");
                wroResPropInfo.load(in);
                caches.put(wroResPropId, wroResPropInfo);
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
        return wroResPropInfo;
    }

}
