package com.pappgroup.pappapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // Soft delete fields - mesajı hangi kullanıcının sildiği
    @Column(name = "deleted_by_sender", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deletedBySender = false;

    @Column(name = "deleted_by_receiver", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deletedByReceiver = false;

    @Column(name = "sender_deleted_at")
    private LocalDateTime senderDeletedAt;

    @Column(name = "receiver_deleted_at")
    private LocalDateTime receiverDeletedAt;

    @PrePersist
    public void prePersist() {
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.deletedBySender == null) {
            this.deletedBySender = false;
        }
        if (this.deletedByReceiver == null) {
            this.deletedByReceiver = false;
        }
    }
}