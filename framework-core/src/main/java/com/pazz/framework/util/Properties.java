package com.pazz.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:42
 * @description: 读取UTF-8字符集的properties
 */
public class Properties extends HashMap<String, String> {

    private static final long serialVersionUID = 6844431364587351965L;
    private static final String CHARSET_NAME = "UTF-8";

    public Properties() {
        super();
    }

    public Properties(Properties defaults) {
        putAll(defaults);
    }

    public String getProperty(String key) {
        return get(key);
    }

    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public void load(Reader reader) throws IOException {
        loadInputStream(new ReaderInputStream(reader));
    }

    public synchronized void load(InputStream inStream) throws IOException {
        loadInputStream(new ReaderInputStream(inStream));
    }

    private void loadInputStream(ReaderInputStream lr) throws IOException {
        String line;
        BufferedReader bufferedReader = lr.reader();
        int count = 0;
        while (null != (line = bufferedReader.readLine())) {
            count++;
            line = line.trim();
            if (null == line || line.length() == 0) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            String[] strs = line.split("=", 2);
            if (strs.length != 2) {
                throw new PropertiesLoadException("load properties error,line " + count + ":\"" + line + "\" config error");
            }
            String key = strs[0].trim();
            String value = strs[1].trim();
            put(key, value);
        }
    }

    class ReaderInputStream {
        public ReaderInputStream(InputStream inStream) {
            this.inStream = inStream;
        }

        public ReaderInputStream(Reader reader) {
            this.reader = reader;
        }

        InputStream inStream;
        Reader reader;
        BufferedReader bufferedReader;

        BufferedReader getBufferedReader() {
            return bufferedReader;
        }

        BufferedReader reader() throws IOException {
            if (null != inStream) {
                return bufferedReader = new BufferedReader(
                        new InputStreamReader(inStream, CHARSET_NAME));
            } else {
                return bufferedReader = new BufferedReader(reader);
            }
        }
    }

}
