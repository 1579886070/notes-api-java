package com.zxxwl.common.utils.media;


import com.zxxwl.common.utils.Console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VideoService extends FileService {

    protected String dir;

    public int start = 0;
    public int size  = 20480000; // 20M

    public int end = 0;

    public VideoService(){
    }

    public VideoService slice(int start, int end, int maxSize){
        int last = maxSize - 1;

        end = Math.min(last, end);
        start = Math.max(0, start);

        if( this.size > maxSize )
            this.size = maxSize;

        if( end > start )
            size = end - start;

        this.start = start;
        this.end   = start + size - 1;

        return this;
    }

    public byte[] read(String path){
        File file = this.getFile(path);

        if( file == null )
            return null;

        FileInputStream stream = this.toStream(file);
        if( stream == null )
            return null;

        byte[] bytes = new byte[size];
        try {
            stream.read(bytes, start, size);
        } catch (IOException e) {
            Console.bug(e.getMessage());
        }finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }
}
