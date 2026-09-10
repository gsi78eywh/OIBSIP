# OIBSIP · Task 2: To-Do App with Login & SQLite Database Guide

> Comprehensive technical documentation, architecture manual, and verification guide for **Task 2: To-Do App with Login** in the **Oasis Infobyte Internship Program (OIBSIP)**.

---

## 📊 Task Tracking & Compliance Matrix

| Requirement / Deliverable | Implementation Details | Status |
| :--- | :--- | :---: |
| **Login Screen** | [`LoginActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/LoginActivity.java) with email & password inputs, session check, validation toasts, and Sign In action. | ✅ Complete |
| **Sign-Up Screen** | [`RegisterActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/RegisterActivity.java) with name, email, password, confirm password, validation, duplicate check, and Register action. | ✅ Complete |
| **Password Hashing (No Plain Text)** | [`PasswordHasher.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/security/PasswordHasher.java) implements standard Java 64-character SHA-256 cryptographic hashing before SQLite storage. | ✅ Complete |
| **Session Management & Logout** | [`SessionManager.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/session/SessionManager.java) persists active session via `SharedPreferences`. Logout clears session and redirects to login. | ✅ Complete |
| **Task List Screen** | [`TodoListActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/TodoListActivity.java) with user greeting, pending/completed summary, and scrollable `RecyclerView`. | ✅ Complete |
| **"Add Task" Dialog** | Custom Material dialog (`dialog_add_task.xml`) to input task name (required) and optional notes. | ✅ Complete |
| **Mark as Complete (Strikethrough)** | Checkbox in [`TaskAdapter.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/adapter/TaskAdapter.java) toggles completion, updates SQLite, and applies `Paint.STRIKE_THRU_TEXT_FLAG`. | ✅ Complete |
| **Permanent Task Deletion** | Trash button with confirmation dialog triggers `db.delete(TABLE_TASKS, ...)` in SQLite. | ✅ Complete |
| **User-Specific Task Isolation** | Foreign key `user_id` links tasks to users. Queries strictly filter `WHERE user_id = ?` so users only access their own tasks. | ✅ Complete |
| **Empty State Display** | Friendly empty-state illustration and prompt ("No Tasks Found — Tap + to create your first task") appears when no tasks exist. | ✅ Complete |
| **Decoupled Pure Java Engine** | Business logic, validation, and in-memory repository in [`TodoEngine.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/engine/TodoEngine.java). | ✅ Complete |
| **Desktop Windows App** | Standalone Java Swing GUI runnable via [`run_todo.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_todo.bat) without an emulator. | ✅ Complete |
| **Interactive Web Simulator** | Complete browser simulator with simulated SHA-256 and local storage at [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html). | ✅ Complete |
| **Automated JUnit 4 Tests** | 14 comprehensive unit tests in [`TodoEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/todo/TodoEngineTest.java) (100% pass rate). | ✅ Complete |

---

## 🏗️ Architecture & Component Directory

```
OIBSIP/
├── app/src/main/
│   ├── AndroidManifest.xml                         # Registered Login, Register, and TodoList activities
│   ├── java/com/oibsip/todo/
│   │   ├── LoginActivity.java                      # Login screen & session verification
│   │   ├── RegisterActivity.java                   # Account creation & password hashing
│   │   ├── TodoListActivity.java                   # User dashboard, filters, dialog & task list
│   │   ├── adapter/
│   │   │   └── TaskAdapter.java                    # RecyclerView adapter with strikethrough & delete
│   │   ├── db/
│   │   │   └── TodoDbHelper.java                   # SQLiteOpenHelper (users & tasks tables)
│   │   ├── engine/
│   │   │   └── TodoEngine.java                     # Decoupled core engine for testing & desktop
│   │   ├── model/
│   │   │   ├── TaskItem.java                       # Task entity (id, userId, title, notes, isCompleted)
│   │   │   └── User.java                           # User entity (id, name, email, passwordHash)
│   │   ├── security/
│   │   │   └── PasswordHasher.java                 # Standard Java SHA-256 hashing utility
│   │   └── session/
│   │       └── SessionManager.java                 # SharedPreferences session wrapper
│   └── res/
│       ├── drawable/                               # ic_todo, ic_add, ic_delete, ic_logout, ic_person, ic_lock, ic_email
│       └── layout/
│           ├── activity_login.xml                  # Login screen layout
│           ├── activity_register.xml               # Register screen layout
│           ├── activity_todo_list.xml              # Task list layout with empty state & FAB
│           ├── item_task.xml                       # Task card row with checkbox & delete button
│           └── dialog_add_task.xml                 # Add task modal dialog layout
├── app/src/test/java/com/oibsip/todo/
│   ├── TodoEngineTest.java                         # 14 JUnit 4 unit tests (100% pass)
│   └── DesktopTodoApp.java                         # Standalone Windows Desktop Swing GUI
├── run_todo.bat                                    # 1-Click Desktop To-Do Launcher
└── web/
    └── todo.html                                   # Interactive browser simulator
```

---

## 💾 SQLite Database Schema & Relations

In [`TodoDbHelper.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/db/TodoDbHelper.java), local persistence is structured as a relational SQLite database (`oibsip_todo.db`):

```sql
-- 1. USERS TABLE
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at INTEGER NOT NULL
);

-- 2. TASKS TABLE (User-Specific)
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    notes TEXT,
    is_completed INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index for fast user query lookups
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
```

