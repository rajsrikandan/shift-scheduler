package com.scheduler.service;

import com.scheduler.dto.TaskRequest;
import com.scheduler.dto.TaskResponse;
import com.scheduler.entity.Priority;
import com.scheduler.entity.Task;
import com.scheduler.exception.ResourceNotFoundException;
import com.scheduler.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TaskService {

    private static final Map<Priority, Integer> PRIORITY_WEIGHT =
            Map.of(Priority.HIGH, 0, Priority.MEDIUM, 1, Priority.LOW, 2);

    private static final Comparator<Task> TASK_ORDER = Comparator
            .comparing(Task::getCompleted)
            .thenComparingInt(t -> PRIORITY_WEIGHT.getOrDefault(t.getPriority(), 3));

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setNotes(request.getNotes() != null ? request.getNotes() : "");
        task.setCompleted(false);
        task.setCompletedAt(null);
        return toResponse(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(String priorityParam) {
        List<Task> tasks;
        if (priorityParam != null && !priorityParam.isBlank()) {
            Priority priority = parsePriority(priorityParam);
            tasks = repository.findByPriority(priority);
        } else {
            tasks = repository.findAll();
        }
        return tasks.stream()
                .sorted(TASK_ORDER)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(UUID id) {
        return toResponse(getOrThrow(id));
    }

    public TaskResponse update(UUID id, TaskRequest request) {
        Task task = getOrThrow(id);
        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setNotes(request.getNotes() != null ? request.getNotes() : "");
        return toResponse(repository.save(task));
    }

    public void delete(UUID id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    public TaskResponse toggleCompleted(UUID id) {
        Task task = getOrThrow(id);
        boolean nowCompleted = !task.getCompleted();
        task.setCompleted(nowCompleted);
        task.setCompletedAt(nowCompleted ? Instant.now() : null);
        return toResponse(repository.save(task));
    }

    private Task getOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    private Priority parsePriority(String value) {
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid priority '" + value + "'. Must be one of: HIGH, MEDIUM, LOW");
        }
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse res = new TaskResponse();
        res.setId(t.getId());
        res.setCreatedAt(t.getCreatedAt());
        res.setUpdatedAt(t.getUpdatedAt());
        res.setCompleted(t.getCompleted());
        res.setCompletedAt(t.getCompletedAt());
        res.setTitle(t.getTitle());
        res.setDueDate(t.getDueDate());
        res.setPriority(t.getPriority().name());
        res.setNotes(t.getNotes());
        return res;
    }
}
