-- Addresses
CREATE TYPE address_type AS ENUM ('billing', 'shipping', 'distributor');

CREATE TABLE IF NOT EXISTS address (
    address_id SERIAL PRIMARY KEY,
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    apt_number VARCHAR(20)
);


CREATE TABLE IF NOT EXISTS address_relation (
    id SERIAL PRIMARY KEY,
    address_id INTEGER REFERENCES address (address_id),
    type ADDRESS_TYPE,
    UNIQUE (address_id, type)
);

-- Address specific views
CREATE OR REPLACE VIEW distributor_address AS
SELECT a.address_id, street, city, state, zip_code, apt_number, type
FROM address a
INNER JOIN address_relation ar ON (a.address_id = ar.address_id)
WHERE type = 'distributor';

CREATE OR REPLACE VIEW shipping_address AS
SELECT a.address_id, street, city, state, zip_code, apt_number, type
FROM address a
INNER JOIN address_relation ar ON (a.address_id = ar.address_id)
WHERE type = 'shipping';

CREATE OR REPLACE VIEW billing_address AS
SELECT a.address_id, street, city, state, zip_code, apt_number, type
FROM address a
INNER JOIN address_relation ar ON (a.address_id = ar.address_id)
WHERE type = 'billing';

-- Distributor
CREATE TABLE IF NOT EXISTS distributor (
    distributor_id serial PRIMARY KEY,
    distributor_phone VARCHAR(20) UNIQUE NOT NULL,
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES address (address_id)
);

-- Product
CREATE TABLE IF NOT EXISTS product (
    product_id serial PRIMARY KEY,
    product_name VARCHAR(50) UNIQUE NOT NULL,
    product_description VARCHAR(255) NOT NULL,
    product_quantity INT NOT NULL,
    product_in_stock BOOLEAN GENERATED ALWAYS AS (product_quantity > 0) STORED,
    current_unit_price NUMERIC(10, 2) NOT NULL,
    distributor_id INT,
    FOREIGN KEY (distributor_id) REFERENCES distributor (distributor_id)
);

