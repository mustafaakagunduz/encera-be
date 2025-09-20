package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.request.PropertyCreateRequest;
import com.pappgroup.pappapp.dto.request.PropertySearchRequest;
import com.pappgroup.pappapp.dto.request.PropertyUpdateRequest;
import com.pappgroup.pappapp.dto.response.PropertyResponse;
import com.pappgroup.pappapp.dto.response.PropertyStatsResponse;
import com.pappgroup.pappapp.dto.response.PropertySummaryResponse;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import com.pappgroup.pappapp.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // ========== PUBLIC ENDPOİNTLER ==========

    // Tüm aktif onaylanmış ilanları getir (özet format)
    @GetMapping("/public")
    public ResponseEntity<Page<PropertySummaryResponse>> getAllActiveProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getAllActiveProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlan detayı getir ve görüntülenme artır (tam format)
    @GetMapping("/public/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        try {
            Optional<PropertyResponse> property = propertyService.getPropertyById(id);
            if (property.isPresent()) {
                // Görüntülenme sayısını artır
                propertyService.incrementViewCount(id);
                return ResponseEntity.ok(property.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Listing type'a göre filtrele
    @GetMapping("/public/listing-type/{listingType}")
    public ResponseEntity<Page<PropertySummaryResponse>> getPropertiesByListingType(
            @PathVariable ListingType listingType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPropertiesByListingType(listingType, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Property type'a göre filtrele
    @GetMapping("/public/property-type/{propertyType}")
    public ResponseEntity<Page<PropertySummaryResponse>> getPropertiesByPropertyType(
            @PathVariable PropertyType propertyType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPropertiesByPropertyType(propertyType, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Şehir bazında arama
    @GetMapping("/public/city/{city}")
    public ResponseEntity<Page<PropertySummaryResponse>> getPropertiesByCity(
            @PathVariable String city,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPropertiesByCity(city, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Şehir ve ilçe bazında arama
    @GetMapping("/public/location/{city}/{district}")
    public ResponseEntity<Page<PropertySummaryResponse>> getPropertiesByCityAndDistrict(
            @PathVariable String city,
            @PathVariable String district,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPropertiesByCityAndDistrict(city, district, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Fiyat aralığında arama
    @GetMapping("/public/price-range")
    public ResponseEntity<Page<PropertySummaryResponse>> getPropertiesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPropertiesByPriceRange(minPrice, maxPrice, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Başlık ile arama
    @GetMapping("/public/search-by-title")
    public ResponseEntity<Page<PropertySummaryResponse>> searchByTitle(
            @RequestParam String title,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.searchByTitle(title, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Açıklama ile arama
    @GetMapping("/public/search-by-description")
    public ResponseEntity<Page<PropertySummaryResponse>> searchByDescription(
            @RequestParam String description,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.searchByDescription(description, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Gelişmiş arama/filtreleme (POST method - complex request body)
    @PostMapping("/public/search")
    public ResponseEntity<Page<PropertySummaryResponse>> searchProperties(
            @Valid @RequestBody PropertySearchRequest searchRequest,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.searchProperties(searchRequest, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Basit URL-based arama (GET method - query parameters)
    @GetMapping("/public/search")
    public ResponseEntity<Page<PropertySummaryResponse>> searchPropertiesSimple(
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false) Boolean elevator,
            @RequestParam(required = false) Boolean parking,
            @RequestParam(required = false) Boolean balcony,
            @RequestParam(required = false) Boolean security,
            @RequestParam(required = false) Boolean negotiable,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean pappSellable,
            @RequestParam(required = false) Integer minRoomCount,
            @RequestParam(required = false) Integer maxRoomCount,
            @RequestParam(required = false) Integer hallCount,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            PropertySearchRequest searchRequest = new PropertySearchRequest();
            searchRequest.setListingType(listingType);
            searchRequest.setPropertyType(propertyType);
            searchRequest.setCity(city);
            searchRequest.setDistrict(district);
            searchRequest.setNeighborhood(neighborhood);
            searchRequest.setMinPrice(minPrice);
            searchRequest.setMaxPrice(maxPrice);
            searchRequest.setMinArea(minArea);
            searchRequest.setMaxArea(maxArea);
            searchRequest.setFurnished(furnished);
            searchRequest.setElevator(elevator);
            searchRequest.setParking(parking);
            searchRequest.setBalcony(balcony);
            searchRequest.setSecurity(security);
            searchRequest.setNegotiable(negotiable);
            searchRequest.setFeatured(featured);
            searchRequest.setPappSellable(pappSellable);
            searchRequest.setMinRoomCount(minRoomCount);
            searchRequest.setMaxRoomCount(maxRoomCount);
            searchRequest.setHallCount(hallCount);
            searchRequest.setKeyword(keyword);

            Page<PropertySummaryResponse> properties = propertyService.searchProperties(searchRequest, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Öne çıkarılan ilanlar
    @GetMapping("/public/featured")
    public ResponseEntity<Page<PropertySummaryResponse>> getFeaturedProperties(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getFeaturedProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // En çok görüntülenen ilanlar
    @GetMapping("/public/most-viewed")
    public ResponseEntity<Page<PropertySummaryResponse>> getMostViewedProperties(
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getMostViewedProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PAPP ile satılabilir ilanlar
    @GetMapping("/public/papp-sellable")
    public ResponseEntity<Page<PropertySummaryResponse>> getPappSellableProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertySummaryResponse> properties = propertyService.getPappSellableProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== KULLANICI ENDPOİNTLERİ ==========

    // Kullanıcının tüm ilanları (tam format)
    @GetMapping("/user/my-properties")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getCurrentUserProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getCurrentUserProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kullanıcının belirli tipte ilanları
    @GetMapping("/user/by-listing-type/{listingType}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getCurrentUserPropertiesByListingType(
            @PathVariable ListingType listingType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getCurrentUserPropertiesByListingType(listingType, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kullanıcının onaylanmış ilanları
    @GetMapping("/user/approved")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getCurrentUserApprovedProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getCurrentUserApprovedProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kullanıcının pasif ilanları
    @GetMapping("/user/inactive")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getCurrentUserInactiveProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getCurrentUserInactiveProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kullanıcının ilan sayısı
    @GetMapping("/user/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Long> getCurrentUserPropertyCount() {
        try {
            Long count = propertyService.getCurrentUserPropertyCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kullanıcının istatistikleri
    @GetMapping("/user/stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyStatsResponse> getCurrentUserStats() {
        try {
            PropertyStatsResponse stats = propertyService.getCurrentUserStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Yeni ilan oluştur
    @PostMapping("/user/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyCreateRequest request) {
        try {
            PropertyResponse createdProperty = propertyService.createProperty(request);
            return ResponseEntity.ok(createdProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlan güncelle
    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyUpdateRequest request) {
        try {
            PropertyResponse updatedProperty = propertyService.updateProperty(id, request);
            return ResponseEntity.ok(updatedProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlan sil
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlanı yeniden yayınla
    @PostMapping("/user/{id}/republish")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> republishProperty(@PathVariable Long id) {
        try {
            PropertyResponse property = propertyService.republishProperty(id);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlan durumunu değiştir (aktif/pasif)
    @PostMapping("/user/{id}/toggle-status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> togglePropertyStatus(@PathVariable Long id) {
        try {
            PropertyResponse property = propertyService.togglePropertyStatus(id);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlanı şikayet et
    @PostMapping("/user/{id}/report")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> reportProperty(@PathVariable Long id) {
        try {
            propertyService.reportProperty(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== ADMIN ENDPOİNTLERİ ==========



    // Onay bekleyen ilanlar
    @GetMapping("/admin/pending-approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getPendingApprovalProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getPendingApprovalProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Şikayet edilmiş ilanlar
    @GetMapping("/admin/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getReportedProperties(
            @PageableDefault(size = 20, sort = "reportCount", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getReportedProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Onaylanmış ilanlar
    @GetMapping("/admin/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getApprovedProperties(
            @PageableDefault(size = 20, sort = "approvedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getApprovedProperties(pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Çok şikayet alan ilanlar
    @GetMapping("/admin/high-reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyResponse>> getHighReportCountProperties(
            @RequestParam(defaultValue = "3") Integer minReportCount,
            @PageableDefault(size = 20, sort = "reportCount", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PropertyResponse> properties = propertyService.getHighReportCountProperties(minReportCount, pageable);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Toplam onaylanmış ilan sayısı (İstatistik)
    @GetMapping("/admin/approved-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getApprovedPropertyCount() {
        try {
            Long count = propertyService.getApprovedPropertyCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Sistem istatistikleri
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyStatsResponse> getSystemStats() {
        try {
            PropertyStatsResponse stats = propertyService.getSystemStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlanı onayla
    @PostMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> approveProperty(@PathVariable Long id) {
        try {
            PropertyResponse property = propertyService.approveProperty(id);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlanı reddet
    @PostMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> rejectProperty(@PathVariable Long id) {
        try {
            PropertyResponse property = propertyService.rejectProperty(id);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Şikayetleri temizle
    @PostMapping("/admin/{id}/clear-reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> clearPropertyReports(@PathVariable Long id) {
        try {
            PropertyResponse property = propertyService.clearPropertyReports(id);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: İlan güncelle (tüm alanları)
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> adminUpdateProperty(@PathVariable Long id, @Valid @RequestBody PropertyUpdateRequest request) {
        try {
            PropertyResponse updatedProperty = propertyService.adminUpdateProperty(id, request);
            return ResponseEntity.ok(updatedProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: İlan sil
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDeleteProperty(@PathVariable Long id) {
        try {
            propertyService.adminDeleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== RESİM YÖNETİMİ ENDPOİNTLERİ ==========

    // İlana resim ekle
    @PostMapping("/user/{id}/add-images")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> addPropertyImages(
            @PathVariable Long id,
            @RequestBody java.util.List<String> imageUrls) {
        try {
            PropertyResponse property = propertyService.addPropertyImages(id, imageUrls);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // İlandan resim çıkar
    @DeleteMapping("/user/{id}/remove-image")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> removePropertyImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {
        try {
            PropertyResponse property = propertyService.removePropertyImage(id, imageUrl);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Birincil resmi ayarla
    @PostMapping("/user/{id}/set-primary-image")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyResponse> setPrimaryImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {
        try {
            PropertyResponse property = propertyService.setPrimaryImage(id, imageUrl);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
