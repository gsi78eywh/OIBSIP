package com.oibsip.unitconverter.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Category enum metadata, descriptions, and string representations.
 */
public class CategoryTest {

    @Test
    public void testCategoryCount() {
        Category[] categories = Category.values();
        assertEquals("There should be exactly 6 measurement categories", 6, categories.length);
    }

    @Test
    public void testCategoryMetadataIntegrity() {
        for (Category category : Category.values()) {
            assertNotNull("Category enum name must not be null", category.name());
            assertNotNull("Display name must not be null for " + category.name(), category.getDisplayName());
            assertFalseEmpty("Display name must not be empty for " + category.name(), category.getDisplayName());
            assertNotNull("Description must not be null for " + category.name(), category.getDescription());
            assertFalseEmpty("Description must not be empty for " + category.name(), category.getDescription());
            assertEquals("toString() must match getDisplayName()", category.getDisplayName(), category.toString());
        }
    }

    @Test
    public void testSpecificCategoryValues() {
        assertEquals("Length", Category.LENGTH.getDisplayName());
        assertEquals("Distance & dimensions", Category.LENGTH.getDescription());

        assertEquals("Weight / Mass", Category.WEIGHT.getDisplayName());
        assertEquals("Mass & weight measurements", Category.WEIGHT.getDescription());

        assertEquals("Temperature", Category.TEMPERATURE.getDisplayName());
        assertEquals("Thermal measurements", Category.TEMPERATURE.getDescription());

        assertEquals("Volume / Capacity", Category.VOLUME.getDisplayName());
        assertEquals("Liquid & dry capacity", Category.VOLUME.getDescription());

        assertEquals("Speed / Velocity", Category.SPEED.getDisplayName());
        assertEquals("Rate of motion", Category.SPEED.getDescription());

        assertEquals("Time", Category.TIME.getDisplayName());
        assertEquals("Duration & time intervals", Category.TIME.getDescription());
    }

    @Test
    public void testValueOfLookup() {
        assertEquals(Category.LENGTH, Category.valueOf("LENGTH"));
        assertEquals(Category.WEIGHT, Category.valueOf("WEIGHT"));
        assertEquals(Category.TEMPERATURE, Category.valueOf("TEMPERATURE"));
        assertEquals(Category.VOLUME, Category.valueOf("VOLUME"));
        assertEquals(Category.SPEED, Category.valueOf("SPEED"));
        assertEquals(Category.TIME, Category.valueOf("TIME"));
    }

    private void assertFalseEmpty(String message, String value) {
        assertTrue(message, value != null && !value.trim().isEmpty());
    }
}
