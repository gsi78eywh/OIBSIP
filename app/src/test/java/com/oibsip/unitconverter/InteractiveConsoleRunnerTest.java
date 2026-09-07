package com.oibsip.unitconverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for InteractiveConsoleRunner direct CLI execution and error handling.
 */
public class InteractiveConsoleRunnerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testDirectConversionSuccess() {
        InteractiveConsoleRunner.main(new String[]{"100", "m", "cm"});
        String output = outContent.toString();
        assertTrue("Output should contain result", output.contains("100 m = 10000 cm") || output.contains("100 m = 10,000 cm"));
        assertTrue("Output should contain formula", output.contains("Formula: 1 m = 100 cm"));
    }

    @Test
    public void testDirectConversionTemperature() {
        InteractiveConsoleRunner.main(new String[]{"0", "°C", "°F"});
        String output = outContent.toString();
        assertTrue("Output should convert 0 °C to 32 °F", output.contains("32 °F"));
    }

    @Test
    public void testDirectConversionUnrecognizedUnit() {
        InteractiveConsoleRunner.main(new String[]{"50", "unknown_unit", "m"});
        String output = outContent.toString();
        assertTrue("Should report unrecognized unit", output.contains("[ERROR] Could not recognize one of the units"));
    }

    @Test
    public void testDirectConversionCrossCategoryMismatch() {
        InteractiveConsoleRunner.main(new String[]{"10", "m", "kg"});
        String output = outContent.toString();
        assertTrue("Should reject cross-category conversion", output.contains("[ERROR] Cannot convert between different categories"));
    }

    @Test
    public void testDirectConversionBelowAbsoluteZero() {
        InteractiveConsoleRunner.main(new String[]{"-300", "°C", "K"});
        String output = outContent.toString();
        assertTrue("Should reject temperature below absolute zero", output.contains("[ERROR] Value is below absolute zero!"));
    }

    @Test
    public void testDirectConversionInvalidNumber() {
        InteractiveConsoleRunner.main(new String[]{"not_a_number", "m", "km"});
        String output = outContent.toString();
        assertTrue("Should report invalid numeric value", output.contains("[ERROR] Invalid numeric value"));
    }
}
