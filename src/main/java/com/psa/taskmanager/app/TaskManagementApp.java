package com.psa.taskmanager.app;

import com.psa.taskmanager.gui.TaskManagerFrame;
import com.psa.taskmanager.model.Task;
import com.psa.taskmanager.model.TaskPriority;
import com.psa.taskmanager.model.TaskStatus;

import java.time.LocalDate;

/**
 * Entry point for launching the Task Management System GUI.
 */
public final class TaskManagementApp {

    private TaskManagementApp() {
    }

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        seedSampleData(taskManager);
        TaskManagerFrame.launch(taskManager);
    }

    private static void seedSampleData(TaskManager taskManager) {
        Task bugFix = new Task(
                "Fix critical bug #456",
                "Resolve the production outage affecting payment processing.",
                LocalDate.now(),
                TaskPriority.CRITICAL,
                TaskStatus.PENDING
        );

        Task projectReport = new Task(
                "Submit project report",
                "Compile sprint summary and submit to leadership.",
                LocalDate.now().plusDays(1),
                TaskPriority.HIGH,
                TaskStatus.PENDING
        );

        Task clientPrep = new Task(
                "Client meeting prep",
                "Gather metrics and talking points for tomorrow's client call.",
                LocalDate.now(),
                TaskPriority.HIGH,
                TaskStatus.PENDING
        );

        Task databaseOpt = new Task(
                "Database optimization",
                "Review slow queries and add missing indexes.",
                LocalDate.now().plusDays(2),
                TaskPriority.MEDIUM,
                TaskStatus.PENDING
        );

        Task teamStandup = new Task(
                "Team standup",
                "Daily sync with the engineering team.",
                LocalDate.now(),
                TaskPriority.LOW,
                TaskStatus.PENDING
        );

        Task codeReview = new Task(
                "Code review",
                "Review open pull requests for the analytics service.",
                LocalDate.now(),
                TaskPriority.MEDIUM,
                TaskStatus.PENDING
        );

        taskManager.addTask(bugFix);
        taskManager.addTask(projectReport);
        taskManager.addTask(clientPrep);
        taskManager.addTask(databaseOpt);
        taskManager.addTask(teamStandup);
        taskManager.addTask(codeReview);

        taskManager.markUrgent(bugFix);
        taskManager.markUrgent(projectReport);
        taskManager.markUrgent(clientPrep);

        taskManager.markTaskCompleted(codeReview);
    }
}

