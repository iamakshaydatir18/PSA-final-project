package com.psa.taskmanager.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


public class Task implements Comparable<Task> {

  private final UUID id;
  private String name;
  private String description;
  private LocalDate dueDate;
  private TaskPriority priority;
  private TaskStatus status;

  public Task(
    String name,
    String description,
    LocalDate dueDate,
    TaskPriority priority,
    TaskStatus status
  ) {
    this(UUID.randomUUID(), name, description, dueDate, priority, status);
  }

  public Task(
    UUID id,
    String name,
    String description,
    LocalDate dueDate,
    TaskPriority priority,
    TaskStatus status
  ) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Task name must not be blank");
    }
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.name = name.trim();
    this.description = description == null ? "" : description.trim();
    this.dueDate = Objects.requireNonNull(dueDate, "dueDate must not be null");
    this.priority =
      Objects.requireNonNull(priority, "priority must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Task name must not be blank");
    }
    this.name = name.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? "" : description.trim();
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = Objects.requireNonNull(dueDate, "dueDate must not be null");
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public void setPriority(TaskPriority priority) {
    this.priority =
      Objects.requireNonNull(priority, "priority must not be null");
  }

  public TaskStatus getStatus() {
    return status;
  }

  public void setStatus(TaskStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  public boolean isCompleted() {
    return status == TaskStatus.COMPLETED;
  }

  @Override
  public int compareTo(Task other) {
    return this.name.compareToIgnoreCase(other.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Task task)) {
      return false;
    }
    return id.equals(task.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return name + " (due " + dueDate + ", " + priority + ")";
  }
}
