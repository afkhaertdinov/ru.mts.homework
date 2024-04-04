package ru.mts.Services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class RunFile {
    public void inpRun(FileChannel inputChannel, FileChannel outputChannel, long beginPosition, long endPosition) throws IOException {
        ByteBuffer charBuffer = null;

        MappedByteBuffer inputMappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, beginPosition, endPosition - beginPosition);
        if (inputMappedByteBuffer != null) {
            if(inputMappedByteBuffer.hasRemaining()) {
                byte[] data = new byte[inputMappedByteBuffer.remaining()];
                inputMappedByteBuffer.get(data);

                MappedByteBuffer outputMappedByteBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, beginPosition, endPosition - beginPosition);
//                System.out.println("position=" + charBuffer.position()); System.out.println("limit=" + charBuffer.limit());
                if (outputMappedByteBuffer != null) {
                    outputMappedByteBuffer.put(data);
            }

//            charBuffer = Charset.forName("UTF-8").decode(inputMappedByteBuffer);
//            System.out.println(charBuffer);
        }
//                    Charset.forName("utf-8").encode(charBuffer));

        }

    }

}
