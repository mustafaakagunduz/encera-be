-- Initial users migration
-- This file creates 2 admin users and 2 regular users when the application starts
-- Only runs if the users table is empty to avoid duplicates

-- Admin Users (Password: admin123)
INSERT INTO users (email, phone_number, password, first_name, last_name, role, enabled, is_verified, preferred_language, theme_preference, email_notifications_enabled, sms_notifications_enabled, new_listing_alerts_enabled, price_change_alerts_enabled, marketing_emails_enabled, created_at, updated_at) 
SELECT * FROM (SELECT 
    'admin1@pappgroup.com' as email,
    '+905551234567' as phone_number,
    '$2a$10$M5hN2xHcjDqn1TQvxOb.YepD8qE3i8qQgZMJ.9FJZ4t9qO3i1L5ma' as password,
    'Admin' as first_name,
    'Bir' as last_name,
    'ADMIN' as role,
    true as enabled,
    true as is_verified,
    'tr' as preferred_language,
    'light' as theme_preference,
    true as email_notifications_enabled,
    false as sms_notifications_enabled,
    true as new_listing_alerts_enabled,
    true as price_change_alerts_enabled,
    false as marketing_emails_enabled,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'admin1@pappgroup.com'
) LIMIT 1;

INSERT INTO users (email, phone_number, password, first_name, last_name, role, enabled, is_verified, preferred_language, theme_preference, email_notifications_enabled, sms_notifications_enabled, new_listing_alerts_enabled, price_change_alerts_enabled, marketing_emails_enabled, created_at, updated_at) 
SELECT * FROM (SELECT 
    'admin2@pappgroup.com' as email,
    '+905551234568' as phone_number,
    '$2a$10$M5hN2xHcjDqn1TQvxOb.YepD8qE3i8qQgZMJ.9FJZ4t9qO3i1L5ma' as password,
    'Admin' as first_name,
    'İki' as last_name,
    'ADMIN' as role,
    true as enabled,
    true as is_verified,
    'tr' as preferred_language,
    'light' as theme_preference,
    true as email_notifications_enabled,
    false as sms_notifications_enabled,
    true as new_listing_alerts_enabled,
    true as price_change_alerts_enabled,
    false as marketing_emails_enabled,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'admin2@pappgroup.com'
) LIMIT 1;

-- Regular Users (Password: user123)
INSERT INTO users (email, phone_number, password, first_name, last_name, role, enabled, is_verified, preferred_language, theme_preference, email_notifications_enabled, sms_notifications_enabled, new_listing_alerts_enabled, price_change_alerts_enabled, marketing_emails_enabled, created_at, updated_at)
SELECT * FROM (SELECT 
    'user1@example.com' as email,
    '+905551234569' as phone_number,
    '$2a$10$L4gM1xGcjCpn0SQvxNa.XdpC7pE2h7pPfZLI.8EIY3s8pN2h0K4la' as password,
    'Kullanıcı' as first_name,
    'Bir' as last_name,
    'USER' as role,
    true as enabled,
    true as is_verified,
    'tr' as preferred_language,
    'light' as theme_preference,
    true as email_notifications_enabled,
    false as sms_notifications_enabled,
    true as new_listing_alerts_enabled,
    true as price_change_alerts_enabled,
    false as marketing_emails_enabled,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'user1@example.com'
) LIMIT 1;

INSERT INTO users (email, phone_number, password, first_name, last_name, role, enabled, is_verified, preferred_language, theme_preference, email_notifications_enabled, sms_notifications_enabled, new_listing_alerts_enabled, price_change_alerts_enabled, marketing_emails_enabled, created_at, updated_at)
SELECT * FROM (SELECT 
    'user2@example.com' as email,
    '+905551234570' as phone_number,
    '$2a$10$L4gM1xGcjCpn0SQvxNa.XdpC7pE2h7pPfZLI.8EIY3s8pN2h0K4la' as password,
    'Kullanıcı' as first_name,
    'İki' as last_name,
    'USER' as role,
    true as enabled,
    true as is_verified,
    'tr' as preferred_language,
    'light' as theme_preference,
    true as email_notifications_enabled,
    false as sms_notifications_enabled,
    true as new_listing_alerts_enabled,
    true as price_change_alerts_enabled,
    false as marketing_emails_enabled,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'user2@example.com'
) LIMIT 1;