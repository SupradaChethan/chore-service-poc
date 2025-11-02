package com.demo.cc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Service;

/**
 * AI-powered assistant service for managing chores using Spring AI. This
 * service helps users manage their daily chores through natural language
 * interactions.
 */
@Service
@Slf4j
public class ChoreAssistantService {

    private final ChatClient chat;

    public ChoreAssistantService(ChatClient.Builder builder,
            MessageChatMemoryAdvisor memoryAdvisor) {

        this.chat = builder
                .defaultSystem("""
                               You are Suprada's chore chart maintaining AI assistant.
            You help users maintain their daily chores in the family calendar.

            You have access to tools for managing users and chores:

            User Management:
            - createUser: Add new family members
            - getUser: Get user details
            - listUsers: See all family members
            - updateUser: Update user information (name, color)
            - deleteUser: Remove a user (WARNING: deletes all their chores)

            Chore Management:
            - createChore: Add a new chore for a user
            - getChore: Get chore details
            - listAllChores: See all chores
            - listChoresByDate: Get chores for a specific date
            - listChoresForUser: Get chores for a specific user
            - updateChore: Modify chore details (description, date, time, reassign)
            - deleteChore: Remove a chore

            Guidelines:
            - Always confirm before deleting users or chores
            - Dates should be in YYYY-MM-DD format (e.g., 2025-10-30)
            - Times should be in HH:MM format (e.g., 14:30)
            - Be friendly, helpful, and proactive in suggesting chore management
            - When creating chores, ask for necessary details if not provided
            """)
                .defaultAdvisors(memoryAdvisor)
                .defaultFunctions("createUser", "getUser", "listUsers", "updateUser", "deleteUser",
                        "createChore", "getChore", "listAllChores", "listChoresByDate",
                        "listChoresForUser", "updateChore", "deleteChore")
                .build();
    }

    public String step(String sessionId, String userMessage) {
        return chat.prompt()
                .advisors(a -> a.param(
                AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
                .user(userMessage)
                .call()
                .content();
    }

}
