package com.pappgroup.pappapp.repository;

import com.pappgroup.pappapp.entity.Comment;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPropertyOrderByCreatedAtDesc(Property property, Pageable pageable);

    boolean existsByUserAndProperty(User user, Property property);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.property = :property")
    Double findAverageRatingByProperty(@Param("property") Property property);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.property = :property")
    Long countByProperty(@Param("property") Property property);

    // For getting comments about a specific user (comments on properties owned by the user)
    Page<Comment> findByPropertyUserOrderByCreatedAtDesc(User propertyOwner, Pageable pageable);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.property.user = :propertyOwner")
    Double findAverageRatingByPropertyUser(@Param("propertyOwner") User propertyOwner);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.property.user = :propertyOwner")
    Long countByPropertyUser(@Param("propertyOwner") User propertyOwner);

    void deleteByUserAndProperty(User user, Property property);
}