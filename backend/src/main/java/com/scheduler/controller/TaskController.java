package com.scheduler.controller;

import com.scheduler.dto.TaskRequest;
import com.scheduler.dto.TaskResponse;
import com.scheduler.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody @Valid TaskRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<TaskResponse> findAll(
            @RequestParam(required = false) String priority) {
        return service.findAll(priority);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/toggle")
    public TaskResponse toggle(@PathVariable UUID id) {
        return service.toggleCompleted(id);
    }
}
