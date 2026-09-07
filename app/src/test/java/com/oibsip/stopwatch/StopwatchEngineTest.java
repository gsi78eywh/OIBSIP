package com.oibsip.stopwatch;

import com.oibsip.stopwatch.engine.StopwatchEngine;
import com.oibsip.stopwatch.model.LapItem;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test suite for StopwatchEngine state transitions, precision calculations,
 * lap recording, and time formatting.
 */
public class StopwatchEngineTest {

    private StopwatchEngine engine;

    @Before
    public void setUp() {
        engine = new StopwatchEngine();
    }

    @Test
    public void testInitialState() {
        assertEquals(StopwatchEngine.State.STOPPED, engine.getState());
        assertTrue(engine.isStopped());
        assertFalse(engine.isRunning());
        assertFalse(engine.isPaused());
        assertEquals(0L, engine.getElapsedTime(1000L));
        assertEquals(0, engine.getLapCount());
        assertTrue(engine.getLaps().isEmpty());
    }

    @Test
    public void testStartAndElapsedTime() {
        long t0 = 1000L;
        assertTrue(engine.start(t0));
        assertEquals(StopwatchEngine.State.RUNNING, engine.getState());
        assertTrue(engine.isRunning());

        // Calling start while running returns false
        assertFalse(engine.start(t0 + 500));

        // Advance simulated time by 2500ms
        long elapsed = engine.getElapsedTime(t0 + 2500L);
        assertEquals(2500L, elapsed);
    }

    @Test
    public void testPauseAndResume() {
        long t0 = 1000L;
        engine.start(t0);

        // Run for 3000ms, then pause at t0 + 3000
        long tPause = t0 + 3000L;
        assertTrue(engine.pause(tPause));
        assertEquals(StopwatchEngine.State.PAUSED, engine.getState());
        assertTrue(engine.isPaused());

        // Calling pause while already paused returns false
        assertFalse(engine.pause(tPause + 500));

        // Elapsed time remains frozen during pause
        assertEquals(3000L, engine.getElapsedTime(tPause + 5000L));

        // Resume at tPause + 5000 (after 5 seconds idle)
        long tResume = tPause + 5000L;
        assertTrue(engine.start(tResume));
        assertEquals(StopwatchEngine.State.RUNNING, engine.getState());

        // Run for another 2000ms (total should be 3000 + 2000 = 5000ms)
        assertEquals(5000L, engine.getElapsedTime(tResume + 2000L));
    }

    @Test
    public void testReset() {
        engine.start(1000L);
        engine.pause(4000L);
        engine.recordLap(4000L);

        engine.reset();
        assertEquals(StopwatchEngine.State.STOPPED, engine.getState());
        assertEquals(0L, engine.getElapsedTime(5000L));
        assertEquals(0, engine.getLapCount());
        assertTrue(engine.getLaps().isEmpty());
    }

    @Test
    public void testLapRecording() {
        // Cannot record lap while stopped
        assertNull(engine.recordLap(1000L));

        long t0 = 10000L;
        engine.start(t0);

        // Lap 1 recorded at +4000ms
        LapItem lap1 = engine.recordLap(t0 + 4000L);
        assertNotNull(lap1);
        assertEquals(1, lap1.getLapNumber());
        assertEquals(4000L, lap1.getLapDurationMillis());
        assertEquals(4000L, lap1.getTotalElapsedMillis());
        assertEquals("00:04.00", lap1.getFormattedLapDuration());
        assertEquals("00:04.00", lap1.getFormattedTotalElapsed());

        // Lap 2 recorded at +7500ms (split should be 3500ms)
        LapItem lap2 = engine.recordLap(t0 + 7500L);
        assertNotNull(lap2);
        assertEquals(2, lap2.getLapNumber());
        assertEquals(3500L, lap2.getLapDurationMillis());
        assertEquals(7500L, lap2.getTotalElapsedMillis());
        assertEquals("00:03.50", lap2.getFormattedLapDuration());
        assertEquals("00:07.50", lap2.getFormattedTotalElapsed());

        // Laps list should have 2 items, latest first (index 0 is lap2)
        List<LapItem> laps = engine.getLaps();
        assertEquals(2, laps.size());
        assertEquals(2, laps.get(0).getLapNumber());
        assertEquals(1, laps.get(1).getLapNumber());
    }

    @Test
    public void testTimeFormatting() {
        // 0 ms -> 00:00.00
        assertEquals("00:00.00", StopwatchEngine.formatTime(0L));
        assertEquals("00:00", StopwatchEngine.formatMainDigits(0L));
        assertEquals(".00", StopwatchEngine.formatCentiseconds(0L));

        // 65430 ms -> 01:05.43
        assertEquals("01:05.43", StopwatchEngine.formatTime(65430L));
        assertEquals("01:05", StopwatchEngine.formatMainDigits(65430L));
        assertEquals(".43", StopwatchEngine.formatCentiseconds(65430L));

        // Over 1 hour: 3665430 ms -> 01:01:05.43
        assertEquals("01:01:05.43", StopwatchEngine.formatTime(3665430L));
        assertEquals("01:01:05", StopwatchEngine.formatMainDigits(3665430L));
        assertEquals(".43", StopwatchEngine.formatCentiseconds(3665430L));
    }

    @Test
    public void testLifecycleStateRestoration() {
        engine.restoreState(StopwatchEngine.State.PAUSED, 15000L, 0L, 10000L, null);
        assertEquals(StopwatchEngine.State.PAUSED, engine.getState());
        assertEquals(15000L, engine.getElapsedTime(99999L));
        assertEquals(0, engine.getLapCount());
    }
}
