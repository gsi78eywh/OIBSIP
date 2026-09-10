package com.oibsip.todo.engine;

import com.oibsip.todo.model.TaskItem;
import com.oibsip.todo.model.User;
import com.oibsip.todo.security.PasswordHasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TodoEngine {

    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<Long, TaskItem> tasksById = new HashMap<>();

    private final AtomicLong userIdSequence = new AtomicLong(1);
    private final AtomicLong taskIdSequence = new AtomicLong(1);

    public boolean isEmailRegistered(String email) {
        if (email == null) return false;
        return usersByEmail.containsKey(email.trim().toLowerCase());
    }

    public User registerUser(String name, String email, String plainPassword) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (isEmailRegistered(normalizedEmail)) {
            return null; // Already exists
        }

        long id = userIdSequence.getAndIncrement();
        String passwordHash = PasswordHasher.hash(plainPassword);
        User user = new User(id, name.trim(), normalizedEmail, passwordHash, System.currentTimeMillis());

        usersById.put(id, user);
        usersByEmail.put(normalizedEmail, user);
        return user;
    }

    public User authenticateUser(String email, String plainPassword) {
        if (email == null || plainPassword == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();
        User user = usersByEmail.get(normalizedEmail);
        if (user == null) {
            return null;
        }

        if (PasswordHasher.verify(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User getUserById(long userId) {
        return usersById.get(userId);
    }

    public TaskItem createTask(long userId, String title, String notes) {
        if (!usersById.containsKey(userId)) {
            throw new IllegalArgumentException("User does not exist with ID: " + userId);
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        long taskId = taskIdSequence.getAndIncrement();
        TaskItem item = new TaskItem(taskId, userId, title.trim(), notes, false, System.currentTimeMillis());
        tasksById.put(taskId, item);
        return item;
    }

    public List<TaskItem> getTasksForUser(long userId, String filter) {
        List<TaskItem> list = new ArrayList<>();
        for (TaskItem task : tasksById.values()) {
            if (task.getUserId() == userId) {
                if ("PENDING".equalsIgnoreCase(filter) && task.isCompleted()) {
                    continue;
                }
                if ("COMPLETED".equalsIgnoreCase(filter) && !task.isCompleted()) {
                    continue;
                }
                list.add(task);
            }
        }

        // Sort: Pending first, then newest first
        Collections.sort(list, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem a, TaskItem b) {
                if (a.isCompleted() != b.isCompleted()) {
                    return a.isCompleted() ? 1 : -1;
                }
                return Long.compare(b.getCreatedAt(), a.getCreatedAt());
            }
        });

        return list;
    }

    public boolean updateTaskStatus(long taskId, boolean isCompleted) {
        TaskItem task = tasksById.get(taskId);
        if (task == null) {
            return false;
        }
        task.setCompleted(isCompleted);
        return true;
    }

    public boolean deleteTask(long taskId) {
        return tasksById.remove(taskId) != null;
    }

    public int[] getTaskCounts(long userId) {
        int pending = 0;
        int completed = 0;
        for (TaskItem task : tasksById.values()) {
            if (task.getUserId() == userId) {
                if (task.isCompleted()) {
                    completed++;
                } else {
                    pending++;
                }
            }
        }
        return new int[]{pending, completed};
    }

    public void clearAll() {
        usersById.clear();
        usersByEmail.clear();
        tasksById.clear();
        userIdSequence.set(1);
        taskIdSequence.set(1);
    }
}
