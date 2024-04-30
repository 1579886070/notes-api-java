package com.zxxwl.common.crypto;

import java.util.UUID;

public class Random {
    public static String uuid(){
        return UUID.randomUUID().toString();
    }

    public static int number(int min, int max){
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

    public static String code(int length){
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < length; i++)
            code.append((char)Random.number(48, 57));

        return code.toString();
    }

    public static String chars(int length){
        return Random.chars(length, true);
    }

    public static String chars(int length, boolean safe){
        int max = 255;

        if( safe )
            max = 61;

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < length; i++){
            if( safe ){
                int code = Random.number(0, max);
                if( code < 10 )
                    code += 48;
                else if ( code < 36 )
                    code += 55;
                else
                    code += 61;

                builder.append((char) code);
            }
            else
                builder.append((char)Random.number(0, max));
        }

        return builder.toString();
    }
}