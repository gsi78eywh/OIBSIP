# OIBSIP · Oasis Infobyte Internship Program

> Repository containing project deliverables, source code, and documentation for assigned internship tasks at **Oasis Infobyte (OIBSIP)**.

---

## 📱 TASK 1 · Unit Converter Application

### 🎯 Objective
Build a modern, intuitive, and responsive Android application that converts numeric values between standard units of measurement across various physical domains (Length, Weight/Mass, Temperature, Volume, Speed, and Time) based on user input.

---

### ✨ Feature Checklist

- [x] **Input field for numeric value**: Material outlined text field (`TextInputEditText`) supporting decimal input, signed values, and one-tap clear button.
- [x] **Source unit dropdown (Spinner)**: Custom-styled dropdown displaying unit name and symbol (e.g., *Centimeter (cm)*).
- [x] **Target unit dropdown (Spinner)**: Matching dropdown with smart default pairings.
- [x] **Unit Swap Button**: Instantly flips source and target units with immediate re-calculation.
- [x] **Convert Button**: Primary action button that validates input and computes the converted value.
- [x] **Result Display**: High-contrast, bold converted value with unit symbol, calculation formula / equation breakdown, and a **Copy to Clipboard** button.
- [x] **6 Measurement Categories** (Exceeds the 3 required):
  1. **Length**: Millimeter (mm), Centimeter (cm), Meter (m), Kilometer (km), Inch (in), Foot (ft), Yard (yd), Mile (mi).
  2. **Weight / Mass**: Milligram (mg), Gram (g), Kilogram (kg), Metric Ton (t), Ounce (oz), Pound (lb).
  3. **Temperature**: Celsius (°C), Fahrenheit (°F), Kelvin (K).
  4. **Volume / Capacity**: Milliliter (mL), Liter (L), US Fluid Ounce (fl oz), US Cup, US Pint (pt), US Gallon (gal).
  5. **Speed**: Meter/second (m/s), Kilometer/hour (km/h), Miles/hour (mph), Knot (kn).
  6. **Time**: Millisecond (ms), Second (s), Minute (min), Hour (hr), Day (d).
- [x] **Input Validation & Safety**:
  - Displays a `Toast` message if the input field is empty (`"Please enter a numeric value to convert"`).
  - Displays a `Toast` message if the value is non-numeric or invalid (`"Please enter a valid number"`).
  - Physical boundary checks (e.g., alert if temperature is below Absolute Zero: `0 K`, `-273.15 °C`, `-459.67 °F`).
- [x] **Category Selector**: Category dropdown that dynamically repopulates unit dropdowns with category-specific units and resets stale results.
- [x] **Reset / Clear Button**: Clears the input, error indicators, and resets the result view.

---

### 🛠️ Tech Stack & Architecture

- **Platform**: Android OS (API 21+ / Android 5.0 Lollipop to Android 14+)
- **IDE**: Android Studio
- **Language**: Java 8+ / Java 17
- **UI & Layouts**: XML (Material Components 3, Responsive `ScrollView`, `CardView`, Vector Drawables)
- **Build System**: Gradle 8.5 with Android Gradle Plugin (AGP) 8.2.2
- **Testing**: JUnit 4 unit tests covering 34 mathematical conversion scenarios and edge cases.

#### Project Directory Structure
```
OIBSIP/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/oibsip/unitconverter/
│   │   │   │   ├── MainActivity.java           # Main Activity & UI Event Handlers
│   │   │   │   ├── converter/
│   │   │   │   │   └── UnitConverter.java      # Conversion Engine & Math Formulas
│   │   │   │   └── model/
│   │   │   │       ├── Category.java           # Measurement Category Enum
│   │   │   │       └── Unit.java               # Unit Data Model
│   │   │   └── res/
│   │   │       ├── drawable/                   # Vector icons & card/spinner shapes
│   │   │       ├── layout/
│   │   │       │   ├── activity_main.xml       # Main responsive Material layout
│   │   │       │   ├── item_spinner.xml        # Custom spinner item layout
│   │   │       │   └── item_spinner_dropdown.xml # Custom dropdown popup item
│   │   │       ├── values/
│   │   │       │   ├── colors.xml              # Indigo & Slate Material palette
│   │   │       │   ├── strings.xml             # Localized UI string resources
│   │   │       │   └── themes.xml              # Application theme definition
│   │   │       └── xml/                        # Backup & data extraction rules
│   │   └── test/
│   │       └── java/com/oibsip/unitconverter/
│   │           ├── UnitConverterTest.java      # JUnit 4 test suite
│   │           └── UnitConverterVerification.java # Standalone verification runner
│   ├── build.gradle                            # App module Gradle configuration
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle                                # Root project Gradle configuration
├── settings.gradle                             # Project module settings
├── gradlew & gradlew.bat                       # Gradle wrapper scripts
└── README.md
```

