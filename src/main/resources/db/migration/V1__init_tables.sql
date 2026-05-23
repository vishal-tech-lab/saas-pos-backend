CREATE TABLE customer (

    customerid BIGSERIAL PRIMARY KEY,

    customername VARCHAR(255),

    customernumber INTEGER
);

CREATE TABLE expense (

    expenseid BIGSERIAL PRIMARY KEY,

    date DATE,

    category VARCHAR(255),

    amount DOUBLE PRECISION,

    description VARCHAR(255)
);

CREATE TABLE product (

    itemid BIGSERIAL PRIMARY KEY,

    itemname VARCHAR(255),

    price DOUBLE PRECISION,

    category VARCHAR(255)
);

CREATE TABLE itemcategory (

    itemcategoryid BIGSERIAL PRIMARY KEY,

    itemcategoryname VARCHAR(255)
);

CREATE TABLE expensecategory (

    expensecategoryid BIGSERIAL PRIMARY KEY,

    expensecategory VARCHAR(255)
);

CREATE TABLE payment (

    paymentid BIGSERIAL PRIMARY KEY,

    date DATE,

    customername VARCHAR(255),

    customerpayment DOUBLE PRECISION
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
        createdat TIMESTAMP



);
CREATE TABLE closeregister (

    closeregisterid BIGSERIAL PRIMARY KEY,

    closedat TIMESTAMP

);
CREATE TABLE users (

    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(255),

    password VARCHAR(255),

    role VARCHAR(255),

    status VARCHAR(255)
);