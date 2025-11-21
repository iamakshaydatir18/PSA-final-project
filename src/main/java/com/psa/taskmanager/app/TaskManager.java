package com.psa.taskmanager.app;

import com.psa.taskmanager.datastructures.ArrayBag;
import com.psa.taskmanager.datastructures.Bag;
import com.psa.taskmanager.datastructures.BinaryHeapPriorityQueue;
import com.psa.taskmanager.datastructures.BinarySearchTree;
import com.psa.taskmanager.datastructures.LinkedBinarySearchTree;
import com.psa.taskmanager.datastructures.LinkedSimpleList;
import com.psa.taskmanager.datastructures.PriorityQueue;
import com.psa.taskmanager.datastructures.SimpleList;
import com.psa.taskmanager.model.Task;
import com.psa.taskmanager.model.TaskPriority;
import com.psa.taskmanager.model.TaskStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Core service orchestrating custom data structures.
 */
public class TaskManager {

    private final Bag<Task> taskBag;
    private final BinarySearchTree<Task> taskTree;
    private final PriorityQueue<Task> urgentQueue;
    private final SimpleList<Task> dailyList;

    public TaskManager() {
        this.taskBag = new ArrayBag<>();
        this.taskTree = new LinkedBinarySearchTree<>();
        this.urgentQueue = new BinaryHeapPriorityQueue<>(TaskManager::compareUrgency);
        this.dailyList = new LinkedSimpleList<>();
    }

    /**
     * Adds a new task to the manager, defaulting to {@link TaskStatus#PENDING}
     * and automatically enrolling high-priority tasks in the urgent queue.
     *
     * @param task task to add
     */
    public void addTask(Task task) {
        Objects.requireNonNull(task, "task must not be null");
        task.setStatus(TaskStatus.PENDING);
        taskBag.add(task);
        taskTree.insert(task);
        addToPriorityQueue(task);
        refreshDailyList();
    }

    /**
     * Schedules a task in the urgent queue if it qualifies as high priority.
     *
     * @param task task to prioritize
     */
    public boolean markUrgent(Task task) {
        return addToPriorityQueue(task);
    }

    /**
     * Removes the highest-priority urgent task.
     *
     * @return completed task or empty if none
     */
    public Optional<Task> completeNextUrgent() {
        Task next = urgentQueue.extract();
        if (next != null) {
            next.setStatus(TaskStatus.COMPLETED);
            refreshDailyList();
        }
        return Optional.ofNullable(next);
    }

    /**
     * Marks a task as completed, removing it from urgent and daily structures.
     *
     * @param task task to complete
     */
    public void markTaskCompleted(Task task) {
        if (task == null) {
            return;
        }
        task.setStatus(TaskStatus.COMPLETED);
        removeFromPriorityQueue(task);
        refreshDailyList();
    }

    /**
     * Updates a task's properties and refreshes relevant data structures.
     * If the task name changes, it will be reinserted into the BST.
     * Priority and status changes will update the priority queue and daily list accordingly.
     *
     * @param task the task to update
     * @param newName new task name
     * @param newDescription new description
     * @param newDueDate new due date
     * @param newPriority new priority
     * @param newStatus new status
     */
    public void updateTask(Task task, String newName, String newDescription, LocalDate newDueDate,
                           TaskPriority newPriority, TaskStatus newStatus) {
        if (task == null) {
            return;
        }
        String oldName = task.getName();
        TaskPriority oldPriority = task.getPriority();
        TaskStatus oldStatus = task.getStatus();

        boolean nameChanged = !oldName.equals(newName);
        boolean priorityChanged = oldPriority != newPriority;
        boolean statusChanged = oldStatus != newStatus;

        task.setName(newName);
        task.setDescription(newDescription);
        task.setDueDate(newDueDate);
        task.setPriority(newPriority);
        task.setStatus(newStatus);

        if (nameChanged) {
            taskTree.remove(task);
            taskTree.insert(task);
        }

        if (priorityChanged || statusChanged) {
            removeFromPriorityQueue(task);
            if (isHighPriority(task) && newStatus != TaskStatus.COMPLETED) {
                addToPriorityQueue(task);
            }
        }

        refreshDailyList();
    }

