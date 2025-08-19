package com.pappgroup.pappapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStatsResponse {

    // Kullanıcı istatistikleri
    private Long totalProperties;
    private Long activeProperties;
    private Long approvedProperties;
    private Long pendingApprovalProperties;
    private Long inactiveProperties;
    private Long totalViews;

    // Admin istatistikleri
    private Long totalSystemProperties;
    private Long totalApprovedSystemProperties;
    private Long pendingApprovalSystemProperties;
    private Long reportedSystemProperties;
    private Long featuredSystemProperties;

    // Tip bazında sayılar
    private Long saleProperties;
    private Long rentProperties;
    private Long residentialProperties;
    private Long commercialProperties;
    private Long landProperties;
    private Long dailyRentalProperties;
}