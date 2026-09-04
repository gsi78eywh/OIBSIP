# 📘 Task 1 · Unit Converter Application — Complete Technical Documentation

> **Program:** Oasis Infobyte Internship Program (OIBSIP)  
> **Task:** Task 1 · Unit Converter Application  
> **Tech Stack:** Android Studio, Java, XML, Gradle  
> **Architecture Pattern:** Model-View-Controller (MVC)

---

## 📑 Table of Contents
1. [Project Overview & Purpose](#1-project-overview--purpose)
2. [Architectural Pattern (MVC)](#2-architectural-pattern-mvc)
3. [File-by-File Purpose Directory](#3-file-by-file-purpose-directory)
   - [Build & Configuration Files](#a-build--configuration-files)
   - [App Configuration & Manifest](#b-app-configuration--manifest)
   - [Java Source Code (Business Logic & Controllers)](#c-java-source-code)
   - [XML Layouts & User Interface](#d-xml-layouts--user-interface)
   - [XML Resources (Colors, Themes, Strings, Shapes)](#e-xml-resources)
   - [Testing & Verification Suite](#f-testing--verification-suite)
4. [Conversion Mathematics & The "Base Unit" Pattern](#4-conversion-mathematics--the-base-unit-pattern)
5. [Input Validation & Safety Strategy](#5-input-validation--safety-strategy)
6. [Viva / Interview Defense Guide](#6-viva--interview-defense-guide)

---

## 1. Project Overview & Purpose

The **Unit Converter Application** provides a fast, accurate, and user-friendly mobile utility to convert values across standard measurement units.

### Core Objectives Solved:
- **Zero Ambiguity Conversions:** Supports **6 measurement categories** (Length, Weight, Temperature, Volume, Speed, Time) covering both metric and imperial units.
- **Graceful Error Handling:** Prevents runtime crashes from empty inputs, malformed decimal points, or invalid physical values through instant `Toast` alerts.
- **Modern User Experience:** Features instant Unit Swapping, one-tap Result Copying to the Android clipboard, and live mathematical formula explanations.

---

## 2. Architectural Pattern (MVC)

The project adheres to the **Model-View-Controller (MVC)** architectural design to ensure strict separation of concerns, maintainability, and clean code:

```
                      ┌────────────────────────────────────────┐
                      │                 VIEW                   │
                      │  activity_main.xml, layouts, themes    │
                      │  (What the user sees on the screen)    │
                      └───────────────────┬────────────────────┘
                                          │ User interacts (taps button / changes dropdown)
                                          ▼
                      ┌────────────────────────────────────────┐
                      │              CONTROLLER                │
                      │          MainActivity.java             │
                      │  (Listens to events, validates input,  │
                      │   calls math engine, updates UI)       │
                      └───────┬────────────────────────▲───────┘
  Passes input values & units │                        │ Returns formatted
  for calculation             ▼                        │ result string
                      ┌────────────────────────────────────────┐
                      │                 MODEL                  │
                      │  • UnitConverter.java (Math Engine)    │
                      │  • Category.java (Domain Categories)   │
                      │  • Unit.java (Unit Data Model)         │
                      └────────────────────────────────────────┘
```

---

## 3. File-by-File Purpose Directory

### A. Build & Configuration Files

| File Path | Purpose & Functionality |
| :--- | :--- |
| [`settings.gradle`](file:///c:/Users/SethAndreyJabagat/OIBSIP/settings.gradle) | Defines the project name (`OIBSIP-UnitConverter`), registers Maven Central & Google repositories, and includes the `:app` module into the build. |
| [`build.gradle`](file:///c:/Users/SethAndreyJabagat/OIBSIP/build.gradle) | Top-level project build script. Declares the Android Gradle Plugin (`com.android.application` version 8.2.2) and root clean tasks. |
| [`gradle.properties`](file:///c:/Users/SethAndreyJabagat/OIBSIP/gradle.properties) | JVM memory settings (`-Xmx2048m`) and AndroidX flags (`android.useAndroidX=true`, `android.nonTransitiveRClass=true`) for fast, conflict-free compilation. |
| [`gradlew`](file:///c:/Users/SethAndreyJabagat/OIBSIP/gradlew) & [`gradlew.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/gradlew.bat) | Self-contained Gradle wrapper executable scripts for Linux/macOS (`gradlew`) and Windows (`gradlew.bat`). Allows any developer to compile the project without manually installing Gradle. |
| [`gradle/wrapper/gradle-wrapper.properties`](file:///c:/Users/SethAndreyJabagat/OIBSIP/gradle/wrapper/gradle-wrapper.properties) | Specifies the exact Gradle distribution version (`8.5-bin.zip`) to download and use. |
| [`.gitignore`](file:///c:/Users/SethAndreyJabagat/OIBSIP/.gitignore) | Excludes generated build artifacts (`.gradle/`, `build/`, `*.apk`, `.idea/`) from Git commits to keep the repository lightweight. |

---

### B. App Configuration & Manifest

| File Path | Purpose & Functionality |
| :--- | :--- |
| [`app/build.gradle`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/build.gradle) | The application module build script. Configures `compileSdk 34`, `minSdk 21` (supports 99%+ of Android devices), `targetSdk 34`, Java 8 compatibility, and dependencies (`appcompat`, `material`, `constraintlayout`, `junit`). |
| [`app/proguard-rules.pro`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/proguard-rules.pro) | Defines code shrinking, obfuscation, and optimization rules for release builds. |
| [`app/src/main/AndroidManifest.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/AndroidManifest.xml) | The app identity blueprint registered with the Android OS. Specifies application label, app icons, theme, and registers `MainActivity` with `MAIN` and `LAUNCHER` intent filters. |
| [`app/src/main/res/xml/backup_rules.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/xml/backup_rules.xml) & [`data_extraction_rules.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/xml/data_extraction_rules.xml) | Configures secure Android 12+ cloud and device transfer backup behaviors. |

---

### C. Java Source Code

#### 1. [`Category.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/model/Category.java)
- **Role:** Data Model (Enum).
- **Purpose:** Enumerates the supported physical categories (`LENGTH`, `WEIGHT`, `TEMPERATURE`, `VOLUME`, `SPEED`, `TIME`).
- **Why it matters:** Provides type safety. Instead of passing arbitrary strings like `"length"` which can have typos, the compiler guarantees only valid categories are selected.

#### 2. [`Unit.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/model/Unit.java)
- **Role:** Data Model.
- **Purpose:** Represents an individual measurement unit with fields:
  - `id`: Unique identifier (e.g., `"len_cm"`).
  - `name`: Full display name (e.g., `"Centimeter"`).
  - `symbol`: Short unit symbol (e.g., `"cm"`).
  - `category`: The parent category it belongs to.
  - `factorToBase`: Mathematical ratio relative to the category's base unit.
- **Why it matters:** Allows the conversion engine to treat all linear units uniformly through simple multiplication/division.

#### 3. [`UnitConverter.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/converter/UnitConverter.java)
- **Role:** Business Logic Engine (Pure Java).
- **Purpose:** 
  1. Houses the master registry of all units organized by category.
  2. Executes linear conversions via base units (`baseValue = input * factorFrom; target = baseValue / factorTo`).
  3. Executes non-linear affine conversions for Temperature (Celsius $\leftrightarrow$ Fahrenheit $\leftrightarrow$ Kelvin).
  4. Validates physical limits (flags temperatures below Absolute Zero: $-273.15^\circ\text{C}$).
  5. Formats outputs cleanly using `DecimalFormat` (strips trailing zeros, handles up to 6 decimal places, formats extreme values in scientific notation).
  6. Generates human-readable conversion equation strings (e.g., `"1 km = 1,000 m  •  (2.5 × 1,000 = 2,500)"`).
- **Why it matters:** Decoupled from the Android framework. Because it does not reference Android UI classes, it can be tested with 100% pure Java unit tests in milliseconds.

#### 4. [`MainActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/MainActivity.java)
- **Role:** Controller.
- **Purpose:** 
  1. Initializes and binds UI components on screen creation (`onCreate`).
  2. Populates `spinnerCategory`, `spinnerFromUnit`, and `spinnerToUnit` using `ArrayAdapter`.
  3. Handles category selection: dynamically swaps the available units in the From/To spinners with smart default pairings (e.g. Centimeter $\to$ Meter).
  4. Manages the **Convert** button:
     - Checks if the input is empty $\to$ displays a warning `Toast`.
     - Validates numeric decimal format $\to$ displays an error `Toast`.
     - Verifies physical limits $\to$ displays an Absolute Zero `Toast`.
     - Calls `UnitConverter.convert(...)` and updates the result text views.
  5. Manages the **Swap** button: reverses the source and target units and immediately recalculates.
  6. Manages the **Reset** button: clears the input and resets display cards.
  7. Manages the **Copy** button: transfers the result text into the Android system clipboard with a confirmation `Toast`.
  8. Manages keyboard dismissal (`InputMethodManager`) to provide a clean visual result.

---

### D. XML Layouts & User Interface

| File Path | Purpose & Functionality |
| :--- | :--- |
| [`app/src/main/res/layout/activity_main.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/layout/activity_main.xml) | The main visual screen. Uses a vertical `ScrollView` wrapping Material 3 cards for: Header & Tagline, Category Selection Card, Conversion Card (Numeric Input, From Spinner, Swap Button, To Spinner, Action Buttons), and Result Card (Value, Unit, Formula, Copy Button). |
| [`app/src/main/res/layout/item_spinner.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/layout/item_spinner.xml) | Custom layout for the closed Spinner view. Provides comfortable padding (14dp start, 12dp top/bottom) and readable typography. |
| [`app/src/main/res/layout/item_spinner_dropdown.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/layout/item_spinner_dropdown.xml) | Custom layout for each item in the open dropdown popup list. Includes touch ripple feedback (`?attr/selectableItemBackground`). |

---

### E. XML Resources

| File Path | Purpose & Functionality |
| :--- | :--- |
| [`app/src/main/res/values/colors.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/values/colors.xml) | Centralized color palette: Deep Indigo (`#4338CA`), Slate neutrals (`#0F172A`, `#F8FAFC`), Emerald result tint (`#F0FDF4`, `#15803D`). |
| [`app/src/main/res/values/strings.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/values/strings.xml) | Localized string catalog for all UI headers, hints, button labels, and `Toast` warning messages. |
| [`app/src/main/res/values/themes.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/values/themes.xml) | Base application theme inheriting from `Theme.MaterialComponents.DayNight.NoActionBar`. Sets status bar color and material styles. |
| [`app/src/main/res/drawable/shape_card_background.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/drawable/shape_card_background.xml) | Rounded rectangular shape (16dp radius) with 1dp border for input cards. |
| [`app/src/main/res/drawable/shape_result_card.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/drawable/shape_result_card.xml) | Soft green tinted background with rounded corners for the result card. |
| [`app/src/main/res/drawable/shape_spinner_background.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/drawable/shape_spinner_background.xml) | Layered drawable that draws a rounded border around the Spinner and embeds a dropdown arrow (`ic_arrow_drop_down`) aligned to the right. |
| [`app/src/main/res/drawable/ic_*.xml`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/drawable/) | Scalable vector graphics (SVG/XML): `ic_swap` (swap units), `ic_convert` (calculate), `ic_copy` (clipboard), `ic_clear` (reset), `ic_category` (grid icon), `ic_scale` (ruler/scale). |
| [`app/src/main/res/mipmap-anydpi-v26/`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/res/mipmap-anydpi-v26/) | Adaptive app launcher icons conforming to Android 8.0+ standards. |

---

### F. Testing & Verification Suite

| File Path | Purpose & Functionality |
| :--- | :--- |
| [`app/src/test/java/com/oibsip/unitconverter/UnitConverterTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/unitconverter/UnitConverterTest.java) | JUnit 4 test class executable in Android Studio. Verifies conversion mathematics across all units. |
| [`app/src/test/java/com/oibsip/unitconverter/UnitConverterVerification.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/unitconverter/UnitConverterVerification.java) | Standalone verification runner that can be compiled and executed directly from the terminal with any JDK to validate all 34 test cases without external IDE dependencies. |

---

## 4. Conversion Mathematics & The "Base Unit" Pattern

### The Problem with Direct Mapping
If an app supports $N$ units in a category, direct conversions would require $N \times (N - 1)$ conversion functions. For 8 length units, that would mean **56 separate formulas**!

### The Base Unit Solution
We designate **one anchor unit** per category:
- **Length:** Meter ($\text{m}$)
- **Weight:** Kilogram ($\text{kg}$)
- **Volume:** Liter ($\text{L}$)
- **Speed:** Meter per second ($\text{m/s}$)
- **Time:** Second ($\text{s}$)

Every unit simply declares its conversion factor relative to the anchor:
$$\text{Base Value} = \text{Input Value} \times \text{fromUnit.getFactorToBase()}$$
$$\text{Target Value} = \frac{\text{Base Value}}{\text{toUnit.getFactorToBase()}}$$

#### Example: Converting 5 Feet to Centimeters
1. `Foot.factorToBase` = $0.3048$ $\implies \text{Base Value} = 5 \times 0.3048 = 1.524\text{ meters}$.
2. `Centimeter.factorToBase` = $0.01$ $\implies \text{Target Value} = \frac{1.524}{0.01} = \mathbf{152.4\text{ cm}}$.

### Non-Linear Conversions (Temperature)
Temperature does not have a fixed zero point across scales, requiring affine transformation via Celsius:
- **Fahrenheit to Celsius:** $C = (F - 32) \times \frac{5}{9}$
- **Kelvin to Celsius:** $C = K - 273.15$
- **Celsius to Fahrenheit:** $F = (C \times \frac{9}{5}) + 32$
- **Celsius to Kelvin:** $K = C + 273.15$

---

## 5. Input Validation & Safety Strategy

The app prevents crashes and invalid states through three levels of validation:

1. **Empty Field Check:**
   - Detects `rawInput.trim().isEmpty()`.
   - Action: Displays `Toast.makeText(..., "Please enter a numeric value to convert", Toast.LENGTH_SHORT).show()` and marks the text input layout with an error indicator.
2. **Numeric Format Check:**
   - Wrapped inside a `try-catch (NumberFormatException)`.
   - Action: Displays `Toast.makeText(..., "Please enter a valid number", Toast.LENGTH_SHORT).show()`.
3. **Physical Law Boundary Check:**
   - Identifies temperatures below Absolute Zero ($0\text{ K}$, $-273.15^\circ\text{C}$, $-459.67^\circ\text{F}$).
   - Action: Displays `Toast` warning that temperatures below Absolute Zero are physically impossible.

---

## 6. Viva / Interview Defense Guide

When presenting this project to mentors or evaluators, here are the key technical highlights to mention:

1. **Why Java and XML?**  
   *"Java provides explicit object-oriented structure, while XML strictly separates UI presentation from business logic, aligning with Android's native development paradigms."*
2. **How do you avoid code duplication when converting units?**  
   *"I used the Base Unit Pattern. Every unit stores its ratio to a primary base unit. This reduced formula complexity from $O(N^2)$ to $O(N)$."*
3. **How does the category selector reset the dropdowns?**  
   *"In `MainActivity.java`, an `OnItemSelectedListener` on the category spinner triggers `onCategoryChanged()`, which builds a fresh `ArrayAdapter` populated with units for that category and resets the result views."*
4. **How do you ensure UI responsiveness?**  
   *"All mathematical operations are lightweight $O(1)$ operations, and keyboard management ensures virtual keyboards do not obscure the converted results."*
