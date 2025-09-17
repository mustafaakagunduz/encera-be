package com.pappgroup.pappapp.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserPreferencesUpdateRequest {

    @Pattern(regexp = "^(tr|en)$", message = "Language must be 'tr' or 'en'")
    private String preferredLanguage;

    @Pattern(regexp = "^(light|dark|auto)$", message = "Theme must be 'light', 'dark', or 'auto'")
    private String themePreference;

    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;
    private Boolean newListingAlertsEnabled;
    private Boolean priceChangeAlertsEnabled;
    private Boolean marketingEmailsEnabled;
}