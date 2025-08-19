package com.pappgroup.pappapp.repository;

import com.pappgroup.pappapp.entity.Property;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Kullanıcının ilanlarını getir
    List<Property> findByUser(User user);

    // Kullanıcının ilanlarını sayfalı getir
    Page<Property> findByUser(User user, Pageable pageable);

    // Kullanıcının ilan sayısı
    long countByUser(User user);

    // Listing type'a göre filtrele
    Page<Property> findByListingType(ListingType listingType, Pageable pageable);

    // Property type'a göre filtrele
    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);

    // Şehir bazında arama
    Page<Property> findByCityIgnoreCase(String city, Pageable pageable);

    // Şehir ve ilçe bazında arama
    Page<Property> findByCityIgnoreCaseAndDistrictIgnoreCase(String city, String district, Pageable pageable);

    // Fiyat aralığında arama
    Page<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Öne çıkarılan ilanlar
    Page<Property> findByFeaturedTrue(Pageable pageable);

    // PAPP ile satılabilir ilanlar
    Page<Property> findByPappSellableTrue(Pageable pageable);

    // Başlık ile arama (içinde geçen)
    Page<Property> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Açıklama ile arama
    Page<Property> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    // Kombinasyon sorguları
    @Query("SELECT p FROM Property p WHERE " +
            "(:listingType IS NULL OR p.listingType = :listingType) AND " +
            "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
            "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND " +
            "(:district IS NULL OR LOWER(p.district) = LOWER(:district)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minArea IS NULL OR p.grossArea >= :minArea) AND " +
            "(:maxArea IS NULL OR p.grossArea <= :maxArea)")
    Page<Property> findWithFilters(@Param("listingType") ListingType listingType,
                                   @Param("propertyType") PropertyType propertyType,
                                   @Param("city") String city,
                                   @Param("district") String district,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minArea") Integer minArea,
                                   @Param("maxArea") Integer maxArea,
                                   Pageable pageable);

    // Aktif kullanıcının ilanları (enabled user)
    @Query("SELECT p FROM Property p WHERE p.user.enabled = true")
    Page<Property> findByActiveUsers(Pageable pageable);

    // Kullanıcının belirli tipte ilanları
    Page<Property> findByUserAndListingType(User user, ListingType listingType, Pageable pageable);

    // ========== ONAY SİSTEMİ ==========

    // Onaylanmamış ilanlar (Admin için)
    Page<Property> findByApprovedFalse(Pageable pageable);

    // Onaylanmış ilanlar (Public için)
    Page<Property> findByApprovedTrueAndActiveTrue(Pageable pageable);

    // Onaylanmış ve aktif ilanlar sayısı
    long countByApprovedTrueAndActiveTrue();

    // Kullanıcının onaylanmış ilanları
    Page<Property> findByUserAndApprovedTrue(User user, Pageable pageable);

    // ========== ŞİKAYET SİSTEMİ ==========

    // Şikayet edilmiş ilanlar (Admin için)
    Page<Property> findByReportedTrue(Pageable pageable);

    // Şikayet sayısı yüksek ilanlar
    @Query("SELECT p FROM Property p WHERE p.reportCount >= :minReportCount ORDER BY p.reportCount DESC")
    Page<Property> findByReportCountGreaterThanEqual(@Param("minReportCount") Integer minReportCount, Pageable pageable);

    // ========== İSTATİSTİK ==========

    // En çok görüntülenen ilanlar
    Page<Property> findByApprovedTrueAndActiveTrueOrderByViewCountDesc(Pageable pageable);

    // Kullanıcının toplam görüntülenme sayısı
    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Property p WHERE p.user = :user")
    Long getTotalViewCountByUser(@Param("user") User user);

    // ========== AKTİFLİK DURUMU ==========

    // Aktif ilanlar
    Page<Property> findByActiveTrue(Pageable pageable);

    // Pasif ilanlar (Kullanıcının kendi ilanları için)
    Page<Property> findByUserAndActiveFalse(User user, Pageable pageable);

    // ========== GELİŞMİŞ FİLTRELEME ==========

    @Query("SELECT p FROM Property p WHERE " +
            "p.approved = true AND p.active = true AND " +
            "(:listingType IS NULL OR p.listingType = :listingType) AND " +
            "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
            "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND " +
            "(:district IS NULL OR LOWER(p.district) = LOWER(:district)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minArea IS NULL OR p.grossArea >= :minArea) AND " +
            "(:maxArea IS NULL OR p.grossArea <= :maxArea) AND " +
            "(:furnished IS NULL OR p.furnished = :furnished) AND " +
            "(:elevator IS NULL OR p.elevator = :elevator) AND " +
            "(:parking IS NULL OR p.parking = :parking)")
    Page<Property> findActivePropertiesWithFilters(@Param("listingType") ListingType listingType,
                                                   @Param("propertyType") PropertyType propertyType,
                                                   @Param("city") String city,
                                                   @Param("district") String district,
                                                   @Param("minPrice") BigDecimal minPrice,
                                                   @Param("maxPrice") BigDecimal maxPrice,
                                                   @Param("minArea") Integer minArea,
                                                   @Param("maxArea") Integer maxArea,
                                                   @Param("furnished") Boolean furnished,
                                                   @Param("elevator") Boolean elevator,
                                                   @Param("parking") Boolean parking,
                                                   Pageable pageable);
}