CREATE TABLE IF NOT EXISTS product_type (
    category_id serial PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_category (
    category_id INT,
    product_id INT,
    PRIMARY KEY (category_id, product_id),
    FOREIGN KEY (category_id) REFERENCES product_type(category_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE IF NOT EXISTS product_price_change (
    price_change_id serial PRIMARY KEY,
    product_id INT,
    new_product_price NUMERIC(10, 2) NOT NULL,
    old_product_price NUMERIC(10, 2) NOT NULL,
    price_change_date TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Customer
CREATE TABLE IF NOT EXISTS customer (
    customer_id SERIAL PRIMARY KEY,            
    firstname VARCHAR(50) NOT NULL,           
    lastname VARCHAR(50) NOT NULL,            
    email_address VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(15)                   
);

-- Customer Order
CREATE TYPE order_status AS ENUM ('pending', 'shipped', 'delivered');

CREATE TABLE IF NOT EXISTS payment_info(
	payment_id SERIAL PRIMARY KEY,
	customer_id INT,
	billing_address_id INT,
	firstname VARCHAR(50) NOT NULL,
	lastname VARCHAR(50) NOT NULL,
	card_number INT NOT NULL,
	exp_date DATE NOT NULL,
	cvv INT NOT NULL,
	FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
	FOREIGN KEY (billing_address_id) REFERENCES address (address_id)
);

CREATE TABLE IF NOT EXISTS customer_order (
	order_id SERIAL PRIMARY KEY,
	customer_id INT NOT NULL,
	payment_id INT NOT NULL,
	shipping_id INT NOT NULL,
	order_status ORDER_STATUS NOT NULL DEFAULT('pending'),
	order_date DATE,
	FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
	FOREIGN KEY (payment_id) REFERENCES payment_info (payment_id),
	FOREIGN KEY (shipping_id) REFERENCES Address(address_id)
);

CREATE TABLE IF NOT EXISTS order_item (
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product (product_id),
    FOREIGN KEY (order_id) REFERENCES customer_order (order_id),
    PRIMARY KEY (order_id, product_id)
);

CREATE TABLE IF NOT EXISTS wishlist_item (
	wishlist_id SERIAL PRIMARY KEY,
	product_id int,
	quantity INT NOT NULL,
	FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE IF NOT EXISTS wishlist(
	wishlist_id INT,
	customer_id INT,
	PRIMARY KEY (wishlist_id,customer_id),
	FOREIGN KEY (wishlist_id) REFERENCES wishlist_item (wishlist_id),
	FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- Inserting Data

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

INSERT INTO distributor (distributor_phone, address_id) VALUES
('7543450122', 2),
('3451231234', 3);

INSERT INTO customer (firstname, lastname, email_address, phone_number) 
VALUES
    ('Karsten', 'Larson', 'karsten.larson@example.com', '1234567890'),
    ('Owen', 'Sailer', 'owen.sailer@example.com', '9876543210'),
    ('Patrick', 'Arbach', 'patrick.arbach@example.com', '4561237890'),
    ('Kaitlyn', 'Nickel', 'kaitlyn.nickel@example.com', '7893214560');

INSERT INTO product_type (category_name) VALUES
('Electronics'),
('Furniture'),
('Clothing'),
('Toys'),
('Groceries'),
('Sports');

INSERT INTO product (product_name, product_description, product_quantity, current_unit_price, distributor_id) VALUES
('Smartphone', 'A high-end smartphone with a 6.5-inch screen', 0, 799.99, 1),
('Laptop', 'A lightweight laptop with 8GB RAM', 50, 999.99, 2),
('Sofa', 'A comfortable three-seater sofa', 20, 599.99, 1),
('T-Shirt', 'A cotton T-shirt, size M', 150, 19.99, 2),
('Action Figure', 'A collectible action figure', 200, 29.99, 2),
('Apple', 'Fresh red apples', 0, 2.99, 1),
('Banana', 'Yellow ripe bananas', 400, 1.99, 2),
('Basketball', 'Official size basketball', 30, 29.99, 2),
('Headphones', 'Noise-canceling over-ear headphones', 75, 199.99, 1),
('Table Lamp', 'LED table lamp with adjustable brightness', 60, 49.99, 2),
('Shirt', 'Cotton button-up shirt, size L', 120, 39.99, 1),
('Jacket', 'Winter jacket, size M', 40, 129.99, 2),
('Refrigerator', 'Energy-efficient refrigerator', 10, 799.99, 2),
('Toy Car', 'Battery-operated toy car', 0, 19.99, 1),
('Football', 'Size 5 leather football', 50, 39.99, 1);

INSERT INTO product_category (category_id, product_id) VALUES
(1, 1),  -- Smartphone, Electronics
(2, 2),  -- Laptop, Furniture
(3, 3),  -- Sofa, Furniture
(4, 4),  -- T-Shirt, Clothing
(5, 5),  -- Action Figure, Toys
(6, 6),  -- Apple, Groceries
(1, 7),  -- Banana, Groceries
(3, 8),  -- Basketball, Sports
(4, 9),  -- Headphones, Electronics
(2, 10), -- Table Lamp, Furniture
(3, 11), -- Shirt, Clothing
(6, 12), -- Jacket, Groceries
(2, 13), -- Refrigerator, Furniture
(5, 14), -- Toy Car, Toys
(6, 15); -- Football, Sports

INSERT INTO product_price_change (product_id, new_product_price, old_product_price) VALUES
(1, 849.99, 799.99),  -- Smartphone
(2, 1050.00, 999.99), -- Laptop
(3, 639.99, 599.99),  -- Sofa
(5, 39.99, 29.99),    -- Action Figure
(8, 34.99, 29.99),    -- Basketball
(9, 219.99, 199.99);  -- Headphones

INSERT INTO wishlist_item (product_id, quantity) VALUES
	(1,2), --Smartphone
	(2,1), --Laptop
	(3,2), --Sofa
	(7,1), --Banana
	(4,3); -- Tshirt

INSERT INTO wishlist (wishlist_id, customer_id) VALUES
	(1,1),
	(2,3),
	(3,4),
	(4,2),
	(5,1);

INSERT INTO payment_info(customer_id, billing_address_id, firstname, lastname, card_number, exp_date, cvv) VALUES
	(1,1,'Karsten', 'Larson', 123778979, '2026-05-30', 234),
	(1,1,'Karsten', 'Larson', 768908764, '2029-10-30', 008),
	(2,4, 'Owen', 'Sailer', 123456788, '2030-11-30', 448),
	(3,4, 'Patrick', 'Arbach', 556788976, '2023-09-30', 478);

INSERT INTO customer_order (customer_id, payment_id, shipping_id,
	order_status)
VALUES 
	(1, 1, 1, 'pending'),
	(1, 2, 1, 'shipped'),
	(2, 3, 3, 'delivered');

INSERT INTO order_item (product_id, order_id, unit_price, quantity) VALUES
	(3, 1, 599.99, 1),
	(6, 1, 2.99, 12),
	(11, 1, 39.99, 2),
	(13, 2, 799.99, 1),
	(7, 3, 1.99, 4),
	(9, 3, 199.99, 1);

