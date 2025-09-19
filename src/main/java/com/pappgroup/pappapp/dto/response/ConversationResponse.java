package com.pappgroup.pappapp.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationResponse {

    private Long otherUserId;
    private String otherUserName;
    private String otherUserEmail;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Boolean hasUnreadMessages;
    private Long unreadCount;
    private Long propertyId;
    private String propertyTitle;
}