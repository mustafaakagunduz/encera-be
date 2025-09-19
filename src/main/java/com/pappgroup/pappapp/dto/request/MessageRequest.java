package com.pappgroup.pappapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    private Long propertyId; // Optional - if message is about a specific property

    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 2000, message = "Message content must be between 1 and 2000 characters")
    private String content;
}