CREATE TYPE address_type AS ENUM ('billing', 'shipping', 'distributor');

CREATE TABLE IF NOT EXISTS address (
	id SERIAL PRIMARY KEY,
	street VARCHAR(100) NOT NULL,
	city VARCHAR(50) NOT NULL,
	state VARCHAR(50) NOT NULL,
	zip_code VARCHAR(10) NOT NULL,
	apt_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS address_relation (
	id SERIAL PRIMARY KEY,
	address_id INTEGER REFERENCES address (id),
	type ADDRESS_TYPE,
	UNIQUE (address_id, type)
);

INSERT INTO address (street, city, state, zip_code)
VALUES 
('123 Street', 'Fargo', 'ND', '58103'),
('256 S Ave', 'Grand Forks', 'ND', '58206'),
('Loser Ave', 'Brookings', 'SD', '78983-02'),
('1600 Pennsylvania Ave', 'Washington DC', 'Washington DC', '12345');

INSERT INTO address_relation (address_id, type)
VALUES
(1, 'billing'),
(1, 'shipping'),
(2, 'distributor'),
(3, 'shipping'),
(3, 'distributor'),
(4, 'billing'),
(4, 'shipping');

