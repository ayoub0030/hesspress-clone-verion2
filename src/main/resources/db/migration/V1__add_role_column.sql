-- Add role column to users table
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Set existing admin user to ADMIN role
UPDATE users SET role = 'ADMIN' WHERE email = 'admin';

-- Create admin user if it doesn't exist
INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin', '$2a$10$Uj0ZgD9OY5k.DfBZA1PQG.wtDTeQFO.CPMgJ3h9LkYkSHTPE.NGMK', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin');
