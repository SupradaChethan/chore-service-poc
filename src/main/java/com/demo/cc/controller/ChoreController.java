package com.demo.cc.controller;

import com.demo.cc.dto.ChoreDto;
import com.demo.cc.service.ChoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing chores in the chore calendar application.
 * Provides CRUD operations for chores with filtering by date and user.
 */
@RestController
@RequestMapping("/api/v1/chores")
@RequiredArgsConstructor
@Slf4j
public class ChoreController {

    private final ChoreService choreService;

    /**
     * Get all chores, optionally filtered by date
     * @param date Optional date filter
     * @return List of chores
     */
    @GetMapping
    public ResponseEntity<List<ChoreDto>> getChores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/v1/chores - Fetching chores with date filter: {}", date);
        List<ChoreDto> chores = date != null
                ? choreService.getChoresByDate(date)
                : choreService.getAllChores();
        return ResponseEntity.ok(chores);
    }

    /**
     * Get chores by user ID and date
     * @param userId User ID
     * @param date Date
     * @return List of chores for the user on the specified date
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChoreDto>> getChoresByUserAndDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/v1/chores/user/{} - Fetching chores for date: {}", userId, date);
        List<ChoreDto> chores = choreService.getChoresByUserAndDate(userId, date);
        return ResponseEntity.ok(chores);
    }

    /**
     * Get chore by ID
     * @param id Chore ID
     * @return Chore details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChoreDto> getChoreById(@PathVariable Long id) {
        log.info("GET /api/v1/chores/{} - Fetching chore", id);
        ChoreDto chore = choreService.getChoreById(id);
        return ResponseEntity.ok(chore);
    }

    /**
     * Create a new chore
     * @param choreDto Chore details
     * @return Created chore
     */
    @PostMapping
    public ResponseEntity<ChoreDto> createChore(@RequestBody ChoreDto choreDto) {
        log.info("POST /api/v1/chores - Creating chore for user: {}", choreDto.getUserId());
        ChoreDto createdChore = choreService.createChore(choreDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChore);
    }

    /**
     * Update an existing chore
     * @param id Chore ID
     * @param choreDto Updated chore details
     * @return Updated chore
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChoreDto> updateChore(@PathVariable Long id, @RequestBody ChoreDto choreDto) {
        log.info("PUT /api/v1/chores/{} - Updating chore", id);
        ChoreDto updatedChore = choreService.updateChore(id, choreDto);
        return ResponseEntity.ok(updatedChore);
    }

    /**
     * Delete a chore
     * @param id Chore ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChore(@PathVariable Long id) {
        log.info("DELETE /api/v1/chores/{} - Deleting chore", id);
        choreService.deleteChore(id);
        return ResponseEntity.noContent().build();
    }
}
