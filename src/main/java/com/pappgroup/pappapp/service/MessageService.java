package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.MessageRequest;
import com.pappgroup.pappapp.dto.response.ConversationResponse;
import com.pappgroup.pappapp.dto.response.MessageResponse;
import com.pappgroup.pappapp.entity.Message;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.MessageRepository;
import com.pappgroup.pappapp.repository.PropertyRepository;
import com.pappgroup.pappapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public MessageResponse sendMessage(Long senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (senderId.equals(request.getReceiverId())) {
            throw new RuntimeException("Cannot send message to yourself");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setIsRead(false);
        message.setDeletedBySender(false);
        message.setDeletedByReceiver(false);

        // If message is about a property
        if (request.getPropertyId() != null) {
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new RuntimeException("Property not found"));
            message.setProperty(property);
        }

        Message savedMessage = messageRepository.save(message);
        return convertToMessageResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(Long userId, Long otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new RuntimeException("Cannot get conversation with yourself");
        }

        List<Message> messages = messageRepository.findConversationBetweenUsers(userId, otherUserId);
        return messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        List<Message> lastMessages = messageRepository.findUserConversations(userId);
        Map<Long, ConversationResponse> conversationsMap = new HashMap<>();

        for (Message message : lastMessages) {
            Long otherUserId = message.getSender().getId().equals(userId)
                    ? message.getReceiver().getId()
                    : message.getSender().getId();

            if (!conversationsMap.containsKey(otherUserId)) {
                User otherUser = message.getSender().getId().equals(userId)
                        ? message.getReceiver()
                        : message.getSender();

                ConversationResponse conversation = new ConversationResponse();
                conversation.setOtherUserId(otherUserId);
                conversation.setOtherUserName(otherUser.getFirstName() + " " + otherUser.getLastName());
                conversation.setOtherUserEmail(otherUser.getEmail());
                conversation.setLastMessage(message.getContent());
                conversation.setLastMessageTime(message.getCreatedAt());

                if (message.getProperty() != null) {
                    conversation.setPropertyId(message.getProperty().getId());
                    conversation.setPropertyTitle(message.getProperty().getTitle());
                }

                // Check for unread messages
                List<Message> unreadMessages = messageRepository.findUnreadMessagesBetweenUsers(userId, otherUserId);
                conversation.setHasUnreadMessages(!unreadMessages.isEmpty());
                conversation.setUnreadCount((long) unreadMessages.size());

                conversationsMap.put(otherUserId, conversation);
            }
        }

        return new ArrayList<>(conversationsMap.values());
    }

    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("You can only mark your own messages as read");
        }

        if (!message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    public void markConversationAsRead(Long userId, Long otherUserId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessagesBetweenUsers(userId, otherUserId);

        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }

        if (!unreadMessages.isEmpty()) {
            messageRepository.saveAll(unreadMessages);
        }
    }

    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getUserMessages(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return messages.map(this::convertToMessageResponse);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getPropertyMessages(Long propertyId) {
        List<Message> messages = messageRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
        return messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse convertToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.setSenderEmail(message.getSender().getEmail());
        response.setReceiverId(message.getReceiver().getId());
        response.setReceiverName(message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName());
        response.setReceiverEmail(message.getReceiver().getEmail());

        if (message.getProperty() != null) {
            response.setPropertyId(message.getProperty().getId());
            response.setPropertyTitle(message.getProperty().getTitle());
        }

        response.setContent(message.getContent());
        response.setIsRead(message.getIsRead());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        response.setReadAt(message.getReadAt());

        return response;
    }

    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Kullanıcı mesajın göndereni mi alıcısı mı kontrolü
        if (message.getSender().getId().equals(userId)) {
            // Gönderen tarafından siliniyor
            message.setDeletedBySender(true);
            message.setSenderDeletedAt(LocalDateTime.now());
        } else if (message.getReceiver().getId().equals(userId)) {
            // Alıcı tarafından siliniyor
            message.setDeletedByReceiver(true);
            message.setReceiverDeletedAt(LocalDateTime.now());
        } else {
            throw new RuntimeException("You can only delete messages that you sent or received");
        }

        messageRepository.save(message);

        // Eğer her iki kullanıcı da mesajı sildiyse, gerçekten sil
        if (message.getDeletedBySender() && message.getDeletedByReceiver()) {
            messageRepository.delete(message);
        }
    }

    public void deleteConversation(Long userId, Long otherUserId) {
        // Kullanıcının belirtilen kişiyle olan tüm mesajlarını soft delete yap
        List<Message> messages = messageRepository.findConversationBetweenUsers(userId, otherUserId);

        for (Message message : messages) {
            if (message.getSender().getId().equals(userId)) {
                // Kullanıcı gönderen ise
                message.setDeletedBySender(true);
                message.setSenderDeletedAt(LocalDateTime.now());
            } else if (message.getReceiver().getId().equals(userId)) {
                // Kullanıcı alıcı ise
                message.setDeletedByReceiver(true);
                message.setReceiverDeletedAt(LocalDateTime.now());
            }
        }

        // Güncellenen mesajları kaydet
        messageRepository.saveAll(messages);

        // Eğer her iki kullanıcı da tüm mesajları sildiyse, gerçekten sil
        messages.stream()
            .filter(msg -> msg.getDeletedBySender() && msg.getDeletedByReceiver())
            .forEach(messageRepository::delete);
    }
}