-- =============================================================================
-- TEST DATA: KLANTEN (CUSTOMERS) - KORRIZJEARRE FOAR REGISTRATIONTYPE
-- =============================================================================

-- KLANT YN HEEMSKERK (Brûkt no NATIVE ynstee fan STANDARD)
INSERT INTO customer (id, public_id, first_name, last_name, gender, phone_number, email, image_url, registration_type, address_id, profile_id)
VALUES (1, 'u1111111-e4f5-4a1b-8c2d-3e4f5a6b7c8d', 'Sven', 'De Vries', 'MALE', '0612345678', 'sven@heemskerk.nl', 'http://example.com', 'NATIVE', 101, 1);

-- KLANT YN BEVERWIJK (Brûkt no NATIVE ynstee fan STANDARD)
INSERT INTO customer (id, public_id, first_name, last_name, gender, phone_number, email, image_url, registration_type, address_id, profile_id)
VALUES (2, 'u2222222-5f6a-4b7c-8d9e-0f1a2b3c4d5e', 'Lisa', 'Bakker', 'FEMALE', '0687654321', 'lisa@beverwijk.nl', 'http://example.com', 'NATIVE', 102, 2);

-- KLANT YN CASTRICUM (Dit wie al GOOGLE en is dus goed)
INSERT INTO customer (id, public_id, first_name, last_name, gender, phone_number, email, image_url, registration_type, address_id, profile_id)
VALUES (3, 'u3333333-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Bram', 'Meijer', 'MALE', '0654321098', 'bram@castricum.nl', 'http://example.com', 'GOOGLE', 103, 3);

-- KLANT YN UITGEEST (Brûkt no GOOGLE ynstee fan APPLE)
INSERT INTO customer (id, public_id, first_name, last_name, gender, phone_number, email, image_url, registration_type, address_id, profile_id)
VALUES (4, 'u4444444-b5a6-4f7e-8d9c-0b1a2f3e4d5c', 'Emma', 'Jansen', 'FEMALE', '0698765432', 'emma@uitgeest.nl', 'http://example.com', 'GOOGLE', 104, 4);

-- CUSTOMER IN ALKMAAR (Distant from Heemskerk cluster, right next to Alkmaar shop)
INSERT INTO customer (id, public_id, first_name, last_name, gender, phone_number, email, image_url, registration_type, address_id, profile_id)
VALUES (5, 'u5555555-b5a6-4f7e-8d9c-0b1a2f3e4d5c', 'Daan', 'Dijkstra', 'MALE', '0645678901', 'daan@alkmaar.nl', 'http://example.com', 'NATIVE', 105, 5);
