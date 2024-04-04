package ru.mts.DTO;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class СounterAtomic {
    AtomicInteger value = new AtomicInteger(0);

    public void increment(){
        value.incrementAndGet();
    }
}
