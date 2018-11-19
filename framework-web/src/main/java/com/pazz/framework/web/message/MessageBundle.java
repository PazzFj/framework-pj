package com.pazz.framework.web.message;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.ICache;
import com.pazz.framework.util.Properties;
import com.pazz.framework.web.cache.AbstractDynamicMessageCache;
import com.pazz.framework.web.cache.MessageCache;
import com.pazz.framework.web.context.UserContext;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:37
 * @description: 国际化资源接口实现类
 */
@Component
public class MessageBundle implements IMessageBundle {

    /**
     * @param locale
     * @param key
     * @param args
     * @return
     * @since:0.6
     */
    @Override
    public String getMessage(Locale locale, String key, Object... args) {
        if (key == null) {
            return null;
        }
        MessageCache cache = (MessageCache) CacheManager.getInstance().getCache(MessageCache.UUID);
        if (locale == null) {
            //没有传入locale的用服务器系统默认的locale
            locale = Locale.getDefault();
        }
        Properties properties = cache.get(locale.toString());
        if (properties != null) {
            String value = properties.getProperty(key, key);
            if (!value.equals(key)) {
                // 格式化
                return (args == null || args.length == 0) ? value
                        : MessageFormat.format(value, args);
            }
        }

        // 没有国际化信息返回key
        return key;

    }

    /**
     * @param key
     * @param args
     * @return
     * @since:0.6
     */
    @Override
    public String getMessage(String key, Object... args) {
        return getMessage(UserContext.getUserLocale(), key, args);
    }

    /**
     * @param key
     * @param args
     * @return
     * @since:0.6
     */
    @Override
    public String getDynamicMessage(String key, Object... args) {
        return getDynamicMessage(UserContext.getUserLocale(), key, args);
    }

    /**
     * @param locale
     * @param key
     * @param args
     * @return
     * @since:0.6
     */
    @Override
    public String getDynamicMessage(Locale locale, String key, Object... args) {
        ICache<String, Map<String, String>> cache = CacheManager.getInstance()
                .getCache(AbstractDynamicMessageCache.UUID);
        Map<String, String> map = cache.get(locale.toString());
        if (map == null) {
            return key;
        }
        String message = map.get(key);
        if (message == null) {
            return key;
        }
        return (args == null || args.length == 0) ? message : MessageFormat
                .format(message, args);
    }


}
