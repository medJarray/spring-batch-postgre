DROP TABLE IF EXISTS temp_orders;
DROP TABLE IF EXISTS temp_sales;
DROP TABLE IF EXISTS temp_products;



CREATE TABLE temp_orders  (
  id serial
    constraint orders_pkey
    primary key,
  order_id varchar(255) not null,
  order_date varchar(255) not null,
  client_full_name varchar(255) not null,
  amount varchar(255) not null,
  verified varchar(255) not null,
  phone_number varchar(255) not null,
  email varchar(255) not null,
  address varchar(255) not null
);
SELECT SETVAL('temp_orders_id_seq', 1, false);
