package com.oibsip.todo;

import com.oibsip.todo.engine.TodoEngine;
import com.oibsip.todo.model.TaskItem;
import com.oibsip.todo.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DesktopTodoApp extends JFrame {

    private final TodoEngine engine = new TodoEngine();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);

    private User currentUser = null;
    private String currentFilter = "ALL";

    // Views in To-Do List screen
    private JLabel lblGreeting;
    private JLabel lblTaskCounts;
    private JPanel taskListPanel;
    private JPanel emptyStatePanel;
    private JScrollPane scrollPane;

    public DesktopTodoApp() {
        super("OIBSIP · Task 2: To-Do App with Login");
        initSeedData();
        setupFrame();
        buildScreens();
        cardLayout.show(mainContainer, "LOGIN");
    }

    private void initSeedData() {
        User demo = engine.registerUser("Alex Demo", "demo@example.com", "password123");
        if (demo != null) {
            engine.createTask(demo.getId(), "Complete Oasis Infobyte Task 2", "Implement SQLite login, registration, and task CRUD");
            engine.createTask(demo.getId(), "Review SQLiteOpenHelper tutorial", "Check best practices for cursor handling");
            TaskItem done = engine.createTask(demo.getId(), "Set up Android Studio project", "Initial gradle build and layout files");
            engine.updateTaskStatus(done.getId(), true);
        }
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 720);
        setMinimumSize(new Dimension(420, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
    }

    private void buildScreens() {
        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createRegisterPanel(), "REGISTER");
        mainContainer.add(createTodoListPanel(), "TODOLIST");
        add(mainContainer);
    }

    // --- SCREEN 1: LOGIN ---

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(32, 32, 32, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Header
        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Sign in to access your personal to-do list", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(71, 85, 105));

        // Form Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(24, 20, 24, 20)
        ));

        JTextField tfEmail = new JTextField("demo@example.com");
        tfEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfEmail.setPreferredSize(new Dimension(300, 38));

        JPasswordField tfPassword = new JPasswordField("password123");
        tfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfPassword.setPreferredSize(new Dimension(300, 38));

        JButton btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(67, 56, 202));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(300, 44));

        btnLogin.addActionListener(e -> {
            String email = tfEmail.getText().trim();
            String pass = new String(tfPassword.getPassword());

            User user = engine.authenticateUser(email, pass);
            if (user != null) {
                currentUser = user;
                refreshTodoList();
                cardLayout.show(mainContainer, "TODOLIST");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.\n(Demo: demo@example.com / password123)", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(new Color(71, 85, 105));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(71, 85, 105));

        card.add(lblEmail);
        card.add(Box.createVerticalStrut(6));
        card.add(tfEmail);
        card.add(Box.createVerticalStrut(14));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(tfPassword);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);

        // Switch to Register
        JLabel lblGoRegister = new JLabel("Don't have an account? Sign Up", SwingConstants.CENTER);
        lblGoRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGoRegister.setForeground(new Color(67, 56, 202));
        lblGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGoRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainContainer, "REGISTER");
            }
        });

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 8, 0); panel.add(lblTitle, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0); panel.add(lblSubtitle, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0); panel.add(card, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0); panel.add(lblGoRegister, gbc);

        return panel;
    }

    // --- SCREEN 2: REGISTER ---

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(32, 32, 32, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Register to start managing your daily tasks", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(71, 85, 105));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(24, 20, 24, 20)
        ));

        JTextField tfName = new JTextField();
        tfName.setPreferredSize(new Dimension(300, 36));

        JTextField tfEmail = new JTextField();
        tfEmail.setPreferredSize(new Dimension(300, 36));

        JPasswordField tfPassword = new JPasswordField();
        tfPassword.setPreferredSize(new Dimension(300, 36));

        JButton btnRegister = new JButton("Create Account");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRegister.setBackground(new Color(67, 56, 202));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(300, 44));

        btnRegister.addActionListener(e -> {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            String pass = new String(tfPassword.getPassword());

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = engine.registerUser(name, email, pass);
                if (user != null) {
                    JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainContainer, "LOGIN");
                } else {
                    JOptionPane.showMessageDialog(this, "This email is already registered.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(new JLabel("Full Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(tfName);
        card.add(Box.createVerticalStrut(10));
        card.add(new JLabel("Email Address"));
        card.add(Box.createVerticalStrut(4));
        card.add(tfEmail);
        card.add(Box.createVerticalStrut(10));
        card.add(new JLabel("Password (min 6 chars)"));
        card.add(Box.createVerticalStrut(4));
        card.add(tfPassword);
        card.add(Box.createVerticalStrut(18));
        card.add(btnRegister);

        JLabel lblGoLogin = new JLabel("Already have an account? Sign In", SwingConstants.CENTER);
        lblGoLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGoLogin.setForeground(new Color(67, 56, 202));
        lblGoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGoLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainContainer, "LOGIN");
            }
        });

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 8, 0); panel.add(lblTitle, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0); panel.add(lblSubtitle, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 18, 0); panel.add(card, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0); panel.add(lblGoLogin, gbc);

        return panel;
    }

    // --- SCREEN 3: TO-DO LIST ---

    private JPanel createTodoListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel userBox = new JPanel();
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));
        userBox.setBackground(Color.WHITE);

        lblGreeting = new JLabel("Hello, User!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGreeting.setForeground(new Color(15, 23, 42));

        lblTaskCounts = new JLabel("0 Pending • 0 Completed");
        lblTaskCounts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTaskCounts.setForeground(new Color(100, 116, 139));

        userBox.add(lblGreeting);
        userBox.add(Box.createVerticalStrut(2));
        userBox.add(lblTaskCounts);

        JButton btnLogout = new JButton("Log Out");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(241, 245, 249));
        btnLogout.setForeground(new Color(239, 68, 68));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainContainer, "LOGIN");
        });

        header.add(userBox, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        // Filter Bar & Add Button
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setBackground(new Color(248, 250, 252));
        toolBar.setBorder(new EmptyBorder(12, 20, 8, 20));

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterGroup.setBackground(new Color(248, 250, 252));

        JButton btnAll = new JButton("All");
        JButton btnPending = new JButton("Pending");
        JButton btnCompleted = new JButton("Completed");

        btnAll.addActionListener(e -> { currentFilter = "ALL"; refreshTodoList(); });
        btnPending.addActionListener(e -> { currentFilter = "PENDING"; refreshTodoList(); });
        btnCompleted.addActionListener(e -> { currentFilter = "COMPLETED"; refreshTodoList(); });

        filterGroup.add(btnAll);
        filterGroup.add(btnPending);
        filterGroup.add(btnCompleted);

        JButton btnAdd = new JButton("+ Add Task");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(67, 56, 202));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showAddTaskDialog());

        toolBar.add(filterGroup, BorderLayout.WEST);
        toolBar.add(btnAdd, BorderLayout.EAST);

        // Center: Task List or Empty State
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(new Color(248, 250, 252));
        taskListPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(248, 250, 252));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // Empty state
        emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setBackground(new Color(248, 250, 252));
        JLabel lblEmpty = new JLabel("<html><center><b>No Tasks Found</b><br><span style='color:#64748B;'>You're all caught up! Click '+ Add Task' above.</span></center></html>", SwingConstants.CENTER);
        lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emptyStatePanel.add(lblEmpty);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(toolBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showAddTaskDialog() {
        JTextField tfTitle = new JTextField();
        JTextArea taNotes = new JTextArea(3, 20);

        Object[] message = {
                "Task Name *:", tfTitle,
                "Notes (Optional):", new JScrollPane(taNotes)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = tfTitle.getText().trim();
            String notes = taNotes.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task name cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            engine.createTask(currentUser.getId(), title, notes);
            refreshTodoList();
        }
    }

    private void refreshTodoList() {
        if (currentUser == null) return;

        lblGreeting.setText("Hello, " + currentUser.getName() + "!");
        int[] counts = engine.getTaskCounts(currentUser.getId());
        lblTaskCounts.setText(counts[0] + " Pending • " + counts[1] + " Completed");

        taskListPanel.removeAll();
        List<TaskItem> tasks = engine.getTasksForUser(currentUser.getId(), currentFilter);

        if (tasks.isEmpty()) {
            taskListPanel.add(emptyStatePanel);
        } else {
            for (TaskItem task : tasks) {
                taskListPanel.add(createTaskCard(task));
                taskListPanel.add(Box.createVerticalStrut(10));
            }
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private JPanel createTaskCard(TaskItem task) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        // Checkbox
        JCheckBox cb = new JCheckBox();
        cb.setSelected(task.isCompleted());
        cb.setBackground(Color.WHITE);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        String titleHtml = task.isCompleted()
                ? "<html><strike><span style='color:#94A3B8;'>" + task.getTitle() + "</span></strike></html>"
                : "<html><b>" + task.getTitle() + "</b></html>";

        JLabel lblTitle = new JLabel(titleHtml);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        info.add(lblTitle);

        if (task.getNotes() != null && !task.getNotes().isEmpty()) {
            JLabel lblNotes = new JLabel(task.getNotes());
            lblNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblNotes.setForeground(new Color(100, 116, 139));
            info.add(Box.createVerticalStrut(2));
            info.add(lblNotes);
        }

        JLabel lblDate = new JLabel(task.getFormattedDate());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDate.setForeground(new Color(148, 163, 184));
        info.add(Box.createVerticalStrut(4));
        info.add(lblDate);

        // Delete button
        JButton btnDel = new JButton("✕");
        btnDel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDel.setForeground(new Color(239, 68, 68));
        btnDel.setBackground(new Color(254, 242, 242));
        btnDel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btnDel.setFocusPainted(false);
        btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete task \"" + task.getTitle() + "\"?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                engine.deleteTask(task.getId());
                refreshTodoList();
            }
        });

        cb.addActionListener(e -> {
            engine.updateTaskStatus(task.getId(), cb.isSelected());
            refreshTodoList();
        });

        card.add(cb, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(btnDel, BorderLayout.EAST);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DesktopTodoApp().setVisible(true);
        });
    }
}
