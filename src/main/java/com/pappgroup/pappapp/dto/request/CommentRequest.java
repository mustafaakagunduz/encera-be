package com.pappgroup.pappapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CommentRequest {

    @NotNull(message = "Property ID is required")
    @Positive(message = "Property ID must be positive")
    private Long propertyId;

    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
    private String comment;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
}