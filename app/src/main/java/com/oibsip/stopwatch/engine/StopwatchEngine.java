package com.oibsip.stopwatch.engine;

import com.oibsip.stopwatch.model.LapItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Core logic engine for the stopwatch.
 * Handles timing calculations, state transitions, and lap tracking.
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

    public boolean start(long now) {
        if (state == State.RUNNING) {
            return false;
        }
        startTimeMillis = now;
        state = State.RUNNING;
        return true;
    }

    public boolean pause(long now) {
        if (state != State.RUNNING) {
            return false;
        }
        accumulatedElapsedMillis += (now - startTimeMillis);
        startTimeMillis = 0L;
        state = State.PAUSED;
        return true;
    }

    public void reset() {
        state = State.STOPPED;
        startTimeMillis = 0L;
        accumulatedElapsedMillis = 0L;
        lastLapTotalElapsedMillis = 0L;
        laps.clear();
    }

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

        laps.add(0, lap);
        return lap;
    }

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

    public static String formatTime(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;

        long hours = totalMillis / 3600000;
        long minutes = (totalMillis % 3600000) / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long centiseconds = (totalMillis % 1000) / 10;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds);
        } else {
            return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centiseconds);
        }
    }

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

    public static String formatCentiseconds(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;
        long centiseconds = (totalMillis % 1000) / 10;
        return String.format(Locale.US, ".%02d", centiseconds);
    }

    public void restoreState(State savedState, long savedAccumulated, long savedStartTime,
                             long savedLastLapElapsed, List<LapItem> savedLaps) {
        this.state = savedState != null ? savedState : State.STOPPED;
        this.accumulatedElapsedMillis = savedAccumulated;
        this.startTimeMillis = savedStartTime;
        this.lastLapTotalElapsedMillis = savedLastLapElapsed;
        this.laps.clear();
        if (savedLaps != null) {
            this.laps.addAll(savedLaps);
        }
    }
}
