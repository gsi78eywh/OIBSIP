package com.oibsip.todo;

import com.oibsip.todo.engine.TodoEngine;
import com.oibsip.todo.model.TaskItem;
import com.oibsip.todo.model.User;
import com.oibsip.todo.security.PasswordHasher;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TodoEngineTest {

    private TodoEngine engine;

    @Before
    public void setUp() {
        engine = new TodoEngine();
        engine.clearAll();
    }

    @Test
    public void testPasswordHasher_Sha256Integrity() {
        String plain = "SecretPassword123";
        String hash1 = PasswordHasher.hash(plain);
        String hash2 = PasswordHasher.hash(plain);

        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 hex is exactly 64 chars
        assertEquals(hash1, hash2); // Deterministic hashing
        assertNotEquals(plain, hash1); // Never stores plain text
        assertTrue(PasswordHasher.verify(plain, hash1));
        assertFalse(PasswordHasher.verify("WrongPassword", hash1));
    }

    @Test
    public void testRegisterUser_Success() {
        User user = engine.registerUser("Alice Smith", "alice@example.com", "password123");
        assertNotNull(user);
        assertTrue(user.getId() > 0);
        assertEquals("Alice Smith", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertNotEquals("password123", user.getPasswordHash());
    }

    @Test
    public void testRegisterUser_DuplicateEmail() {
        engine.registerUser("Alice", "alice@example.com", "password123");
        User duplicate = engine.registerUser("Alice Clone", "ALICE@example.com", "differentPassword");
        assertNull("Duplicate email should be rejected", duplicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_EmptyName() {
        engine.registerUser("", "user@example.com", "password123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_InvalidEmail() {
        engine.registerUser("User", "not-an-email", "password123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_ShortPassword() {
        engine.registerUser("User", "user@example.com", "12345"); // < 6 chars
    }

    @Test
    public void testAuthenticateUser_Success() {
        engine.registerUser("Bob", "bob@example.com", "correctPass1");
        User authenticated = engine.authenticateUser("bob@example.com", "correctPass1");
        assertNotNull(authenticated);
        assertEquals("Bob", authenticated.getName());
    }

    @Test
    public void testAuthenticateUser_WrongPassword() {
        engine.registerUser("Bob", "bob@example.com", "correctPass1");
        User failed = engine.authenticateUser("bob@example.com", "wrongPass");
        assertNull(failed);
    }

    @Test
    public void testAuthenticateUser_UnknownUser() {
        User unknown = engine.authenticateUser("nonexistent@example.com", "anyPass");
        assertNull(unknown);
    }

    @Test
    public void testTaskCreation_Success() {
        User user = engine.registerUser("Charlie", "charlie@example.com", "secure123");
        TaskItem task = engine.createTask(user.getId(), "Buy groceries", "Milk, eggs, bread");

        assertNotNull(task);
        assertTrue(task.getId() > 0);
        assertEquals(user.getId(), task.getUserId());
        assertEquals("Buy groceries", task.getTitle());
        assertEquals("Milk, eggs, bread", task.getNotes());
        assertFalse(task.isCompleted());
    }

    @Test
    public void testUserTaskIsolation() {
        User user1 = engine.registerUser("User 1", "u1@example.com", "pass123");
        User user2 = engine.registerUser("User 2", "u2@example.com", "pass123");

        engine.createTask(user1.getId(), "Task 1 for User 1", "Note 1");
        engine.createTask(user1.getId(), "Task 2 for User 1", "Note 2");
        engine.createTask(user2.getId(), "Task 1 for User 2", "Secret Note");

        List<TaskItem> u1Tasks = engine.getTasksForUser(user1.getId(), "ALL");
        List<TaskItem> u2Tasks = engine.getTasksForUser(user2.getId(), "ALL");

        assertEquals(2, u1Tasks.size());
        assertEquals(1, u2Tasks.size());
        assertEquals("Task 1 for User 2", u2Tasks.get(0).getTitle());
    }

    @Test
    public void testUpdateTaskStatus_AndFilters() {
        User user = engine.registerUser("Diana", "diana@example.com", "pass123");
        TaskItem task1 = engine.createTask(user.getId(), "Pending Task", "Note");
        TaskItem task2 = engine.createTask(user.getId(), "Done Task", "Note");

        engine.updateTaskStatus(task2.getId(), true);

        List<TaskItem> allTasks = engine.getTasksForUser(user.getId(), "ALL");
        List<TaskItem> pendingTasks = engine.getTasksForUser(user.getId(), "PENDING");
        List<TaskItem> completedTasks = engine.getTasksForUser(user.getId(), "COMPLETED");

        assertEquals(2, allTasks.size());
        assertEquals(1, pendingTasks.size());
        assertEquals("Pending Task", pendingTasks.get(0).getTitle());
        assertEquals(1, completedTasks.size());
        assertEquals("Done Task", completedTasks.get(0).getTitle());
    }

    @Test
    public void testDeleteTask() {
        User user = engine.registerUser("Evan", "evan@example.com", "pass123");
        TaskItem task = engine.createTask(user.getId(), "To Delete", "Delete me");

        assertEquals(1, engine.getTasksForUser(user.getId(), "ALL").size());
        boolean deleted = engine.deleteTask(task.getId());
        assertTrue(deleted);
        assertEquals(0, engine.getTasksForUser(user.getId(), "ALL").size());
    }

    @Test
    public void testGetTaskCounts() {
        User user = engine.registerUser("Fiona", "fiona@example.com", "pass123");
        TaskItem t1 = engine.createTask(user.getId(), "Task 1", "");
        TaskItem t2 = engine.createTask(user.getId(), "Task 2", "");
        TaskItem t3 = engine.createTask(user.getId(), "Task 3", "");

        engine.updateTaskStatus(t1.getId(), true);

        int[] counts = engine.getTaskCounts(user.getId());
        assertEquals(2, counts[0]); // 2 pending
        assertEquals(1, counts[1]); // 1 completed
    }
}
