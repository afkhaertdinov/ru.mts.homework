package ru.mts;

import ru.mts.DTO.СounterAtomic;
import ru.mts.Services.CopyTextInFile;
import ru.mts.Services.RowFibonacci;

import java.io.*;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

//TODO 1. Потокобезопасный счетчик: Создайте класс счетчика,
//        который можно увеличивать из нескольких потоков без использования синхронизации.
//        Используйте атомарные операции или классы из пакета java.util.concurrent.atomic.
        {
            final int NUMBER_OF_THREADS = 8; // Количество создаваемых потоков

            СounterAtomic counter = new СounterAtomic(); // Создаём экземпляр класса, счётчик которого будем увеличивать
            Runnable runnable = () -> {  // Задача, в которой будет увеличиваться счётчик counter
                for (int i = 0; i < 9999; i++) {
                    counter.increment();
                }
            };

            for (int i = 0; i <= NUMBER_OF_THREADS; i++) // Создаём NUMBER_OF_THREADS потоков и запускаем
                new Thread(runnable).start();

            try { // Даём время потокам поработать
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // Верим в лучшее, готовимся к худшему
            }

            System.out.println("Задание № 1: Потокобезопасный счетчик.");
            System.out.println("Значение счётчика итератора = " + counter.getValue()); // Проверяем результат
        }

//TODO 2. Чтение и запись в файл параллельно:
//        Разделите файл на части и создайте потоки для параллельного чтения и записи в разные части файла.
//        Прочитанный текст выведите в консоль.
        {
            String inputFile = "src/main/java/ru/mts/Onegin.txt"; // Входной файл (то, что написал Онегин)
            String outputFile = "src/main/java/ru/mts/Tatyana.txt"; // Получаемый файл (то, что возможно получила Татьяна)
            Files.deleteIfExists(Path.of(outputFile)); // Удаляем файл, если он существует, затем создадим новый.
            final int NUMBER_OF_THREADS = 5; // Количество создаваемых потоков
            List<Thread> threads = new ArrayList<>(NUMBER_OF_THREADS); // Тут будет список потоков

            try (FileInputStream fileInputStream = new FileInputStream(inputFile); RandomAccessFile fileOutputStream = new RandomAccessFile(outputFile, "rw")) {
                FileChannel inputChannel = fileInputStream.getChannel(); // Канал входного файла
                FileChannel outputChannel = fileOutputStream.getChannel(); // Канал выходного файла

                System.out.println();
                System.out.println("Задание № 2: Чтение и запись в файл параллельно.");
                long bufferSize = inputChannel.size() / NUMBER_OF_THREADS + 1; // размер буфера для каждого потока

                for (int i = 0; i < NUMBER_OF_THREADS; i++) { // В цикле выдаём каждому потоку задачу
                    threads.add(new Thread(new CopyTextInFile(inputChannel, outputChannel, bufferSize * i, Math.min(bufferSize * (i + 1), inputChannel.size()))));
                    threads.get(i).setName("Поток № " + (i + 1));
                    threads.get(i).start();
                }
                for (Thread thread: threads)
                    thread.join(); // Дожидаемся окончания работы всех потоков
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // Верим в лучшее, готовимся к худшему
            }

            // Распечатывает окончательный вариант outputFile, который по частям скопировали наши потоки.
            try (FileInputStream fileInputStream = new FileInputStream(outputFile)) {
                FileChannel inputChannel = fileInputStream.getChannel();
                System.out.println("===========================");
                System.out.println("По итогу работы программы Татьяна получила от Онегина следующее письмо:");
                System.out.println();
                MappedByteBuffer mappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputChannel.size());
                if (mappedByteBuffer != null) {
                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(mappedByteBuffer);
                    System.out.println(charBuffer);
                }
            }
        }

//TODO 3. Параллельное вычисление ряда чисел Фибоначчи:
//        Разделите ряд чисел Фибоначчи на части и создайте отдельный поток для вычисления каждой части.
//        Затем объедините результаты. Прочитанный текст выведите в консоль.
        {
            final int NUMBER_OF_THREADS = 10; // Количество создаваемых потоков
            final int START_NUMBER = 1; // Начальное значение вычисляемого ряда Фибоначчи
            final int END_NUMBER = 926; // Конечное значение вычисляемого ряда Фибоначчи

            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS); // Определяем пул из NUMBER_OF_THREADS потоков
            List<Future<BigInteger[]>> futures = new ArrayList<>(); // Список ассоциированных с Callable задач Future

            int rowSize = (END_NUMBER - START_NUMBER + 1) / NUMBER_OF_THREADS; // размер ряда каждого потока
            int rowRemains = (END_NUMBER - START_NUMBER + 1) % NUMBER_OF_THREADS; // остаток от деления

            System.out.println();
            System.out.println("Задание № 3: Параллельное вычисление ряда чисел Фибоначчи.");
            int startNumber = START_NUMBER; // Для первого потока начальная позиция всегда START_NUMBER
            //noinspection ConstantValue,PointlessArithmeticExpression
            int endNumber = START_NUMBER - 1 + rowSize + (rowRemains-- > 0 ? 1 : 0); // Рассчитываем конечную позицию для первого потока
            System.out.println("Создаём " + NUMBER_OF_THREADS + " потоков с указанными ниже диапазонами:");
            // Приступаем к построению ряда Фибоначчи
            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                System.out.println("[" + startNumber + ":" + endNumber + "] - " + (endNumber - startNumber + 1) + " элементов");
                // Создаём новый поток и ожидаем получить результат в списке futures
                futures.add(executor.submit(new RowFibonacci(startNumber, endNumber)));
                // Начальная позиция startNumber следующего потока всегда на 1 больше конечной позиции endNumber предыдущего потока
                startNumber = endNumber + 1;
                // Конечная позиция следующего потока больше конечной позиции предыдущего потока
                // на (rowSize - размер ряда каждого потока) и остаток от rowSize, если он остался
                endNumber = endNumber + rowSize + (rowRemains-- > 0 ? 1 : 0);
            }
            // Получаем массивы с потоков и объединяем
            List<BigInteger> bigIntegerList = new ArrayList<>();
            for (Future<BigInteger[]> future : futures)
                bigIntegerList.addAll(Arrays.asList(future.get()));

            // Завершаем работу потоков
            executor.shutdown(); // Даём команду на завершение работы потоков
            try { // Ожидаем завершение работы потоков 60 секунд
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    executor.shutdownNow(); // Если работа потоков не завершена, пытаемся их отменить
            } catch (InterruptedException e) {
                executor.shutdownNow(); // Верим в лучшее, готовимся к худшему
            }
            // Печатаем полученный ряд Фибоначчи
            System.out.println();
            System.out.println("Ряд Фибоначи [" + START_NUMBER + ":" + END_NUMBER + "]");
            AtomicInteger i = new AtomicInteger(START_NUMBER);
            bigIntegerList.forEach(bigInteger -> { System.out.println("Fibonacci[" + i + "]: " + bigInteger); i.getAndIncrement(); });
        }
    }
}
