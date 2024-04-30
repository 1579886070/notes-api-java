package com.zxxwl.common.utils.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class FileService {
    protected String dir;

    FileService(){
    }

    public File getFile(String path){
        File file = new File(path);
        if( !file.exists() || !file.isFile() )
            return null;

        return file;
    }

    FileInputStream toStream(File file){
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        return stream;
    }

    static boolean isUnix(){
        return File.separator.equals("/");
    }
}
