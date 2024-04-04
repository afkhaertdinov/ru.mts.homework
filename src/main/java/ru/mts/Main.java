package ru.mts;

import ru.mts.DTO.СounterAtomic;
import ru.mts.Services.RunFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
//    static String inputFile = "src/main/java/ru/mts/Hamlet.txt";
    static String inputFile = "src/main/java/ru/mts/Onegin.txt";
    static String outputFile = "src/main/java/ru/mts/Tatyana.txt";


    public static void main(String[] args) throws IOException {

//TODO 1. Потокобезопасный счетчик: Создайте класс счетчика,
//        который можно увеличивать из нескольких потоков без использования синхронизации.
//        Используйте атомарные операции или классы из пакета java.util.concurrent.atomic.
/*
        {
            СounterAtomic counter = new СounterAtomic();
            Runnable runnable = () -> {
                for (int i = 0; i < 9999; i++) {
                    counter.increment();
                }
            };
            for (int i = 0; i <= 8; i++)
                new Thread(runnable).start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(counter.getValue());
        }
*/

//TODO 2. Чтение и запись в файл параллельно:
//        Разделите файл на части и создайте потоки для параллельного чтения и записи в разные части файла.
//        Прочитанный текст выведите в консоль.
        {
            List<String> strings = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            Files.deleteIfExists(Path.of(outputFile));
            String string = "";
            RunFile runFile1 = new RunFile();
            RunFile runFile2 = new RunFile();
            RunFile runFile3 = new RunFile();
/*
            while ((string = reader.readLine()) != null)
                strings.add(string);
            strings.forEach(System.out::println);
*/
            try (FileInputStream fileInputStream = new FileInputStream(inputFile); RandomAccessFile fileOutputStream = new RandomAccessFile(outputFile, "rw")) {
                FileChannel inputChannel = fileInputStream.getChannel();
                FileChannel outputChannel = fileOutputStream.getChannel();
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

                long channelSize = inputChannel.size();
                int kol = 3;
//                int counter = (int) (channelSize / 1024) + 1;
                long bufferSize = channelSize / kol;
//                while (1024 < bufferSize) {
//                    bufferSize = bufferSize / kol;
//                    counter++;
//                }
//                long position = 1 * bufferSize * counter - 1;
                System.out.println("channelSize = " + channelSize);
//                System.out.println("buferSize = " + bufferSize);
//                System.out.println("counter = " + counter);
//                System.out.println("position = " + position);
//                bufferSize = 512;
                runFile1.inpRun(inputChannel,outputChannel,0,bufferSize);
                System.out.println();
                runFile3.inpRun(inputChannel,outputChannel,bufferSize*2,channelSize);
                System.out.println();
                runFile2.inpRun(inputChannel,outputChannel,bufferSize,bufferSize*2);

                System.out.println("Печать созданного файла:");
                System.out.println();
                MappedByteBuffer mappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, channelSize);
                if (mappedByteBuffer != null) {
                    CharBuffer charBuffer = null;
                    charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
                    System.out.println(charBuffer);

                }


/*
                MappedByteBuffer mappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 128, channelSize - 128);
                CharBuffer charBuffer = null;
                if (mappedByteBuffer != null) {
                    charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
                    System.out.println("test1 - " + charBuffer);
                }
                mappedByteBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, 128, channelSize - 128);

                if (mappedByteBuffer != null) {
                    mappedByteBuffer.put(
                            Charset.forName("utf-8").encode(charBuffer));
                }
*/
/*
charBuffer = null;
                mappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 128+256, 256);
                if (mappedByteBuffer != null) {
                    charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
                    System.out.println("test2 - " + charBuffer);
                }
                mappedByteBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, 128+256, 256);

                if (mappedByteBuffer != null) {
                    mappedByteBuffer.put(
                            Charset.forName("utf-8").encode(charBuffer));
                }
*/



//                    ByteBuffer buff = ByteBuffer.allocate(bufferSize);

//                    if (bufferSize > inputChannel.channelSize()) {
//                        bufferSize = (int) inputChannel.channelSize();
//                    }
/*
                for (int i = 0; i < counter; i++){
                    inputChannel.read(buff,bufferSize);
                    //outputChannel.position(bufferSize*2);
                    string = new String(buff.array(), StandardCharsets.UTF_8);
                    System.out.println(string);
                    outputChannel.write(ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8), 0, buff.position()));
//                    outputChannel.write(ByteBuffer.wrap(buff.array()));
                            //ByteBuffer.wrap(buff.array(), 0, bufferSize));
                    buff.flip();
                    System.out.println(outputChannel.channelSize());
                }
*/

            }
//                int bufferSize = 1024;


//                outputChannel.position(0);
/*
                while (inputChannel.read(buff) > 0) {
                    System.out.println(new String(buff.array(), StandardCharsets.UTF_8));
//                    byteOut.write(buff.array(), 0, buff.position());
                    outputChannel.write(ByteBuffer.wrap(buff.array(), 0, buff.position()));
                    //write(ByteBuffer.wrap(buff.array()));
//                    position += bufferSize;
                    System.out.println(outputChannel.channelSize());
//                    outputChannel.close();
                    buff.flip();
//                    buff.clear();
                }
*/


//                string = byteOut.toString(StandardCharsets.UTF_8);
//                System.out.println(string);
        }

    }

}
