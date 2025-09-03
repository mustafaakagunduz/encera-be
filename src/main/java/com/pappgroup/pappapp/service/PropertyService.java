package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.PropertyCreateRequest;
import com.pappgroup.pappapp.dto.request.PropertySearchRequest;
import com.pappgroup.pappapp.dto.request.PropertyUpdateRequest;
import com.pappgroup.pappapp.dto.response.PropertyResponse;
import com.pappgroup.pappapp.dto.response.PropertyStatsResponse;
import com.pappgroup.pappapp.dto.response.PropertySummaryResponse;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import com.pappgroup.pappapp.repository.PropertyRepository;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    // ========== PUBLIC METODLAR ==========

    public Page<PropertySummaryResponse> getAllActiveProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByApprovedTrueAndActiveTrue(pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Optional<PropertyResponse> getPropertyById(Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(this::convertToResponse);
    }

    public Page<PropertySummaryResponse> getPropertiesByListingType(ListingType listingType, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByListingType(listingType, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getPropertiesByPropertyType(PropertyType propertyType, Pageable pageable) {

        Page<Property> properties = propertyRepository.findByPropertyTypeAndApprovedTrueAndActiveTrue(propertyType, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getPropertiesByCity(String city, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByCityIgnoreCase(city, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getPropertiesByCityAndDistrict(String city, String district, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByCityIgnoreCaseAndDistrictIgnoreCase(city, district, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getFeaturedProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByFeaturedTrue(pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getPappSellableProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByPappSellableTrue(pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> searchByTitle(String title, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> searchByDescription(String description, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> searchProperties(PropertySearchRequest searchRequest, Pageable pageable) {
        Page<Property> properties = propertyRepository.findActivePropertiesWithFilters(
                searchRequest.getListingType(),
                searchRequest.getPropertyType(),
                searchRequest.getCity(),
                searchRequest.getDistrict(),
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getMinArea(),
                searchRequest.getMaxArea(),
                searchRequest.getFurnished(),
                searchRequest.getElevator(),
                searchRequest.getParking(),
                pageable
        );
        return properties.map(this::convertToSummaryResponse);
    }

    public Page<PropertySummaryResponse> getMostViewedProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByApprovedTrueAndActiveTrueOrderByViewCountDesc(pageable);
        return properties.map(this::convertToSummaryResponse);
    }

    @Transactional
    public void incrementViewCount(Long propertyId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();
            property.setViewCount(property.getViewCount() + 1);
            propertyRepository.save(property);
        }
    }

    // ========== KULLANICI METODLARI ==========

    public Page<PropertyResponse> getCurrentUserProperties(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Property> properties = propertyRepository.findByUser(currentUser, pageable);
        return properties.map(this::convertToResponse);
    }

    public long getCurrentUserPropertyCount() {
        User currentUser = getCurrentUser();
        return propertyRepository.countByUser(currentUser);
    }

    public Page<PropertyResponse> getCurrentUserPropertiesByListingType(ListingType listingType, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Property> properties = propertyRepository.findByUserAndListingType(currentUser, listingType, pageable);
        return properties.map(this::convertToResponse);
    }

    public Page<PropertyResponse> getCurrentUserApprovedProperties(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Property> properties = propertyRepository.findByUserAndApprovedTrue(currentUser, pageable);
        return properties.map(this::convertToResponse);
    }

    public Page<PropertyResponse> getCurrentUserInactiveProperties(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Property> properties = propertyRepository.findByUserAndActiveFalse(currentUser, pageable);
        return properties.map(this::convertToResponse);
    }

    public PropertyStatsResponse getCurrentUserStats() {
        User currentUser = getCurrentUser();

        PropertyStatsResponse stats = new PropertyStatsResponse();
        stats.setTotalProperties(propertyRepository.countByUser(currentUser));
        stats.setApprovedProperties(propertyRepository.findByUserAndApprovedTrue(currentUser, Pageable.unpaged()).getTotalElements());
        stats.setActiveProperties(propertyRepository.findByUserAndActiveFalse(currentUser, Pageable.unpaged()).getTotalElements());
        stats.setTotalViews(propertyRepository.getTotalViewCountByUser(currentUser));

        return stats;
    }

    // ========== CRUD METODLARI ==========

    @Transactional
    public PropertyResponse createProperty(PropertyCreateRequest request) {
        User currentUser = getCurrentUser();

        Property property = new Property();
        mapCreateRequestToEntity(request, property);
        property.setUser(currentUser);
        property.setActive(true);
        property.setApproved(false);
        property.setLastPublished(LocalDateTime.now());

        Property savedProperty = propertyRepository.save(property);
        return convertToResponse(savedProperty);
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyUpdateRequest request) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!existingProperty.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own properties");
        }

        mapUpdateRequestToEntity(request, existingProperty);
        Property updatedProperty = propertyRepository.save(existingProperty);

        return convertToResponse(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own properties");
        }

        propertyRepository.delete(property);
    }

    @Transactional
    public PropertyResponse republishProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only republish your own properties");
        }

        property.setActive(true);
        property.setApproved(false);
        property.setLastPublished(LocalDateTime.now());
        Property updatedProperty = propertyRepository.save(property);

        return convertToResponse(updatedProperty);
    }

    @Transactional
    public PropertyResponse togglePropertyStatus(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only toggle your own properties");
        }

        property.setActive(!property.getActive());
        if (property.getActive()) {
            property.setLastPublished(LocalDateTime.now());
        }
        Property updatedProperty = propertyRepository.save(property);

        return convertToResponse(updatedProperty);
    }

    // ========== ADMIN METODLARI ==========

    public Page<PropertyResponse> getPendingApprovalProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByApprovedFalse(pageable);
        return properties.map(this::convertToResponse);
    }

    public Page<PropertyResponse> getReportedProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByReportedTrue(pageable);
        return properties.map(this::convertToResponse);
    }

    public Page<PropertyResponse> getHighReportCountProperties(Integer minReportCount, Pageable pageable) {
        Page<Property> properties = propertyRepository.findByReportCountGreaterThanEqual(minReportCount, pageable);
        return properties.map(this::convertToResponse);
    }

    public Long getApprovedPropertyCount() {
        return propertyRepository.countByApprovedTrueAndActiveTrue();
    }

    public PropertyStatsResponse getSystemStats() {
        PropertyStatsResponse stats = new PropertyStatsResponse();

        stats.setTotalSystemProperties(propertyRepository.count());
        stats.setTotalApprovedSystemProperties(propertyRepository.countByApprovedTrueAndActiveTrue());
        stats.setPendingApprovalSystemProperties(propertyRepository.findByApprovedFalse(Pageable.unpaged()).getTotalElements());
        stats.setReportedSystemProperties(propertyRepository.findByReportedTrue(Pageable.unpaged()).getTotalElements());
        stats.setFeaturedSystemProperties(propertyRepository.findByFeaturedTrue(Pageable.unpaged()).getTotalElements());

        return stats;
    }

    @Transactional
    public PropertyResponse approveProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentAdmin = getCurrentUser();

        property.setApproved(true);
        property.setApprovedAt(LocalDateTime.now());
        property.setApprovedBy(currentAdmin.getId());

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    @Transactional
    public PropertyResponse rejectProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setApproved(false);
        property.setActive(false);

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    @Transactional
    public PropertyResponse clearPropertyReports(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setReported(false);
        property.setReportCount(0);
        property.setLastReportedAt(null);

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    public Page<PropertyResponse> getApprovedProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findByApprovedTrueAndActiveTrue(pageable);
        return properties.map(this::convertToResponse);
    }

    @Transactional
    public PropertyResponse adminUpdateProperty(Long id, PropertyUpdateRequest request) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        mapUpdateRequestToEntity(request, existingProperty);
        Property updatedProperty = propertyRepository.save(existingProperty);

        return convertToResponse(updatedProperty);
    }

    @Transactional
    public void adminDeleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyRepository.delete(property);
    }

    // ========== ŞİKAYET SİSTEMİ ==========

    @Transactional
    public void reportProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot report your own property");
        }

        property.setReported(true);
        property.setReportCount(property.getReportCount() + 1);
        property.setLastReportedAt(LocalDateTime.now());

        if (property.getReportCount() >= 5) {
            property.setActive(false);
        }

        propertyRepository.save(property);
    }

    // ========== MAPPING METODLARI ==========

    private void mapCreateRequestToEntity(PropertyCreateRequest request, Property property) {
        property.setTitle(request.getTitle());
        property.setListingType(request.getListingType());
        property.setPropertyType(request.getPropertyType());
        property.setCity(request.getCity());
        property.setDistrict(request.getDistrict());
        property.setNeighborhood(request.getNeighborhood());
        property.setPrice(request.getPrice());
        property.setNegotiable(request.getNegotiable());
        property.setGrossArea(request.getGrossArea());
        property.setNetArea(request.getNetArea());
        property.setElevator(request.getElevator());
        property.setParking(request.getParking());
        property.setBalcony(request.getBalcony());
        property.setSecurity(request.getSecurity());
        property.setDescription(request.getDescription());
        property.setFurnished(request.getFurnished());
        property.setPappSellable(request.getPappSellable()); // YENİ ALAN EKLENDİ
        property.setRoomConfiguration(request.getRoomConfiguration());
        property.setMonthlyFee(request.getMonthlyFee());
        property.setDeposit(request.getDeposit());
    }

    private void mapUpdateRequestToEntity(PropertyUpdateRequest request, Property property) {
        property.setTitle(request.getTitle());
        property.setListingType(request.getListingType());
        property.setPropertyType(request.getPropertyType());
        property.setCity(request.getCity());
        property.setDistrict(request.getDistrict());
        property.setNeighborhood(request.getNeighborhood());
        property.setPrice(request.getPrice());
        property.setNegotiable(request.getNegotiable());
        property.setGrossArea(request.getGrossArea());
        property.setNetArea(request.getNetArea());
        property.setElevator(request.getElevator());
        property.setParking(request.getParking());
        property.setBalcony(request.getBalcony());
        property.setSecurity(request.getSecurity());
        property.setDescription(request.getDescription());
        property.setFurnished(request.getFurnished());
        property.setRoomConfiguration(request.getRoomConfiguration());
        property.setMonthlyFee(request.getMonthlyFee());
        property.setDeposit(request.getDeposit());

        // YENİ EKLENEN - İlan editlendiğinde pending durumuna geçmesi için
        if (request.getApproved() != null) {
            property.setApproved(request.getApproved());
            // Eğer approved false yapılıyorsa, approvedAt ve approvedBy temizlenir
            if (!request.getApproved()) {
                property.setApprovedAt(null);
                property.setApprovedBy(null);
            }
        }

        if (request.getActive() != null) {
            boolean wasInactive = !property.getActive();
            property.setActive(request.getActive());

            // Eğer pasif ilan editleniyorsa, aktif yap ve onay bekliyor durumuna geç
            if (wasInactive && request.getActive()) {
                property.setApproved(false);
                property.setApprovedAt(null);
                property.setApprovedBy(null);
                property.setLastPublished(LocalDateTime.now());
            }
        }
    }

    private PropertyResponse convertToResponse(Property property) {
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
        response.setRoomConfiguration(property.getRoomConfiguration());
        response.setMonthlyFee(property.getMonthlyFee());
        response.setDeposit(property.getDeposit());

        response.setActive(property.getActive());
        response.setApproved(property.getApproved());
        response.setApprovedAt(property.getApprovedAt());

        response.setViewCount(property.getViewCount());
        response.setReported(property.getReported());
        response.setReportCount(property.getReportCount());

        PropertyResponse.PropertyOwnerResponse owner = new PropertyResponse.PropertyOwnerResponse();
        owner.setId(property.getUser().getId());
        owner.setFirstName(property.getUser().getFirstName());
        owner.setLastName(property.getUser().getLastName());
        response.setOwner(owner);

        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());
        response.setLastPublished(property.getLastPublished());

        return response;
    }

    private PropertySummaryResponse convertToSummaryResponse(Property property) {
        PropertySummaryResponse response = new PropertySummaryResponse();

        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setListingType(property.getListingType());
        response.setPropertyType(property.getPropertyType());
        response.setCity(property.getCity());
        response.setDistrict(property.getDistrict());
        response.setPrice(property.getPrice());
        response.setNegotiable(property.getNegotiable());
        response.setGrossArea(property.getGrossArea());
        response.setElevator(property.getElevator());
        response.setParking(property.getParking());
        response.setBalcony(property.getBalcony());
        response.setFurnished(property.getFurnished());
        response.setRoomConfiguration(property.getRoomConfiguration());
        response.setFeatured(property.getFeatured());
        response.setPappSellable(property.getPappSellable());
        response.setViewCount(property.getViewCount());
        response.setCreatedAt(property.getCreatedAt());

        return response;
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}