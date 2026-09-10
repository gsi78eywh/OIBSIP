package com.oibsip.todo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oibsip.todo.model.TaskItem;
import com.oibsip.todo.model.User;

import java.util.ArrayList;
import java.util.List;

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "oibsip_todo.db";
    private static final int DATABASE_VERSION = 1;

    // Table: users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Table: tasks
    public static final String TABLE_TASKS = "tasks";
    public static final String COL_TASK_ID = "id";
    public static final String COL_TASK_USER_ID = "user_id";
    public static final String COL_TASK_TITLE = "title";
    public static final String COL_TASK_NOTES = "notes";
    public static final String COL_TASK_IS_COMPLETED = "is_completed";
    public static final String COL_TASK_CREATED_AT = "created_at";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_USER_PASSWORD_HASH + " TEXT NOT NULL, "
                + COL_USER_CREATED_AT + " INTEGER NOT NULL)";

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " ("
                + COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TASK_USER_ID + " INTEGER NOT NULL, "
                + COL_TASK_TITLE + " TEXT NOT NULL, "
                + COL_TASK_NOTES + " TEXT, "
                + COL_TASK_IS_COMPLETED + " INTEGER NOT NULL DEFAULT 0, "
                + COL_TASK_CREATED_AT + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COL_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)";

        String createTaskUserIndex = "CREATE INDEX idx_tasks_user_id ON " + TABLE_TASKS + "(" + COL_TASK_USER_ID + ")";

        db.execSQL(createUsersTable);
        db.execSQL(createTasksTable);
        db.execSQL(createTaskUserIndex);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- User Authentication Methods ---

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?", new String[]{email.trim().toLowerCase()},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public long registerUser(String name, String email, String passwordHash) {
        if (isEmailRegistered(email)) {
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name.trim());
        values.put(COL_USER_EMAIL, email.trim().toLowerCase());
        values.put(COL_USER_PASSWORD_HASH, passwordHash);
        values.put(COL_USER_CREATED_AT, System.currentTimeMillis());

        return db.insert(TABLE_USERS, null, values);
    }

    public User authenticateUser(String email, String passwordHash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD_HASH + " = ?",
                new String[]{email.trim().toLowerCase(), passwordHash},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT));
            user = new User(id, name, userEmail, passwordHash, createdAt);
        }
        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_ID + " = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL));
            String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD_HASH));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT));
            user = new User(userId, name, email, passwordHash, createdAt);
        }
        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    // --- Task CRUD Methods ---

    public long insertTask(long userId, String title, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USER_ID, userId);
        values.put(COL_TASK_TITLE, title.trim());
        values.put(COL_TASK_NOTES, notes != null ? notes.trim() : "");
        values.put(COL_TASK_IS_COMPLETED, 0);
        values.put(COL_TASK_CREATED_AT, System.currentTimeMillis());

        return db.insert(TABLE_TASKS, null, values);
    }

    public List<TaskItem> getTasksForUser(long userId, String filter) {
        List<TaskItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COL_TASK_USER_ID + " = ?";
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(userId));

        if ("PENDING".equalsIgnoreCase(filter)) {
            selection += " AND " + COL_TASK_IS_COMPLETED + " = 0";
        } else if ("COMPLETED".equalsIgnoreCase(filter)) {
            selection += " AND " + COL_TASK_IS_COMPLETED + " = 1";
        }

        Cursor cursor = db.query(TABLE_TASKS, null,
                selection, selectionArgs.toArray(new String[0]),
                null, null, COL_TASK_IS_COMPLETED + " ASC, " + COL_TASK_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_NOTES));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_IS_COMPLETED)) == 1;
                long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_CREATED_AT));

                list.add(new TaskItem(id, userId, title, notes, completed, createdAt));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public boolean updateTaskStatus(long taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_IS_COMPLETED, isCompleted ? 1 : 0);

        int rows = db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    public int[] getTaskCounts(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int pending = 0;
        int completed = 0;

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_TASK_IS_COMPLETED + ", COUNT(*) FROM " + TABLE_TASKS
                        + " WHERE " + COL_TASK_USER_ID + " = ? GROUP BY " + COL_TASK_IS_COMPLETED,
                new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int status = cursor.getInt(0);
                int count = cursor.getInt(1);
                if (status == 1) {
                    completed = count;
                } else {
                    pending = count;
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return new int[]{pending, completed};
    }
}
