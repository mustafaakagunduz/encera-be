package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.CommentRequest;
import com.pappgroup.pappapp.dto.response.CommentResponse;
import com.pappgroup.pappapp.dto.response.PropertyRatingResponse;
import com.pappgroup.pappapp.entity.Comment;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.CommentRepository;
import com.pappgroup.pappapp.repository.PropertyRepository;
import com.pappgroup.pappapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long userId, CommentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check if user already commented on this property
        if (commentRepository.existsByUserAndProperty(user, property)) {
            throw new RuntimeException("You have already commented on this property");
        }

        // Check if user is trying to comment on their own property
        if (property.getUser().getId().equals(userId)) {
            throw new RuntimeException("You cannot comment on your own property");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setProperty(property);
        comment.setComment(request.getComment());
        comment.setRating(request.getRating());

        Comment savedComment = commentRepository.save(comment);
        log.info("User {} added comment to property {}", userId, request.getPropertyId());

        return convertToCommentResponse(savedComment);
    }

    public Page<CommentResponse> getCommentsByProperty(Long propertyId, int page, int size) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPropertyOrderByCreatedAtDesc(property, pageable);

        return comments.map(this::convertToCommentResponse);
    }

    public PropertyRatingResponse getPropertyRating(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Double averageRating = commentRepository.findAverageRatingByProperty(property);
        Long totalComments = commentRepository.countByProperty(property);

        return new PropertyRatingResponse(
                averageRating != null ? averageRating : 0.0,
                totalComments
        );
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if the user is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setComment(request.getComment());
        comment.setRating(request.getRating());

        Comment updatedComment = commentRepository.save(comment);
        log.info("User {} updated comment {}", userId, commentId);

        return convertToCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if the user is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
        log.info("User {} deleted comment {}", userId, commentId);
    }

    public Page<CommentResponse> getCommentsByUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPropertyUserOrderByCreatedAtDesc(user, pageable);

        return comments.map(this::convertToCommentResponse);
    }

    public PropertyRatingResponse getUserRating(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double averageRating = commentRepository.findAverageRatingByPropertyUser(user);
        Long totalComments = commentRepository.countByPropertyUser(user);

        return new PropertyRatingResponse(
                averageRating != null ? averageRating : 0.0,
                totalComments
        );
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPropertyId(comment.getProperty().getId());
        response.setUserId(comment.getUser().getId());
        response.setUserName(comment.getUser().getEmail());
        response.setUserFirstName(comment.getUser().getFirstName());
        response.setUserLastName(comment.getUser().getLastName());
        response.setComment(comment.getComment());
        response.setRating(comment.getRating());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}