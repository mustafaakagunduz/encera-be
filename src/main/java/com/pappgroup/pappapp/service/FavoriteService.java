package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.response.PropertyResponse;
import com.pappgroup.pappapp.entity.Favorite;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.RoomConfiguration;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.FavoriteRepository;
import com.pappgroup.pappapp.repository.PropertyRepository;
import com.pappgroup.pappapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleFavorite(Long userId, Long propertyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        boolean exists = favoriteRepository.existsByUserAndProperty(user, property);

        if (exists) {
            favoriteRepository.deleteByUserAndProperty(user, property);
            log.info("Removed property {} from user {}'s favorites", propertyId, userId);
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setProperty(property);
            favoriteRepository.save(favorite);
            log.info("Added property {} to user {}'s favorites", propertyId, userId);
            return true;
        }
    }

    public boolean isFavorite(Long userId, Long propertyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        return favoriteRepository.existsByUserAndProperty(user, property);
    }

    public Page<PropertyResponse> getUserFavorites(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Property> favoritePage = favoriteRepository.findFavoritePropertiesByUser(user, pageable);

        return favoritePage.map(this::convertToPropertyResponse);
    }

    public List<Long> getUserFavoriteIds(Long userId) {
        return favoriteRepository.findFavoritePropertyIdsByUserId(userId);
    }

    public Long getFavoriteCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        return favoriteRepository.countByProperty(property);
    }

    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        favoriteRepository.deleteByUserAndProperty(user, property);
        log.info("Removed property {} from user {}'s favorites", propertyId, userId);
    }

    private PropertyResponse convertToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setListingType(property.getListingType());
        response.setPropertyType(property.getPropertyType());
        response.setCity(property.getCity());
        response.setDistrict(property.getDistrict());
        response.setNeighborhood(property.getNeighborhood());
        response.setPrice(property.getPrice());
        response.setNegotiable(property.getNegotiable());
        response.setGrossArea(property.getGrossArea());
        response.setNetArea(property.getNetArea());
        response.setElevator(property.getElevator());
        response.setParking(property.getParking());
        response.setBalcony(property.getBalcony());
        response.setSecurity(property.getSecurity());
        response.setDescription(property.getDescription());
        response.setFeatured(property.getFeatured());
        response.setPappSellable(property.getPappSellable());
        response.setFurnished(property.getFurnished());
        // RoomConfiguration artık ayrı alanlar
        if (property.getRoomCount() != null && property.getHallCount() != null) {
            RoomConfiguration roomConfig = new RoomConfiguration(property.getRoomCount(), property.getHallCount());
            response.setRoomConfiguration(roomConfig);
        }
        response.setMonthlyFee(property.getMonthlyFee());
        response.setDeposit(property.getDeposit());
        response.setBuildingAge(property.getBuildingAge());
        response.setTotalFloors(property.getTotalFloors());
        response.setCurrentFloor(property.getCurrentFloor());
        response.setHeatingTypes(property.getHeatingTypes());
        response.setActive(property.getActive());
        response.setApproved(property.getApproved());
        response.setApprovedAt(property.getApprovedAt());
        response.setApprovedBy(property.getApprovedBy());
        response.setLastPublished(property.getLastPublished());
        response.setReported(property.getReported());
        response.setReportCount(property.getReportCount());
        response.setLastReportedAt(property.getLastReportedAt());
        response.setViewCount(property.getViewCount());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        // Owner bilgilerini ekle
        if (property.getUser() != null) {
            PropertyResponse.PropertyOwnerResponse owner = new PropertyResponse.PropertyOwnerResponse();
            owner.setId(property.getUser().getId());
            owner.setFirstName(property.getUser().getFirstName());
            owner.setLastName(property.getUser().getLastName());
            owner.setEmail(property.getUser().getEmail());
            owner.setPhoneNumber(property.getUser().getPhoneNumber());
            response.setOwner(owner);
        }

        return response;
    }
}