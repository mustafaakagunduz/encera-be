package com.pappgroup.pappapp.entity;

public record RoomConfiguration(
        Integer roomCount,
        Integer hallCount
) {

    public RoomConfiguration {
        if (roomCount != null && roomCount < 0) {
            throw new IllegalArgumentException("Room count cannot be negative");
        }
        if (hallCount != null && hallCount < 0) {
            throw new IllegalArgumentException("Hall count cannot be negative");
        }
    }

    // Frontend için string format (örn: "2+1", "3+1")
    public String getDisplayFormat() {
        if (roomCount == null || hallCount == null) {
            return null;
        }
        return roomCount + "+" + hallCount;
    }

    // String'den RoomConfiguration oluşturmak için (örn: "2+1" -> RoomConfiguration(2, 1))
    public static RoomConfiguration fromString(String configuration) {
        if (configuration == null || configuration.trim().isEmpty()) {
            return new RoomConfiguration(null, null);
        }

        try {
            String[] parts = configuration.split("\\+");
            if (parts.length == 2) {
                int rooms = Integer.parseInt(parts[0].trim());
                int halls = Integer.parseInt(parts[1].trim());
                return new RoomConfiguration(rooms, halls);
            }
        } catch (NumberFormatException e) {
            // Invalid format, return null values
        }

        return new RoomConfiguration(null, null);
    }
}