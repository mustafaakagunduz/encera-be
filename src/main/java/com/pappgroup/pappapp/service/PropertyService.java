package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.PropertyCreateRequest;
import com.pappgroup.pappapp.dto.request.PropertySearchRequest;
import com.pappgroup.pappapp.dto.request.PropertyUpdateRequest;
import com.pappgroup.pappapp.dto.response.PropertyResponse;
import com.pappgroup.pappapp.dto.response.PropertyStatsResponse;
import com.pappgroup.pappapp.dto.response.PropertySummaryResponse;
import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.RoomConfiguration;
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
    private final IStorageService storageService;

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
        System.out.println("=== DEBUG: Search Properties Request ===");
        System.out.println("ListingType: " + searchRequest.getListingType());
        System.out.println("PropertyType: " + searchRequest.getPropertyType());
        System.out.println("City: " + searchRequest.getCity());
        System.out.println("District: " + searchRequest.getDistrict());
        System.out.println("MinPrice: " + searchRequest.getMinPrice());
        System.out.println("MaxPrice: " + searchRequest.getMaxPrice());

        Page<Property> properties = propertyRepository.findActivePropertiesWithFilters(
                searchRequest.getListingType(),
                searchRequest.getPropertyType(),
                normalizeText(searchRequest.getCity()),
                normalizeText(searchRequest.getDistrict()),
                normalizeText(searchRequest.getNeighborhood()),
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getMinArea(),
                searchRequest.getMaxArea(),
                searchRequest.getFurnished(),
                searchRequest.getElevator(),
                searchRequest.getParking(),
                searchRequest.getBalcony(),
                searchRequest.getSecurity(),
                searchRequest.getNegotiable(),
                searchRequest.getFeatured(),
                searchRequest.getPappSellable(),
               
                searchRequest.getMinRoomCount(),
                searchRequest.getMaxRoomCount(),
                searchRequest.getHallCount(),
                normalizeText(searchRequest.getKeyword()),
                pageable
        );

        System.out.println("=== DEBUG: Query Result ===");
        System.out.println("Total Elements: " + properties.getTotalElements());
        System.out.println("Total Pages: " + properties.getTotalPages());
        System.out.println("Current Page: " + properties.getNumber());
        System.out.println("Content Size: " + properties.getContent().size());

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

    public Page<PropertySummaryResponse> getPropertiesByUserId(Long userId, Pageable pageable) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        User user = userOptional.get();
        Page<Property> properties = propertyRepository.findByUserAndApprovedTrueAndActiveTrue(user, pageable);
        return properties.map(this::convertToSummaryResponse);
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
        // RoomConfiguration artık ayrı alanlar
        if (request.getRoomConfiguration() != null) {
            property.setRoomCount(request.getRoomConfiguration().roomCount());
            property.setHallCount(request.getRoomConfiguration().hallCount());
        }
        property.setMonthlyFee(request.getMonthlyFee());
        property.setDeposit(request.getDeposit());

        // Resim yönetimi
        if (request.getImageUrls() != null) {
            property.setImageUrls(request.getImageUrls());
        }
        if (request.getPrimaryImageUrl() != null) {
            property.setPrimaryImageUrl(request.getPrimaryImageUrl());
        } else if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            // Eğer primaryImageUrl belirtilmemişse, ilk resmi birincil yap
            property.setPrimaryImageUrl(request.getImageUrls().get(0));
        }
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
        // RoomConfiguration artık ayrı alanlar
        if (request.getRoomConfiguration() != null) {
            property.setRoomCount(request.getRoomConfiguration().roomCount());
            property.setHallCount(request.getRoomConfiguration().hallCount());
        }
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
        // RoomConfiguration artık ayrı alanlar
        if (property.getRoomCount() != null && property.getHallCount() != null) {
            RoomConfiguration roomConfig = new RoomConfiguration(property.getRoomCount(), property.getHallCount());
            response.setRoomConfiguration(roomConfig);
        }
        response.setMonthlyFee(property.getMonthlyFee());
        response.setDeposit(property.getDeposit());

        response.setActive(property.getActive());
        response.setApproved(property.getApproved());
        response.setApprovedAt(property.getApprovedAt());

        response.setImageUrls(property.getImageUrls());
        response.setPrimaryImageUrl(property.getPrimaryImageUrl());

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
        // RoomConfiguration artık ayrı alanlar
        if (property.getRoomCount() != null && property.getHallCount() != null) {
            RoomConfiguration roomConfig = new RoomConfiguration(property.getRoomCount(), property.getHallCount());
            response.setRoomConfiguration(roomConfig);
        }
        response.setFeatured(property.getFeatured());
        response.setPappSellable(property.getPappSellable());
        response.setPrimaryImageUrl(property.getPrimaryImageUrl());
        response.setViewCount(property.getViewCount());
        response.setCreatedAt(property.getCreatedAt());

        return response;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ========== RESİM YÖNETİMİ ==========

    @Transactional
    public PropertyResponse addPropertyImages(Long propertyId, java.util.List<String> imageUrls) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only add images to your own properties");
        }

        if (property.getImageUrls() == null) {
            property.setImageUrls(new java.util.ArrayList<>());
        }

        property.getImageUrls().addAll(imageUrls);

        if (property.getPrimaryImageUrl() == null && !imageUrls.isEmpty()) {
            property.setPrimaryImageUrl(imageUrls.get(0));
        }

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    @Transactional
    public PropertyResponse removePropertyImage(Long propertyId, String imageUrl) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only remove images from your own properties");
        }

        if (property.getImageUrls() != null) {
            property.getImageUrls().remove(imageUrl);
        }

        if (imageUrl.equals(property.getPrimaryImageUrl())) {
            if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                property.setPrimaryImageUrl(property.getImageUrls().get(0));
            } else {
                property.setPrimaryImageUrl(null);
            }
        }

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    @Transactional
    public PropertyResponse setPrimaryImage(Long propertyId, String imageUrl) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User currentUser = getCurrentUser();

        if (!property.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only set primary image for your own properties");
        }

        if (property.getImageUrls() == null || !property.getImageUrls().contains(imageUrl)) {
            throw new RuntimeException("Image URL not found in property images");
        }

        property.setPrimaryImageUrl(imageUrl);

        Property updatedProperty = propertyRepository.save(property);
        return convertToResponse(updatedProperty);
    }

    @Transactional
    public void makeAllPropertyImagesPublic() {
        try {
            Page<Property> properties = propertyRepository.findAll(Pageable.unpaged());

            for (Property property : properties.getContent()) {
                // Primary image'ı public yap
                if (property.getPrimaryImageUrl() != null && !property.getPrimaryImageUrl().isEmpty()) {
                    try {
                        storageService.makeFilePublic(property.getPrimaryImageUrl());
                    } catch (Exception e) {
                        System.err.println("Failed to make primary image public for property " + property.getId() + ": " + e.getMessage());
                    }
                }

                // Diğer resimlerini public yap
                if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                    for (String imageUrl : property.getImageUrls()) {
                        try {
                            storageService.makeFilePublic(imageUrl);
                        } catch (Exception e) {
                            System.err.println("Failed to make image public for property " + property.getId() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to make property images public: " + e.getMessage());
        }
    }
}
