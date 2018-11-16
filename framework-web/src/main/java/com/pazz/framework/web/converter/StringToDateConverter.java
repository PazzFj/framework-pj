package com.pazz.framework.web.converter;

import com.pazz.framework.util.string.StringUtil;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: 彭坚
 * @create: 2018/11/16 16:08
 * @description: String 转换成 日期
 */
public class StringToDateConverter implements Converter<String, Date> {

    private static final String DATE_FROMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private String datePattern;

    public StringToDateConverter() {

    }

    public StringToDateConverter(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public Date convert(String source) {
        if (StringUtil.isNotBlank(source)) {
            try {
                if (source.length() > 10 && source.charAt(10) == 'T') {
                    source = source.replace('T', ' '); // 去掉j'T'
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(StringUtil.isNotBlank(datePattern) ? datePattern : (source.length() == 10 ? DATE_FROMAT : DATE_TIME_FORMAT));
                dateFormat.setLenient(false);
                return dateFormat.parse(source);
            } catch (ParseException e) {
                throw new IllegalArgumentException("invalid date format. Please use this pattern\"" + datePattern + "\"");
            }
        }
        return null;
    }
}