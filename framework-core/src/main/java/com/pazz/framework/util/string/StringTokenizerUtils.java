package com.pazz.framework.util.string;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:55
 * @description:
 */
public class StringTokenizerUtils {

    /**
     * 将str将多个分隔符进行切分，
     * <p>
     * 示例：StringTokenizerUtils.split("1,2;3 4"," ,;");
     * 返回: ["1","2","3","4"]
     *
     * @param str
     * @param seperators
     * @return
     */
    @SuppressWarnings("all")
    public static String[] split(String str, String seperators) {
        StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
        List result = new ArrayList();

        while (tokenlizer.hasMoreElements()) {
            Object s = tokenlizer.nextElement();
            result.add(s);
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

}