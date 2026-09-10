package com.oibsip.todo.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskItem {
    private long id;
    private long userId;
    private String title;
    private String notes;
    private boolean isCompleted;
    private long createdAt;

    public TaskItem(long id, long userId, String title, String notes, boolean isCompleted, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.notes = notes != null ? notes : "";
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public TaskItem(long userId, String title, String notes) {
        this(-1, userId, title, notes, false, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault());
        return sdf.format(new Date(createdAt));
    }
}
