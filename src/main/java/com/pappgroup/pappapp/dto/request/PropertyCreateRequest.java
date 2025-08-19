package com.pappgroup.pappapp.dto.request;

import com.pappgroup.pappapp.entity.RoomConfiguration;
import com.pappgroup.pappapp.enums.ListingType;
import com.pappgroup.pappapp.enums.PropertyType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyCreateRequest {

    @NotBlank(message = "İlan başlığı boş olamaz")
    @Size(max = 255, message = "İlan başlığı 255 karakterden uzun olamaz")
    private String title;

    @NotNull(message = "İlan tipi seçilmelidir")
    private ListingType listingType;

    @NotNull(message = "Emlak tipi seçilmelidir")
    private PropertyType propertyType;

    @NotBlank(message = "İl seçilmelidir")
    @Size(max = 100, message = "İl adı 100 karakterden uzun olamaz")
    private String city;

    @NotBlank(message = "İlçe seçilmelidir")
    @Size(max = 100, message = "İlçe adı 100 karakterden uzun olamaz")
    private String district;

    @NotBlank(message = "Mahalle seçilmelidir")
    @Size(max = 100, message = "Mahalle adı 100 karakterden uzun olamaz")
    private String neighborhood;

    @NotNull(message = "Fiyat belirtilmelidir")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0'dan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Fiyat formatı geçersiz")
    private BigDecimal price;

    private Boolean negotiable = false;

    @Min(value = 1, message = "Brüt alan en az 1 m² olmalıdır")
    private Integer grossArea;

    @Min(value = 1, message = "Net alan en az 1 m² olmalıdır")
    private Integer netArea;

    private Boolean elevator = false;

    private Boolean parking = false;

    private Boolean balcony = false;

    private Boolean security = false;

    @Size(max = 2000, message = "Açıklama 2000 karakterden uzun olamaz")
    private String description;

    private Boolean furnished = false;

    private RoomConfiguration roomConfiguration;

    @DecimalMin(value = "0.0", message = "Aidat negatif olamaz")
    @Digits(integer = 8, fraction = 2, message = "Aidat formatı geçersiz")
    private BigDecimal monthlyFee;

    @DecimalMin(value = "0.0", message = "Depozito negatif olamaz")
    @Digits(integer = 13, fraction = 2, message = "Depozito formatı geçersiz")
    private BigDecimal deposit;
}