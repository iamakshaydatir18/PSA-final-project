package com.psa.taskmanager.model;

/**
 * Task priority level.
 */
public enum TaskPriority {
    LOW(3), // least priority
    MEDIUM(2),
    HIGH(1),
    CRITICAL(0); // most priority

    private final int urgencyLevel;

    TaskPriority(int urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }
}

