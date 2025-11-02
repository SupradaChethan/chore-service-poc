package com.demo.cc.config;

import com.demo.cc.domain.Chore;
import com.demo.cc.domain.User;
import com.demo.cc.repository.ChoreRepository;
import com.demo.cc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Tool configuration for Spring AI to interact with the chore service.
 * Provides function calling capabilities for managing users and chores.
 */
@Component
@Slf4j
public class ToolConfig {

    private final UserRepository userRepository;
    private final ChoreRepository choreRepository;

    public ToolConfig(UserRepository userRepository, ChoreRepository choreRepository) {
        this.userRepository = userRepository;
        this.choreRepository = choreRepository;
    }

    // ===== User DTOs =====

    public record CreateUserRequest(String name, String color) {}
    public record CreateUserResponse(Long userId, String name, String color, boolean created) {}

    public record GetUserRequest(Long userId) {}
    public record UserInfo(Long userId, String name, String color, int choreCount) {}

    public record UpdateUserRequest(Long userId, String name, String color) {}
    public record UpdateUserResponse(Long userId, String name, String color, boolean updated) {}

    public record DeleteUserRequest(Long userId, boolean confirm) {}
    public record DeleteUserResponse(Long userId, boolean deleted, String message) {}

    public record UserListResponse(int count, List<UserInfo> users) {}

    // ===== Chore DTOs =====

    public record CreateChoreRequest(String description, String time, String date, Long userId) {}
    public record CreateChoreResponse(Long choreId, String description, String time, String date, Long userId, String userName, boolean created) {}

    public record GetChoreRequest(Long choreId) {}
    public record ChoreInfo(Long choreId, String description, String time, String date, Long userId, String userName) {}

    public record UpdateChoreRequest(Long choreId, String description, String time, String date, Long userId) {}
    public record UpdateChoreResponse(Long choreId, String description, String time, String date, Long userId, boolean updated) {}

    public record DeleteChoreRequest(Long choreId, boolean confirm) {}
    public record DeleteChoreResponse(Long choreId, boolean deleted, String message) {}

    public record ChoresByDateRequest(String date) {}
    public record ChoresByUserRequest(Long userId, String date) {}
    public record ChoreListResponse(int count, List<ChoreInfo> chores) {}

