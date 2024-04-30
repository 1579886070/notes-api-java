package com.zxxwl.common.utils.media;



import com.zxxwl.common.utils.Console;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DocService extends FileService {

    public byte[] read(String path){
        File file = this.getFile(path);

        if( file == null )
            return null;

        FileInputStream stream = this.toStream(file);
        if( stream == null )
            return null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        while (true){
            try {
                int size = stream.read(buffer, 0, 1024);
                if( size == -1)
                    break;

                bytes.write(buffer);
            } catch (IOException e) {
                return null;
            }
        }

        try {
            stream.close();
        } catch (IOException e) {
            Console.log(e.getMessage());
        }

        try {
            bytes.flush();
        } catch (IOException e) {
            Console.log(e.getMessage());
        }

        byte[] result = bytes.toByteArray();
        try {
            bytes.close();
        } catch (IOException e) {
            Console.log(e.getMessage());
        }

        return result;
    }
}
