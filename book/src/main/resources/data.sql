-- Clear existing data (optional)
DELETE FROM bookings;
DELETE FROM time_slots;
DELETE FROM cabs;
DELETE FROM drivers;
DELETE FROM users;



-- Insert cabs
INSERT INTO cabs (registration_number, status) VALUES
('KA01AB1234', 'AVAILABLE'),
('KA02CD5678', 'AVAILABLE'),
('KA03EF9012', 'MAINTENANCE');


