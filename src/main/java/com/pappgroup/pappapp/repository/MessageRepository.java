package com.pappgroup.pappapp.repository;

import com.pappgroup.pappapp.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Kullanıcının gönderdiği veya aldığı tüm mesajları getir
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.createdAt DESC")
    Page<Message> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // İki kullanıcı arasındaki mesajları getir (soft delete kontrolü ile)
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :user1Id AND m.receiver.id = :user2Id AND " +
           "((:user1Id = m.sender.id AND m.deletedBySender = false) OR (:user1Id = m.receiver.id AND m.deletedByReceiver = false))) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id AND " +
           "((:user1Id = m.sender.id AND m.deletedBySender = false) OR (:user1Id = m.receiver.id AND m.deletedByReceiver = false))) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // Kullanıcının konuşmalarını getir (her kişiyle son mesaj) - soft delete kontrolü ile
    @Query("SELECT m FROM Message m WHERE m.id IN (" +
           "SELECT MAX(m2.id) FROM Message m2 WHERE " +
           "(m2.sender.id = :userId OR m2.receiver.id = :userId) AND " +
           "((:userId = m2.sender.id AND m2.deletedBySender = false) OR (:userId = m2.receiver.id AND m2.deletedByReceiver = false)) " +
           "GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END" +
           ") ORDER BY m.createdAt DESC")
    List<Message> findUserConversations(@Param("userId") Long userId);

    // Kullanıcının okunmamış mesaj sayısı - soft delete kontrolü ile
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false AND m.deletedByReceiver = false")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);

    // Belirli bir emlak ilanı için mesajlar
    @Query("SELECT m FROM Message m WHERE m.property.id = :propertyId ORDER BY m.createdAt DESC")
    List<Message> findByPropertyIdOrderByCreatedAtDesc(@Param("propertyId") Long propertyId);

    // Kullanıcının belirli bir kişiyle okunmamış mesajları - soft delete kontrolü ile
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false AND m.deletedByReceiver = false")
    List<Message> findUnreadMessagesBetweenUsers(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}