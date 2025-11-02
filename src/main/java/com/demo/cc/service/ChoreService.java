package com.demo.cc.service;

import com.demo.cc.domain.Chore;
import com.demo.cc.domain.User;
import com.demo.cc.dto.ChoreDto;
import com.demo.cc.repository.ChoreRepository;
import com.demo.cc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChoreService {

    private final ChoreRepository choreRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChoreDto> getAllChores() {
        log.debug("Fetching all chores");
        return choreRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChoreDto> getChoresByDate(LocalDate date) {
        log.debug("Fetching chores for date: {}", date);
        return choreRepository.findByDate(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChoreDto> getChoresByUserAndDate(Long userId, LocalDate date) {
        log.debug("Fetching chores for user {} on date: {}", userId, date);
        return choreRepository.findByUserIdAndDate(userId, date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChoreDto getChoreById(Long id) {
        log.debug("Fetching chore with id: {}", id);
        Chore chore = choreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chore not found with id: " + id));
        return convertToDto(chore);
    }

    @Transactional
    public ChoreDto createChore(ChoreDto choreDto) {
        log.debug("Creating new chore for user: {}", choreDto.getUserId());
        User user = userRepository.findById(choreDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + choreDto.getUserId()));

        Chore chore = new Chore();
        chore.setDescription(choreDto.getDescription());
        chore.setTime(choreDto.getTime());
        chore.setDate(choreDto.getDate());
        chore.setUser(user);

        Chore savedChore = choreRepository.save(chore);
        return convertToDto(savedChore);
    }

    @Transactional
    public ChoreDto updateChore(Long id, ChoreDto choreDto) {
        log.debug("Updating chore with id: {}", id);
        Chore chore = choreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chore not found with id: " + id));

        chore.setDescription(choreDto.getDescription());
        chore.setTime(choreDto.getTime());
        chore.setDate(choreDto.getDate());

        if (choreDto.getUserId() != null && !chore.getUser().getId().equals(choreDto.getUserId())) {
            User user = userRepository.findById(choreDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + choreDto.getUserId()));
            chore.setUser(user);
        }

        Chore updatedChore = choreRepository.save(chore);
        return convertToDto(updatedChore);
    }

    @Transactional
    public void deleteChore(Long id) {
        log.debug("Deleting chore with id: {}", id);
        if (!choreRepository.existsById(id)) {
            throw new RuntimeException("Chore not found with id: " + id);
        }
        choreRepository.deleteById(id);
    }

    private ChoreDto convertToDto(Chore chore) {
        ChoreDto dto = new ChoreDto();
        dto.setId(chore.getId());
        dto.setDescription(chore.getDescription());
        dto.setTime(chore.getTime());
        dto.setDate(chore.getDate());
        dto.setUserId(chore.getUser().getId());
        return dto;
    }
}
