-- V2__create_ordering_tables.sql
-- QR Table Ordering System

CREATE TABLE IF NOT EXISTS table_master (

    table_id BIGSERIAL PRIMARY KEY,

    table_name VARCHAR(255) NOT NULL,

    branchid BIGINT NOT NULL,

    qr_url VARCHAR(500),

    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_table_master_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);

CREATE TABLE IF NOT EXISTS customer_order (

    order_id BIGSERIAL PRIMARY KEY,

    table_id BIGINT NOT NULL,

    branchid BIGINT NOT NULL,

    total_amount DOUBLE PRECISION NOT NULL DEFAULT 0,

    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP,

    CONSTRAINT fk_customer_order_table
    FOREIGN KEY (table_id)
    REFERENCES table_master(table_id)
    ON DELETE CASCADE,

    CONSTRAINT fk_customer_order_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);

CREATE TABLE IF NOT EXISTS customer_order_item (

    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    product_name VARCHAR(255) NOT NULL,

    qty DOUBLE PRECISION NOT NULL,

    price DOUBLE PRECISION NOT NULL,

    total DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_customer_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES customer_order(order_id)
    ON DELETE CASCADE,

    CONSTRAINT fk_customer_order_item_product
    FOREIGN KEY (product_id)
    REFERENCES product(itemid)
    ON DELETE RESTRICT

);

-- ==================================================
-- INDEXES
-- ==================================================

CREATE INDEX IF NOT EXISTS idx_table_master_status
ON table_master(status);

CREATE INDEX IF NOT EXISTS idx_table_master_branchid
ON table_master(branchid);

CREATE INDEX IF NOT EXISTS idx_customer_order_table_id
ON customer_order(table_id);

CREATE INDEX IF NOT EXISTS idx_customer_order_branchid
ON customer_order(branchid);

CREATE INDEX IF NOT EXISTS idx_customer_order_order_status
ON customer_order(order_status);

CREATE INDEX IF NOT EXISTS idx_customer_order_payment_status
ON customer_order(payment_status);

CREATE INDEX IF NOT EXISTS idx_customer_order_item_order_id
ON customer_order_item(order_id);

CREATE INDEX IF NOT EXISTS idx_customer_order_item_product_id
ON customer_order_item(product_id);