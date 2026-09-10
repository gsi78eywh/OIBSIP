# OIBSIP · Task 4: Quiz Application Guide

> Comprehensive guide for building, running, and verifying **Task 4: Quiz Application** in the **Oasis Infobyte Internship Program (OIBSIP)**.

---

## 1. Application Overview

The **Quiz Application** is an interactive, multiple-choice educational quiz utility built with **Android Studio, Java, and XML Material Design 3**. It presents questions on Computer Science, programming, and technology fundamentals one at a time, provides instant visual feedback with explanations, and tracks accuracy and high scores.

### Feature Checklist & Compliance
- [x] **Welcome Screen with Start Button**: Attractive landing screen (`QuizWelcomeActivity`) with topic overview, rules card, personal best score, and start button.
- [x] **Question Screen**: Displays current question text, category & difficulty chip, question counter (e.g., "Question 3 of 10"), live score badge, and an animated progress bar.
- [x] **4 Answer Options**: Styled interactive cards with letter prefixes (A, B, C, D) and smooth hover/press states.
- [x] **15 Curated Questions**: Pre-loaded question bank spanning data structures, algorithms, computer architecture, networking, and programming languages.
- [x] **Randomized Shuffling**: Questions are sampled in a shuffled order each session, and answer choices are dynamically scrambled while preserving correct answer tracking.
- [x] **Immediate Answer Feedback**:
  - Tapping the **correct** answer highlights it in **Green (`#10B981`)** with a checkmark.
  - Tapping a **wrong** answer highlights it in **Red (`#EF4444`)** with a cross, while simultaneously revealing the **correct answer in Green** so the user learns.
  - An explanation card automatically reveals the rationale behind the correct answer.
  - Options are locked to prevent multiple answers per question.
- [x] **Next Button**: Advances to the following question (or "View Results" on the final question).
- [x] **Score Tracking**: Live score updates dynamically throughout the quiz.
- [x] **Results Screen**: Detailed performance breakdown displaying accuracy percentage, qualitative grade, correct count, incorrect count, total questions, and personal best celebration.
- [x] **Restart Quiz**: 1-tap restart button that re-samples and scrambles a new quiz session immediately.

---

## 2. Architecture & File Structure

```
OIBSIP/
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml                         # Registered Quiz activities
│       │   ├── java/com/oibsip/
│       │   │   ├── quiz/
│       │   │   │   ├── model/
│       │   │   │   │   ├── Question.java                   # Question model with option shuffling
│       │   │   │   │   └── QuizResult.java                 # Score calculations & grade remarks
│       │   │   │   ├── data/
│       │   │   │   │   └── QuestionBank.java               # 15+ curated CS/Tech trivia questions
│       │   │   │   ├── engine/
│       │   │   │   │   └── QuizEngine.java                 # Decoupled core logic engine
│       │   │   │   ├── QuizWelcomeActivity.java            # Welcome screen & personal best
│       │   │   │   ├── QuizActivity.java                   # Interactive question screen
│       │   │   │   └── QuizResultActivity.java             # Score breakdown & restart screen
│       │   │   ├── stopwatch/                              # Task 5 Stopwatch
│       │   │   └── unitconverter/                          # Task 1 Unit Converter
│       │   └── res/
│       │       ├── drawable/                               # ic_quiz, ic_trophy, ic_check_circle, ic_cancel
│       │       └── layout/
│       │           ├── activity_quiz_welcome.xml           # Welcome screen layout
│       │           ├── activity_quiz.xml                   # Question & 4 options layout
│       │           └── activity_quiz_result.xml            # Score card & stats breakdown layout
│       └── test/java/com/oibsip/quiz/
│           ├── QuizEngineTest.java                         # JUnit 4 test suite (10 automated tests)
│           └── DesktopQuizApp.java                         # Windows Desktop Swing GUI
├── run_quiz.bat                                            # 1-Click Desktop Quiz Launcher
├── run_tests.bat                                           # Multi-app test runner (44 JUnit tests)
└── web/
    └── quiz.html                                           # Interactive web simulator with mobile frame
```

---

## 3. How to Run

### Option 1: Desktop Application on Windows (No Emulator Needed)
Double-click or run in terminal:
```powershell
.\run_quiz.bat
```
This compiles the code and opens an interactive desktop window with the full quiz experience!

### Option 2: Automated Tests
Run all 44 JUnit tests and 155 verification checks:
```powershell
.\run_tests.bat
```

### Option 3: Android Studio
1. Open the `OIBSIP` project in Android Studio.
2. Run the application on your emulator or connected phone.
3. Tap **"🧠 Task 4 · Quiz"** from the main dashboard or launch `QuizWelcomeActivity` directly.

### Option 4: Web Simulator
Open `web/quiz.html` in any web browser to interact with the responsive mobile simulation.

---

## 4. Question Bank Topics

| Category | Example Question | Difficulty |
| :--- | :--- | :--- |
| **Computer Hardware** | What does CPU stand for in computer systems? | Easy |
| **Data Structures** | Which data structure operates on LIFO? | Easy |
| **History of Computing** | Who is known as the father of modern Computer Science? | Medium |
| **Programming Languages** | Which language was created by James Gosling in 1995? | Easy |
| **Algorithms** | What is the search time complexity of a balanced BST? | Medium |
| **Networking** | Which protocol securely encrypts web traffic? | Easy |
| **OOP Concepts** | What concept allows a child class to rewrite a parent method? | Medium |
| **Databases** | What does SQL stand for? | Easy |
| **Computer Hardware** | Which primary memory loses data when powered off? | Easy |
| **Networking** | What does IP address stand for? | Easy |
| **Developer Tools** | Which Git command records staged changes? | Easy |
| **System Software** | Which of the following is NOT an Operating System? | Easy |
| **Android Development** | What is the primary purpose of AndroidManifest.xml? | Medium |
| **Digital Logic** | What decimal value does binary 00001010 represent? | Medium |
| **Algorithms** | Which sorting algorithm consistently achieves O(n log n)? | Medium |