    // ===== Helper Methods =====

    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD, e.g., 2025-10-30");
        }
    }

    private static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:MM or HH:MM:SS, e.g., 14:30");
        }
    }

    private UserInfo toUserInfo(User user) {
        return new UserInfo(
            user.getId(),
            user.getName(),
            user.getColor(),
            user.getChores() != null ? user.getChores().size() : 0
        );
    }

    private ChoreInfo toChoreInfo(Chore chore) {
        return new ChoreInfo(
            chore.getId(),
            chore.getDescription(),
            chore.getTime() != null ? chore.getTime().toString() : null,
            chore.getDate().toString(),
            chore.getUser().getId(),
            chore.getUser().getName()
        );
    }

    // ===== User Tools =====

    @Description("""
        Create a new user in the chore system.
        Required: name (user's full name)
        Optional: color (hex color code for calendar display, e.g., #FF5733)
        Returns the created user's information.
        """)
    public CreateUserResponse createUser(CreateUserRequest req) {
        log.info("createUser called with name='{}', color='{}'", req.name(), req.color());

        if (req.name() == null || req.name().isBlank()) {
            log.error("createUser failed: User name is required");
            throw new IllegalArgumentException("User name is required");
        }

        // Check if user with same name already exists (case-insensitive)
        if (userRepository.existsByNameIgnoreCase(req.name().trim())) {
            log.error("createUser failed: User with name '{}' already exists", req.name());
            throw new IllegalArgumentException("User with name '" + req.name().trim() + "' already exists");
        }

        User user = new User();
        user.setName(req.name().trim());
        user.setColor(req.color() != null ? req.color().trim() : "#3B82F6");

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id={}, name='{}', color='{}'",
                savedUser.getId(), savedUser.getName(), savedUser.getColor());

        return new CreateUserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getColor(),
            true
        );
    }

    @Description("Get detailed information about a specific user by their userId")
    public UserInfo getUser(GetUserRequest req) {
        log.info("getUser called with userId={}", req.userId());

        if (req.userId() == null) {
            log.error("getUser failed: userId is required");
            throw new IllegalArgumentException("userId is required");
        }

        UserInfo userInfo = userRepository.findById(req.userId())
            .map(this::toUserInfo)
            .orElseThrow(() -> {
                log.error("User not found with id={}", req.userId());
                return new RuntimeException("User not found with id: " + req.userId());
            });

        log.info("User retrieved: id={}, name='{}', choreCount={}",
                userInfo.userId(), userInfo.name(), userInfo.choreCount());
        return userInfo;
    }

    @Description("List all users in the chore system with their basic information")
    public UserListResponse listUsers() {
        log.info("listUsers called");

        List<UserInfo> users = userRepository.findAll().stream()
            .map(this::toUserInfo)
            .toList();

        log.info("Retrieved {} users", users.size());
        return new UserListResponse(users.size(), users);
    }

    @Description("""
        Update an existing user's information.
        Required: userId
        Optional: name (new name), color (new color code)
        At least one of name or color must be provided.
        """)
    public UpdateUserResponse updateUser(UpdateUserRequest req) {
        log.info("updateUser called with userId={}, name='{}', color='{}'",
                req.userId(), req.name(), req.color());

        if (req.userId() == null) {
            log.error("updateUser failed: userId is required");
            throw new IllegalArgumentException("userId is required");
        }

        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> {
                log.error("User not found with id={}", req.userId());
                return new RuntimeException("User not found with id: " + req.userId());
            });

        boolean updated = false;
        if (req.name() != null && !req.name().isBlank()) {
            String newName = req.name().trim();
            // Check if the new name is already taken by another user (case-insensitive)
            if (!user.getName().equalsIgnoreCase(newName) && userRepository.existsByNameIgnoreCase(newName)) {
                log.error("updateUser failed: User with name '{}' already exists", newName);
                throw new IllegalArgumentException("User with name '" + newName + "' already exists");
            }
            user.setName(newName);
            updated = true;
        }
        if (req.color() != null && !req.color().isBlank()) {
            user.setColor(req.color().trim());
            updated = true;
        }

        if (!updated) {
            log.error("updateUser failed: At least one of name or color must be provided");
            throw new IllegalArgumentException("At least one of name or color must be provided");
        }

        User savedUser = userRepository.save(user);
        log.info("User updated successfully: id={}, name='{}', color='{}'",
                savedUser.getId(), savedUser.getName(), savedUser.getColor());

        return new UpdateUserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getColor(),
            true
        );
    }

    @Description("""
        Delete a user from the system. This will also delete all their chores (cascade delete).
        Required: userId, confirm (must be true to proceed)
        WARNING: This operation cannot be undone.
        """)
    public DeleteUserResponse deleteUser(DeleteUserRequest req) {
        log.info("deleteUser called with userId={}, confirm={}", req.userId(), req.confirm());

        if (req.userId() == null) {
            log.error("deleteUser failed: userId is required");
            throw new IllegalArgumentException("userId is required");
        }

        if (!req.confirm()) {
            log.warn("deleteUser requires confirmation for userId={}", req.userId());
            return new DeleteUserResponse(
                req.userId(),
                false,
                "Confirmation required. This will delete the user and ALL their chores. Re-issue with confirm:true"
            );
        }

        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> {
                log.error("User not found with id={}", req.userId());
                return new RuntimeException("User not found with id: " + req.userId());
            });

        int choreCount = user.getChores() != null ? user.getChores().size() : 0;
        userRepository.deleteById(req.userId());

        log.info("User deleted successfully: userId={}, choreCount={}", req.userId(), choreCount);

        return new DeleteUserResponse(
            req.userId(),
            true,
            "User deleted successfully along with " + choreCount + " chore(s)"
        );
    }

    // ===== Chore Tools =====

    @Description("""
        Create a new chore for a user.
        Required: description (what needs to be done), userId (who will do it)
        Optional: date (YYYY-MM-DD format, defaults to today), time (HH:MM format)
        Example: description='Take out trash', userId=1, date='2025-10-30', time='18:00'
        """)
    public CreateChoreResponse createChore(CreateChoreRequest req) {
        log.info("createChore called with description='{}', userId={}, date='{}', time='{}'",
                req.description(), req.userId(), req.date(), req.time());

        if (req.description() == null || req.description().isBlank()) {
            log.error("createChore failed: Chore description is required");
            throw new IllegalArgumentException("Chore description is required");
        }
        if (req.userId() == null) {
            log.error("createChore failed: userId is required");
            throw new IllegalArgumentException("userId is required");
        }

        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> {
                log.error("User not found with id={}", req.userId());
                return new RuntimeException("User not found with id: " + req.userId());
            });

        Chore chore = new Chore();
        chore.setDescription(req.description().trim());
        chore.setDate(parseDate(req.date()));
        chore.setTime(parseTime(req.time()));
        chore.setUser(user);

        Chore savedChore = choreRepository.save(chore);

        log.info("Chore created successfully: choreId={}, description='{}', date={}, time={}, userId={}",
                savedChore.getId(), savedChore.getDescription(), savedChore.getDate(),
                savedChore.getTime(), savedChore.getUser().getId());

        return new CreateChoreResponse(
            savedChore.getId(),
            savedChore.getDescription(),
            savedChore.getTime() != null ? savedChore.getTime().toString() : null,
            savedChore.getDate().toString(),
            savedChore.getUser().getId(),
            savedChore.getUser().getName(),
            true
        );
    }

    @Description("Get detailed information about a specific chore by its choreId")
    public ChoreInfo getChore(GetChoreRequest req) {
        log.info("getChore called with choreId={}", req.choreId());

        if (req.choreId() == null) {
            log.error("getChore failed: choreId is required");
            throw new IllegalArgumentException("choreId is required");
        }

        ChoreInfo choreInfo = choreRepository.findById(req.choreId())
            .map(this::toChoreInfo)
            .orElseThrow(() -> {
                log.error("Chore not found with id={}", req.choreId());
                return new RuntimeException("Chore not found with id: " + req.choreId());
            });

        log.info("Chore retrieved: choreId={}, description='{}', userId={}",
                choreInfo.choreId(), choreInfo.description(), choreInfo.userId());
        return choreInfo;
    }

    @Description("List all chores in the system across all users and dates")
    public ChoreListResponse listAllChores() {
        List<ChoreInfo> chores = choreRepository.findAll().stream()
            .map(this::toChoreInfo)
            .toList();
        return new ChoreListResponse(chores.size(), chores);
    }

    @Description("""
        List all chores scheduled for a specific date across all users.
        Required: date (YYYY-MM-DD format, e.g., '2025-10-30')
        """)
    public ChoreListResponse listChoresByDate(ChoresByDateRequest req) {
        LocalDate date = parseDate(req.date());
        List<ChoreInfo> chores = choreRepository.findByDate(date).stream()
            .map(this::toChoreInfo)
            .toList();
        return new ChoreListResponse(chores.size(), chores);
    }

    @Description("""
        List chores for a specific user, optionally filtered by date.
        Required: userId
        Optional: date (YYYY-MM-DD format). If not provided, returns all chores for the user.
        """)
    public ChoreListResponse listChoresForUser(ChoresByUserRequest req) {
        if (req.userId() == null) {
            throw new IllegalArgumentException("userId is required");
        }

        List<ChoreInfo> chores;
        if (req.date() != null && !req.date().isBlank()) {
            LocalDate date = parseDate(req.date());
            chores = choreRepository.findByUserIdAndDate(req.userId(), date).stream()
                .map(this::toChoreInfo)
                .toList();
        } else {
            User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + req.userId()));
            chores = user.getChores().stream()
                .map(this::toChoreInfo)
                .toList();
        }

        return new ChoreListResponse(chores.size(), chores);
    }

    @Description("""
        Update an existing chore.
        Required: choreId
        Optional: description, date (YYYY-MM-DD), time (HH:MM), userId (reassign to different user)
        At least one field must be provided to update.
        """)
    public UpdateChoreResponse updateChore(UpdateChoreRequest req) {
        if (req.choreId() == null) {
            throw new IllegalArgumentException("choreId is required");
        }

        Chore chore = choreRepository.findById(req.choreId())
            .orElseThrow(() -> new RuntimeException("Chore not found with id: " + req.choreId()));

        boolean updated = false;

        if (req.description() != null && !req.description().isBlank()) {
            chore.setDescription(req.description().trim());
            updated = true;
        }
        if (req.date() != null && !req.date().isBlank()) {
            chore.setDate(parseDate(req.date()));
            updated = true;
        }
        if (req.time() != null && !req.time().isBlank()) {
            chore.setTime(parseTime(req.time()));
            updated = true;
        }
        if (req.userId() != null) {
            User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + req.userId()));
            chore.setUser(user);
            updated = true;
        }

        if (!updated) {
            throw new IllegalArgumentException("At least one field (description, date, time, or userId) must be provided");
        }

        Chore savedChore = choreRepository.save(chore);

        return new UpdateChoreResponse(
            savedChore.getId(),
            savedChore.getDescription(),
            savedChore.getTime() != null ? savedChore.getTime().toString() : null,
            savedChore.getDate().toString(),
            savedChore.getUser().getId(),
            true
        );
    }

    @Description("""
        Delete a chore from the system.
        Required: choreId, confirm (must be true to proceed)
        This operation cannot be undone.
        """)
    public DeleteChoreResponse deleteChore(DeleteChoreRequest req) {
        if (req.choreId() == null) {
            throw new IllegalArgumentException("choreId is required");
        }

        if (!req.confirm()) {
            return new DeleteChoreResponse(
                req.choreId(),
                false,
                "Confirmation required. Re-issue with confirm:true to delete this chore"
            );
        }

        if (!choreRepository.existsById(req.choreId())) {
            throw new RuntimeException("Chore not found with id: " + req.choreId());
        }

        choreRepository.deleteById(req.choreId());

        return new DeleteChoreResponse(
            req.choreId(),
            true,
            "Chore deleted successfully"
        );
    }
}
