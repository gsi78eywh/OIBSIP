package com.oibsip.stopwatch.engine;

import com.oibsip.stopwatch.model.LapItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Platform-independent, high-precision Stopwatch logic engine.
 * Decoupled from Android UI to allow comprehensive JUnit 4 unit testing
 * and direct desktop/CLI execution.
 */
public class StopwatchEngine {

    public enum State {
        STOPPED,
        RUNNING,
        PAUSED
    }

    private State state = State.STOPPED;
    private long startTimeMillis = 0L;
    private long accumulatedElapsedMillis = 0L;
    private long lastLapTotalElapsedMillis = 0L;
    private final List<LapItem> laps = new ArrayList<>();

    // =========================================================================
    // CORE CONTROLS
    // =========================================================================

    /**
     * Starts the timer from zero (if stopped) or resumes from the paused position.
     * @param now Current timestamp in milliseconds (e.g., SystemClock.uptimeMillis() or System.currentTimeMillis())
     * @return true if state transitioned to RUNNING
     */
    public boolean start(long now) {
        if (state == State.RUNNING) {
            return false;
        }

        startTimeMillis = now;
        state = State.RUNNING;
        return true;
    }

    /**
     * Freezes the timer at its current elapsed duration.
     * @param now Current timestamp in milliseconds
     * @return true if state transitioned to PAUSED
     */
    public boolean pause(long now) {
        if (state != State.RUNNING) {
            return false;
        }

        accumulatedElapsedMillis += (now - startTimeMillis);
        startTimeMillis = 0L;
        state = State.PAUSED;
        return true;
    }

    /**
     * Stops the stopwatch, resets all counters to zero, and clears recorded laps.
     */
    public void reset() {
        state = State.STOPPED;
        startTimeMillis = 0L;
        accumulatedElapsedMillis = 0L;
        lastLapTotalElapsedMillis = 0L;
        laps.clear();
    }

    /**
     * Records a lap with the split duration since the last lap and cumulative time.
     * @param now Current timestamp in milliseconds
     * @return The newly recorded LapItem, or null if stopwatch is not running
     */
    public LapItem recordLap(long now) {
        if (state != State.RUNNING) {
            return null;
        }

        long currentTotal = getElapsedTime(now);
        long lapDuration = currentTotal - lastLapTotalElapsedMillis;
        lastLapTotalElapsedMillis = currentTotal;

        int lapNumber = laps.size() + 1;
        LapItem lap = new LapItem(
                lapNumber,
                lapDuration,
                currentTotal,
                formatTime(lapDuration),
                formatTime(currentTotal)
        );

        // Prepend to display latest lap at the top
        laps.add(0, lap);
        return lap;
    }

    // =========================================================================
    // QUERY METHODS
    // =========================================================================

    /**
     * Returns the total elapsed time in milliseconds.
     */
    public long getElapsedTime(long now) {
        switch (state) {
            case RUNNING:
                return accumulatedElapsedMillis + (now - startTimeMillis);
            case PAUSED:
                return accumulatedElapsedMillis;
            case STOPPED:
            default:
                return 0L;
        }
    }

    public State getState() {
        return state;
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public List<LapItem> getLaps() {
        return Collections.unmodifiableList(laps);
    }

    public int getLapCount() {
        return laps.size();
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getAccumulatedElapsedMillis() {
        return accumulatedElapsedMillis;
    }

    // =========================================================================
    // TIME FORMATTING (Centisecond & Millisecond precision)
    // =========================================================================

    /**
     * Formats milliseconds into a standard digital display string:
     * - "MM:SS.cs" (e.g., "01:23.45") for times under 1 hour.
     * - "HH:MM:SS.cs" (e.g., "01:02:03.45") when hours exceed 0.
     */
    public static String formatTime(long totalMillis) {
        if (totalMillis < 0) {
            totalMillis = 0;
        }

        long hours = totalMillis / 3600000;
        long minutes = (totalMillis % 3600000) / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long centiseconds = (totalMillis % 1000) / 10; // 2 decimal digits

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds);
        } else {
            return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centiseconds);
        }
    }

    /**
     * Formats only the primary time portion ("00:00" or "00:00:00").
     */
    public static String formatMainDigits(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;

        long hours = totalMillis / 3600000;
        long minutes = (totalMillis % 3600000) / 60000;
        long seconds = (totalMillis % 60000) / 1000;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Formats only the centisecond fraction portion (e.g., ".00" to ".99").
     */
    public static String formatCentiseconds(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;
        long centiseconds = (totalMillis % 1000) / 10;
        return String.format(Locale.US, ".%02d", centiseconds);
    }

    // =========================================================================
    // STATE RESTORATION (Android Activity Lifecycle)
    // =========================================================================

    public void restoreState(State savedState, long savedAccumulated, long savedStartTime,
                             long savedLastLapElapsed, List<LapItem> savedLaps) {
        this.state = savedState;
        this.accumulatedElapsedMillis = savedAccumulated;
        this.startTimeMillis = savedStartTime;
        this.lastLapTotalElapsedMillis = savedLastLapElapsed;
        this.laps.clear();
        if (savedLaps != null) {
            this.laps.addAll(savedLaps);
        }
    }
}
