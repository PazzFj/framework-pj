package com.pazz.framework.web.xss;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Redirect implements Tactics {

    private HttpServletResponse response;

    private String path;

    public Redirect(HttpServletResponse response, String path) {
        this.response = response;
        this.path = path;
    }

    @Override
    public String process(String target, String regex) throws ParametersValidatorException {
        try {
            response.sendRedirect(path);
            throw new ParametersValidatorException("redirect");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
