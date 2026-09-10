# OIBSIP · Task 4: Quiz Application Comprehensive Guide

> Comprehensive technical documentation, architecture reference, and verification manual for **Task 4: Quiz Application** in the **Oasis Infobyte Internship Program (OIBSIP)**.

---

## 📊 Task Tracking & Compliance Matrix

| Requirement / Deliverable | Implementation Details | Status |
| :--- | :--- | :---: |
| **Welcome Screen with Start Button** | Attractive landing screen ([`QuizWelcomeActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/QuizWelcomeActivity.java)) with topic overview, rules card, persistent personal best, and Start button. | ✅ Complete |
| **Question Screen** | Displays question text, category & difficulty chip, question counter (e.g., *"Question 3 of 10"*), live score badge, and an animated progress bar ([`QuizActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/QuizActivity.java)). | ✅ Complete |
| **4 Answer Options** | Styled interactive cards with letter prefixes (A, B, C, D) and smooth state transitions. | ✅ Complete |
| **15 Curated Questions** | Pre-loaded question bank ([`QuestionBank.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/data/QuestionBank.java)) spanning data structures, algorithms, computer architecture, networking, and programming fundamentals. | ✅ Complete |
| **Randomized Shuffling** | Questions are randomly sampled each round, and the 4 choices are dynamically scrambled while preserving accurate correct-answer tracking. | ✅ Complete |
| **Immediate Answer Feedback** | Correct answer selected highlights in **Green (`#10B981`)** with a checkmark; wrong answer highlights in **Red (`#EF4444`)** with a cross while revealing the correct answer in Green. Explanation card reveals below. | ✅ Complete |
| **Option Locking** | Options are locked immediately after selection to prevent multiple answers per question. | ✅ Complete |
| **Next Button** | Advances to the next question (transitions to *"View Results"* on final question). | ✅ Complete |
| **Score Tracking** | Dynamic live score tracking updated throughout the quiz. | ✅ Complete |
| **Results Screen** | Detailed breakdown ([`QuizResultActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/QuizResultActivity.java)) displaying accuracy percentage, qualitative feedback grade, correct count, incorrect count, and high-score celebration. | ✅ Complete |
| **Restart Quiz** | 1-tap restart button that re-samples and scrambles a new quiz session immediately. | ✅ Complete |
| **Decoupled Pure Java Engine** | Core game loop, score calculation, and question shuffling isolated in [`QuizEngine.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/engine/QuizEngine.java). | ✅ Complete |
| **Desktop Windows App** | Standalone Desktop GUI runnable via [`run_quiz.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_quiz.bat) without needing an emulator. | ✅ Complete |
| **Interactive Web Simulator** | Browser-based mobile simulation accessible at [`web/quiz.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/quiz.html). | ✅ Complete |
| **Automated JUnit 4 Tests** | 10 comprehensive unit tests in [`QuizEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/quiz/QuizEngineTest.java) with 100% pass rate. | ✅ Complete |

---

## 🏗️ Architecture & Component Directory

```
OIBSIP/
├── app/src/main/
│   ├── AndroidManifest.xml                         # Registered Quiz activities
│   ├── java/com/oibsip/quiz/
│   │   ├── QuizWelcomeActivity.java                # Welcome screen & personal best storage
│   │   ├── QuizActivity.java                       # 4-option question UI & feedback loop
│   │   ├── QuizResultActivity.java                 # Score breakdown & restart screen
│   │   ├── data/
│   │   │   └── QuestionBank.java                   # 15+ curated CS & Tech trivia questions
│   │   ├── engine/
│   │   │   └── QuizEngine.java                     # Decoupled core game engine
│   │   └── model/
│   │       ├── Question.java                       # Question model with option shuffling
│   │       └── QuizResult.java                     # Score calculations & qualitative grades
│   └── res/
│       ├── drawable/
│       │   ├── ic_quiz.xml, ic_trophy.xml          # Header & reward icons
│       │   ├── ic_check_circle.xml, ic_cancel.xml  # Feedback icons
│       │   ├── shape_quiz_option_default.xml       # Neutral card border
│       │   ├── shape_quiz_option_correct.xml       # Green feedback card border
│       │   └── shape_quiz_option_wrong.xml         # Red feedback card border
│       └── layout/
│           ├── activity_quiz_welcome.xml           # Welcome screen layout
│           ├── activity_quiz.xml                   # Question & 4 options layout
│           └── activity_quiz_result.xml            # Score card & stats breakdown layout
├── app/src/test/java/com/oibsip/quiz/
│   ├── QuizEngineTest.java                         # JUnit 4 test suite (10 automated tests)
│   └── DesktopQuizApp.java                         # Windows Desktop Swing GUI
├── run_quiz.bat                                    # 1-Click Desktop Quiz Launcher
└── web/
    └── quiz.html                                   # Interactive web simulator
```

---

## ⚙️ Core Engineering Concepts

### 1. Dynamic Question & Option Scrambling
In [`Question.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/model/Question.java), when `createShuffledCopy()` is called:
1. It captures the text of the correct answer string: `String correctValue = options.get(correctOptionIndex)`.
2. It creates a mutable copy of the option strings and randomizes their order: `Collections.shuffle(shuffledOptions)`.
3. It finds the new index of `correctValue` within the scrambled list: `shuffledOptions.indexOf(correctValue)`.
4. It constructs a new `Question` instance with the updated correct index.
> **Outcome:** Users never encounter identical question patterns, preventing muscle-memory answering and ensuring true knowledge retention.

### 2. Immediate Visual Feedback & Option Locking
When an option is selected:
- The UI triggers `QuizEngine.submitAnswer(selectedIndex)`.
- If correct: The selected card turns **Emerald Green (`#10B981`)** with a checkmark icon.
- If wrong: The selected card turns **Crimson Red (`#EF4444`)** with a cancel icon, and the correct option simultaneously illuminates in **Emerald Green** so the learner immediately absorbs the right answer.
- An explanation card slides into view below the options with rationale.
- All option views are immediately disabled (`card.setClickable(false)`) to eliminate duplicate submissions.
- The "Next" button activates.

### 3. Result Analytics & Grade Calculation
In [`QuizResult.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/model/QuizResult.java):
- **Accuracy Percentage:** $\text{Percentage} = \text{round}((\frac{\text{correctCount}}{\text{totalCount}}) \times 100)$
- **Qualitative Grade Matrix:**
  - $\ge 90\%$: *"Outstanding! Master of Tech!"* (Gold Trophy)
  - $\ge 70\%$: *"Great Job! Solid Knowledge!"* (Silver Trophy)
  - $\ge 50\%$: *"Good Effort! Keep Practicing!"* (Bronze Trophy)
  - $< 50\%$: *"Needs Improvement! Try Again!"* (Review Prompt)

---

## 🚀 4 Ways to Run & Verify

### 1. Windows Desktop Application (No Emulator Needed)
Double-click [`run_quiz.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_quiz.bat) or execute in PowerShell:
```cmd
.\run_quiz.bat
```
- Launches the complete Java Swing desktop GUI.
- Experience the full quiz cycle: Welcome Screen $\rightarrow$ Question Screen with green/red answer states $\rightarrow$ Results Screen $\rightarrow$ Restart.

### 2. Automated JUnit 4 Test Suite
Execute the multi-app test runner:
```cmd
.\run_tests.bat
```
Runs 10 dedicated test cases in [`QuizEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/quiz/QuizEngineTest.java):
- Initial state verification
- Question and option shuffling integrity
- Correct answer submission and score increment
- Wrong answer handling without score change
- Prevention of duplicate submissions on the same question
- Quiz completion workflow and final result calculations
- Edge cases: perfect score (10/10), partial score (7/10), and zero score (0/10)

### 3. Interactive Web Simulator
Open [`web/quiz.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/quiz.html) in Google Chrome, Edge, or Firefox:
- Features an ultra-clean mobile mockup with dark glassmorphic styling.
- Experience instant color-coded answers, progress bars, and high score tracking directly in your browser.

### 4. Android Studio
1. Open the project in Android Studio.
2. Launch [`QuizWelcomeActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/quiz/QuizWelcomeActivity.java) or tap the **"🧠 Open Task 4 · Quiz App"** button in [`MainActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/unitconverter/MainActivity.java).
3. Run on an Android Emulator or connected physical phone.

---

## 📚 Question Bank Curated Topics

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

---

## 🎙️ Client & Viva Interview Defense Guide

### Key Talking Points:
1. **"How does the quiz ensure fair replayability?"**
   > *"Questions are sampled randomly from the question bank each round, and the 4 options inside each question are dynamically scrambled. Even if a user sees the same question twice, the answer position (A, B, C, D) is scrambled."*

2. **"Why did you choose immediate feedback instead of waiting until the end?"**
   > *"Immediate visual feedback (green for correct, red for incorrect with the correct answer revealed) transforms the app from a simple testing tool into an active learning utility. The explanation card reinforces why the answer was correct."*

3. **"How are high scores saved?"**
   > *"We use Android SharedPreferences to persist the player's highest score locally. It loads automatically on the Welcome screen and notifies the player on the Results screen when they set a new personal best."*
