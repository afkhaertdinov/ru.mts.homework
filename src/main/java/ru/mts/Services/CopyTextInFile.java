package ru.mts.Services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Класс, для копирования части одного файла в другой, имплементирует Runnable.
 * Через конструктор задаём файловый канал исходного и выходного файла, начальную и конечную позицию в файле.
 */
public class CopyTextInFile implements Runnable {
    FileChannel inputChannel;
    FileChannel outputChannel;
    long beginPosition;
    long endPosition;

    /**
     * Конструктор инициализируешь параметры экземпляра класса
     * @param inputChannel  // Канал исходного файла
     * @param outputChannel // Канал выходного файла
     * @param beginPosition // Начальная позицию, с которой (включительно) начнётся копирование
     * @param endPosition   // Конечная позиция, до которой (не включительно) будет выполнено копирование
     */
    public CopyTextInFile(FileChannel inputChannel, FileChannel outputChannel, long beginPosition, long endPosition) {
        if (inputChannel.equals(outputChannel))
            throw new IllegalArgumentException("Выходной и входной каналы не могут быть одинаковы");
        if (endPosition <= beginPosition)
            throw new IllegalArgumentException("Конечная позиция должна быть больше начальной");

        this.inputChannel = inputChannel;
        this.outputChannel = outputChannel;
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
    }

    /**
     * Метод копирует данные из файлового канала inputChannel в outputChannel, в диапазоне [beginPosition:endPosition),
     * которые предварительно инициализируются через конструктор
     */
    @Override
    public void run() {
        try {
            MappedByteBuffer inputMappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, beginPosition, endPosition - beginPosition);
            if (inputMappedByteBuffer != null) {
                if (inputMappedByteBuffer.hasRemaining()) {
                    byte[] data = new byte[inputMappedByteBuffer.remaining()];
                    inputMappedByteBuffer.get(data);

                    MappedByteBuffer outputMappedByteBuffer = outputChannel.map(FileChannel.MapMode.READ_WRITE, beginPosition, endPosition - beginPosition);
                    if (outputMappedByteBuffer != null) {
                        outputMappedByteBuffer.put(data);
                        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data));
                        System.out.printf("---------------------------%n%s скопировал данный текст:%n%s%n", Thread.currentThread().getName(), charBuffer);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
