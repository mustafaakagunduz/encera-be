package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.response.PropertyResponse;
import com.pappgroup.pappapp.service.FavoriteService;
import com.pappgroup.pappapp.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.pappgroup.pappapp.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{propertyId}/toggle")
    public ResponseEntity<?> toggleFavorite(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            boolean isFavorited = favoriteService.toggleFavorite(userId, propertyId);

            return ResponseUtil.success(Map.of(
                "isFavorited", isFavorited,
                "message", isFavorited ? "Property added to favorites" : "Property removed from favorites"
            ));
        } catch (Exception e) {
            return ResponseUtil.error("Failed to toggle favorite: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{propertyId}/status")
    public ResponseEntity<?> getFavoriteStatus(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            boolean isFavorited = favoriteService.isFavorite(userId, propertyId);

            return ResponseUtil.success(Map.of("isFavorited", isFavorited));
        } catch (Exception e) {
            return ResponseUtil.error("Failed to get favorite status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            Page<PropertyResponse> favorites = favoriteService.getUserFavorites(userId, page, size);

            return ResponseUtil.success(favorites);
        } catch (Exception e) {
            return ResponseUtil.error("Failed to get user favorites: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ids")
    public ResponseEntity<?> getUserFavoriteIds(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            List<Long> favoriteIds = favoriteService.getUserFavoriteIds(userId);

            return ResponseUtil.success(Map.of("favoriteIds", favoriteIds));
        } catch (Exception e) {
            return ResponseUtil.error("Failed to get user favorite IDs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Long userId = userPrincipal.getId();
            favoriteService.removeFavorite(userId, propertyId);

            return ResponseUtil.success(Map.of("message", "Property removed from favorites"));
        } catch (Exception e) {
            return ResponseUtil.error("Failed to remove favorite: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{propertyId}/count")
    public ResponseEntity<?> getFavoriteCount(@PathVariable Long propertyId) {
        try {
            Long count = favoriteService.getFavoriteCount(propertyId);
            return ResponseUtil.success(Map.of("count", count));
        } catch (Exception e) {
            return ResponseUtil.error("Failed to get favorite count: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}