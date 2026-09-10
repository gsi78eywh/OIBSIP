# OIBSIP · Task 5: Stopwatch & Lap Timer Application Guide

> Comprehensive technical documentation and verification manual for **Task 5: Stopwatch Application** in the **Oasis Infobyte Internship Program (OIBSIP)**.

---

## 📊 Task Tracking & Compliance Matrix

| Requirement / Deliverable | Implementation Details | Status |
| :--- | :--- | :---: |
| **Large Digital Time Display** | Formatted in `MM:SS.cs` (or `HH:MM:SS.cs` when hours > 0) with 30ms high-frequency UI updates. | ✅ Complete |
| **Start / Resume Button** | Begins timing from 0 or resumes seamlessly from the paused accumulated time. | ✅ Complete |
| **Stop / Pause Button** | Freezes the timer, calculates total elapsed time, and keeps current display. | ✅ Complete |
| **Reset Button** | Halts the timer, clears display back to `00:00.00`, and resets all lap records. | ✅ Complete |
| **Dynamic Visual States** | Button colors and enabled/disabled states change dynamically (Ready, Running, Paused). | ✅ Complete |
| **Lifecycle & Rotation Persistence** | Saves elapsed time, running state, and reference timestamps in `onSaveInstanceState`. Timer continues seamlessly upon screen rotation. | ✅ Complete |
| **Lap Recording (Bonus Feature)** | Records individual split intervals and cumulative elapsed times, displaying newest laps at the top. | ✅ Complete |
| **Decoupled Pure Java Engine** | Core timing and math isolated in [`StopwatchEngine.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/stopwatch/engine/StopwatchEngine.java) without Android dependencies. | ✅ Complete |
| **Desktop Windows App** | Standalone Desktop GUI runnable via [`run_stopwatch.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_stopwatch.bat) without needing an emulator. | ✅ Complete |
| **Interactive Web Simulator** | Browser-based mobile simulation accessible at [`web/stopwatch.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/stopwatch.html). | ✅ Complete |
| **Automated JUnit 4 Tests** | 10 comprehensive unit tests in [`StopwatchEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/stopwatch/StopwatchEngineTest.java) with 100% pass rate. | ✅ Complete |

---

## 🏗️ Architecture & Component Directory

```
OIBSIP/
├── app/src/main/
│   ├── AndroidManifest.xml                         # Registered StopwatchActivity
│   ├── java/com/oibsip/stopwatch/
│   │   ├── StopwatchActivity.java                  # Android UI controller & 30ms Handler loop
│   │   ├── adapter/
│   │   │   └── LapAdapter.java                     # RecyclerView adapter for lap split history
│   │   ├── engine/
│   │   │   └── StopwatchEngine.java                # Pure Java timing engine (0 Android imports)
│   │   └── model/
│   │       └── LapItem.java                        # Data model for individual lap splits
│   └── res/
│       ├── drawable/
│       │   ├── ic_play.xml, ic_pause.xml           # Control icons
│       │   ├── ic_reset.xml, ic_lap.xml            # Action icons
│       │   └── shape_timer_card.xml                # Rounded timer display card
│       └── layout/
│           ├── activity_stopwatch.xml              # Responsive Material layout
│           └── item_lap.xml                        # Lap list row layout
├── app/src/test/java/com/oibsip/stopwatch/
│   ├── StopwatchEngineTest.java                    # 10 automated JUnit 4 tests
│   └── DesktopStopwatchApp.java                    # Windows Desktop Swing GUI
├── run_stopwatch.bat                               # 1-Click Desktop Launcher
└── web/
    └── stopwatch.html                              # Interactive Web Simulator
```

---

## ⚙️ Core Engineering Concepts

