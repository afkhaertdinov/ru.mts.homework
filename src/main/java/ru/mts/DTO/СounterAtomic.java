package ru.mts.DTO;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс счётчик для многопоточных задач
 */
@Getter
public class СounterAtomic {
    AtomicInteger value = new AtomicInteger(0);

    /**
     * Инкремент счётчика
      */
    public void increment(){
        value.incrementAndGet();
    }
}