### 🔐 Relational Integrity:
- **Foreign Key Constraint:** Every task is bound to `tasks.user_id = users.id`.
- **User Scoping:** When fetching tasks, the query executes `SELECT * FROM tasks WHERE user_id = ? ORDER BY is_completed ASC, created_at DESC`. User A never sees User B's tasks.
- **Cascade Deletion:** If a user account is deleted, all associated tasks are cleaned up automatically.

---

## 🔒 Security & Password Hashing (SHA-256)

In [`PasswordHasher.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/security/PasswordHasher.java):
- Passwords are never stored in plain text.
- Before insertion into SQLite, the password string is converted to a 256-bit hash using standard `java.security.MessageDigest.getInstance("SHA-256")`:
  $$\text{Hash} = \text{SHA-256}(\text{plainTextPassword})$$
- Output is formatted as a 64-character hexadecimal string.
- When logging in, the entered password is hashed with the exact same algorithm and compared against the stored hash.

---

## 🚀 4 Ways to Run & Verify

### 1. Windows Desktop Application (No Emulator Needed)
Double-click [`run_todo.bat`](file:///c:/Users/SethAndreyJabagat/OIBSIP/run_todo.bat) or execute in PowerShell:
```cmd
.\run_todo.bat
```
- A pre-seeded demo account is available (`demo@example.com` / `password123`) or you can click **"Sign Up"** to register a new account.
- Experience the complete user journey: Register $\rightarrow$ Login $\rightarrow$ Add Task $\rightarrow$ Toggle Completed $\rightarrow$ Delete Task $\rightarrow$ Filter $\rightarrow$ Logout.

### 2. Automated JUnit 4 Test Suite
Execute the repository test runner:
```cmd
.\run_tests.bat
```
Runs 14 dedicated test cases in [`TodoEngineTest.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/test/java/com/oibsip/todo/TodoEngineTest.java):
- SHA-256 deterministic 64-character hash integrity.
- Successful user registration.
- Duplicate email rejection.
- Input validation (empty name, malformed email, short passwords < 6 chars).
- Authentication with correct vs incorrect passwords.
- User-specific task isolation (User 1's tasks invisible to User 2).
- Task creation, completion toggling, deletion, and filters (Pending vs Completed).

### 3. Interactive Web Simulator
Open [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html) in any modern browser:
- Features an interactive mobile frame with realistic status bar and dark glassmorphic styling.
- Simulates client-side SHA-256 hashing and user-specific task management using browser local storage.

### 4. Android Studio
1. Open the repository in Android Studio.
2. Launch [`LoginActivity.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/LoginActivity.java) or tap **"📝 TASK 2 · To-Do App with Login"** on the main navigation screen.
3. Test account registration, login, task additions, and logout on an Android emulator or connected device.

---

## 🎙️ Client & Viva Interview Defense Guide

### Key Questions & Model Answers:

1. **"Why did you use SHA-256 hashing instead of storing plain text passwords?"**
   > *"Storing plain text passwords is a critical security vulnerability. If a database file is ever inspected or leaked, plain text passwords expose user accounts. SHA-256 is a one-way cryptographic hash function built into Java standard libraries, meaning the original password cannot be reverse-engineered from the hash."*

2. **"How does the database guarantee that tasks remain user-specific?"**
   > *"The SQLite schema implements a relational foreign key `user_id` on the `tasks` table. All SELECT, UPDATE, and DELETE queries are parameterized with the current session's `user_id`. This guarantees absolute data isolation between different accounts."*

3. **"How does session management work upon app restart?"**
   > *"We use `SessionManager` backed by Android `SharedPreferences`. When a user successfully logs in, their user ID and credentials are saved. On app launch, `LoginActivity` checks `sessionManager.isLoggedIn()`. If true, it immediately routes the user to `TodoListActivity` without requiring them to log in again."*

---

## ☁️ Supabase Cloud Backend Integration

The To-Do App supports hybrid cloud persistence via **Supabase** (PostgreSQL + PostgREST + GoTrue Auth), alongside offline SQLite persistence.

### 🔑 Project Configuration
- **Supabase URL:** `https://leneusmnmwxmuyjxfqjx.supabase.co`
- **PostgREST REST API Base:** `https://leneusmnmwxmuyjxfqjx.supabase.co/rest/v1/`
- **Publishable Key:** `sb_publishable_FBGOzmhk8Lk2xLbPQCXEkA_g0vFAs6j`
- **Java Client:** [`SupabaseClient.java`](file:///c:/Users/SethAndreyJabagat/OIBSIP/app/src/main/java/com/oibsip/todo/supabase/SupabaseClient.java) (zero external dependencies, using Java `HttpURLConnection`)
- **Web Simulator Client:** [`web/todo.html`](file:///c:/Users/SethAndreyJabagat/OIBSIP/web/todo.html) (using `@supabase/supabase-js` v2)

### 🗄️ PostgreSQL Database Schema for Supabase
```sql
-- Run in Supabase Dashboard -> SQL Editor:
CREATE TABLE IF NOT EXISTS public.tasks (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    notes TEXT DEFAULT '',
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- Enable Row Level Security (RLS)
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;

-- Allow authenticated users to manage their own tasks
CREATE POLICY "Users can manage own tasks" 
ON public.tasks 
FOR ALL 
TO authenticated 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- (Optional) Allow public demo / anon access
CREATE POLICY "Allow public demo tasks" 
ON public.tasks 
FOR ALL 
TO anon 
USING (true) 
WITH CHECK (true);
```

### ⚙️ Supabase Dashboard Settings
1. **Instant Sign-In without Email Delay:**
   - In Supabase Dashboard $\rightarrow$ **Authentication** $\rightarrow$ **Providers** $\rightarrow$ **Email**.
   - Set **"Confirm email"** to **OFF** so newly registered users can log in immediately.

