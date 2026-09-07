# OIBSIP · Task 5: Stopwatch Application Guide

> Comprehensive guide for building, running, and verifying **Task 5: Stopwatch & Lap Timer Application** in the **Oasis Infobyte Internship Program (OIBSIP)**.

---

## 1. Application Overview

The **Stopwatch Application** is a precision timing utility built with **Android Studio, Java, and XML Material Design 3**. It accurately tracks elapsed time with millisecond precision, supports lap split intervals, handles Android activity lifecycle events, and provides visual button feedback.

### Features & Compliance
- [x] **Large Time Display**: Formatted in `MM:SS.cs` (or `HH:MM:SS.cs` when hours exceed 0) with 30ms high-precision display updates.
- [x] **Start / Resume Button**: Starts timer from zero or resumes seamlessly from paused state.
- [x] **Pause Button**: Freezes the timer and stores accumulated elapsed time.
- [x] **Reset Button**: Halts the timer, resets time to `00:00.00`, and clears lap history.
- [x] **Dynamic Visual Button States**: Start disabled while running; Pause disabled while stopped; Reset and Resume active when paused.
- [x] **Lap Recording (Bonus)**: Computes lap split intervals and cumulative times, displaying newest laps at the top of a scrollable list.
- [x] **Lifecycle & Rotation Persistence**: Implements `onSaveInstanceState` and `onRestoreInstanceState` so the timer never loses elapsed time when the screen rotates or the user navigates away.

---

## 2. Architecture & File Structure

```
OIBSIP/
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml                         # Registered both Tasks
│       │   ├── java/com/oibsip/
│       │   │   ├── stopwatch/
│       │   │   │   ├── StopwatchActivity.java              # Android UI & Handler loop
│       │   │   │   ├── adapter/
│       │   │   │   │   └── LapAdapter.java                 # Custom lap list adapter
│       │   │   │   ├── engine/
│       │   │   │   │   └── StopwatchEngine.java            # Pure Java stopwatch engine
│       │   │   │   └── model/
│       │   │   │       └── LapItem.java                    # Lap data model
│       │   │   └── unitconverter/                          # Task 1 Unit Converter
│       │   └── res/
│       │       ├── drawable/                               # ic_play, ic_pause, ic_reset, ic_lap
│       │       └── layout/
│       │           ├── activity_stopwatch.xml              # Stopwatch responsive layout
│       │           └── item_lap.xml                        # Lap row item layout
│       └── test/java/com/oibsip/stopwatch/
│           ├── StopwatchEngineTest.java                    # JUnit 4 automated test suite
│           └── DesktopStopwatchApp.java                    # Windows Desktop Swing GUI
├── run_stopwatch.bat                                       # 1-Click Desktop Stopwatch Launcher
├── run_tests.bat                                           # 1-Click Multi-App Test Runner
└── web/
    └── stopwatch.html                                      # Interactive web simulator
```

---

## 3. How to Run

### Option 1: Desktop Application on Windows (No Emulator Needed)
Double-click or execute in PowerShell:
```powershell
.\run_stopwatch.bat
```
This compiles the code and opens a live desktop window with the timer, buttons, and lap list!

### Option 2: Automated Tests
Run all 34 JUnit tests and 155 verification checks:
```powershell
.\run_tests.bat
```

### Option 3: Android Studio
1. Open the `OIBSIP` project in Android Studio.
2. Select `StopwatchActivity` or open the app and tap **"⏱️ Open Task 5 · Stopwatch App"**.
3. Run on your emulator or connected device.

### Option 4: Web Simulator
Open `web/stopwatch.html` in any modern web browser.
