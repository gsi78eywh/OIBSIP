# Oasis Infobyte Internship Program (OIBSIP)
### Android App Development Track · Project Deliverables

> Comprehensive repository containing source code, test suites, desktop launchers, web simulators, and technical documentation for assigned internship tasks at **Oasis Infobyte (OIBSIP)**.

---

## 📊 Task Tracking & Deliverables Dashboard

| Task ID | Application Name | Core Scope & Features | Status | Android Activity | Desktop App | Web Simulator | Unit Tests | Dedicated Guide |
| :--- | :--- | :--- | :---: | :--- | :--- | :--- | :--- | :--- |
| **TASK 1** | **Unit Converter** | 6 physical categories, 32 units, 2-step base math, unit swapping, equation breakdown, clipboard copy, input validation | **100% Complete** ✅ | [`MainActivity`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/MainActivity.java) | `run_app.bat` | [`web/index.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/index.html) | 34 Tests (`UnitConverterTest`) + 155 Verification Checks | [DOCUMENTATION.md](file:///c:/Users/SethAndreyJabagat/OIBSIP/DOCUMENTATION.md) |
| **TASK 2** | **To-Do App with Login** | User registration, login, SHA-256 password hashing, SQLite relational persistence, user-specific tasks, completion strikethrough, delete, session manager | **100% Complete** ✅ | [`LoginActivity`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/LoginActivity.java) | `run_todo.bat` | [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html) | 14 Tests (`TodoEngineTest`) | [TODO_GUIDE.md](file:///c:/Users/SethAndreyJabagat/OIBSIP/TODO_GUIDE.md) |
| **TASK 4** | **Quiz Application** | 15 curated CS/Tech questions, dynamic shuffle, 4 choices, instant green/red feedback, explanations, score analytics, high-score storage | **100% Complete** ✅ | [`QuizWelcomeActivity`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/QuizWelcomeActivity.java) | `run_quiz.bat` | [`web/quiz.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/quiz.html) | 10 Tests (`QuizEngineTest`) | [QUIZ_GUIDE.md](file:///c:/Users/SethAndreyJabagat/OIBSIP/QUIZ_GUIDE.md) |
| **TASK 5** | **Stopwatch & Lap Timer** | Millisecond precision, Start/Pause/Reset, dynamic button states, lap split intervals, Android lifecycle & rotation persistence | **100% Complete** ✅ | [`StopwatchActivity`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/stopwatch/StopwatchActivity.java) | `run_stopwatch.bat` | [`web/stopwatch.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/stopwatch.html) | 10 Tests (`StopwatchEngineTest`) | [STOPWATCH_GUIDE.md](file:///c:/Users/SethAndreyJabagat/OIBSIP/STOPWATCH_GUIDE.md) |

---

## ⚡ Quick Start Cheat Sheet

### 1. Run Automated Test Suite (All Tasks)
Verify all 58 JUnit 4 unit tests and 155 programmatic mathematical checks in under 1 second:
```cmd
.\run_tests.bat
```

### 2. Launch Desktop GUI Applications (No Emulator Required)
Every application features an interactive Windows Desktop GUI built on the decoupled Java engine:
- **Unit Converter Desktop GUI**: `.\run_app.bat`
- **Unit Converter Console CLI**: `.\run_converter.bat`
- **To-Do App with Login Desktop GUI**: `.\run_todo.bat`
- **Quiz Application Desktop GUI**: `.\run_quiz.bat`
- **Stopwatch & Lap Timer Desktop GUI**: `.\run_stopwatch.bat`

### 3. Launch Web Simulators in Browser
Test responsive mobile simulations directly in any modern browser:
- **Unit Converter**: [`web/index.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/index.html)
- **To-Do App with Login**: [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html)
- **Quiz Application**: [`web/quiz.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/quiz.html)
- **Stopwatch**: [`web/stopwatch.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/stopwatch.html)

### 4. Run in Android Studio
1. Open **Android Studio** and select **File > Open...** -> select the `OIBSIP` repository folder.
2. Allow Gradle sync to complete (`compileSdk 34`, `minSdk 21`, Material Components 3).
3. Select an emulator or connected physical Android device and click **Run** (`Shift + F10`).
4. The main screen provides 1-tap navigation buttons to switch between **Unit Converter**, **To-Do App**, **Quiz Application**, and **Stopwatch**.

---

## 📱 TASK 1 · Unit Converter Application

### 🎯 Objective
Build a modern, intuitive, and responsive Android application that converts numeric values between standard units of measurement across various physical domains (Length, Weight/Mass, Temperature, Volume, Speed, and Time) based on user input.

### ✨ Feature Checklist & Compliance
- [x] **Input field for numeric value**: Material outlined text field (`TextInputEditText`) supporting decimal input, signed values, and one-tap clear button.
- [x] **Source unit dropdown (Spinner)**: Custom-styled dropdown displaying unit name and symbol (e.g., *Centimeter (cm)*).
- [x] **Target unit dropdown (Spinner)**: Matching dropdown with smart default pairings.
- [x] **Unit Swap Button**: Instantly flips source and target units with immediate re-calculation.
- [x] **Convert Button**: Primary action button that validates input and computes the converted value.
- [x] **Result Display**: High-contrast, bold converted value with unit symbol, calculation formula / equation breakdown, and a **Copy to Clipboard** button.
- [x] **6 Measurement Categories (32 total units)**:
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

## 📝 TASK 2 · To-Do App with Login

### 🎯 Objective
Build a secure task management app where users register and log in before accessing their personal to-do lists. Task data must persist locally using a SQLite relational database.

### ✨ Feature Checklist & Compliance
- [x] **Login screen**: Email and password fields with validation toasts, auto-session routing, and Login button (`LoginActivity.java`).
- [x] **Sign-up screen**: Fields for full name, email, password, and confirm password + Register button (`RegisterActivity.java`).
- [x] **Password Hashing in SQLite**: Passwords converted to 64-character hexadecimal SHA-256 hashes using Java standard `MessageDigest` (never stored in plain text).
- [x] **Logout button**: Clears active session in `SessionManager` (`SharedPreferences`) and returns user to login screen.
- [x] **Task list screen**: Displays all tasks linked specifically to the logged-in user with pending and completed summary counters (`TodoListActivity.java`).
- [x] **"Add Task" button**: Floating Action Button (FAB) opens an input dialog to enter task name (required) and optional notes (`dialog_add_task.xml`).
- [x] **Mark as complete**: Checkbox toggles task completion state with strikethrough styling and updates SQLite database status.
- [x] **Permanent task deletion**: Trash icon with confirmation alert dialog permanently deletes task from SQLite.
- [x] **User-specific task isolation**: SQLite foreign key constraint `tasks.user_id = users.id` guarantees User A never sees User B's tasks.
- [x] **Empty state display**: Friendly graphic and prompt appears when the task list is empty.
- [x] **Filter chips**: Quick filter tabs to view "All", "Pending", or "Completed" tasks.
- [x] **Desktop Windows App**: Runnable via `.\run_todo.bat`.
- [x] **Web Simulator**: Runnable in browser via [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html).
- [x] **JUnit 4 Automated Tests**: 14 tests in [`TodoEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/todo/TodoEngineTest.java).

---

## 🧠 TASK 4 · Quiz Application

### 🎯 Objective
Build an interactive, multiple-choice quiz application on Computer Science & Technology fundamentals. Users answer questions one at a time with instant visual feedback and review their final score and performance metrics upon completion.

### ✨ Feature Checklist & Compliance
- [x] **Welcome Screen with Start Button**: Landing screen (`QuizWelcomeActivity`) with topic overview, rules card, persistent high-score tracking via `SharedPreferences`, and "Start Quiz" button.
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
- [x] **Web Simulator**: Runnable in any browser via [`web/quiz.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/quiz.html).
- [x] **JUnit 4 Automated Tests**: Complete test coverage via `QuizEngineTest.java`.

---

## ⏱️ TASK 5 · Stopwatch Application

### 🎯 Objective
Build a functional, high-precision stopwatch app with start, stop/pause, reset, and lap recording controls that accurately tracks elapsed time across Android lifecycle events.

### ✨ Feature Checklist & Compliance
- [x] **Large Digital Time Display**: Formatted in `MM:SS.cs` (or `HH:MM:SS.cs`) with 30ms real-time UI updates.
- [x] **Start / Resume Button**: Begins timer from 0 or resumes from paused elapsed state.
- [x] **Stop / Pause Button**: Freezes timer and preserves total accumulated time.
- [x] **Reset Button**: Halts timer, clears display to `00:00.00`, and resets lap history.
- [x] **Dynamic Visual States**: Button colors and enabled/disabled states change dynamically based on stopwatch state (Ready / Running / Paused).
- [x] **Lifecycle & Orientation Persistence**: Implements `onSaveInstanceState` and wall-clock time tracking via `SystemClock.uptimeMillis()` so timing is never lost when navigating away or rotating screen.
- [x] **(Bonus) Lap Recording**: Records individual lap split duration and cumulative elapsed time in a scrollable list with newest laps on top.
- [x] **Desktop Windows App**: Runnable via `.\run_stopwatch.bat`.
- [x] **Web Simulator**: Runnable in browser via [`web/stopwatch.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/stopwatch.html).
- [x] **JUnit 4 Automated Tests**: Complete test coverage via `StopwatchEngineTest.java`.

---

## 📁 Repository Directory Structure

```
OIBSIP/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml                         # Registered all 4 tasks
│   │   │   ├── java/com/oibsip/
│   │   │   │   ├── unitconverter/                          # [TASK 1] Unit Converter
│   │   │   │   │   ├── MainActivity.java                   # Controller & UI event handlers
│   │   │   │   │   ├── converter/UnitConverter.java        # Pure Java conversion engine
│   │   │   │   │   └── model/Category.java, Unit.java      # Data models
│   │   │   │   ├── todo/                                   # [TASK 2] To-Do App with Login
│   │   │   │   │   ├── LoginActivity.java                  # Login activity
│   │   │   │   │   ├── RegisterActivity.java               # Registration activity
│   │   │   │   │   ├── TodoListActivity.java               # Task management activity
│   │   │   │   │   ├── adapter/TaskAdapter.java            # RecyclerView adapter
│   │   │   │   │   ├── db/TodoDbHelper.java                # SQLite database helper
│   │   │   │   │   ├── engine/TodoEngine.java              # Decoupled to-do engine
│   │   │   │   │   ├── model/User.java, TaskItem.java      # Data models
│   │   │   │   │   ├── security/PasswordHasher.java        # SHA-256 password hasher
│   │   │   │   │   └── session/SessionManager.java         # SharedPreferences session manager
│   │   │   │   ├── quiz/                                   # [TASK 4] Quiz Application
│   │   │   │   │   ├── QuizWelcomeActivity.java            # Welcome & high-score screen
│   │   │   │   │   ├── QuizActivity.java                   # 4-option question UI
│   │   │   │   │   ├── QuizResultActivity.java             # Score breakdown screen
│   │   │   │   │   ├── data/QuestionBank.java              # 15 curated CS questions
│   │   │   │   │   ├── engine/QuizEngine.java              # Decoupled quiz logic engine
│   │   │   │   │   └── model/Question.java, QuizResult.java# Data models
│   │   │   │   └── stopwatch/                              # [TASK 5] Stopwatch & Laps
│   │   │   │       ├── StopwatchActivity.java              # Android UI & 30ms Handler loop
│   │   │   │       ├── adapter/LapAdapter.java             # Lap list adapter
│   │   │   │       ├── engine/StopwatchEngine.java         # Decoupled timing engine
│   │   │   │       └── model/LapItem.java                  # Lap data model
│   │   │   └── res/
│   │   │       ├── drawable/                               # Vector icons & state shapes
│   │   │       ├── layout/                                 # Responsive Material layouts
│   │   │       └── values/                                 # colors.xml, strings.xml, themes.xml
│   │   └── test/java/com/oibsip/                           # Automated JUnit test suites & desktop apps
│   │       ├── unitconverter/                              # Tests & DesktopUnitConverterApp
│   │       ├── todo/                                       # TodoEngineTest & DesktopTodoApp
│   │       ├── quiz/                                       # QuizEngineTest & DesktopQuizApp
│   │       └── stopwatch/                                  # StopwatchEngineTest & DesktopStopwatchApp
│   └── build.gradle                                        # App-level Gradle build script
├── web/                                                    # Web simulators
│   ├── index.html                                          # Task 1 Unit Converter simulator
│   ├── todo.html                                           # Task 2 To-Do App simulator
│   ├── quiz.html                                           # Task 4 Quiz App simulator
│   └── stopwatch.html                                      # Task 5 Stopwatch simulator
├── run_tests.bat                                           # 1-Click Multi-App Test Runner (All 4 Tasks)
├── run_app.bat                                             # 1-Click Desktop Unit Converter
├── run_todo.bat                                            # 1-Click Desktop To-Do App
├── run_quiz.bat                                            # 1-Click Desktop Quiz Launcher
├── run_stopwatch.bat                                       # 1-Click Desktop Stopwatch Launcher
└── README.md                                               # Portfolio master overview
```

---

## 📚 Complete Documentation Index

For in-depth explanations, architectural diagrams, defense guides, and build instructions, consult the dedicated guides below:

| Documentation File | Target Audience | Primary Contents |
| :--- | :--- | :--- |
| [**DOCUMENTATION.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/DOCUMENTATION.md) | Evaluator / Tech Lead | Master repository technical architecture manual covering all tasks, design patterns, data flows, and test matrices. |
| [**TODO_GUIDE.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/TODO_GUIDE.md) | Reviewer / Developer | Deep-dive for **Task 2 (To-Do App)**: SQLite relational schema, SHA-256 hashing, session management, and CRUD operations. |
| [**QUIZ_GUIDE.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/QUIZ_GUIDE.md) | Reviewer / Developer | Deep-dive for **Task 4 (Quiz App)**: dynamic shuffling, instant visual feedback, option locking, and score grading. |
| [**STOPWATCH_GUIDE.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/STOPWATCH_GUIDE.md) | Reviewer / Developer | Deep-dive for **Task 5 (Stopwatch)**: timing accuracy, state machine, lifecycle persistence, and lap calculations. |
| [**CLIENT_EXPLANATION_GUIDE.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/CLIENT_EXPLANATION_GUIDE.md) | Client / Non-Technical | Plain-English script to explain the entire project in 30 seconds with zero confusing technical jargon. |
| [**BUILD_PROCESS_AND_DEBUG_GUIDE.md**](file:///c:/Users/SethAndreyJabagat/OIBSIP/BUILD_PROCESS_AND_DEBUG_GUIDE.md) | Developer / Maintainer | Step-by-step instructions for Android Studio setup, Gradle sync, Logcat filtering, and common debugging tips. |

---

## 🧪 Testing & Quality Assurance Summary

All components are continuously verified through the automated test runner [`run_tests.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_tests.bat):

```
====================================================================
     OIBSIP Task 1 - Unit Converter Comprehensive Verification      
====================================================================
Summary: 155 passed, 0 failed.
Status:  [SUCCESS] All tests passed cleanly!
====================================================================

[3/3] Running JUnit 4 Test Suite (Unit Converter + Stopwatch + Quiz + To-Do App)...
JUnit version 4.13.2
..........................................................
Time: 0.365

OK (58 tests)

=========================================================
 ALL TEST SUITES PASSED PERFECTLY (100% SUCCESS)
=========================================================
```

- **Programmatic Verification**: 155 mathematical boundary and identity checks.
- **JUnit 4 Test Suite**: 58 automated unit tests covering all pure Java engines.
- **Pass Rate**: 100% across all modules.
