CREATE TABLE branches (

    branchid BIGSERIAL PRIMARY KEY,

    branchname VARCHAR(255) NOT NULL,

    branchtype VARCHAR(255) NOT NULL,

    address VARCHAR(500),

    phone VARCHAR(50),

    status VARCHAR(100)

);



CREATE TABLE itemcategory (

    itemcategoryid BIGSERIAL PRIMARY KEY,

    itemcategoryname VARCHAR(255),
    branchid BIGINT,
    CONSTRAINT fk_itemcategory_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);



CREATE TABLE expensecategory (

    expensecategoryid BIGSERIAL PRIMARY KEY,

    expensecategory VARCHAR(255),
    branchid BIGINT,
    CONSTRAINT fk_expensecategory_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);



CREATE TABLE customer (

    customerid BIGSERIAL PRIMARY KEY,

    customername VARCHAR(255),

    customernumber BIGINT

);



CREATE TABLE product (

    itemid BIGSERIAL PRIMARY KEY,

    itemname VARCHAR(255) NOT NULL,

    price DOUBLE PRECISION,

    category VARCHAR(255),

    branchid BIGINT,
    CONSTRAINT fk_product_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid),

    createdat TIMESTAMP DEFAULT NOW()

);



CREATE TABLE users (

    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(255) UNIQUE NOT NULL,

    password VARCHAR(255) NOT NULL,

    role VARCHAR(255),

    status VARCHAR(255),

    branchid BIGINT,

    CONSTRAINT fk_users_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);



CREATE TABLE payment (

    paymentid BIGSERIAL PRIMARY KEY,

    date DATE,

    customername VARCHAR(255),

    customerpayment DOUBLE PRECISION,

    branchid BIGINT,

    CONSTRAINT fk_payment_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);



CREATE TABLE expense (

    expenseid BIGSERIAL PRIMARY KEY,

    date DATE,

    category VARCHAR(255),

    amount DOUBLE PRECISION,

    description VARCHAR(255),

    branchid BIGINT,

    CONSTRAINT fk_expense_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);



CREATE TABLE salesitem (

    salesitemid BIGSERIAL PRIMARY KEY,

    billno VARCHAR(255),

    itemname VARCHAR(255),

    qty DOUBLE PRECISION,

    price DOUBLE PRECISION,

    total DOUBLE PRECISION,

    customerid BIGINT NULL,

    paymentmethod VARCHAR(255),

    createdat TIMESTAMP DEFAULT NOW(),

    branchid BIGINT,

    CONSTRAINT fk_salesitem_customer
    FOREIGN KEY (customerid)
    REFERENCES customer(customerid),

    CONSTRAINT fk_salesitem_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);







CREATE TABLE branch_stock (

    stockid BIGSERIAL PRIMARY KEY,

    branchid BIGINT NOT NULL,

    productid BIGINT NOT NULL,

    qty DOUBLE PRECISION DEFAULT 0,

    CONSTRAINT fk_branch_stock_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid),

    CONSTRAINT fk_branch_stock_product
    FOREIGN KEY (productid)
    REFERENCES product(itemid)

);



CREATE TABLE stock_transfer (

    transferid BIGSERIAL PRIMARY KEY,

    frombranchid BIGINT,

    tobranchid BIGINT,

    productid BIGINT,

    qty DOUBLE PRECISION,

    transferdate TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_transfer_frombranch
    FOREIGN KEY (frombranchid)
    REFERENCES branches(branchid),

    CONSTRAINT fk_transfer_tobranch
    FOREIGN KEY (tobranchid)
    REFERENCES branches(branchid),

    CONSTRAINT fk_transfer_product
    FOREIGN KEY (productid)
    REFERENCES product(itemid)

);

CREATE TABLE register_session (

    sessionid BIGSERIAL PRIMARY KEY,

    branchid BIGINT NOT NULL,

    openedat TIMESTAMP,

    closedat TIMESTAMP,

    active BOOLEAN,

    total_sales DOUBLE PRECISION,

    cash_sales DOUBLE PRECISION,

    upi_sales DOUBLE PRECISION,

    total_bills INTEGER,

    CONSTRAINT fk_register_session_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid)

);


CREATE TABLE kitchen_production (

    productionid BIGSERIAL PRIMARY KEY,

    branchid BIGINT,

    productid BIGINT,

    qty DOUBLE PRECISION,

    productiondate TIMESTAMP DEFAULT NOW(),

    notes VARCHAR(500),

    CONSTRAINT fk_kitchen_production_branch
    FOREIGN KEY (branchid)
    REFERENCES branches(branchid),

    CONSTRAINT fk_kitchen_production_product
    FOREIGN KEY (productid)
    REFERENCES product(itemid)

);