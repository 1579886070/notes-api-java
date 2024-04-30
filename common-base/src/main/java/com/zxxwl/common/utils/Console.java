package com.zxxwl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console {
    protected static Logger logger = null;

    private static final boolean isDev = true;

    public static void CreateLogger(Class name){
        logger = LoggerFactory.getLogger(name);
    }

    public static void log(String message){
        if( isDev )
            System.out.println(new StringBuilder().append("Info: ").append(message).toString());
        else
            logger.info(message);
    }

    public static void log(String... messages){
        for (String message: messages) {
            System.out.print(message);
            System.out.print(" ");
        }

        System.out.print("\n");
    }

    public static void bug(String message){
        if( isDev )
            System.out.println(new StringBuilder().append("Error: ").append(message).toString());
        else
            logger.error(message);
    }

    public static void bug(String... messages){
        for(String message: messages)
            Console.bug(message);
    }
}