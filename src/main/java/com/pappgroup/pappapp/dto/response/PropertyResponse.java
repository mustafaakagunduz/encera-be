package com.pappgroup.pappapp.dto.response;

import com.pappgroup.pappapp.entity.RoomConfiguration;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyResponse {

    private Long id;

    private String title;

    private ListingType listingType;

    private PropertyType propertyType;

    private String city;

    private String district;

    private String neighborhood;

    private BigDecimal price;

    private Boolean negotiable;

    private Integer grossArea;

    private Integer netArea;

    private Boolean elevator;

    private Boolean parking;

    private Boolean balcony;

    private Boolean security;

    private String description;

    private Boolean featured;

    private Boolean pappSellable;

    private Boolean furnished;

    private RoomConfiguration roomConfiguration;

    private BigDecimal monthlyFee;

    private BigDecimal deposit;

    // Bina bilgileri
    private Integer buildingAge;
    
    private Integer totalFloors;
    
    private Integer currentFloor;
    
    private List<String> heatingTypes;

    // İlan durumu
    private Boolean active;

    private Boolean approved;

    private LocalDateTime approvedAt;

    // İstatistikler
    private Long viewCount;

    private Boolean reported;

    private Integer reportCount;

    // Kullanıcı bilgisi (kısıtlı)
    private PropertyOwnerResponse owner;

    // Tarihler
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastPublished;

    private Long approvedBy;

    private LocalDateTime lastReportedAt;

    // İç sınıf - Güvenlik için sadece gerekli bilgiler
    @Data
    public static class PropertyOwnerResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;

    }

    @Data
    public static class Owner {
        private Long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
    }
}