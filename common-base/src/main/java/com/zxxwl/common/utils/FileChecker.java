package com.zxxwl.common.utils;

import net.sf.jmimemagic.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileChecker {
    public enum Mime{
        Image,
        Video,
        Audio,
        File,
        ALL,
    }

    public static Set<String> IMAGE_TYPES = new HashSet<>(){{
        add("image/jpeg");
        add("image/webp");
        add("image/jpg");
        add("image/png");
        add("image/bmp");
        add("image/gif");
    }};

    public static Set<String> VIDEO_TYPES = new HashSet<>(){{
        add("video/mpeg");
        add("video/mp4");
        add("video/ogg");
        add("video/quicktime");
    }};

    public static Set<String> AUDIO_TYPES = new HashSet<>(){{
        add("audio/mid");
        add("audio/mpeg");
        add("audio/basic");
        add("audio/x-wav");
        add("audio/x-aiff");
        add("audio/x-mpegurl");
        add("audio/x-pn-realaudio");
    }};

    public static Set<String> FILE_TYPES = new HashSet<>(){{
        add("application/x-7z-compressed");
        add("application/zip");
        add("application/x-rar");
        add("application/msword");
        add("application/vnd.ms-excel");
        add("application/rtf");
        add("text/plain");
        add("application/pdf");
        add("application/vnd.ms-powerpoint");
        add("application/octet-stream");
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }};

    public static String mime(File file){
        try {
            MagicMatch match = Magic.getMagicMatch(file, true, false);
            return match.getMimeType();
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            return null;
        }
    }

    public static boolean check(String text, Mime mime){
        switch (mime){
            case File:
                return FILE_TYPES.contains(text);
            case Image:
                return IMAGE_TYPES.contains(text);
            case Video:
                return VIDEO_TYPES.contains(text);
            case Audio:
                return AUDIO_TYPES.contains(text);
            case ALL:
                return FILE_TYPES.contains(text)
                        || IMAGE_TYPES.contains(text)
                        || VIDEO_TYPES.contains(text)
                        || AUDIO_TYPES.contains(text);
        }

        return false;
    }
}
