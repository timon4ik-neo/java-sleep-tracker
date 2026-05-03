package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;



import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

    class SleepTrackerAppTest {

        private SleepingSession session(
                int startDay,
                int startHour,
                int startMinute,
                int endDay,
                int endHour,
                int endMinute,
                SleepQuality quality
        ) {
            return new SleepingSession(
                    LocalDateTime.of(2025, 10, startDay, startHour, startMinute),
                    LocalDateTime.of(2025, 10, endDay, endHour, endMinute),
                    quality
            );
        }
        @Test
        void countSessionsShouldReturnZeroForEmptyList() {
            SleepAnalysisResult result = SleepTrackerApp.countSessions(List.of());

            assertEquals(0, result.getValue());
        }

        @Test
        void countSessionsShouldReturnCorrectCount() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 7, 0, SleepQuality.GOOD),
                    session(2, 23, 0, 3, 8, 0, SleepQuality.NORMAL),
                    session(3, 14, 0, 3, 15, 0, SleepQuality.BAD)
            );

            SleepAnalysisResult result = SleepTrackerApp.countSessions(sessions);

            assertEquals(3, result.getValue());
        }


        @Test
        void minDurationShouldReturnZeroForEmptyList() {
            SleepAnalysisResult result = SleepTrackerApp.minDuration(List.of());

            assertEquals("0 минут", result.getValue());
        }

        @Test
        void minDurationShouldReturnShortestSession() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.GOOD),
                    session(2, 14, 0, 2, 15, 0, SleepQuality.NORMAL),
                    session(3, 23, 0, 4, 8, 0, SleepQuality.BAD)
            );

            SleepAnalysisResult result = SleepTrackerApp.minDuration(sessions);

            assertEquals("60 минут", result.getValue());
        }

        @Test
        void maxDurationShouldReturnZeroForEmptyList() {
            SleepAnalysisResult result = SleepTrackerApp.maxDuration(List.of());

            assertEquals("0 минут", result.getValue());
        }

        @Test
        void maxDurationShouldReturnLongestSession() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.GOOD),
                    session(2, 14, 0, 2, 15, 0, SleepQuality.NORMAL),
                    session(3, 23, 0, 4, 9, 0, SleepQuality.BAD)
            );

            SleepAnalysisResult result = SleepTrackerApp.maxDuration(sessions);

            assertEquals("600 минут", result.getValue());
        }

        @Test
        void averageDurationShouldReturnZeroForEmptyList() {
            SleepAnalysisResult result = SleepTrackerApp.averageDuration(List.of());

            assertEquals("0 минут", result.getValue());
        }

        @Test
        void averageDurationShouldReturnAverageValue() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.GOOD),
                    session(2, 14, 0, 2, 15, 0, SleepQuality.NORMAL),
                    session(3, 23, 0, 4, 8, 0, SleepQuality.BAD)
            );

            SleepAnalysisResult result = SleepTrackerApp.averageDuration(sessions);


            assertEquals("360 минут", result.getValue());
        }


        @Test
        void badQualityCountShouldReturnZeroIfThereAreNoBadSessions() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.GOOD),
                    session(2, 23, 0, 3, 8, 0, SleepQuality.NORMAL)
            );

            SleepAnalysisResult result = SleepTrackerApp.badQualityCount(sessions);

            assertEquals(0L, result.getValue());
        }

        @Test
        void badQualityCountShouldReturnCorrectCount() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.BAD),
                    session(2, 23, 0, 3, 8, 0, SleepQuality.NORMAL),
                    session(3, 23, 0, 4, 7, 0, SleepQuality.BAD)
            );

            SleepAnalysisResult result = SleepTrackerApp.badQualityCount(sessions);

            assertEquals(2L, result.getValue());
        }

        @Test
        void sleeplessNightsShouldReturnZeroForEmptyList() {
            SleepAnalysisResult result = SleepTrackerApp.sleeplessNights(List.of());

            assertEquals(0L, result.getValue());
        }

        @Test
        void sleeplessNightsShouldNotCountNightIfUserSleptFrom23To3() {
            List<SleepingSession> sessions = List.of(
                    session(1, 23, 0, 2, 3, 0, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.sleeplessNights(sessions);

            assertEquals(0L, result.getValue());
        }

        @Test
        void sleeplessNightsShouldNotCountNightIfUserSleptFrom2To7() {
            List<SleepingSession> sessions = List.of(
                    session(2, 2, 0, 2, 7, 0, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.sleeplessNights(sessions);

            assertEquals(0L, result.getValue());
        }

        @Test
        void sleeplessNightsShouldCountNightIfUserSleptOnlyAfter7() {
            List<SleepingSession> sessions = List.of(
                    session(2, 7, 0, 2, 11, 0, SleepQuality.NORMAL)
            );

            SleepAnalysisResult result = SleepTrackerApp.sleeplessNights(sessions);

            assertEquals(1L, result.getValue());
        }

        @Test
        void sleeplessNightsShouldCountMissingNightBetweenTwoSessions() {
            List<SleepingSession> sessions = List.of(
                    session(1, 22, 0, 2, 6, 0, SleepQuality.GOOD),
                    session(3, 22, 0, 4, 6, 0, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.sleeplessNights(sessions);

            assertEquals(1L, result.getValue());
        }

        @Test
        void chronotypeShouldReturnOwlIfOwlNightsAreMostFrequent() {
            List<SleepingSession> sessions = List.of(
                    session(1, 23, 30, 2, 10, 0, SleepQuality.GOOD),
                    session(2, 23, 45, 3, 10, 30, SleepQuality.NORMAL),
                    session(3, 22, 30, 4, 8, 0, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.chronotype(sessions);

            assertEquals(Chronotype.OWL, result.getValue());
        }

        @Test
        void chronotypeShouldReturnLarkIfLarkNightsAreMostFrequent() {
            List<SleepingSession> sessions = List.of(
                    session(1, 21, 30, 2, 6, 30, SleepQuality.GOOD),
                    session(2, 21, 0, 3, 6, 0, SleepQuality.NORMAL),
                    session(3, 22, 30, 4, 8, 0, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.chronotype(sessions);

            assertEquals(Chronotype.LARK, result.getValue());
        }

        @Test
        void chronotypeShouldReturnPigeonIfThereIsTie() {
            List<SleepingSession> sessions = List.of(
                    session(1, 23, 30, 2, 10, 0, SleepQuality.GOOD),
                    session(2, 21, 30, 3, 6, 30, SleepQuality.GOOD)
            );

            SleepAnalysisResult result = SleepTrackerApp.chronotype(sessions);

            assertEquals(Chronotype.PIGEON, result.getValue());
        }

        @Test
        void chronotypeShouldIgnoreDaySleep() {
            List<SleepingSession> sessions = List.of(
                    session(1, 14, 0, 1, 15, 0, SleepQuality.GOOD),
                    session(1, 23, 30, 2, 10, 0, SleepQuality.GOOD),
                    session(2, 23, 45, 3, 10, 30, SleepQuality.NORMAL)
            );

            SleepAnalysisResult result = SleepTrackerApp.chronotype(sessions);

            assertEquals(Chronotype.OWL, result.getValue());
        }
    }
