package com.pappgroup.pappapp.dto.response;

import com.pappgroup.pappapp.entity.RoomConfiguration;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertySummaryResponse {

    private Long id;

    private String title;

    private ListingType listingType;

    private PropertyType propertyType;

    private String city;

    private String district;

    private BigDecimal price;

    private Boolean negotiable;

    private Integer grossArea;

    private Boolean elevator;

    private Boolean parking;

    private Boolean balcony;

    private Boolean furnished;

    private RoomConfiguration roomConfiguration;

    private Boolean featured;

    private Boolean pappSellable;

    // Bina bilgileri
    private Integer buildingAge;
    
    private Integer totalFloors;
    
    private Integer currentFloor;
    
    private List<String> heatingTypes;

    // İstatistikler
    private Long viewCount;

    // Tarih
    private LocalDateTime createdAt;

    // Kapak fotoğrafı URL'i (ileride)
    private String coverImageUrl;
}