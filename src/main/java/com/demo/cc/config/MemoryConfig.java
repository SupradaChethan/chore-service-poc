/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.cc.config;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * Configuration for chat memory to maintain conversation history
 *
 * @author schethan
 */
@Configuration
public class MemoryConfig {

  @Bean
  ChatMemory chatMemory() {
    // Use in-memory storage for conversation history
    return new InMemoryChatMemory();
  }

  @Bean
  MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
    // Keep the last 20 messages per conversation
    return new MessageChatMemoryAdvisor(chatMemory, CHAT_MEMORY_CONVERSATION_ID_KEY, 20);
  }
}
