package com.oibsip.stopwatch.model;

import java.util.Objects;

/**
 * Represents an individual recorded lap in the Stopwatch application.
 */
public class LapItem {

    private final int lapNumber;
    private final long lapDurationMillis;
    private final long totalElapsedMillis;
    private final String formattedLapDuration;
    private final String formattedTotalElapsed;

    public LapItem(int lapNumber, long lapDurationMillis, long totalElapsedMillis,
                   String formattedLapDuration, String formattedTotalElapsed) {
        this.lapNumber = lapNumber;
        this.lapDurationMillis = lapDurationMillis;
        this.totalElapsedMillis = totalElapsedMillis;
        this.formattedLapDuration = formattedLapDuration;
        this.formattedTotalElapsed = formattedTotalElapsed;
    }

    public int getLapNumber() {
        return lapNumber;
    }

    public long getLapDurationMillis() {
        return lapDurationMillis;
    }

    public long getTotalElapsedMillis() {
        return totalElapsedMillis;
    }

    public String getFormattedLapDuration() {
        return formattedLapDuration;
    }

    public String getFormattedTotalElapsed() {
        return formattedTotalElapsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LapItem lapItem = (LapItem) o;
        return lapNumber == lapItem.lapNumber &&
                lapDurationMillis == lapItem.lapDurationMillis &&
                totalElapsedMillis == lapItem.totalElapsedMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lapNumber, lapDurationMillis, totalElapsedMillis);
    }

    @Override
    public String toString() {
        return "Lap " + lapNumber + ": " + formattedLapDuration + " (Total: " + formattedTotalElapsed + ")";
    }
}
