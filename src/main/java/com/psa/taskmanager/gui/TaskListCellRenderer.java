package com.psa.taskmanager.gui;

import com.psa.taskmanager.model.Task;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import java.time.format.DateTimeFormatter;

/**
 * Custom list cell renderer providing readable formatting.
 */
public class TaskListCellRenderer extends DefaultListCellRenderer {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Task task) {
            String formatted = "%s | Due: %s | Priority: %s | Status: %s".formatted(
                    task.getName(),
                    task.getDueDate().format(FORMATTER),
                    task.getPriority(),
                    task.getStatus()
            );
            setText(formatted);
        }

        return component;
    }
}

