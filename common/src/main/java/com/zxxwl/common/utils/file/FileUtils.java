package com.zxxwl.common.utils.file;

import javax.activation.MimetypesFileTypeMap;

public class FileUtils {
    public static final String VIDEO = "video";
    public static final String IMAGE = "image";
    public static final String SEPARATOR = "/";
    public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件后缀名 包含`.`
     *
     * @param fileName 文件名
     * @return extensionWithDot
     */
    public static String getExtensionWithDot(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取 ContentType
     *
     * @param contentType ContentType
     * @param suffix2Dot  suffix2Dot
     * @return contentType
     * @apiNote 不会返回空
     */
    public static String getContentType(String contentType, String suffix2Dot) {
        if (MIME_APPLICATION_OCTET_STREAM.equals(contentType)) {
            // 获取 真实 contentType
            return new MimetypesFileTypeMap().getContentType(suffix2Dot);
        }
        return contentType;
    }

    @Deprecated
    private String getNewName(String suffix) {
//        return DateUtils.dateTimeNow() + "-" + IdUtils.fastSimpleUUID() + suffix;
        return "";
    }
}
