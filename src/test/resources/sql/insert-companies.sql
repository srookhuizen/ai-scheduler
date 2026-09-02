-- =============================================================================
-- TEST DATA: BEDRIUWEN (UPDATED HOUSE_CALL RADIUS FOR FAILURE TESTING)
-- =============================================================================

-- HEEMSKERK
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (1, '7a2b9c3d-e4f5-4a1b-8c2d-3e4f5a6b7c8d', 'JEFF ROOZE Barbers & Academy', '74839201', NULL, 'SHOP', 1);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (2, '1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e', 'Barber Bekker', '62940183', NULL, 'SHOP', 2);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (3, 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Sultan Kapsalon', '81039482', NULL, 'SHOP', 3);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (4, 'f1e2d3c4-b5a6-4f7e-8d9c-0b1a2f3e4d5c', 'MODERNBARBER', '53920184', 25, 'HOUSE_CALL', 4);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (5, '9f8e7d6c-5b4a-4321-bcde-f0123456789a', 'Kapperij De Passage', '39201847', NULL, 'SHOP', 5);

-- BEVERWIJK
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (6, '2d3e4f5a-6b7c-4d8e-9f0a-1b2c3d4e5f6a', 'Thomas Barbershop', '90182736', NULL, 'SHOP', 6);
-- Barbers On Tour: Changed work_radius from 15 to 5 (Bram is ~8.3km away -> Excluded ❌)
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (7, 'bcde0123-4567-489a-bcde-f0123456789b', 'Barbers On Tour', '47382910', 5, 'HOUSE_CALL', 7);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (8, '3e4f5a6b-7c8d-4e9f-0a1b-2c3d4e5f6a7b', 'Kapsalon AYHAM', '18273645', NULL, 'SHOP', 8);
-- Dunya Hair Care: Changed work_radius from 40 to 3 (Bram is ~8.3km away -> Excluded ❌)
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (9, 'cdef1234-5678-49ab-cdef-0123456789bc', 'Dunya Hair Care', '29384756', 3, 'HOUSE_CALL', 9);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (10, '4f5a6b7c-8d9e-4f0a-1b2c-3d4e5f6a7b8c', 'Kapsalon Overloop', '76543210', NULL, 'SHOP', 10);

-- CASTRICUM
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (11, 'def12345-6789-4abc-def0-123456789bcd', 'De Bakkumse Barbier', '88392014', NULL, 'SHOP', 11);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (12, '5a6b7c8d-9e0f-4a1b-2c3d-4e5f6a7b8c9d', 'Your Rockabilly Barber', '49201834', 25, 'HOUSE_CALL', 12);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (13, 'ef123456-7890-4bcd-ef01-23456789bcde', 'Mancave Barbers', '30194827', NULL, 'SHOP', 13);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (14, '6b7c8d9e-0f1a-4b2c-3d4e-5f6a7b8c9d0e', 'Barbershop Dawo', '71839205', NULL, 'SHOP', 14);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (15, 'f1234567-8901-4cde-f012-3456789bcdef', 'Armin Kapper', '62948103', NULL, 'SHOP', 15);

-- UITGEEST
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (16, '7c8d9e0f-1a2b-4c3d-4e5f-6a7b8c9d0e1f', 'Haarmode Monique', '29384102', NULL, 'SHOP', 16);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (17, '01234567-8901-4def-0123-456789bcdef0', 'Kapsalon de Boet', '48392015', NULL, 'SHOP', 17);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (18, '8d9e0f1a-2b3c-4d4e-5f6a-7b8c9d0e1f2a', 'Trend Hair Uitgeest', '71039485', NULL, 'SHOP', 18);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (19, '12345678-9012-4ef0-1234-56789bcdef01', 'Hair & Beauty Salon Jinny', '62940174', 25, 'HOUSE_CALL', 19);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (20, '9e0f1a2b-3c4d-4e5f-6a7b-8c9d0e1f2a3b', 'Kapper Studio 11', '18273650', NULL, 'SHOP', 20);

-- =============================================================================
-- EXTRA COMPANIES
-- =============================================================================
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (21, 'e1234567-8901-4abc-def0-111122223333', 'The Haarlem Barber', '88492011', NULL, 'SHOP', 21);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (22, 'e7654321-8901-4abc-def0-444455556666', 'Zijlstra Barbers', '77392014', NULL, 'SHOP', 22);
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id) VALUES (23, 'e9876543-8901-4abc-def0-777788889999', 'Cheese City Cuts Alkmaar', '66294018', NULL, 'SHOP', 23);