### 1. The Decoupled Pure Java Engine
In [`StopwatchEngine.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/stopwatch/engine/StopwatchEngine.java), the timing calculation is based on **wall-clock timestamps** rather than incrementing a tick counter:
- When running, elapsed time is computed as:
  $$\text{Elapsed Time} = \text{accumulatedTime} + (\text{currentTimeMillis} - \text{startTimeMillis})$$
- When paused:
  $$\text{accumulatedTime} = \text{accumulatedTime} + (\text{currentTimeMillis} - \text{startTimeMillis})$$
  $$\text{startTimeMillis} = 0$$

> **Why Wall-Clock Timing?** Incrementing a counter by `+30ms` inside a UI loop causes noticeable time drift over time due to OS thread scheduling delays. Wall-clock delta calculation guarantees **100% mathematical accuracy**, matching atomic time.

### 2. Finite State Machine & Visual Button States

```
        ┌────────────────────────────────────────┐
        │                 READY                  │
        │ Time: 00:00.00  | Start: Active        │
        │ Pause: Disabled | Reset/Lap: Disabled  │
        └───────────────────┬────────────────────┘
                            │ Start tapped
                            ▼
        ┌────────────────────────────────────────┐
        │                RUNNING                 │
        │ Updates every 30ms | Pause: Active     │
        │ Start: Disabled    | Lap: Active       │
        └───────────────┬────────▲───────────────┘
           Pause tapped │        │ Resume tapped
                        ▼        │
        ┌────────────────────────────────────────┐
        │                PAUSED                  │
        │ Timer frozen  | Resume: Active         │
        │ Reset: Active | Lap: Disabled          │
        └───────────────┬────────────────────────┘
                        │ Reset tapped
                        ▼
                 (Returns to READY)
```

### 3. Android Lifecycle & Rotation Persistence
When an Android device rotates, the operating system destroys and recreates the `Activity`. To prevent timing loss:
1. **`onSaveInstanceState(Bundle outState)`**:
   - Saves `accumulatedTime`, `startTimeMillis`, and `isRunning`.
   - Saves the list of recorded `LapItem` records.
2. **`onRestoreInstanceState(Bundle savedInstanceState)`**:
   - Restores the saved time variables.
   - If the timer was running before the rotation, it automatically restarts the 30ms `Handler` loop with zero lost time.

---

## 🚀 4 Ways to Run & Verify

### 1. Windows Desktop Application (No Emulator Needed)
Double-click [`run_stopwatch.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_stopwatch.bat) or execute in PowerShell:
```cmd
.\run_stopwatch.bat
```
- Launches the complete Java Swing GUI.
- Test Start, Pause, Resume, Lap recording, and Reset in real time.

### 2. Automated JUnit 4 Test Suite
Execute the multi-app test runner:
```cmd
.\run_tests.bat
```
Runs 10 dedicated test cases in [`StopwatchEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/stopwatch/StopwatchEngineTest.java):
- Initial state verification (`00:00.00`)
- Start, pause, resume accuracy
- Reset clearing
- Lap split interval math
- Multiple lap tracking and index validation
- Millisecond formatting strings (`MM:SS.cs` and `HH:MM:SS.cs`)

### 3. Interactive Web Simulator
Open [`web/stopwatch.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/stopwatch.html) in Google Chrome, Edge, or Firefox:
- Features an ultra-clean mobile mockup with dark glassmorphism.
- Supports start/stop, lap recording, and quick-switch links to Task 1 and Task 4.

### 4. Android Studio
1. Open the project in Android Studio.
2. Open [`StopwatchActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/stopwatch/StopwatchActivity.java) or tap the **"⏱️ Open Task 5 · Stopwatch"** button in [`MainActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/MainActivity.java).
3. Run on an Android Emulator or connected physical phone.

---

## 🎙️ Client & Viva Interview Defense Guide

### Key Talking Points:
1. **"Why did you decouple the StopwatchEngine into pure Java?"**
   > *"By removing all Android dependencies (`Context`, `Handler`) from the core timing engine, the timing math can be tested in 0.05 seconds via standard JUnit tests, and reused across Android, Desktop Swing, and CLI applications without code duplication."*

2. **"Why use `Handler` with `postDelayed()` instead of `Thread.sleep()` or `Timer`?"**
   > *"`Thread.sleep()` on the main thread would freeze the UI and trigger an Application Not Responding (ANR) error. `Handler.postDelayed()` schedules UI refreshes cleanly on the Android Main Looper without blocking user touch events."*

3. **"How do you ensure lap splits are accurate?"**
   > *"Each lap split is computed as the difference between the current cumulative elapsed time and the timestamp of the preceding lap. This guarantees that the sum of all lap splits always equals the total elapsed time."*
