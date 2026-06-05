CREATE TABLE customer_display (
    displayid BIGSERIAL PRIMARY KEY,
    branchid BIGINT NOT NULL,
    billno VARCHAR(255),
    total DOUBLE PRECISION DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    updatedat TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_customer_display_branchid
        FOREIGN KEY (branchid)
        REFERENCES branches(branchid)
        ON DELETE CASCADE
);

CREATE TABLE customer_display_item (
    id BIGSERIAL PRIMARY KEY,
    displayid BIGINT NOT NULL,
    itemname VARCHAR(255) NOT NULL,
    qty DOUBLE PRECISION NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    total DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_customer_display_item_displayid
        FOREIGN KEY (displayid)
        REFERENCES customer_display(displayid)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_customer_display_branchid
ON customer_display(branchid);

CREATE INDEX IF NOT EXISTS idx_customer_display_item_displayid
ON customer_display_item(displayid);