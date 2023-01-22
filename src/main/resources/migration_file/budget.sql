create table budget (
    id bigint not null auto_increment,
    budget_id varchar(255),
    category varchar(255),
    depot_id varchar(255),
    depot_name varchar(255),
    field_colleague_id varchar(255),
    field_colleague_name varchar(255),
    is_depot_received boolean default false,
    is_inssu boolean default true,
    month varchar(255),
    package_size varchar(255),
    product_name varchar(255),
    production_unit varchar(255),
    quantity int,
    sap_code varchar(255),
    sbu ENUM('A','B','C','D','E','N'),
    ssu_id varchar(255),
    year int,
    primary key (id)
);