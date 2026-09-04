package com.oibsip.unitconverter.model;

import java.util.Objects;

/**
 * Represents a single measurement unit belonging to a specific Category.
 */
public class Unit {
    private final String id;
    private final String name;
    private final String symbol;
    private final Category category;
    private final double factorToBase;

    public Unit(String id, String name, String symbol, Category category, double factorToBase) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.category = category;
        this.factorToBase = factorToBase;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Category getCategory() {
        return category;
    }

    /**
     * For linear categories, 1 [This Unit] = factorToBase * [Base Unit].
     * (e.g., 1 km = 1000 m; 1 cm = 0.01 m).
     * For Temperature, special formulas are handled in UnitConverter.
     */
    public double getFactorToBase() {
        return factorToBase;
    }

    public String getDisplayLabel() {
        if (symbol == null || symbol.trim().isEmpty()) {
            return name;
        }
        return name + " (" + symbol + ")";
    }

    @Override
    public String toString() {
        return getDisplayLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(id, unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
