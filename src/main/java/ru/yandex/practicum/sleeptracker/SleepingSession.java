package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public class SleepingSession {
    LocalDateTime startTime;
    LocalDateTime end;
    SleepQuality quality;

    public SleepingSession(LocalDateTime startTime, LocalDateTime end, SleepQuality quality) {
        this.startTime = startTime;
        this.end = end;
        this.quality = quality;
    }
}
