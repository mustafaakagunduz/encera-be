package com.pappgroup.pappapp.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long receiverId;
    private String receiverName;
    private String receiverEmail;
    private Long propertyId;
    private String propertyTitle;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
}