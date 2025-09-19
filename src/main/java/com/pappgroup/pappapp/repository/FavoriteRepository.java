package com.pappgroup.pappapp.repository;

import com.pappgroup.pappapp.entity.Favorite;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndProperty(User user, Property property);

    boolean existsByUserAndProperty(User user, Property property);

    void deleteByUserAndProperty(User user, Property property);

    @Query("SELECT f.property FROM Favorite f WHERE f.user = :user ORDER BY f.createdAt DESC")
    Page<Property> findFavoritePropertiesByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT f.property FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Property> findFavoritePropertiesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.property = :property")
    Long countByProperty(@Param("property") Property property);

    @Query("SELECT f.property.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoritePropertyIdsByUserId(@Param("userId") Long userId);
}