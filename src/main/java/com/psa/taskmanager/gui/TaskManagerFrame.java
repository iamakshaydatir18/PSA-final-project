package com.psa.taskmanager.gui;

import com.psa.taskmanager.app.TaskManager;
import com.psa.taskmanager.model.Task;
import com.psa.taskmanager.model.TaskPriority;
import com.psa.taskmanager.model.TaskStatus;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Swing user interface for the Task Management System.
 */
public class TaskManagerFrame extends JFrame {

    private final TaskManager taskManager;

    private final TaskSectionPanel allTasksSection = new TaskSectionPanel("All Tasks", TaskView.ALL);
    private final TaskSectionPanel prioritySection = new TaskSectionPanel("Priority Queue", TaskView.PRIORITY);
    private final TaskSectionPanel todaySection = new TaskSectionPanel("Today's Tasks", TaskView.TODAY);
    private final TaskSectionPanel completedSection = new TaskSectionPanel("Completed", TaskView.COMPLETED);

    public TaskManagerFrame(TaskManager taskManager) {
        super("Task Management System");
        this.taskManager = taskManager;
        initializeUi();
    }

    private void initializeUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1180, 760));
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        refreshAllViews();

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Task Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> openSearchDialog());

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> openAddTaskDialog());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(searchButton);
        actions.add(addTaskButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JScrollPane createMainContent() {
        JPanel board = new JPanel(new GridBagLayout());
        board.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        board.setBackground(new Color(0xF5F7FA));

        addColumn(board, allTasksSection, 0);
        addColumn(board, prioritySection, 1);
        addColumn(board, todaySection, 2);
        addColumn(board, completedSection, 3);

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void addColumn(JPanel board, TaskSectionPanel column, int index) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = index;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, index == 0 ? 0 : 16, 0, 0);
        board.add(column, gbc);
    }

    private void refreshAllViews() {
        allTasksSection.populate(taskManager.listAllTasksSorted());
        prioritySection.populate(taskManager.getUrgentQueueSnapshot());
        todaySection.populate(taskManager.getDailyList());
        completedSection.populate(taskManager.getCompletedTasks());
    }

    private JPanel createTaskCard(Task task, TaskView view) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(320, 220));
        card.setMinimumSize(new Dimension(280, 200));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD6D9E0)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(truncate(task.getName(), 26));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        String statusText = task.getStatus() == TaskStatus.COMPLETED ? "Completed" : "Pending";
        JLabel statusBadge = createBadge(statusText,
                task.getStatus() == TaskStatus.COMPLETED ? new Color(0xD0F5E6) : new Color(0xFFF4D6),
                task.getStatus() == TaskStatus.COMPLETED ? new Color(0x0D6630) : new Color(0x8A6200));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);
        header.add(statusBadge, BorderLayout.EAST);

        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        metaPanel.setOpaque(false);
        metaPanel.add(createBadge("Priority: " + task.getPriority().name(),
                priorityColor(task.getPriority()), Color.WHITE));
        metaPanel.add(createBadge("Due: " + task.getDueDate(), new Color(0xE3E7FF), new Color(0x1A2B6B)));

        JTextArea description = new JTextArea(task.getDescription().isBlank()
                ? "No description provided." : task.getDescription());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(BorderFactory.createEmptyBorder());

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(metaPanel, BorderLayout.NORTH);
        body.add(description, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        switch (view) {
            case ALL -> configureAllTasksActions(task, actions);
            case PRIORITY -> configurePriorityActions(task, actions);
            case TODAY -> configureTodayActions(task, actions);
            case COMPLETED -> {
                // No actions for completed tasks.
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        if (actions.getComponentCount() > 0) {
            card.add(actions, BorderLayout.SOUTH);
        }

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actions.getComponentCount() > 0 && actions.getBounds().contains(e.getPoint())) {
                    return;
                }
                openTaskDetailsDialog(task);
            }
        });

        return card;
    }

    private void configureAllTasksActions(Task task, JPanel actions) {
        if (task.getStatus() != TaskStatus.COMPLETED) {
            JButton completeButton = new JButton("Mark Complete");
            completeButton.addActionListener(e -> {
                taskManager.markTaskCompleted(task);
                refreshAllViews();
            });
            actions.add(completeButton);

            if (!taskManager.isInUrgentQueue(task) && isHighPriority(task)) {
                JButton urgentButton = new JButton("Add to High Priority Queue");
                urgentButton.addActionListener(e -> {
                    boolean added = taskManager.markUrgent(task);
                    if (!added) {
                        showInfo("Only high or critical priority tasks enter the priority queue.");
                    }
                    refreshAllViews();
                });
                actions.add(urgentButton);
            }
        }
    }

    private void configurePriorityActions(Task task, JPanel actions) {
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(e -> {
            taskManager.markTaskCompleted(task);
            refreshAllViews();
        });
        actions.add(completeButton);
    }

    private void configureTodayActions(Task task, JPanel actions) {
        if (task.getStatus() != TaskStatus.COMPLETED) {
            JButton completeButton = new JButton("Mark Complete");
            completeButton.addActionListener(e -> {
                taskManager.markTaskCompleted(task);
                refreshAllViews();
            });
            actions.add(completeButton);
        }
    }

    private JLabel createBadge(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return label;
    }

    private Color priorityColor(TaskPriority priority) {
        return switch (priority) {
            case CRITICAL -> new Color(0xF44336);
            case HIGH -> new Color(0xFF7043);
            case MEDIUM -> new Color(0xFFB300);
            case LOW -> new Color(0x66BB6A);
        };
    }

    private boolean isHighPriority(Task task) {
        TaskPriority priority = task.getPriority();
        return priority == TaskPriority.HIGH || priority == TaskPriority.CRITICAL;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private JPanel createEmptyState(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(32, 0, 32, 0));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void openAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog(this);
        dialog.setVisible(true);
    }

    private void openSearchDialog() {
        SearchTaskDialog dialog = new SearchTaskDialog(this);
        dialog.setVisible(true);
    }

    private void handleTaskCreated(Task task) {
        taskManager.addTask(task);
        refreshAllViews();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void launch(TaskManager taskManager) {
        SwingUtilities.invokeLater(() -> new TaskManagerFrame(taskManager).setVisible(true));
    }

    private enum TaskView {
        ALL("No tasks to display."),
        PRIORITY("No high priority tasks right now."),
        TODAY("Nothing scheduled for today."),
        COMPLETED("No tasks have been completed yet.");

        private final String emptyMessage;

        TaskView(String emptyMessage) {
            this.emptyMessage = emptyMessage;
        }

        String emptyMessage() {
            return emptyMessage;
        }
    }

    private class TaskSectionPanel extends JPanel {

        private final TaskView view;
        private final JPanel contentPanel;

        TaskSectionPanel(String title, TaskView view) {
            super(new BorderLayout());
            this.view = view;
            setOpaque(true);
            setBackground(Color.WHITE);
            setMinimumSize(new Dimension(320, 0));
            setPreferredSize(new Dimension(0, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xD6D9E0)),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(titleLabel, BorderLayout.WEST);

            contentPanel = new JPanel();
            contentPanel.setOpaque(true);
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            JScrollPane columnScroll = new JScrollPane(contentPanel);
            columnScroll.setBorder(BorderFactory.createEmptyBorder());
            columnScroll.getVerticalScrollBar().setUnitIncrement(12);
            columnScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            add(header, BorderLayout.NORTH);
            add(columnScroll, BorderLayout.CENTER);
        }

        void populate(List<Task> tasks) {
            contentPanel.removeAll();
            if (tasks.isEmpty()) {
                contentPanel.add(createEmptyState(view.emptyMessage()));
            } else {
                for (int i = 0; i < tasks.size(); i++) {
                    contentPanel.add(createTaskCard(tasks.get(i), view));
                    if (i < tasks.size() - 1) {
                        contentPanel.add(Box.createVerticalStrut(12));
                    }
                }
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private class AddTaskDialog extends JDialog {

        private final JTextField nameField = new JTextField(24);
        private final JTextArea descriptionArea = new JTextArea(5, 24);
        private final JTextField dueDateField = new JTextField(12);
        private final JComboBox<TaskPriority> priorityCombo = new JComboBox<>(TaskPriority.values());

        AddTaskDialog(JFrame owner) {
            super(owner, "Add Task", true);
            buildUi();
        }

        private void buildUi() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            add(new JLabel("Task Name"), gbc);
            gbc.gridx = 1;
            add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Description"), gbc);
            gbc.gridx = 1;
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descriptionArea);
            descScroll.setPreferredSize(new Dimension(320, 140));
            add(descScroll, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Due Date (YYYY-MM-DD)"), gbc);
            gbc.gridx = 1;
            dueDateField.setText(LocalDate.now().toString());
            add(dueDateField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Priority"), gbc);
            gbc.gridx = 1;
            priorityCombo.setSelectedItem(TaskPriority.MEDIUM);
            add(priorityCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.EAST;
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            JButton saveButton = new JButton("Create Task");
            saveButton.addActionListener(e -> onSave());
            actionPanel.add(cancelButton);
            actionPanel.add(saveButton);
            add(actionPanel, gbc);

            pack();
            setLocationRelativeTo(TaskManagerFrame.this);
        }

        private void onSave() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Task name is required.");
                return;
            }
            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                showError("Invalid due date. Use format YYYY-MM-DD.");
                return;
            }

            Task newTask = new Task(
                    name,
                    descriptionArea.getText().trim(),
                    dueDate,
                    (TaskPriority) priorityCombo.getSelectedItem(),
                    TaskStatus.PENDING
            );

            handleTaskCreated(newTask);
            dispose();
        }
    }

    private class SearchTaskDialog extends JDialog {

        private final JTextField nameField = new JTextField(20);
        private final JTextField dueDateField = new JTextField(12);
        private final JComboBox<Object> statusCombo = new JComboBox<>();
        private final DefaultListModel<Task> resultsModel = new DefaultListModel<>();
        private final JList<Task> resultsList = new JList<>(resultsModel);

        SearchTaskDialog(JFrame owner) {
            super(owner, "Search Tasks", true);
            buildUi();
        }

        private void buildUi() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            add(new JLabel("Name contains"), gbc);
            gbc.gridx = 1;
            add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Due Date (YYYY-MM-DD)"), gbc);
            gbc.gridx = 1;
            add(dueDateField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Status"), gbc);
            gbc.gridx = 1;
            statusCombo.addItem("Any");
            for (TaskStatus status : TaskStatus.values()) {
                if (status != TaskStatus.IN_PROGRESS) {
                    statusCombo.addItem(status);
                }
            }
            add(statusCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(e -> onSearch());
            buttons.add(closeButton);
            buttons.add(searchButton);
            add(buttons, gbc);

            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            resultsList.setCellRenderer(new TaskListCellRenderer());
            JScrollPane resultsScroll = new JScrollPane(resultsList);
            resultsScroll.setPreferredSize(new Dimension(420, 200));
            add(resultsScroll, gbc);

            pack();
            setLocationRelativeTo(TaskManagerFrame.this);
        }

        private void onSearch() {
            LocalDate dueDate = null;
            String dueText = dueDateField.getText().trim();
            if (!dueText.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueText);
                } catch (DateTimeParseException ex) {
                    showError("Invalid due date. Use format YYYY-MM-DD.");
                    return;
                }
            }

            Object statusSelection = statusCombo.getSelectedItem();
            TaskStatus status = statusSelection instanceof TaskStatus selectedStatus ? selectedStatus : null;

            List<Task> results = taskManager.searchTasks(nameField.getText(), dueDate, status);
            resultsModel.clear();
            for (Task task : results) {
                resultsModel.addElement(task);
            }
            if (results.isEmpty()) {
                showInfo("No tasks matched your search criteria.");
            }
        }
    }

    private void openTaskDetailsDialog(Task task) {
        new TaskDetailsDialog(this, task).setVisible(true);
    }

    private class TaskDetailsDialog extends JDialog {

        private final Task task;
        private final JTextField nameField;
        private final JTextArea descriptionArea;
        private final JTextField dueDateField;
        private final JComboBox<TaskPriority> priorityCombo;
        private final JComboBox<TaskStatus> statusCombo;

        TaskDetailsDialog(JFrame owner, Task task) {
            super(owner, "Edit Task", true);
            this.task = task;
            this.nameField = new JTextField(task.getName(), 24);
            this.descriptionArea = new JTextArea(task.getDescription().isBlank()
                    ? "" : task.getDescription(), 5, 24);
            this.dueDateField = new JTextField(task.getDueDate().toString(), 12);
            this.priorityCombo = new JComboBox<>(TaskPriority.values());
            this.priorityCombo.setSelectedItem(task.getPriority());
            this.statusCombo = new JComboBox<>(new TaskStatus[]{TaskStatus.PENDING, TaskStatus.COMPLETED});
            this.statusCombo.setSelectedItem(task.getStatus());
            buildUi();
        }

        private void buildUi() {
            setLayout(new BorderLayout(12, 12));
            JPanel content = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 12, 6, 12);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            content.add(new JLabel("Task Name"), gbc);
            gbc.gridx = 1;
            content.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            content.add(new JLabel("Priority"), gbc);
            gbc.gridx = 1;
            content.add(priorityCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            content.add(new JLabel("Status"), gbc);
            gbc.gridx = 1;
            content.add(statusCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            content.add(new JLabel("Due Date (YYYY-MM-DD)"), gbc);
            gbc.gridx = 1;
            content.add(dueDateField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            content.add(new JLabel("Description"), gbc);
            gbc.gridy++;
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descriptionArea);
            descScroll.setPreferredSize(new Dimension(360, 140));
            content.add(descScroll, gbc);

            add(content, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e -> dispose());
            JButton save = new JButton("Save");
            save.addActionListener(e -> onSave());
            buttons.add(cancel);
            buttons.add(save);
            add(buttons, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(TaskManagerFrame.this);
        }

        private void onSave() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Task name is required.");
                return;
            }
            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                showError("Invalid due date. Use format YYYY-MM-DD.");
                return;
            }

            taskManager.updateTask(
                    task,
                    name,
                    descriptionArea.getText().trim(),
                    dueDate,
                    (TaskPriority) priorityCombo.getSelectedItem(),
                    (TaskStatus) statusCombo.getSelectedItem()
            );

            refreshAllViews();
            dispose();
        }
    }
}

