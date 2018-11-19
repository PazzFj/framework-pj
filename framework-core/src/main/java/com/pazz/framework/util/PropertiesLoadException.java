package com.pazz.framework.util;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:43
 * @description: Properties加载异常
 */
public class PropertiesLoadException extends RuntimeException {

    private static final long serialVersionUID = 3739566355873756268L;

    public PropertiesLoadException() {
        super();
    }

    public PropertiesLoadException(String error) {
        super(error);
    }

    public PropertiesLoadException(String error, Exception e) {
        super(error, e);
    }

}
