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

    void deleteByUserAndProperty(User user, Property property);
}