package com.pappgroup.pappapp.dto.request;

import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertySearchRequest {

    private ListingType listingType;

    private PropertyType propertyType;

    private String city;

    private String district;

    private String neighborhood;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer minArea;

    private Integer maxArea;

    private Boolean furnished;

    private Boolean elevator;

    private Boolean parking;

    private Boolean balcony;

    private Boolean security;

    private Boolean negotiable;

    private Boolean featured;

    private Boolean pappSellable;

    private String keyword; // Başlık ve açıklamada arama için

    // Oda sayısı filtreleme
    private Integer minRoomCount;
    private Integer maxRoomCount;

    // Sorting options
    private String sortBy = "createdAt"; // createdAt, price, viewCount
    private String sortDirection = "DESC"; // ASC, DESC
}