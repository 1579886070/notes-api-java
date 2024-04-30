package com.zxxwl.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Constants {
    public static final Charset DefaultCharset = StandardCharsets.UTF_8;
    public static final String DefaultLanguage = "zh";

    public static boolean isEmpty(String item) {
        return item == null || item.equalsIgnoreCase("");
    }

    public static boolean isNumberString(String item) {
        if (isEmpty(item))
            return false;

        String regex = "^-?\\d+(\\.\\d+)?$";

        return Pattern.matches(regex, item);
    }

    /*public static Request request(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if( attributes == null )
            return null;

        HttpServletRequest req = attributes.getRequest();
        if( req == null )
            return null;

        String uri = req.getRequestURI().substring(1);
        int end = uri.indexOf('/');
        String namespace = end > 0 ? uri.substring(0, end) : uri;

        if( namespace.equalsIgnoreCase("api") )
            return Request.init(req, namespace);
        else
            return Request.init(req);
    }*/
}
