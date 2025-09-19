package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.request.CommentRequest;
import com.pappgroup.pappapp.dto.response.CommentResponse;
import com.pappgroup.pappapp.dto.response.PropertyRatingResponse;
import com.pappgroup.pappapp.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.pappgroup.pappapp.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CommentRequest request) {
        try {
            Long userId = userPrincipal.getId();
            CommentResponse response = commentService.addComment(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding comment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CommentResponse> comments = commentService.getCommentsByProperty(propertyId, page, size);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error getting comments for property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/property/{propertyId}/rating")
    public ResponseEntity<PropertyRatingResponse> getPropertyRating(@PathVariable Long propertyId) {
        try {
            PropertyRatingResponse rating = commentService.getPropertyRating(propertyId);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            log.error("Error getting rating for property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        try {
            Long userId = userPrincipal.getId();
            CommentResponse response = commentService.updateComment(userId, commentId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating comment {}: {}", commentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long commentId) {
        try {
            Long userId = userPrincipal.getId();
            commentService.deleteComment(userId, commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}