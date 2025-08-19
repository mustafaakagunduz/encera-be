package com.pappgroup.pappapp.entity;

import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean negotiable = false;

    private Integer grossArea;

    private Integer netArea;

    @Column(nullable = false)
    private Boolean elevator = false;

    @Column(nullable = false)
    private Boolean parking = false;

    @Column(nullable = false)
    private Boolean balcony = false;

    @Column(nullable = false)
    private Boolean security = false;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(nullable = false)
    private Boolean pappSellable = false;

    @Column(nullable = false)
    private Boolean furnished = false;

    @Embedded
    private RoomConfiguration roomConfiguration;

    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyFee;

    @Column(precision = 15, scale = 2)
    private BigDecimal deposit;

    // İlan durumu ve onay sistemi
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean approved = false;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy; // Admin ID

    @Column(name = "last_published")
    private LocalDateTime lastPublished;

    // Şikayet sistemi
    @Column(nullable = false)
    private Boolean reported = false;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount = 0;

    @Column(name = "last_reported_at")
    private LocalDateTime lastReportedAt;

    // View/İstatistik
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    // User ile ilişki
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}