package com.demo.cc.config;

import com.demo.cc.config.ToolConfig.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * Configuration for Spring AI function calling.
 * Registers tool methods as Function beans that Spring AI can discover.
 */
@Configuration
public class FunctionConfig {

    private final ToolConfig toolConfig;

    public FunctionConfig(ToolConfig toolConfig) {
        this.toolConfig = toolConfig;
    }

    @Bean
    @Description("Create a new user in the chore system")
    public Function<CreateUserRequest, CreateUserResponse> createUser() {
        return toolConfig::createUser;
    }

    @Bean
    @Description("Get detailed information about a specific user by their userId")
    public Function<GetUserRequest, UserInfo> getUser() {
        return toolConfig::getUser;
    }

    @Bean
    @Description("List all users in the chore system with their basic information")
    public Function<Void, UserListResponse> listUsers() {
        return (v) -> toolConfig.listUsers();
    }

    @Bean
    @Description("Update an existing user's information")
    public Function<UpdateUserRequest, UpdateUserResponse> updateUser() {
        return toolConfig::updateUser;
    }

    @Bean
    @Description("Delete a user from the system")
    public Function<DeleteUserRequest, DeleteUserResponse> deleteUser() {
        return toolConfig::deleteUser;
    }

    @Bean
    @Description("Create a new chore for a user")
    public Function<CreateChoreRequest, CreateChoreResponse> createChore() {
        return toolConfig::createChore;
    }

    @Bean
    @Description("Get detailed information about a specific chore by its choreId")
    public Function<GetChoreRequest, ChoreInfo> getChore() {
        return toolConfig::getChore;
    }

    @Bean
    @Description("List all chores in the system across all users and dates")
    public Function<Void, ChoreListResponse> listAllChores() {
        return (v) -> toolConfig.listAllChores();
    }

    @Bean
    @Description("List all chores scheduled for a specific date across all users")
    public Function<ChoresByDateRequest, ChoreListResponse> listChoresByDate() {
        return toolConfig::listChoresByDate;
    }

    @Bean
    @Description("List chores for a specific user, optionally filtered by date")
    public Function<ChoresByUserRequest, ChoreListResponse> listChoresForUser() {
        return toolConfig::listChoresForUser;
    }

    @Bean
    @Description("Update an existing chore")
    public Function<UpdateChoreRequest, UpdateChoreResponse> updateChore() {
        return toolConfig::updateChore;
    }

    @Bean
    @Description("Delete a chore from the system")
    public Function<DeleteChoreRequest, DeleteChoreResponse> deleteChore() {
        return toolConfig::deleteChore;
    }
}
