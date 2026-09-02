-- =============================================================================
-- TEST DATA: ADDRESSES AND COMPANIES (Using Spring-safe CTE blocks)
-- =============================================================================

-- =============================================================================
-- HEEMSKERK
-- =============================================================================
WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Jan van Scorelstraat', '20', '1961 EZ', 'Heemskerk', 52.51014, 4.67389)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 1, '7a2b9c3d-e4f5-4a1b-8c2d-3e4f5a6b7c8d', 'JEFF ROOZE Barbers & Academy', '74839201', 15, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Deutzstraat', '33', '1961 NS', 'Heemskerk', 52.50982, 4.66981)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 2, '1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e', 'Barber Bekker', '62940183', 20, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Kerkplein', '5', '1961 EA', 'Heemskerk', 52.51112, 4.67104)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 3, 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Sultan Kapsalon', '81039482', 10, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Doctor Prinsengalerij', '2', '1962 PR', 'Heemskerk', 52.51310, 4.67815)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 4, 'f1e2d3c4-b5a6-4f7e-8d9c-0b1a2f3e4d5c', 'MODERNBARBER', '53920184', 25, 'HOUSE_CALL', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Doctor Prinsengalerij', '14', '1962 PR', 'Heemskerk', 52.51313, 4.67827)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 5, '9f8e7d6c-5b4a-4321-bcde-f0123456789a', 'Kapperij De Passage', '39201847', 12, 'SHOP', id FROM inserted_address;


-- =============================================================================
-- BEVERWIJK
-- =============================================================================
WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Baanstraat', '2', '1942 CJ', 'Beverwijk', 52.48625, 4.65682)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 6, '2d3e4f5a-6b7c-4d8e-9f0a-1b2c3d4e5f6a', 'Thomas Barbershop', '90182736', 30, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Koningstraat', '23', '1941 BA', 'Beverwijk', 52.47954, 4.65412)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 7, 'bcde0123-4567-489a-bcde-f0123456789b', 'Barbers On Tour', '47382910', 15, 'HOUSE_CALL', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Raadhuisstraat', '1A', '1941 EB', 'Beverwijk', 52.48201, 4.65547)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 8, '3e4f5a6b-7c8d-4e9f-0a1b-2c3d4e5f6a7b', 'Kapsalon AYHAM', '18273645', 20, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Parallelweg', '128 A16', '1948 NN', 'Beverwijk', 52.47890, 4.66450)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 9, 'cdef1234-5678-49ab-cdef-0123456789bc', 'Dunya Hair Care', '29384756', 40, 'HOUSE_CALL', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Wijkerbaan', '19', '1945 SC', 'Beverwijk', 52.49312, 4.66210)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 10, '4f5a6b7c-8d9e-4f0a-1b2c-3d4e5f6a7b8c', 'Kapsalon Overloop', '76543210', 15, 'SHOP', id FROM inserted_address;


-- =============================================================================
-- CASTRICUM
-- =============================================================================
WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Burgemeester Mooijstraat', '30', '1901 ET', 'Castricum', 52.54890, 4.66315)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 11, 'def12345-6789-4abc-def0-123456789bcd', 'De Bakkumse Barbier', '88392014', 15, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Dorpsstraat', '36B', '1901 EL', 'Castricum', 52.54712, 4.66480)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 12, '5a6b7c8d-9e0f-4a1b-2c3d-4e5f6a7b8c9d', 'Your Rockabilly Barber', '49201834', 25, 'HOUSE_CALL', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Overtoom', '1 A', '1901 EW', 'Castricum', 52.54992, 4.65588)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 13, 'ef123456-7890-4bcd-ef01-23456789bcde', 'Mancave Barbers', '30194827', 20, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Dorpsstraat', '31', '1901 EH', 'Castricum', 52.54701, 4.66512)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 14, '6b7c8d9e-0f1a-4b2c-3d4e-5f6a7b8c9d0e', 'Barbershop Dawo', '71839205', 15, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Kooiplein', '12a', '1901 VW', 'Castricum', 52.55110, 4.67490)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 15, 'f1234567-8901-4cde-f012-3456789bcdef', 'Armin Kapper', '62948103', 10, 'SHOP', id FROM inserted_address;


-- =============================================================================
-- UITGEEST
-- =============================================================================
WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Middelweg', '22', '1911 EG', 'Uitgeest', 52.52981, 4.71120)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 16, '7c8d9e0f-1a2b-4c3d-4e5f-6a7b8c9d0e1f', 'Haarmode Monique', '29384102', 15, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Westergeest', '46', '1911 AH', 'Uitgeest', 52.52735, 4.70412)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 17, '01234567-8901-4def-0123-456789bcdef0', 'Kapsalon de Boet', '48392015', 20, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Langebuurt', '31', '1911 AR', 'Uitgeest', 52.52890, 4.70845)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 18, '8d9e0f1a-2b3c-4d4e-5f6a-7b8c9d0e1f2a', 'Trend Hair Uitgeest', '71039485', 10, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Melkmarkt', '4', '1911 BE', 'Uitgeest', 52.52945, 4.71005)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 19, '12345678-9012-4ef0-1234-56789bcdef01', 'Hair & Beauty Salon Jinny', '62940174', 25, 'HOUSE_CALL', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Prinses Beatrixlaan', '11', '1911 GD', 'Uitgeest', 52.53120, 4.71540)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 20, '9e0f1a2b-3c4d-4e5f-6a7b-8c9d0e1f2a3b', 'Kapper Studio 11', '18273650', 12, 'SHOP', id FROM inserted_address;

-- =============================================================================
-- EXTRA ADDRESSES & COMPANIES (FOR DISTANT FILTER TESTING)
-- =============================================================================

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Grote Markt', '1', '2011 RC', 'Haarlem', 52.38131, 4.63641)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 21, 'e1234567-8901-4abc-def0-111122223333', 'The Haarlem Barber', '88492011', 15, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Zijlstraat', '55', '2011 TK', 'Haarlem', 52.38210, 4.63120)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 22, 'e7654321-8901-4abc-def0-444455556666', 'Zijlstra Barbers', '77392014', 20, 'SHOP', id FROM inserted_address;

WITH inserted_address AS (
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Laat', '140', '1811 EJ', 'Alkmaar', 52.63120, 4.75010)
    RETURNING id
    )
INSERT INTO company (id, public_id, name, kvk_number, work_radius, barber_type, address_id)
SELECT 23, 'e9876543-8901-4abc-def0-777788889999', 'Cheese City Cuts Alkmaar', '66294018', 12, 'SHOP', id FROM inserted_address;

-- Distant Customer Address (Has no company)
INSERT INTO address (id, street, number, postal_code, city, latitude, longitude)
VALUES (nextval('address_id_seq'), 'Langestraat', '22', '1811 AG', 'Alkmaar', 52.63240, 4.75120);
