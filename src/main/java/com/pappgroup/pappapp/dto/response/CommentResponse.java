package com.pappgroup.pappapp.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;

    private Long propertyId;

    private Long userId;

    private String userName;

    private String userFirstName;

    private String userLastName;

    private String comment;

    private Integer rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}