---

### 🚀 How to Run in Android Studio

1. **Clone or Open the Repository**:
   - Open **Android Studio**.
   - Click **File** > **Open...**
   - Select the `OIBSIP` repository folder.
2. **Sync Gradle**:
   - Android Studio will automatically detect the Gradle files and initiate a Gradle Sync.
   - Wait for dependencies to download.
3. **Run the App**:
   - Select a target device (Android Emulator or a physical phone via USB Debugging).
   - Click the green **Run** button (or press `Shift + F10`).
4. **Run Unit Tests**:
   - In Android Studio, navigate to `app/src/test/java/com/oibsip/unitconverter/UnitConverterTest.java` or `StopwatchEngineTest.java`.
   - Right-click the class and select **Run**.

---

## 🧠 TASK 4 · Quiz Application

### 🎯 Objective
Build an interactive, multiple-choice quiz application on Computer Science & Technology fundamentals. Users answer questions one at a time with instant visual feedback and review their final score and performance metrics upon completion.

### ✨ Feature Checklist
- [x] **Welcome Screen with Start Button**: Landing screen (`QuizWelcomeActivity`) with topic overview, rules card, persistent high-score tracking, and Start button.
- [x] **Question Screen**: Shows question text, category & difficulty chip, question counter (e.g., *"Question 3 of 10"*), live score badge, and animated linear progress bar (`QuizActivity`).
- [x] **4 Answer Options**: Styled interactive cards with letter prefixes (A, B, C, D) and smooth state transitions.
- [x] **15 Curated Questions**: Pre-loaded question bank spanning Computer Science, Data Structures, Architecture, Networking, and Programming Fundamentals.
- [x] **Randomized Shuffling**: 10 questions randomly sampled per round, and answer options dynamically scrambled while maintaining correct answer tracking.
- [x] **Immediate Answer Feedback**:
  - Correct answer selected: highlights **Green (`#10B981`)** with a checkmark.
  - Wrong answer selected: highlights **Red (`#EF4444`)** with a cross, while revealing the **correct answer in Green** so the user learns.
  - Explanation card slides in with contextual rationale.
  - Options locked to prevent multiple answers per question.
- [x] **Next Button**: Advances to the following question (switches to *"View Results"* on final question).
- [x] **Score Tracking Throughout Quiz**: Live score badge updates dynamically.
- [x] **Results Screen**: Detailed breakdown (`QuizResultActivity`) displaying total score, accuracy percentage, correct count, incorrect count, qualitative feedback grade, and personal best celebration.
- [x] **Restart Quiz**: 1-tap restart button that re-samples and scrambles a new quiz session immediately.
- [x] **Desktop Windows App**: Runnable directly via `.\run_quiz.bat`.
- [x] **Web Simulator**: Runnable in any browser via `web/quiz.html`.
- [x] **JUnit 4 Automated Tests**: Complete test coverage via `QuizEngineTest.java`.

---

## ⏱️ TASK 5 · Stopwatch Application

### 🎯 Objective
Build a functional, high-precision stopwatch app with start, stop/pause, reset, and lap recording controls that accurately tracks elapsed time across Android lifecycle events.

### ✨ Feature Checklist
- [x] **Large Digital Time Display**: Formatted in `MM:SS.cs` (or `HH:MM:SS.cs`) with 30ms real-time UI updates.
- [x] **Start / Resume Button**: Begins timer from 0 or resumes from paused elapsed state.
- [x] **Stop / Pause Button**: Freezes timer and preserves total accumulated time.
- [x] **Reset Button**: Halts timer and clears display to `00:00.00` and clears lap history.
- [x] **Dynamic Visual States**: Button colors and enabled/disabled states change dynamically based on stopwatch state (Ready / Running / Paused).
- [x] **Lifecycle & Orientation Persistence**: Implements `onSaveInstanceState` and wall-clock time tracking via `SystemClock.uptimeMillis()` so timing is never lost when navigating away or rotating screen.
- [x] **(Bonus) Lap Recording**: Records individual lap split duration and cumulative elapsed time in a scrollable list.
- [x] **Desktop Windows App**: Runnable via `.\run_stopwatch.bat`.
- [x] **Web Simulator**: Runnable in browser via `web/stopwatch.html`.


