package ru.yandex.practicum.sleeptracker;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SleepTrackerApp {
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) throws IOException {
        List<SleepingSession> sessions = readSessions(args[0]);

        List<SleepAnalysisResult> results = List.of(
                countSessions(sessions),
                minDuration(sessions),
                maxDuration(sessions),
                averageDuration(sessions),
                badQualityCount(sessions),
                sleeplessNights(sessions),
                chronotype(sessions)
        );

        printResults(results, 0);
    }

    public static void printResults(List<SleepAnalysisResult> results, int index) {
        if (index >= results.size()) {
            return;
        }

        System.out.println(results.get(index));
        printResults(results, index + 1);
    }

    public static List<SleepingSession> readSessions(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));
        return parseSessions(lines, 0, new ArrayList<>());
    }

    public static List<SleepingSession> parseSessions(
            List<String> lines,
            int index,
            List<SleepingSession> sessions
    ) {
        if (index >= lines.size()) {
            return sessions;
        }

        sessions.add(parseSession(lines.get(index)));

        return parseSessions(lines, index + 1, sessions);
    }

    public static SleepingSession parseSession(String line) {
        String[] parts = line.split(";");

        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }

    public static SleepAnalysisResult countSessions(List<SleepingSession> sessions) {
        return new SleepAnalysisResult("Всего сессий сна", sessions.size());
    }

    public static SleepAnalysisResult minDuration(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Минимальная продолжительность сна", "0 минут");
        }

        long result = findMinDuration(sessions, 0, sessions.get(0).getDurationInMinutes());

        return new SleepAnalysisResult("Минимальная продолжительность сна", result + " минут");
    }

    public static long findMinDuration(List<SleepingSession> sessions, int index, long currentMin) {
        if (index >= sessions.size()) {
            return currentMin;
        }

        long duration = sessions.get(index).getDurationInMinutes();
        long newMin = Math.min(currentMin, duration);

        return findMinDuration(sessions, index + 1, newMin);
    }

    public static SleepAnalysisResult maxDuration(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Максимальная продолжительность сна", "0 минут");
        }

        long result = findMaxDuration(sessions, 0, sessions.get(0).getDurationInMinutes());

        return new SleepAnalysisResult("Максимальная продолжительность сна", result + " минут");
    }

    public static long findMaxDuration(List<SleepingSession> sessions, int index, long currentMax) {
        if (index >= sessions.size()) {
            return currentMax;
        }

        long duration = sessions.get(index).getDurationInMinutes();
        long newMax = Math.max(currentMax, duration);

        return findMaxDuration(sessions, index + 1, newMax);
    }

    public static SleepAnalysisResult averageDuration(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Средняя продолжительность сна", "0 минут");
        }

        long totalDuration = sumDuration(sessions, 0, 0);
        long result = Math.round((double) totalDuration / sessions.size());

        return new SleepAnalysisResult("Средняя продолжительность сна", result + " минут");
    }

    public static long sumDuration(List<SleepingSession> sessions, int index, long sum) {
        if (index >= sessions.size()) {
            return sum;
        }

        long newSum = sum + sessions.get(index).getDurationInMinutes();

        return sumDuration(sessions, index + 1, newSum);
    }

    public static SleepAnalysisResult badQualityCount(List<SleepingSession> sessions) {
        long result = countBadQuality(sessions, 0, 0);

        return new SleepAnalysisResult("Сессий с плохим качеством сна", result);
    }

    public static long countBadQuality(List<SleepingSession> sessions, int index, long count) {
        if (index >= sessions.size()) {
            return count;
        }

        long newCount = sessions.get(index).getQuality() == SleepQuality.BAD
                ? count + 1
                : count;


        return countBadQuality(sessions, index + 1, newCount);
    }

    public static SleepAnalysisResult sleeplessNights(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Бессонных ночей", 0L);
        }

        LocalDate firstNight = getFirstNightDate(sessions.get(0));
        LocalDate lastNight = sessions.get(sessions.size() - 1).getEnd().toLocalDate();

        long result = countSleeplessNights(sessions, firstNight, lastNight, 0);

        return new SleepAnalysisResult("Бессонных ночей", result);
    }

    public static long countSleeplessNights(
            List<SleepingSession> sessions,
            LocalDate currentNight,
            LocalDate lastNight,
            long count
    ) {
        if (currentNight.isAfter(lastNight)) {
            return count;
        }

        long newCount = sleptAtNight(sessions, currentNight)
                ? count
                : count + 1;

        return countSleeplessNights(
                sessions,
                currentNight.plusDays(1),
                lastNight,
                newCount
        );
    }

    public static LocalDate getFirstNightDate(SleepingSession firstSession) {
        LocalTime startTime = firstSession.getStart().toLocalTime();
        LocalDate startDate = firstSession.getStart().toLocalDate();

        if (startTime.isAfter(LocalTime.NOON)) {
            return startDate.plusDays(1);
        }

        return startDate;
    }

    public static boolean sleptAtNight(List<SleepingSession> sessions, LocalDate night) {
        LocalDateTime nightStart = LocalDateTime.of(night, LocalTime.of(0, 0));
        LocalDateTime nightEnd = LocalDateTime.of(night, LocalTime.of(6, 0));

        return hasSessionAtNight(sessions, nightStart, nightEnd, 0);
    }

    public static boolean hasSessionAtNight(
            List<SleepingSession> sessions,
            LocalDateTime nightStart,
            LocalDateTime nightEnd,
            int index
    ) {
        if (index >= sessions.size()) {
            return false;
        }

        SleepingSession session = sessions.get(index);

        boolean intersectsNight =
                session.getStart().isBefore(nightEnd)
                        && session.getEnd().isAfter(nightStart);

        if (intersectsNight) {
            return true;
        }

        return hasSessionAtNight(sessions, nightStart, nightEnd, index + 1);
    }

    public static SleepAnalysisResult chronotype(List<SleepingSession> sessions) {
        long owls = countChronotype(sessions, Chronotype.OWL, 0, 0);
        long larks = countChronotype(sessions, Chronotype.LARK, 0, 0);
        long pigeons = countChronotype(sessions, Chronotype.PIGEON, 0, 0);

        Chronotype result;

        if (owls > larks && owls > pigeons) {
            result = Chronotype.OWL;
        } else if (larks > owls && larks > pigeons) {
            result = Chronotype.LARK;
        } else {
            result = Chronotype.PIGEON;
        }

        return new SleepAnalysisResult("Хронотип пользователя", result);
    }

    public static long countChronotype(
            List<SleepingSession> sessions,
            Chronotype chronotype,
            int index,
            long count
    ) {
        if (index >= sessions.size()) {
            return count;
        }

        SleepingSession session = sessions.get(index);

        boolean shouldCount =
                isNightSleep(session)
                        && classify(session) == chronotype;

        long newCount = shouldCount ? count + 1 : count;

        return countChronotype(sessions, chronotype, index + 1, newCount);
    }

    public static boolean isNightSleep(SleepingSession session) {
        return session.getStart().toLocalDate().isBefore(session.getEnd().toLocalDate());
    }

    public static Chronotype classify(SleepingSession session) {
        LocalTime sleepTime = session.getStart().toLocalTime();
        LocalTime wakeTime = session.getEnd().toLocalTime();

        if (sleepTime.isAfter(LocalTime.of(23, 0))
                && wakeTime.isAfter(LocalTime.of(9, 0))) {
            return Chronotype.OWL;
        }

        if (sleepTime.isBefore(LocalTime.of(22, 0))
                && wakeTime.isBefore(LocalTime.of(7, 0))) {
            return Chronotype.LARK;
        }

        return Chronotype.PIGEON;
    }
}