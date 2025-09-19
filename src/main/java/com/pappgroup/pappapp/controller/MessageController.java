package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.request.MessageRequest;
import com.pappgroup.pappapp.dto.response.ConversationResponse;
import com.pappgroup.pappapp.dto.response.MessageResponse;
import com.pappgroup.pappapp.security.UserPrincipal;
import com.pappgroup.pappapp.service.MessageService;
import com.pappgroup.pappapp.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MessageRequest request) {
        try {
            Long senderId = userPrincipal.getId();
            log.info("Sending message from user {} to user {} with content: {}",
                    senderId, request.getReceiverId(), request.getContent());
            MessageResponse response = messageService.sendMessage(senderId, request);
            log.info("Message sent successfully with ID: {}", response.getId());
            return ResponseUtil.success(response);
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            return ResponseUtil.error("Failed to send message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getUserConversations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            List<ConversationResponse> conversations = messageService.getUserConversations(userId);
            return ResponseUtil.success(conversations);
        } catch (Exception e) {
            log.error("Error getting user conversations: {}", e.getMessage());
            return ResponseUtil.error("Failed to get conversations: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> getConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        try {
            Long userId = userPrincipal.getId();
            List<MessageResponse> messages = messageService.getConversation(userId, otherUserId);
            return ResponseUtil.success(messages);
        } catch (Exception e) {
            log.error("Error getting conversation: {}", e.getMessage());
            return ResponseUtil.error("Failed to get conversation: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long messageId) {
        try {
            Long userId = userPrincipal.getId();
            messageService.markAsRead(messageId, userId);
            return ResponseUtil.success(Map.of("message", "Message marked as read"));
        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage());
            return ResponseUtil.error("Failed to mark message as read: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/conversation/{otherUserId}/read")
    public ResponseEntity<?> markConversationAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        try {
            Long userId = userPrincipal.getId();
            messageService.markConversationAsRead(userId, otherUserId);
            return ResponseUtil.success(Map.of("message", "Conversation marked as read"));
        } catch (Exception e) {
            log.error("Error marking conversation as read: {}", e.getMessage());
            return ResponseUtil.error("Failed to mark conversation as read: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            Long unreadCount = messageService.getUnreadMessageCount(userId);
            return ResponseUtil.success(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage());
            return ResponseUtil.error("Failed to get unread count: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = userPrincipal.getId();
            Page<MessageResponse> messages = messageService.getUserMessages(userId, page, size);
            return ResponseUtil.success(messages);
        } catch (Exception e) {
            log.error("Error getting user messages: {}", e.getMessage());
            return ResponseUtil.error("Failed to get messages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<?> getPropertyMessages(@PathVariable Long propertyId) {
        try {
            List<MessageResponse> messages = messageService.getPropertyMessages(propertyId);
            return ResponseUtil.success(messages);
        } catch (Exception e) {
            log.error("Error getting property messages: {}", e.getMessage());
            return ResponseUtil.error("Failed to get property messages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long messageId) {
        try {
            Long userId = userPrincipal.getId();
            messageService.deleteMessage(userId, messageId);
            return ResponseUtil.success(Map.of("message", "Message deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage());
            return ResponseUtil.error("Failed to delete message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> deleteConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        try {
            Long userId = userPrincipal.getId();
            messageService.deleteConversation(userId, otherUserId);
            return ResponseUtil.success(Map.of("message", "Conversation deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting conversation: {}", e.getMessage());
            return ResponseUtil.error("Failed to delete conversation: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}