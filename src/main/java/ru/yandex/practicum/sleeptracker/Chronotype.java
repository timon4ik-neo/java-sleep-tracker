package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    OWL("Сова"),
    LARK("Жаворонок"),
    PIGEON("Голубь");
    private final String title;
    Chronotype(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