    /**
     * Finds a task by identifier.
     *
     * @param id task unique identifier
     * @return matching task if found
     */
    public Optional<Task> findById(UUID id) {
        for (Task task : taskBag) {
            if (task.getId().equals(id)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a task by name using the binary search tree.
     *
     * @param name task name
     * @return matching task if present
     */
    public Optional<Task> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        Task probe = new Task(UUID.randomUUID(), name, "", LocalDate.now(), TaskPriority.LOW,
                TaskStatus.PENDING);
        Task result = taskTree.find(probe);
        return Optional.ofNullable(result);
    }

    /**
     * @return snapshot list of all tasks sorted by name
     */
    public List<Task> listAllTasksSorted() {
        return taskTree.toInOrderList();
    }

    /**
     * @return snapshot list of tasks for the daily list
     */
    public List<Task> getDailyList() {
        refreshDailyList();
        List<Task> result = new ArrayList<>(dailyList.size());
        for (Task task : dailyList) {
            result.add(task);
        }
        return result;
    }

    /**
     * @return snapshot list of urgent tasks in queue order
     */
    public List<Task> getUrgentQueueSnapshot() {
        return urgentQueue.toList();
    }

    /**
     * @return tasks currently marked as completed
     */
    public List<Task> getCompletedTasks() {
        List<Task> results = new ArrayList<>();
        for (Task task : taskBag) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                results.add(task);
            }
        }
        return results;
    }

    /**
     * @return tasks due today
     */
    public List<Task> tasksDueToday() {
        LocalDate today = LocalDate.now();
        List<Task> results = new ArrayList<>();
        for (Task task : taskBag) {
            if (task.getDueDate().equals(today) && task.getStatus() != TaskStatus.COMPLETED) {
                results.add(task);
            }
        }
        return results;
    }

    /**
     * Performs a filtered search across stored tasks using the binary search tree
     * ordering for deterministic results.
     *
     * @param nameQuery substring to match against task names (case-insensitive), may be null
     * @param dueDate   exact due date to match, may be null
     * @param status    task status to match, may be null
     * @return list of tasks satisfying the supplied filters, sorted by name
     */
    public List<Task> searchTasks(String nameQuery, LocalDate dueDate, TaskStatus status) {
        String loweredQuery = nameQuery == null ? "" : nameQuery.trim().toLowerCase();
        boolean hasNameFilter = !loweredQuery.isEmpty();

        List<Task> results = new ArrayList<>();
        for (Task task : taskTree.toInOrderList()) {
            if (hasNameFilter && !task.getName().toLowerCase().contains(loweredQuery)) {
                continue;
            }
            if (dueDate != null && !task.getDueDate().equals(dueDate)) {
                continue;
            }
            if (status != null && task.getStatus() != status) {
                continue;
            }
            results.add(task);
        }
        return results;
    }

    public Bag<Task> getTaskBag() {
        return taskBag;
    }

    public BinarySearchTree<Task> getTaskTree() {
        return taskTree;
    }

    public PriorityQueue<Task> getUrgentQueue() {
        return urgentQueue;
    }

    public SimpleList<Task> getDailyListStructure() {
        return dailyList;
    }

    /**
     * @param task task to test
     * @return true if the urgent queue already contains the task
     */
    public boolean isInUrgentQueue(Task task) {
        for (Task queued : urgentQueue.toList()) {
            if (queued.equals(task)) {
                return true;
            }
        }
        return false;
    }

    private static int compareUrgency(Task first, Task second) {
        int priorityComparison = Integer.compare(first.getPriority().getUrgencyLevel(),
                second.getPriority().getUrgencyLevel());
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return first.getDueDate().compareTo(second.getDueDate());
    }

    private boolean addToPriorityQueue(Task task) {
        if (task == null) {
            return false;
        }
        if (isHighPriority(task) && !isInUrgentQueue(task)) {
            urgentQueue.insert(task);
            return true;
        }
        return false;
    }

    private void removeFromPriorityQueue(Task task) {
        if (task == null || urgentQueue.isEmpty()) {
            return;
        }
        List<Task> snapshot = urgentQueue.toList();
        urgentQueue.clear();
        for (Task candidate : snapshot) {
            if (!candidate.equals(task)) {
                urgentQueue.insert(candidate);
            }
        }
    }

    private void refreshDailyList() {
        dailyList.clear();
        LocalDate today = LocalDate.now();
        for (Task task : taskBag) {
            if (task.getDueDate().equals(today) && task.getStatus() != TaskStatus.COMPLETED) {
                dailyList.add(task);
            }
        }
    }

    private boolean isHighPriority(Task task) {
        return task.getPriority() == TaskPriority.HIGH || task.getPriority() == TaskPriority.CRITICAL;
    }
}

