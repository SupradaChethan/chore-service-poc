package com.demo.cc.service;

import com.demo.cc.domain.User;
import com.demo.cc.dto.UserDto;
import com.demo.cc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.debug("Creating new user: {}", userDto.getName());

        // Check if user with same name already exists (case-insensitive)
        if (userRepository.existsByNameIgnoreCase(userDto.getName())) {
            log.error("User with name '{}' already exists", userDto.getName());
            throw new IllegalArgumentException("User with name '" + userDto.getName() + "' already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setColor(userDto.getColor());
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.debug("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if the new name is already taken by another user (case-insensitive)
        if (!user.getName().equalsIgnoreCase(userDto.getName()) && userRepository.existsByNameIgnoreCase(userDto.getName())) {
            log.error("Cannot update: User with name '{}' already exists", userDto.getName());
            throw new IllegalArgumentException("User with name '" + userDto.getName() + "' already exists");
        }

        user.setName(userDto.getName());
        user.setColor(userDto.getColor());
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setColor(user.getColor());
        return dto;
    }
}
